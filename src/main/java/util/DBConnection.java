package util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {

    private static DBConnection instance;

    private final String url = "jdbc:mysql://localhost:3306/smartounsi";
    private final String user = "root";
    private final String password = "";

    private Connection conn;

    private DBConnection() {
        connect();
    }

    public static DBConnection getInstance() {
        if (instance == null) {
            instance = new DBConnection();
        }
        return instance;
    }

    private void connect() {
        try {
            conn = DriverManager.getConnection(url, user, password);
            System.out.println("Connection established");
        } catch (SQLException e) {
            throw new RuntimeException("Erreur connexion MySQL : " + e.getMessage());
        }
    }

    public Connection getConn() {
        try {
            if (conn == null || conn.isClosed()) {
                System.out.println("Connection was closed. Reconnecting...");
                connect();
            }
        } catch (SQLException e) {
            connect();
        }

        return conn;
    }

    public Connection getConnection() {
        return getConn();
    }
}