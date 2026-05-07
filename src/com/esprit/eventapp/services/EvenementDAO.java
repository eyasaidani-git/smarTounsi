package com.esprit.eventapp.services;

import com.esprit.eventapp.models.Evenement;
import com.esprit.eventapp.utils.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EvenementDAO {

    public void ajouterEvenement(Evenement e) {
        String sql = "INSERT INTO evenement (titre, description, type_evenement, date_evenement, heure_evenement, lieu, statut) VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, e.getTitre());
            ps.setString(2, e.getDescription());
            ps.setString(3, e.getTypeEvenement());
            ps.setDate(4, Date.valueOf(e.getDateEvenement()));
            ps.setTime(5, Time.valueOf(e.getHeureEvenement()));
            ps.setString(6, e.getLieu());
            ps.setString(7, e.getStatut());

            ps.executeUpdate();
            System.out.println("Événement ajouté avec succès.");

        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    public List<Evenement> afficherEvenements() {
        List<Evenement> list = new ArrayList<>();
        String sql = "SELECT * FROM evenement";

        try (Connection conn = DBConnection.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                Evenement e = new Evenement(
                        rs.getInt("id_evenement"),
                        rs.getString("titre"),
                        rs.getString("description"),
                        rs.getString("type_evenement"),
                        rs.getDate("date_evenement").toLocalDate(),
                        rs.getTime("heure_evenement").toLocalTime(),
                        rs.getString("lieu"),
                        rs.getString("statut")
                );
                list.add(e);
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return list;
    }
}
