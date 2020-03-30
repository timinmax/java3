package ru.geekbrains.java2.server;

import ru.geekbrains.java2.server.auth.AuthService;
import ru.geekbrains.java2.server.auth.BaseAuthService;
import ru.geekbrains.java2.server.client.ClientHandler;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class NetworkServer {

    private final int port;
    private final List<ClientHandler> clients = new ArrayList<>();
    private final HashMap<ClientHandler,Long> authTimeout = new HashMap<>();
    private static final int SECOND = 1000;
    private static final int LOGIN_TIMEOUT = 120;
    private final AuthService authService;
    private Thread thCheckAuthTimeout;
    private static Connection dbConnection = null;

    public NetworkServer(int port) {
        this.port = port;
        this.authService = new BaseAuthService();
        this.thCheckAuthTimeout = new Thread(() -> this.checkAuthTimeout());
        this.thCheckAuthTimeout.setDaemon(true);



    }

    private static void connect2DB(){
        if (dbConnection==null){
            try {
                dbConnection = MySqlConEx.getMySQLConnection();//MySqlConEx.getMySQLConnection;
            } catch (SQLException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    public static Connection getDbConnection(){
        connect2DB();
        return dbConnection;
    }

    public void start() {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Сервер был успешно запущен на порту " + port);
            authService.start();
            thCheckAuthTimeout.start();
            while (true) {
                System.out.println("Ожидание клиентского подключения...");
                Socket clientSocket = serverSocket.accept();
                System.out.println("Клиент подлючился");
                createClientHandler(clientSocket);
            }
        } catch (IOException e) {
            System.out.println("Ошибка при работе сервера");
            e.printStackTrace();
        } finally {
            authService.stop();
            try {
                dbConnection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private void createClientHandler(Socket clientSocket) {
        ClientHandler clientHandler = new ClientHandler(this, clientSocket);
        authTimeout.put(clientHandler,new Date().getTime());
        clientHandler.run();
    }

    private void checkAuthTimeout(){
        System.out.println("Запущена проверка тайм-аута.");
        while (true) {
            try {
                Thread.sleep(1000);//оптимизация использования ресурсов
            }catch (InterruptedException e){
                System.out.println(e);
            }

            HashSet<ClientHandler> list2remove = new HashSet<>();
            authTimeout.forEach((ch,to)->{
                int secondsPast = (int)((new Date().getTime() - to)/NetworkServer.SECOND);
                if (secondsPast>=NetworkServer.LOGIN_TIMEOUT){
                    list2remove.add(ch);
                }
            });
            list2remove.stream().forEach(ClientHandler::closeConnection);
        }

    }

    public AuthService getAuthService() {
        return authService;
    }

    public synchronized void broadcastMessage(String message, ClientHandler owner) throws IOException {
        for (ClientHandler client : clients) {
            if (client != owner) {
                client.sendMessage(message);
            }
        }
    }
    public void personalMessage(String recieverNickname, String messageText) throws IOException {
        for (ClientHandler client : clients) {
            if (client.getNickname().toLowerCase().trim().equals(recieverNickname.toLowerCase().trim())) {
                client.sendMessage(messageText);
            }
        }
    }

    public void sendUserList(ClientHandler clientHandler) throws IOException, SQLException {
        String uList = "/ulist " + getUlist();
        if (clientHandler!=null) {
            clientHandler.sendMessage(uList);
        }else {
            broadcastMessage(uList,null);
        }
    }

    public synchronized void subscribe(ClientHandler clientHandler) {
        clients.add(clientHandler);
        authTimeout.remove(clientHandler);
        try {
            sendUserList(null);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public synchronized void unsubscribe(ClientHandler clientHandler) {
        clients.remove(clientHandler);
        authTimeout.remove(clientHandler);
        try {
            sendUserList(null);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public String getUlist() throws SQLException {
        String ulist = "@@@All/on";
        Connection conn = NetworkServer.getDbConnection();
        PreparedStatement stmt = conn.prepareStatement("SELECT DISTINCT nickname FROM users");
        ResultSet rs = stmt.executeQuery();

        while (rs.next()){
            String nickname = rs.getString("nickname");
            ulist += "@@@" + nickname + ((userIsOnline(nickname))?"/on":"/off");
        }
        return ulist.trim();
    }

    public boolean changeNickName(String currentNickname, String newNickname){
        try {
            if (nicknameExists(newNickname)) return false;
            Connection conn = NetworkServer.getDbConnection();
            PreparedStatement stmt = conn.prepareStatement("UPDATE `gbchat`.`users` SET `nickname` = ? WHERE (`nickname` = ?);");
            stmt.setString(1,newNickname);
            stmt.setString(2,currentNickname);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
        try {
            sendUserList(null);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return true;
    }

    private boolean nicknameExists(String nickname) throws SQLException{
        Connection conn = NetworkServer.getDbConnection();
        PreparedStatement stmt = conn.prepareStatement("SELECT DISTINCT  nickname FROM users where nickname=? limit 1");
        stmt.setString(1,nickname);
        ResultSet rs = stmt.executeQuery();

        return rs.next();

    }

    public boolean userIsOnline(String nickname){
        for (ClientHandler client : clients) {
            if (client.getNickname().toLowerCase().trim().equals(nickname.toLowerCase().trim())) {
                return true;
            }
        }
        return false;
    }
}
