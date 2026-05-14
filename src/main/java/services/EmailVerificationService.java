package services;

import util.DBConnection;
import util.PasswordUtil;

import java.security.SecureRandom;
import java.sql.*;
import java.time.LocalDateTime;

public class EmailVerificationService {

    private final Connection conn;
    private final EmailService emailService;

    public EmailVerificationService() {
        this.conn = DBConnection.getInstance().getConn();
        this.emailService = new EmailService();
    }

    public void envoyerCodeVerification(String email) {
        String code = generateCode();

        System.out.println("Code généré pour " + email + " = " + code);

        emailService.sendVerificationCode(email, code);

        String codeHash = PasswordUtil.hashPassword(code);

        String sql = "INSERT INTO email_verification_code (email, code_hash, expire_at, used) " +
                "VALUES (?, ?, ?, 0)";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, email.trim().toLowerCase());
            ps.setString(2, codeHash);
            ps.setTimestamp(3, Timestamp.valueOf(LocalDateTime.now().plusMinutes(10)));

            ps.executeUpdate();

            System.out.println("Code stocké dans la base pour : " + email);

        } catch (SQLException e) {
            throw new RuntimeException("Erreur création code email : " + e.getMessage());
        }
    }

    public boolean verifierCode(String email, String code) {
        String sql = "SELECT * FROM email_verification_code " +
                "WHERE LOWER(email)=LOWER(?) AND used=0 AND expire_at > NOW() " +
                "ORDER BY created_at DESC LIMIT 1";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, email.trim().toLowerCase());

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    String codeHash = rs.getString("code_hash");
                    int idCode = rs.getInt("id_code");

                    if (PasswordUtil.checkPassword(code.trim(), codeHash)) {
                        marquerCodeUtilise(idCode);
                        return true;
                    }
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erreur vérification code email : " + e.getMessage());
        }

        return false;
    }

    private void marquerCodeUtilise(int idCode) {
        String sql = "UPDATE email_verification_code SET used=1 WHERE id_code=?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idCode);
            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Erreur update code email : " + e.getMessage());
        }
    }

    private String generateCode() {
        SecureRandom random = new SecureRandom();
        int value = 100000 + random.nextInt(900000);
        return String.valueOf(value);
    }
}