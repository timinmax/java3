package ru.geekbrains.java2.client.controller;

import java.io.*;
import java.util.HashMap;

public class HistoryLogger {

    private final int LOG_LINES = 100;
    private final String LOG_PATH;
    private HashMap<String,String> chatLog;

    public HistoryLogger(String nickname) {
        this.LOG_PATH = nickname + ".dat";
        setChatLog();
    }

    public void appendMessage(String message){
        String[] messageParts = message.split(":");
        if (messageParts.length>1){
            appendMessage(messageParts[0],message);
        }
    }
    public void appendMessage(String nickcname, String message){
        this.chatLog.compute(nickcname,(k,v)->
                        ((v==null)?"":v)
                        + System.lineSeparator()
                        + message);
    }

    public String getLog(String nickName){
        return this.chatLog.get(nickName);
    }

    public void changeName(String oldNickname, String newNickname){
        this.chatLog.put(newNickname,this.chatLog.get(oldNickname));
        this.chatLog.remove(oldNickname);
    }

    private void setChatLog() {
        try(FileInputStream fis = new FileInputStream(LOG_PATH);
            ObjectInputStream ois = new ObjectInputStream(fis))
        {
            this.chatLog = (HashMap) ois.readObject();
            System.out.println("Log has been restored");
        }catch(IOException ioe)
        {
            System.out.println("Log restore error, got new one");
            this.chatLog = new HashMap<>();
        }catch(ClassNotFoundException c)
        {
            System.out.println("Log restore error, got new one");
            this.chatLog = new HashMap<>();
        }
    }

    public void saveChatLog(){

        chatLog.forEach((k,v)->chatLog.put(k,this.cutString(v)));

        try(FileOutputStream fos = new FileOutputStream(LOG_PATH);
                ObjectOutputStream oos = new ObjectOutputStream(fos))
        {
            oos.writeObject(chatLog);
            System.out.printf("Serialized HashMap data is saved in " + LOG_PATH);
        }catch(IOException ioe)
        {
            ioe.printStackTrace();
        }
    }

    private String cutString(String string2Cut){
        String[] chatLines = string2Cut.split(System.lineSeparator());
        if (chatLines.length <= LOG_LINES){return string2Cut;}

        String message2return = "";
        for (int i = chatLines.length - LOG_LINES;i<chatLines.length; i++){message2return += chatLines[i] + System.lineSeparator();};

        return message2return;
    }
}
