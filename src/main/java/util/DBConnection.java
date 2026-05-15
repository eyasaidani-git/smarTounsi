package util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class DBConnection {

    private static DBConnection instance;

    private final String url = "jdbc:mysql://localhost:3306/smartounsi";
    private final String user = "root";
    private final String password = "";
    private static final String DEFAULT_ADMIN_EMAIL = "hajriikram190@gmail.com";
    private static final String DEFAULT_ADMIN_PASSWORD = "IkramAdmin123";

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
            applyRuntimeMigrations();
            ensureDefaultAdmin();
            System.out.println("Connection established");
        } catch (SQLException e) {
            throw new RuntimeException("Erreur connexion MySQL : " + e.getMessage());
        }
    }

    private void applyRuntimeMigrations() {
        widenColumnIfExists("utilisateur", "mot_de_passe", "VARCHAR(255) NOT NULL");
        widenColumnIfExists("password_reset_code", "code_hash", "VARCHAR(255) NOT NULL");
        widenColumnIfExists("email_verification_code", "code_hash", "VARCHAR(255) NOT NULL");
    }

    private void widenColumnIfExists(String tableName, String columnName, String definition) {
        try {
            if (!columnExists(tableName, columnName)) {
                return;
            }

            try (Statement st = conn.createStatement()) {
                st.execute("ALTER TABLE " + tableName + " MODIFY " + columnName + " " + definition);
            }
        } catch (SQLException e) {
            System.err.println("Migration ignoree pour " + tableName + "." + columnName + " : " + e.getMessage());
        }
    }

    private boolean tableExists(String tableName) throws SQLException {
        String sql = "SELECT COUNT(*) FROM information_schema.tables WHERE table_schema = DATABASE() AND table_name = ?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, tableName);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() && rs.getInt(1) > 0;
            }
        }
    }

    private boolean columnExists(String tableName, String columnName) throws SQLException {
        String sql = "SELECT COUNT(*) FROM information_schema.columns " +
                "WHERE table_schema = DATABASE() AND table_name = ? AND column_name = ?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, tableName);
            ps.setString(2, columnName);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() && rs.getInt(1) > 0;
            }
        }
    }

    private void ensureDefaultAdmin() {
        try {
            if (!tableExists("utilisateur")) {
                return;
            }

            Integer existingId = findUserIdByEmail(DEFAULT_ADMIN_EMAIL);

            if (existingId == null) {
                createDefaultAdmin();
            } else {
                promoteExistingUserToAdmin(existingId);
            }
        } catch (SQLException e) {
            System.err.println("Creation admin ignoree : " + e.getMessage());
        }
    }

    private Integer findUserIdByEmail(String email) throws SQLException {
        String sql = "SELECT id_utilisateur FROM utilisateur WHERE LOWER(email)=LOWER(?) LIMIT 1";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, email);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getInt("id_utilisateur") : null;
            }
        }
    }

    private void createDefaultAdmin() throws SQLException {
        String sql = "INSERT INTO utilisateur " +
                "(nom, prenom, email, mot_de_passe, role, photo_profil, est_actif, filiere, annee, universite, numero_etudiant) " +
                "VALUES (?, ?, ?, ?, 'admin', NULL, 1, NULL, NULL, NULL, NULL)";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, "Hajri");
            ps.setString(2, "Ikram");
            ps.setString(3, DEFAULT_ADMIN_EMAIL);
            ps.setString(4, PasswordUtil.hashPassword(getDefaultAdminPassword()));
            ps.executeUpdate();
        }
    }

    private void promoteExistingUserToAdmin(int idUtilisateur) throws SQLException {
        String currentPassword = getStoredPassword(idUtilisateur);

        if (currentPassword == null || currentPassword.length() < 55 || !PasswordUtil.isBCryptHash(currentPassword)) {
            String sql = "UPDATE utilisateur SET nom='Hajri', prenom='Ikram', mot_de_passe=?, role='admin', est_actif=1 WHERE id_utilisateur=?";

            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, PasswordUtil.hashPassword(getDefaultAdminPassword()));
                ps.setInt(2, idUtilisateur);
                ps.executeUpdate();
            }
        } else {
            String sql = "UPDATE utilisateur SET nom='Hajri', prenom='Ikram', role='admin', est_actif=1 WHERE id_utilisateur=?";

            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, idUtilisateur);
                ps.executeUpdate();
            }
        }
    }

    private String getStoredPassword(int idUtilisateur) throws SQLException {
        String sql = "SELECT mot_de_passe FROM utilisateur WHERE id_utilisateur=?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idUtilisateur);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getString("mot_de_passe") : null;
            }
        }
    }

    private String getDefaultAdminPassword() {
        String configured = AppConfig.get("SMARTOUNSI_DEFAULT_ADMIN_PASSWORD");
        if (configured != null && !configured.isBlank()) {
            return configured.trim();
        }

        return DEFAULT_ADMIN_PASSWORD;
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
