package ru.geekbrains.java2.client.controller;

import ru.geekbrains.java2.client.view.AuthDialog;
import ru.geekbrains.java2.client.view.ClientChat;
import ru.geekbrains.java2.client.model.NetworkService;

import javax.swing.*;
import java.io.IOException;

public class ClientController {

    private final NetworkService networkService;
    private final AuthDialog authDialog;
    private final ClientChat clientChat;
    private HistoryLogger chatLogger;
    private String nickname;

    public ClientController(String serverHost, int serverPort) {
        this.networkService = new NetworkService(serverHost, serverPort);
        this.authDialog = new AuthDialog(this);
        this.clientChat = new ClientChat(this);

    }

    public void runApplication() throws IOException {
        connectToServer();
        runAuthProcess();
    }

    private void runAuthProcess() {
        networkService.setSuccessfulAuthEvent(nickname -> {
            setUserName(nickname);
            this.chatLogger = new HistoryLogger(nickname);
            openChat();
        });
        authDialog.setVisible(true);

    }

    private void openChat() {
        authDialog.dispose();
        networkService.setMessageHandler(this::incomingMessage);
        networkService.setGroupMessageHandler(this::incomingGroupMessage);
        networkService.setRefreshListHandler(clientChat::refreshContactList);
        networkService.setRefreshNicknameHandler(this::setUserName);
        networkService.setSwitchNicknameHandler(this::switchNickname);

        clientChat.setVisible(true);
    }

    private void setUserName(String nickname) {
        this.nickname = nickname;
        clientChat.setTitle(nickname);
    }

    private void incomingMessage(String message){
        chatLogger.appendMessage(message);
        clientChat.showMessages();
    }
    private void incomingGroupMessage(String group,String message){
        chatLogger.appendMessage(group,message);
        clientChat.showMessages();
    }

    private void switchNickname(String oldNickname,String newNickname){
        chatLogger.changeName(oldNickname,newNickname);

    }

    private void connectToServer() throws IOException {
        try {
            networkService.connect();
        } catch (IOException e) {
            System.err.println("Failed to establish server connection");
            throw e;
        }
    }

    public void sendAuthMessage(String login, String pass) throws IOException {
        networkService.sendAuthMessage(login, pass);
    }

    public void sendNewNickMessage(String newNickName) throws IOException {
        networkService.sendChangeNicknameMessage(newNickName);
    }

    public void sendMessage(String reciever, String message) {
        chatLogger.appendMessage(reciever,nickname + ": " + message);
        try {
            networkService.sendMessage("/w " + reciever + " "  + message);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Failed to send message!");
            e.printStackTrace();
        }
    }

    public HistoryLogger getChatLogger() {
        return chatLogger;
    }

    public void shutdown() {
        chatLogger.saveChatLog();
        networkService.close();
    }

    public String getUsername() {
        return nickname;
    }
}
