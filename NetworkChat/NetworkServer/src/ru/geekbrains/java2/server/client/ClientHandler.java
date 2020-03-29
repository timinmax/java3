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

    public String getNickname() {
        return nickname;
    }

    private String nickname;

    public ClientHandler(NetworkServer networkServer, Socket socket) {
        this.networkServer = networkServer;
        this.clientSocket = socket;
    }

    public void run() {
        doHandle(clientSocket);
    }

    private void doHandle(Socket socket) {
        try {
            in = new DataInputStream(socket.getInputStream());
            out = new DataOutputStream(socket.getOutputStream());

            new Thread(() -> {
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
            }).start();

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
                networkServer.personalMessage(recieverNickname, messageText);
            }else if(message.startsWith("/ulist")){
                networkServer.sendUserList(this);
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
                    break;
                }
            }
        }
    }

    public void sendMessage(String message) throws IOException {
        out.writeUTF(message);
    }
}
