package ru.geekbrains.java2.server;

import ru.geekbrains.java2.server.auth.AuthService;
import ru.geekbrains.java2.server.auth.BaseAuthService;
import ru.geekbrains.java2.server.client.ClientHandler;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;

public class NetworkServer {

    private final int port;
    private final List<ClientHandler> clients = new ArrayList<>();
    private final HashMap<ClientHandler,Long> authTimeout = new HashMap<>();
    private static final int SECOND = 1000;
    private static final int LOGIN_TIMEOUT = 120;
    private final AuthService authService;
    private Thread thCheckAuthTimeout;

    public NetworkServer(int port) {
        this.port = port;
        this.authService = new BaseAuthService();
        this.thCheckAuthTimeout = new Thread(() -> this.checkAuthTimeout());
        this.thCheckAuthTimeout.setDaemon(true);

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
    public synchronized void subscribe(ClientHandler clientHandler) {
        clients.add(clientHandler);
        authTimeout.remove(clientHandler);
    }

    public synchronized void unsubscribe(ClientHandler clientHandler) {
        clients.remove(clientHandler);
        authTimeout.remove(clientHandler);
    }


}
