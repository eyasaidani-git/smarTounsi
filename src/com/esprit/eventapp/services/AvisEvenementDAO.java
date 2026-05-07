package com.esprit.eventapp.services;

import com.esprit.eventapp.models.AvisEvenement;
import com.esprit.eventapp.utils.DBConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AvisEvenementDAO {

    public void ajouterAvis(AvisEvenement avis) {
        String sql = "INSERT INTO avis_evenement (id_evenement, nom_auteur, commentaire, note) VALUES (?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, avis.getIdEvenement());
            ps.setString(2, avis.getNomAuteur());
            ps.setString(3, avis.getCommentaire());
            ps.setInt(4, avis.getNote());

            ps.executeUpdate();
            System.out.println("Avis ajouté avec succès.");

        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    public List<AvisEvenement> afficherAvis() {
        List<AvisEvenement> list = new ArrayList<>();
        String sql = "SELECT * FROM avis_evenement";

        try (Connection conn = DBConnection.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                AvisEvenement avis = new AvisEvenement(
                        rs.getInt("id_avis"),
                        rs.getInt("id_evenement"),
                        rs.getString("nom_auteur"),
                        rs.getString("commentaire"),
                        rs.getInt("note"),
                        rs.getTimestamp("date_avis")
                );
                list.add(avis);
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return list;
    }

    public List<AvisEvenement> afficherAvisParEvenement(int idEvenement) {
        List<AvisEvenement> list = new ArrayList<>();
        String sql = "SELECT * FROM avis_evenement WHERE id_evenement = ? ORDER BY date_avis DESC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, idEvenement);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    AvisEvenement avis = new AvisEvenement(
                            rs.getInt("id_avis"),
                            rs.getInt("id_evenement"),
                            rs.getString("nom_auteur"),
                            rs.getString("commentaire"),
                            rs.getInt("note"),
                            rs.getTimestamp("date_avis")
                    );
                    list.add(avis);
                }
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return list;
    }
}
