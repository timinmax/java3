package ru.geekbrains.java2.client.view;

import ru.geekbrains.java2.client.controller.ClientController;

import javax.swing.*;
import java.awt.event.*;
import java.io.IOException;
import java.util.HashMap;

public class ClientChat extends JFrame {

    private JPanel mainPanel;
    private JList<String> usersList;
    private JTextField messageTextField;
    private JButton sendButton;
    private JTextArea chatText;
    private JButton changeLoginButton;
    private JButton addFilterButton;
    private JButton removeFilterButton;
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
        changeLoginButton.addActionListener(e -> ClientChat.this.changeLogin());
        addFilterButton.addActionListener(e -> ClientChat.this.addFilter());
        removeFilterButton.addActionListener(e -> ClientChat.this.removeFilter());
        messageTextField.addActionListener(e -> sendMessage());
        usersList.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    showMessages();
                }
            }
        });
        usersList.addMouseListener(
                   new MouseAdapter() {
                       @Override
                       public void mouseClicked(MouseEvent e) {
                           showMessages();
                       }
                   });
    }

    public void showMessages(){
        SwingUtilities.invokeLater(() -> {
            chatText.setText(controller.getChatLogger().getLog(getSelectedUser()));
        });


    }

    private String getSelectedUser(){
        String selected = usersList.getSelectedValue();
        if(selected==null){
            usersList.setSelectedIndex(0);
            selected = "ALL";
        }
        selected = selected.replaceAll("\\<.+?\\>","");

        return selected;
    }

    private void sendMessage() {
        String message = messageTextField.getText().trim();
        if (message.isEmpty()) {
            return;
        }
        String selected = getSelectedUser();
        controller.sendMessage(selected,message);
        messageTextField.setText(null);
        showMessages();
    }

    private void addFilter(){
        while (true){
            String wordToAdd = JOptionPane.showInputDialog(mainPanel,"Add word to filter");
            if (wordToAdd == null){
                break;
            }

            if (!"".equals(wordToAdd.trim())){
                try {
                    controller.sendAddWordToFilter(wordToAdd.trim());
                } catch (IOException e) {
                    JOptionPane.showMessageDialog(mainPanel, "Ошибка при попытке добавить слово в матофильтр");
                }finally {
                    break;
                }
            }
        }
    }

    private void removeFilter(){
        while (true){
            String wordToRemove = JOptionPane.showInputDialog(mainPanel,"Remove word from filter");
            if (wordToRemove == null){
                break;
            }

            if (!"".equals(wordToRemove.trim())){
                try {
                    controller.removeWordFromFilter(wordToRemove.trim());
                } catch (IOException e) {
                    JOptionPane.showMessageDialog(mainPanel, "Ошибка при попытке удалить слово из матофильтра");
                }finally {
                    break;
                }
            }
        }
    }

    private void changeLogin(){
         while (true){
            String newNickName = JOptionPane.showInputDialog(mainPanel,"new nickname");
            if (newNickName == null){
                break;
            }
            newNickName = newNickName.replaceAll("\\s+","");
            if (!"".equals(newNickName.trim())){
                try {
                    controller.sendNewNickMessage(newNickName.trim());
                } catch (IOException e) {
                    JOptionPane.showMessageDialog(mainPanel, "Ошибка при попытке сменить псевдоним");
                }finally {
                    break;
                }
            }
        }
    }

    public void refreshContactList(HashMap<String,Boolean> userStatusList){
        SwingUtilities.invokeLater(() -> {
            userListModel.clear();
            userStatusList.forEach((k,v)->{
                userListModel.add(userListModel.getSize(),String.format("<html>%s%s%s</html>",((v)?"<b color=green>":""),k,((v)?"</b>":"")));
            });
        });
    }




}
