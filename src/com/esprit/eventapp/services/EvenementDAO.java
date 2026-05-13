package com.esprit.eventapp.services;

import com.esprit.eventapp.models.Evenement;
import com.esprit.eventapp.utils.DBConnection;
import com.esprit.eventapp.utils.SessionManager;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EvenementDAO {

    public boolean ajouterEvenement(Evenement e) {
        String sql = "INSERT INTO evenement (user_id, titre, description, type_evenement, start_date_time, end_date_time, lieu, statut, image, prix, capacity) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            int currentUserId = 0;
            if (SessionManager.getInstance().getCurrentUser() != null) {
                currentUserId = SessionManager.getInstance().getCurrentUser().getId();
            }

            ps.setInt(1, currentUserId);
            ps.setString(2, e.getTitre());
            ps.setString(3, e.getDescription());
            ps.setString(4, e.getTypeEvenement());
            ps.setTimestamp(5, Timestamp.valueOf(e.getDateDebut()));
            ps.setTimestamp(6, Timestamp.valueOf(e.getDateFin()));
            ps.setString(7, e.getLieu());
            ps.setString(8, e.getStatut());
            ps.setString(9, e.getImage());
            ps.setDouble(10, e.getPrix());
            ps.setInt(11, e.getCapacity());

            int affectedRows = ps.executeUpdate();
            return affectedRows > 0;

        } catch (SQLException ex) {
            ex.printStackTrace();
            return false;
        }
    }

    public List<Evenement> afficherEvenements() {
        List<Evenement> list = new ArrayList<>();
        String sql = "SELECT * FROM evenement ORDER BY start_date_time DESC";

        try (Connection conn = DBConnection.getConnection();
                Statement st = conn.createStatement();
                ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                int createurId = rs.getObject("user_id") != null ? rs.getInt("user_id") : 0;
                double prix = rs.getObject("prix") != null ? rs.getDouble("prix") : 0.0;
                int cap = rs.getObject("capacity") != null ? rs.getInt("capacity") : 0;

                Evenement e = new Evenement(
                        rs.getInt("id_evenement"),
                        createurId,
                        rs.getString("titre"),
                        rs.getString("description"),
                        rs.getString("type_evenement"),
                        rs.getTimestamp("start_date_time").toLocalDateTime(),
                        rs.getTimestamp("end_date_time").toLocalDateTime(),
                        rs.getString("lieu"),
                        rs.getString("statut"),
                        rs.getString("image"),
                        prix,
                        cap);
                list.add(e);
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return list;
    }

    public boolean modifierEvenement(Evenement e) {
        String sql = "UPDATE evenement SET titre=?, description=?, type_evenement=?, start_date_time=?, end_date_time=?, lieu=?, statut=?, image=?, prix=?, capacity=? WHERE id_evenement=?";

        try (Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, e.getTitre());
            ps.setString(2, e.getDescription());
            ps.setString(3, e.getTypeEvenement());
            ps.setTimestamp(4, Timestamp.valueOf(e.getDateDebut()));
            ps.setTimestamp(5, Timestamp.valueOf(e.getDateFin()));
            ps.setString(6, e.getLieu());
            ps.setString(7, e.getStatut());
            ps.setString(8, e.getImage());
            ps.setDouble(9, e.getPrix());
            ps.setInt(10, e.getCapacity());
            ps.setInt(11, e.getIdEvenement());

            int affectedRows = ps.executeUpdate();
            return affectedRows > 0;

        } catch (SQLException ex) {
            ex.printStackTrace();
            return false;
        }
    }

    public void supprimerEvenement(int idEvenement) {
        String sql = "DELETE FROM evenement WHERE id_evenement=?";
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idEvenement);
            ps.executeUpdate();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }
}
