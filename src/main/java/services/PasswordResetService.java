package services;

import models.Utilisateur;
import util.DBConnection;
import util.PasswordUtil;

import java.security.SecureRandom;
import java.sql.*;
import java.time.LocalDateTime;

public class PasswordResetService {

    private final Connection conn;
    private final UtilisateurService utilisateurService;
    private final EmailService emailService;

    public PasswordResetService() {
        this.conn = DBConnection.getInstance().getConn();
        this.utilisateurService = new UtilisateurService();
        this.emailService = new EmailService();
    }

    public void envoyerCode(String email) {
        Utilisateur u = utilisateurService.getByEmail(email);

        if (u == null) {
            throw new RuntimeException("Aucun compte trouvé avec cet email.");
        }

        String code = generateCode();
        String codeHash = PasswordUtil.hashPassword(code);

        String sql = "INSERT INTO password_reset_code (id_utilisateur, code_hash, expire_at, used) VALUES (?, ?, ?, 0)";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, u.getId());
            ps.setString(2, codeHash);
            ps.setTimestamp(3, Timestamp.valueOf(LocalDateTime.now().plusMinutes(10)));

            ps.executeUpdate();

            emailService.sendResetCode(u.getEmail(), code);

        } catch (SQLException e) {
            throw new RuntimeException("Erreur création code : " + e.getMessage());
        }
    }

    public int verifierCodeEtRetournerIdUtilisateur(String email, String code) {
        Utilisateur u = utilisateurService.getByEmail(email);

        if (u == null) {
            throw new RuntimeException("Utilisateur introuvable.");
        }

        String sql = "SELECT * FROM password_reset_code " +
                "WHERE id_utilisateur=? AND used=0 AND expire_at > NOW() " +
                "ORDER BY created_at DESC LIMIT 1";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, u.getId());

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    String codeHash = rs.getString("code_hash");

                    if (PasswordUtil.checkPassword(code, codeHash)) {
                        return u.getId();
                    }
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erreur vérification code : " + e.getMessage());
        }

        throw new RuntimeException("Code incorrect ou expiré.");
    }

    public void resetPassword(String email, String code, String nouveauMotDePasse) {
        if (!PasswordUtil.isStrongPassword(nouveauMotDePasse)) {
            throw new RuntimeException(PasswordUtil.getPasswordRulesMessage());
        }

        int idUtilisateur = verifierCodeEtRetournerIdUtilisateur(email, code);

        utilisateurService.updateMotDePasse(idUtilisateur, nouveauMotDePasse);

        String sql = "UPDATE password_reset_code SET used=1 WHERE id_utilisateur=?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idUtilisateur);
            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Erreur désactivation code : " + e.getMessage());
        }
    }

    private String generateCode() {
        SecureRandom random = new SecureRandom();
        int value = 100000 + random.nextInt(900000);
        return String.valueOf(value);
    }
}