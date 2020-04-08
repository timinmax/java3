package ru.geekbrains.java2.server.client;

import ru.geekbrains.java2.server.NetworkServer;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.sql.SQLException;

public class ClientHandler {

    private final NetworkServer networkServer;
    private final Socket clientSocket;

    private DataInputStream in;
    private DataOutputStream out;

    private ObscenitiesFilter obsFilter;

    public String getNickname() {
        return nickname;
    }

    private String nickname;

    public ClientHandler(NetworkServer networkServer, Socket socket) {
        this.networkServer = networkServer;
        this.clientSocket = socket;
        this.obsFilter = new ObscenitiesFilter("common");
    }

    public void run() {
        doHandle(clientSocket);
    }

    public ObscenitiesFilter getObsFilter() {
        return obsFilter;
    }

    private void doHandle(Socket socket) {
        try {
            in = new DataInputStream(socket.getInputStream());
            out = new DataOutputStream(socket.getOutputStream());
            networkServer.getExecutorService().execute(() -> {
                try {
                    authentication();
                    readMessages();
                } catch (IOException e) {
                    System.out.println("Соединение с клиентом " + nickname + " было закрыто!");
                } catch (SQLException e){
                    e.printStackTrace();
                }finally {
                    closeConnection();
                }
            });

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void closeConnection() {
        networkServer.unsubscribe(this);
        try {
            clientSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void readMessages() throws IOException, SQLException {
        while (true) {
            String message = in.readUTF().toLowerCase();
            System.out.printf("От %s: %s%n", nickname, message);
            if ("/end".equals(message)) {
                return;
            }else if (message.startsWith("/w")) {
                String[] messageParts = message.split("\\s+", 3);
                String recieverNickname = messageParts[1];
                String messageText = nickname + ": " + messageParts[2];
                if ("ALL".equals(recieverNickname.trim().toUpperCase())){
                    networkServer.broadcastMessage("/ALL " + messageText, this);
                }else{
                    networkServer.personalMessage(recieverNickname, messageText);
                }
            }else if(message.startsWith("/ulist")){
                networkServer.sendUserList(this);
            }else if(message.startsWith("/osadd")){
                String[] messageParts = message.split("\\s+", 2);
                obsFilter.addToFilter(messageParts[1]);
            }else if(message.startsWith("/osrem")){
                String[] messageParts = message.split("\\s+", 2);
                obsFilter.removeFromFilter(messageParts[1]);
            }else if(message.startsWith("/chnick")){
                String[] messageParts = message.split("\\s+", 2);
                String newNickname = messageParts[1];
                if (networkServer.changeNickName(nickname,newNickname)){

                    networkServer.broadcastMessage(nickname + " переименовался в " + newNickname, this);
                    networkServer.broadcastMessage("/switchnick " + nickname + " " + newNickname, this);
                    this.nickname = newNickname;
                    sendMessage("/newnick " + this.nickname);
                    networkServer.sendUserList(null);
                }
            }else{

                networkServer.broadcastMessage(nickname + ": " + message, this);
            }
        }
    }



    private void authentication() throws IOException {
        while (true) {
            String message = in.readUTF();
            // "/auth login password"
            if (message.startsWith("/auth")) {
                String[] messageParts = message.split("\\s+", 3);
                String login = messageParts[1];
                String password = messageParts[2];
                String username = networkServer.getAuthService().getUsernameByLoginAndPassword(login, password);
                if (username == null) {
                    sendMessage("Отсутствует учетная запись по данному логину и паролю!");
                } else {
                    nickname = username;
                    networkServer.broadcastMessage(nickname + " зашел в чат!", this);
                    sendMessage("/auth " + nickname);
                    networkServer.subscribe(this);
                    this.obsFilter = new ObscenitiesFilter(nickname);
                    break;
                }
            }
        }
    }

    public void sendMessage(String message) throws IOException {
        out.writeUTF(message);
    }
}
