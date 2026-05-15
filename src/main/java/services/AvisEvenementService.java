package services;

import models.AvisEvenement;
import util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class AvisEvenementService {

    private Connection getConn() {
        return DBConnection.getInstance().getConn();
    }

    public void saveOrUpdate(AvisEvenement avis) {
        if (avis == null) {
            throw new RuntimeException("Avis invalide.");
        }

        int note = Math.max(1, Math.min(5, avis.getNote()));
        String sql = """
                INSERT INTO avis_evenement (id_evenement, id_utilisateur, note, commentaire)
                VALUES (?, ?, ?, ?)
                ON DUPLICATE KEY UPDATE note=VALUES(note), commentaire=VALUES(commentaire), date_avis=CURRENT_TIMESTAMP
                """;

        try (PreparedStatement ps = getConn().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, avis.getIdEvenement());
            ps.setInt(2, avis.getIdUtilisateur());
            ps.setInt(3, note);
            ps.setString(4, avis.getCommentaire());
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Erreur avis evenement : " + e.getMessage());
        }
    }

    public AvisEvenement getByUtilisateurAndEvenement(int idUtilisateur, int idEvenement) {
        String sql = "SELECT * FROM avis_evenement WHERE id_utilisateur=? AND id_evenement=? LIMIT 1";

        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setInt(1, idUtilisateur);
            ps.setInt(2, idEvenement);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Erreur lecture avis : " + e.getMessage());
        }

        return null;
    }

    public List<AvisEvenement> getByEvenement(int idEvenement) {
        List<AvisEvenement> avis = new ArrayList<>();
        String sql = "SELECT * FROM avis_evenement WHERE id_evenement=? ORDER BY date_avis DESC";

        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setInt(1, idEvenement);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    avis.add(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Erreur liste avis : " + e.getMessage());
        }

        return avis;
    }

    public double moyenneNote(int idEvenement) {
        String sql = "SELECT AVG(note) FROM avis_evenement WHERE id_evenement=?";

        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setInt(1, idEvenement);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getDouble(1) : 0.0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Erreur moyenne avis : " + e.getMessage());
        }
    }

    public int countAvis(int idEvenement) {
        String sql = "SELECT COUNT(*) FROM avis_evenement WHERE id_evenement=?";

        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setInt(1, idEvenement);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getInt(1) : 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Erreur count avis : " + e.getMessage());
        }
    }

    private AvisEvenement mapRow(ResultSet rs) throws SQLException {
        AvisEvenement avis = new AvisEvenement();
        avis.setId(rs.getInt("id_avis"));
        avis.setIdEvenement(rs.getInt("id_evenement"));
        avis.setIdUtilisateur(rs.getInt("id_utilisateur"));
        avis.setNote(rs.getInt("note"));
        avis.setCommentaire(rs.getString("commentaire"));

        Timestamp dateAvis = rs.getTimestamp("date_avis");
        if (dateAvis != null) {
            avis.setDateAvis(dateAvis.toLocalDateTime());
        }

        return avis;
    }
}
