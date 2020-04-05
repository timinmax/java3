package ru.geekbrains.java2.server.client;

import java.io.*;
import java.util.ArrayList;


public class ObscenitiesFilter {


    private ArrayList<String> obscenitiesList;
    private final String OBS_PATH;

    public ObscenitiesFilter(String nickName) {
        this.OBS_PATH = nickName + "_obs.dat";
        setObscenitiesArray();
    }

    public String applyFilter(String testString){

        if (obscenitiesList.isEmpty()){
            return testString.trim();
        }
        String string2Return = testString.trim();
        for (String s:obscenitiesList) {
            string2Return = string2Return.replaceAll(s,"****");
        }

        return string2Return;
    }

    public void addToFilter(String obscWord){
        if (obscWord == null || obscWord.trim()==""){
            return;
        }

        if (!this.obscenitiesList.contains(obscWord)){
            this.obscenitiesList.add(obscWord);
            saveObscenitiesArray();
        }
    }

    public void removeFromFilter(String obscWord){
        this.obscenitiesList.remove(obscWord);
        saveObscenitiesArray();
    }

    private void setObscenitiesArray() {
        try(FileInputStream fis = new FileInputStream(OBS_PATH);
            ObjectInputStream ois = new ObjectInputStream(fis))
        {
            this.obscenitiesList = (ArrayList<String>) ois.readObject();
            System.out.println("Log has been restored");
        }catch(IOException ioe)
        {
            System.out.println("Log restore error, got new one");
            this.obscenitiesList = new ArrayList<>();
        }catch(ClassNotFoundException c)
        {
            System.out.println("Log restore error, got new one");
            this.obscenitiesList = new ArrayList<>();
        }
    }

    public void saveObscenitiesArray(){
        try(FileOutputStream fos = new FileOutputStream(OBS_PATH);
            ObjectOutputStream oos = new ObjectOutputStream(fos))
        {
            oos.writeObject(obscenitiesList);
        }catch(IOException ioe)
        {
            ioe.printStackTrace();
        }
    }
}
