package ru.geekbrains.java2.client.model;

import ru.geekbrains.java2.client.controller.AuthEvent;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.HashMap;
import java.util.function.Consumer;

public class NetworkService {

    private final String host;
    private final int port;
    private Socket socket;
    private DataInputStream in;
    private DataOutputStream out;

    private Consumer<String> messageHandler;
    private Consumer<HashMap> refreshListHandler;
    private AuthEvent successfulAuthEvent;
    private String nickname;

    public NetworkService(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public void connect() throws IOException {
        socket = new Socket(host, port);
        in = new DataInputStream(socket.getInputStream());
        out = new DataOutputStream(socket.getOutputStream());
        runReadThread();
    }

    private void runReadThread() {
        new Thread(() -> {
            while (true) {
                try {
                    String message = in.readUTF();
                    if (message.startsWith("/auth")) {
                        String[] messageParts = message.split("\\s+", 2);
                        nickname = messageParts[1];
                        successfulAuthEvent.authIsSuccessful(nickname);
                    }else if(message.startsWith("/ulist")){

                        String[] messageParts = message.split("@@@");
                        HashMap<String,Boolean> userList = new HashMap<>();
                        for (int i = 1;i<messageParts.length;i++){
                            String[] usrRec = messageParts[i].split("/");
                            System.out.println(messageParts[i]);
                            userList.put(usrRec[0],"on".equals(usrRec[1]));
                        }
                        refreshListHandler.accept(userList);

                    }
                    else if (messageHandler != null) {
                        messageHandler.accept(message);
                    }
                } catch (IOException e) {
                    System.out.println("Поток чтения был прерван!");
                    return;
                }
            }
        }).start();
    }

    public void sendAuthMessage(String login, String password) throws IOException {
        out.writeUTF(String.format("/auth %s %s", login, password));
    }

    public void sendMessage(String message) throws IOException {
        out.writeUTF(message);
    }

    public void setMessageHandler(Consumer<String> messageHandler) {
        this.messageHandler = messageHandler;
    }

    public void setRefreshListHandler(Consumer<HashMap> refreshListHandler) {
        this.refreshListHandler = refreshListHandler;
    }

    public void setSuccessfulAuthEvent(AuthEvent successfulAuthEvent) {
        this.successfulAuthEvent = successfulAuthEvent;
    }

    public void close() {
        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
