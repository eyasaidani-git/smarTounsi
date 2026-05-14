package services;

import enums.PlanningType;
import models.Planning;
import util.DBConnection;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class PlanningService implements IService<Planning> {
    private final Connection conn;

    public PlanningService() {
        this.conn = DBConnection.getInstance().getConn();
    }

    @Override
    public void add(Planning p) {
        String sql = "INSERT INTO planning (id_utilisateur, titre, date_revision, type_activite) VALUES (?, ?, ?, ?)";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, p.getIdUtilisateur());
            ps.setString(2, p.getTitre());
            ps.setTimestamp(3, Timestamp.valueOf(p.getDateRevision()));
            ps.setString(4, p.getTypeActivite() == null ? PlanningType.REVISION.name() : p.getTypeActivite().name());
            ps.executeUpdate();
            System.out.println("Planning ajoute avec succes.");
        } catch (SQLException e) {
            System.out.println("Erreur add planning : " + e.getMessage());
        }
    }

    @Override
    public void update(Planning p) {
        String sql = "UPDATE planning SET titre=?, date_revision=?, type_activite=? WHERE id_planning=?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, p.getTitre());
            ps.setTimestamp(2, Timestamp.valueOf(p.getDateRevision()));
            ps.setString(3, p.getTypeActivite().name());
            ps.setInt(4, p.getId());
            ps.executeUpdate();
            System.out.println("Planning modifie avec succes.");
        } catch (SQLException e) {
            System.out.println("Erreur update planning : " + e.getMessage());
        }
    }

    @Override
    public void delete(Planning p) {
        String sql = "DELETE FROM planning WHERE id_planning=?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, p.getId());
            ps.executeUpdate();
            System.out.println("Planning supprime avec succes.");
        } catch (SQLException e) {
            System.out.println("Erreur delete planning : " + e.getMessage());
        }
    }

    @Override
    public List<Planning> getAll() {
        List<Planning> list = new ArrayList<>();
        String sql = "SELECT * FROM planning ORDER BY date_revision ASC";

        try (Statement st = conn.createStatement(); ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                list.add(mapRow(rs));
            }
        } catch (SQLException e) {
            System.out.println("Erreur getAll planning : " + e.getMessage());
        }

        return list;
    }

    public Planning getById(int idPlanning) {
        String sql = "SELECT * FROM planning WHERE id_planning=?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idPlanning);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return mapRow(rs);
            }
        } catch (SQLException e) {
            System.out.println("Erreur getById planning : " + e.getMessage());
        }

        return null;
    }

    public List<Planning> getByUtilisateur(int idUtilisateur) {
        List<Planning> list = new ArrayList<>();
        String sql = "SELECT * FROM planning WHERE id_utilisateur=? ORDER BY date_revision ASC";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idUtilisateur);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(mapRow(rs));
            }
        } catch (SQLException e) {
            System.out.println("Erreur getByUtilisateur planning : " + e.getMessage());
        }

        return list;
    }

    public List<Planning> getByDate(int idUtilisateur, LocalDate date) {
        List<Planning> list = new ArrayList<>();
        String sql = "SELECT * FROM planning WHERE id_utilisateur=? AND DATE(date_revision)=? ORDER BY date_revision ASC";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idUtilisateur);
            ps.setDate(2, Date.valueOf(date));
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(mapRow(rs));
            }
        } catch (SQLException e) {
            System.out.println("Erreur getByDate planning : " + e.getMessage());
        }

        return list;
<<<<<<< HEAD
    }

    private Planning mapRow(ResultSet rs) throws SQLException {
        Planning p = new Planning();
        p.setId(rs.getInt("id_planning"));
        p.setIdUtilisateur(rs.getInt("id_utilisateur"));
        p.setTitre(rs.getString("titre"));
        Timestamp dateRevision = rs.getTimestamp("date_revision");
        p.setDateRevision(dateRevision == null ? null : dateRevision.toLocalDateTime());
        p.setTypeActivite(PlanningType.valueOf(rs.getString("type_activite")));
        Timestamp dateCreation = rs.getTimestamp("date_creation");
        p.setDateCreation(dateCreation == null ? null : dateCreation.toLocalDateTime());
        return p;
=======
>>>>>>> origin/GestionNour
    }

    private Planning mapRow(ResultSet rs) throws SQLException {
        Planning p = new Planning();
        p.setId(rs.getInt("id_planning"));
        p.setIdUtilisateur(rs.getInt("id_utilisateur"));
        p.setTitre(rs.getString("titre"));
        Timestamp dateRevision = rs.getTimestamp("date_revision");
        p.setDateRevision(dateRevision == null ? null : dateRevision.toLocalDateTime());
        p.setTypeActivite(PlanningType.valueOf(rs.getString("type_activite")));
        Timestamp dateCreation = rs.getTimestamp("date_creation");
        p.setDateCreation(dateCreation == null ? null : dateCreation.toLocalDateTime());
        return p;
    }
}