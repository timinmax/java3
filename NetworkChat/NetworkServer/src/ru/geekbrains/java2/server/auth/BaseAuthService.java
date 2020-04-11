package ru.geekbrains.java2.server.auth;

import ru.geekbrains.java2.server.NetworkServer;

import java.io.FileInputStream;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;


public class BaseAuthService implements AuthService {
    public BaseAuthService() {
        /*
        USER_DATA.add(new UserData("login1", "pass1", "username1"));
        USER_DATA.add(new UserData("login2", "pass2", "username2"));
        USER_DATA.add(new UserData("login3", "pass3", "username3"));
        */
    }
    private static final List<UserData> USER_DATA = new ArrayList<UserData>();

    private static Logger logger;
    static {
        try(FileInputStream ins = new FileInputStream("log.config")){
            LogManager.getLogManager().readConfiguration(ins);
            logger  = Logger.getLogger(NetworkServer.class.getName());
        }catch (Exception ignore){
            ignore.printStackTrace();
        }
    }

    private static class UserData {
        private String login;
        private String password;
        private String username;

        public UserData(String login, String password, String username) {
            this.login = login;
            this.password = password;
            this.username = username;
        }
    }



    @Override
    public String getUsernameByLoginAndPassword(String login, String password) {
        try {
            Connection conn = NetworkServer.getDbConnection();
            PreparedStatement stmt = conn.prepareStatement("SELECT * FROM users where login=? and pass=?");
            stmt.setString(1,login);
            stmt.setString(2,password);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()){
                return rs.getString("nickname");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        /*
        for (UserData userDatum : USER_DATA) {
            if (userDatum.login.equals(login) && userDatum.password.equals(password)) {
                return userDatum.username;
            }
        }*/
        return null;
    }

    @Override
    public void start() {
        logger.log(Level.INFO,"Сервис аутентификации запущен");
        //System.out.println("Сервис аутентификации запущен");
    }

    @Override
    public void stop() {
        logger.log(Level.INFO,"Сервис аутентификации оставлен");
        //System.out.println("Сервис аутентификации оставлен");
    }
}
