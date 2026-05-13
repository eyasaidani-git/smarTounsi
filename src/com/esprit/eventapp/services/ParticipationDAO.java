package com.esprit.eventapp.services;

import com.esprit.eventapp.models.Participation;
import com.esprit.eventapp.utils.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ParticipationDAO {

    public boolean ajouterParticipation(Participation p) {
        String sql = "INSERT INTO participation (user_id, id_evenement, statut) VALUES (?, ?, ?)";
        
        // 1. Vérifier capacité
        String checkCapSql = "SELECT capacity FROM evenement WHERE id_evenement = ?";
        String countConfirmedSql = "SELECT COUNT(*) FROM participation WHERE id_evenement = ? AND statut IN ('confirmée', 'présent')";
        
        try (Connection conn = DBConnection.getConnection()) {
            // Check current confirmed count
            int confirmed = 0;
            try (PreparedStatement ps = conn.prepareStatement(countConfirmedSql)) {
                ps.setInt(1, p.getIdEvenement());
                ResultSet rs = ps.executeQuery();
                if (rs.next()) confirmed = rs.getInt(1);
            }
            
            // Check capacity
            int capacity = 0;
            try (PreparedStatement ps = conn.prepareStatement(checkCapSql)) {
                ps.setInt(1, p.getIdEvenement());
                ResultSet rs = ps.executeQuery();
                if (rs.next()) capacity = rs.getInt(1);
            }
            
            // Decision
            String finalStatut = (confirmed < capacity) ? "confirmée" : "waitlist";
            p.setStatut(finalStatut);
            
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, p.getIdUser());
                ps.setInt(2, p.getIdEvenement());
                ps.setString(3, p.getStatut());
                int rows = ps.executeUpdate();
                return rows > 0;
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            return false;
        }
    }

    public List<Participation> afficherParticipationsParFiltre(int currentUserId, String role) {
        List<Participation> list = new ArrayList<>();
        String sql;
        
        if ("ADMIN".equals(role)) {
            // Admin voit tout
            sql = "SELECT p.*, u.nom, u.prenom, e.titre " +
                  "FROM participation p " +
                  "LEFT JOIN user u ON p.user_id = u.id " +
                  "LEFT JOIN evenement e ON p.id_evenement = e.id_evenement " +
                  "ORDER BY p.date_participation DESC";
        } else {
            // Créateur voit les participations à SES événements OU un utilisateur voit SES propres participations
            sql = "SELECT p.*, u.nom, u.prenom, e.titre " +
                  "FROM participation p " +
                  "LEFT JOIN user u ON p.user_id = u.id " +
                  "LEFT JOIN evenement e ON p.id_evenement = e.id_evenement " +
                  "WHERE e.user_id = ? OR p.user_id = ? " +
                  "ORDER BY p.date_participation DESC";
        }

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            if (!"ADMIN".equals(role)) {
                ps.setInt(1, currentUserId);
                ps.setInt(2, currentUserId);
            }
            
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Participation p = new Participation(
                        rs.getInt("id_participation"),
                        rs.getInt("user_id"),
                        rs.getInt("id_evenement"),
                        rs.getTimestamp("date_participation"),
                        rs.getString("statut")
                );
                p.setCheckinTime(rs.getTimestamp("checkin_time"));
                p.setUserName((rs.getString("prenom") != null ? rs.getString("prenom") : "") + " " + (rs.getString("nom") != null ? rs.getString("nom") : ""));
                p.setEventTitle(rs.getString("titre") != null ? rs.getString("titre") : "Événement inconnu");
                list.add(p);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return list;
    }

    public void checkIn(int idParticipation) {
        String sql = "UPDATE participation SET statut = 'présent', checkin_time = NOW() WHERE id_participation = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idParticipation);
            ps.executeUpdate();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    public void annulerParticipation(int idParticipation, int idEvenement) {
        String sql = "UPDATE participation SET statut = 'annulée' WHERE id_participation = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idParticipation);
            ps.executeUpdate();
            
            // Auto promotion from waitlist
            promoteFromWaitlist(idEvenement);
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    public void promoteFromWaitlist(int idEvenement) {
        String findNextSql = "SELECT id_participation FROM participation WHERE id_evenement = ? AND statut = 'waitlist' ORDER BY date_participation ASC LIMIT 1";
        String updateSql = "UPDATE participation SET statut = 'confirmée' WHERE id_participation = ?";
        
        try (Connection conn = DBConnection.getConnection()) {
            int nextId = -1;
            try (PreparedStatement ps = conn.prepareStatement(findNextSql)) {
                ps.setInt(1, idEvenement);
                ResultSet rs = ps.executeQuery();
                if (rs.next()) nextId = rs.getInt(1);
            }
            
            if (nextId != -1) {
                try (PreparedStatement ps = conn.prepareStatement(updateSql)) {
                    ps.setInt(1, nextId);
                    ps.executeUpdate();
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    public java.util.Map<String, Integer> getStats() {
        java.util.Map<String, Integer> stats = new java.util.HashMap<>();
        String sql = "SELECT statut, COUNT(*) as count FROM participation GROUP BY statut";
        try (Connection conn = DBConnection.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                stats.put(rs.getString("statut"), rs.getInt("count"));
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return stats;
    }

    public int getWaitlistPosition(int idEvenement, int idUser) {
        String sql = "SELECT COUNT(*) + 1 FROM participation " +
                     "WHERE id_evenement = ? AND statut = 'waitlist' " +
                     "AND date_participation < (SELECT date_participation FROM participation WHERE id_evenement = ? AND user_id = ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idEvenement);
            ps.setInt(2, idEvenement);
            ps.setInt(3, idUser);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return -1;
    }

    public String getUserParticipationStatus(int idEvenement, int idUser) {
        String sql = "SELECT statut FROM participation WHERE id_evenement = ? AND user_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idEvenement);
            ps.setInt(2, idUser);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getString("statut");
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return null;
    }
}
