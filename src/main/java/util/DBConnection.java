package util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {
    private String url = "jdbc:mysql://localhost:3306/smartounsi";
    private String user = "root";
    private String password = "";
    private Connection conn;
    private static DBConnection instance;

    private DBConnection() {
        connect();
    }

    private void connect() {
        try {
            this.conn = DriverManager.getConnection(url, user, password);
            System.out.println("✅ Connexion établie");
        } catch (SQLException e) {
            System.out.println("❌ Connexion échouée : " + e.getMessage());
        }
    }

    public static DBConnection getInstance() {
        if (instance == null) {
            instance = new DBConnection();
        }
        return instance;
    }

    public Connection getConnection() {
        try {
            // Reconnexion automatique si la connexion est morte
            if (conn == null || conn.isClosed()) {
                System.out.println("⚠️ Reconnexion...");
                connect();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return conn;
    }

    // ✅ CORRIGÉ — plus de return null
    public Connection getConn() {
        return getConnection();
    }

}