package ru.geekbrains.java2.server;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;

public class MySqlConEx {

    public static Connection getMySQLConnection() throws SQLException, ClassNotFoundException {
        String hostname = "localhost";
        String dbName = "gbchat";
        String user = "root";
        String password = "123";

        return getMySQLConnection(hostname, dbName, user, password);
    }

    private static Connection getMySQLConnection(
            String hostname,
            String dbName,
            String user,
            String password) throws SQLException, ClassNotFoundException {


        Class.forName("com.mysql.cj.jdbc.Driver");
        String connectionURL = "jdbc:mysql://" + hostname + ":3306/" + dbName+"?&serverTimezone=UTC";
        Connection connection = DriverManager.getConnection(connectionURL, user, password);
        return connection;
    }
}
