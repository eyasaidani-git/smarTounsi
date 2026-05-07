package services;
import models.Planning;
import util.DBConnection;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class PlanningService implements IService<Planning> {
    private Connection conn;
    public PlanningService() {
        this.conn = DBConnection.getInstance().getConn();
    }

    @Override
    public void add(Planning p) {
        String req = "INSERT INTO planning (date_revision, titre, id_utilisateur) VALUES (?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(req)) {
            ps.setDate(1, Date.valueOf(p.getDateRevision()));
            ps.setString(2, p.getTitre());
            ps.setInt(3, p.getIdUtilisateur());
            ps.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Erreur add planning : " + e.getMessage());
        }
    }

    @Override
    public void update(Planning p) {
        String req = "UPDATE planning SET titre=? WHERE id=?";
        try (PreparedStatement ps = conn.prepareStatement(req)) {
            ps.setString(1, p.getTitre());
            ps.setInt(2, p.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Erreur update planning : " + e.getMessage());
        }
    }

    @Override
    public void delete(Planning p) {
        String req = "DELETE FROM planning WHERE id=?";
        try (PreparedStatement ps = conn.prepareStatement(req)) {
            ps.setInt(1, p.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Erreur delete planning : " + e.getMessage());
        }
    }

    @Override
    public List<Planning> getAll() {
        List<Planning> list = new ArrayList<>();
        String req = "SELECT * FROM planning";
        try (Statement st = conn.createStatement(); ResultSet rs = st.executeQuery(req)) {
            while (rs.next()) {
                Planning p = new Planning();
                p.setId(rs.getInt("id"));
                p.setDateRevision(rs.getDate("date_revision").toLocalDate());
                p.setTitre(rs.getString("titre"));
                p.setIdUtilisateur(rs.getInt("id_utilisateur"));
                list.add(p);
            }
        } catch (SQLException e) {
            System.out.println("Erreur getAll planning : " + e.getMessage());
        }
        return list;
    }

    public Planning getByDate(int idUtilisateur, LocalDate date) {
        String req = "SELECT * FROM planning WHERE id_utilisateur=? AND date_revision=?";
        try (PreparedStatement ps = conn.prepareStatement(req)) {
            ps.setInt(1, idUtilisateur);
            ps.setDate(2, Date.valueOf(date));
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                Planning p = new Planning();
                p.setId(rs.getInt("id"));
                p.setDateRevision(rs.getDate("date_revision").toLocalDate());
                p.setTitre(rs.getString("titre"));
                p.setIdUtilisateur(idUtilisateur);
                return p;
            }
        } catch (SQLException e) {
            System.out.println("Erreur getByDate : " + e.getMessage());
        }
        return null;
    }
}
