package services;
import models.Evenement;
import util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EvenementService implements IService<Evenement> {
    private Connection conn;

    public EvenementService() {
        this.conn = DBConnection.getInstance().getConn();
    }

    @Override
    public void add(Evenement e) {
        String req = "INSERT INTO evenement (nom, description, type, tarif, " +
                "emplacement, date_evenement, heure_evenement, id_organisateur) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(req)) {
            ps.setString(1, e.getNom());
            ps.setString(2, e.getDescription());
            ps.setString(3, e.getType());
            ps.setFloat(4, e.getTarif());
            ps.setString(5, e.getEmplacement());
            ps.setDate(6, Date.valueOf(e.getDateEvenement()));
            ps.setTime(7, Time.valueOf(e.getHeureEvenement()));
            ps.setInt(8, e.getIdOrganisateur());
            ps.executeUpdate();
            System.out.println("Événement ajouté ✔");
        } catch (SQLException ex) {
            System.out.println("Erreur add événement : " + ex.getMessage());
        }
    }

    @Override
    public void update(Evenement e) {
        String req = "UPDATE evenement SET nom=?, description=?, tarif=?, " +
                "emplacement=?, date_evenement=?, heure_evenement=? WHERE id=?";
        try (PreparedStatement ps = conn.prepareStatement(req)) {
            ps.setString(1, e.getNom());
            ps.setString(2, e.getDescription());
            ps.setFloat(3, e.getTarif());
            ps.setString(4, e.getEmplacement());
            ps.setDate(5, Date.valueOf(e.getDateEvenement()));
            ps.setTime(6, Time.valueOf(e.getHeureEvenement()));
            ps.setInt(7, e.getId());
            ps.executeUpdate();
        } catch (SQLException ex) {
            System.out.println("Erreur update événement : " + ex.getMessage());
        }
    }

    @Override
    public void delete(Evenement e) {
        try (PreparedStatement ps = conn.prepareStatement("DELETE FROM evenement WHERE id=?")) {
            ps.setInt(1, e.getId());
            ps.executeUpdate();
        } catch (SQLException ex) {
            System.out.println("Erreur delete événement : " + ex.getMessage());
        }
    }

    @Override
    public List<Evenement> getAll() {
        List<Evenement> list = new ArrayList<>();
        String req = "SELECT * FROM evenement ORDER BY date_evenement ASC";
        try (Statement st = conn.createStatement(); ResultSet rs = st.executeQuery(req)) {
            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) {
            System.out.println("Erreur getAll événements : " + e.getMessage());
        }
        return list;
    }
    public List<Evenement> getByType(String type) {
        List<Evenement> list = new ArrayList<>();
        String req = "SELECT * FROM evenement WHERE type=? ORDER BY date_evenement ASC";
        try (PreparedStatement ps = conn.prepareStatement(req)) {
            ps.setString(1, type);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) {
            System.out.println("Erreur getByType : " + e.getMessage());
        }
        return list;
    }

    private Evenement mapRow(ResultSet rs) throws SQLException {
        Evenement e = new Evenement();
        e.setId(rs.getInt("id"));
        e.setNom(rs.getString("nom"));
        e.setDescription(rs.getString("description"));
        e.setType(rs.getString("type"));
        e.setTarif(rs.getFloat("tarif"));
        e.setEmplacement(rs.getString("emplacement"));
        e.setDateEvenement(rs.getDate("date_evenement").toLocalDate());
        Time t = rs.getTime("heure_evenement");
        if (t != null) e.setHeureEvenement(t.toLocalTime());
        e.setIdOrganisateur(rs.getInt("id_organisateur"));
        return e;
    }
}
