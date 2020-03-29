package ru.geekbrains.java2.client.view;

import ru.geekbrains.java2.client.controller.ClientController;

import javax.swing.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.HashMap;

public class ClientChat extends JFrame {

    private JPanel mainPanel;
    private JList<String> usersList;
    private JTextField messageTextField;
    private JButton sendButton;
    private JTextArea chatText;
    private DefaultListModel<String> userListModel = new DefaultListModel<>();

    private ClientController controller;

    public ClientChat(ClientController controller) {
        this.controller = controller;
        usersList.setModel(userListModel);
        setTitle(controller.getUsername());
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setSize(640, 480);
        setLocationRelativeTo(null);
        setContentPane(mainPanel);
        addListeners();
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                controller.shutdown();
            }
        });

    }

       private void addListeners() {
        sendButton.addActionListener(e -> ClientChat.this.sendMessage());
        messageTextField.addActionListener(e -> sendMessage());
    }

    private void sendMessage() {
        String message = messageTextField.getText().trim();
        if (message.isEmpty()) {
            return;
        }
        String selected = usersList.getSelectedValue();
        if(selected==null){
            usersList.setSelectedIndex(0);
            selected = "All";
        }
        selected = selected.replaceAll("\\<.+?\\>","");

        appendOwnMessage(message);
        controller.sendMessage((("All".equals(selected))?"":"/w " + selected + " " ) + message);
        messageTextField.setText(null);
    }

    public void appendMessage(String message) {
        SwingUtilities.invokeLater(() -> {
            chatText.append(message);
            chatText.append(System.lineSeparator());
        });
    }

    public void refreshContactList(HashMap<String,Boolean> userStatusList){
        SwingUtilities.invokeLater(() -> {
            userListModel.clear();
            userStatusList.forEach((k,v)->{
                userListModel.add(userListModel.getSize(),String.format("<html>%s%s%s</html>",((v)?"<b color=green>":""),k,((v)?"</b>":"")));
            });
        });
    }

    private void appendOwnMessage(String message) {
        appendMessage("Я: " + message);
    }


}
