package services;
<<<<<<< HEAD

import enums.PlanningType;
=======
>>>>>>> origin/gestionikram
import models.Planning;
import util.DBConnection;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class PlanningService implements IService<Planning> {
<<<<<<< HEAD

    private final Connection conn;

=======
    private Connection conn;
>>>>>>> origin/gestionikram
    public PlanningService() {
        this.conn = DBConnection.getInstance().getConn();
    }

    @Override
    public void add(Planning p) {
<<<<<<< HEAD
        String sql = "INSERT INTO planning (id_utilisateur, titre, date_revision, type_activite) VALUES (?, ?, ?, ?)";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, p.getIdUtilisateur());
            ps.setString(2, p.getTitre());

            if (p.getDateRevision() == null) {
                ps.setNull(3, Types.TIMESTAMP);
            } else {
                ps.setTimestamp(3, Timestamp.valueOf(p.getDateRevision()));
            }

            if (p.getTypeActivite() == null) {
                ps.setString(4, PlanningType.REVISION.name());
            } else {
                ps.setString(4, p.getTypeActivite().toString());
            }

            ps.executeUpdate();
            System.out.println("Planning ajoute avec succes.");

=======
        String req = "INSERT INTO planning (date_revision, titre, id_utilisateur) VALUES (?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(req)) {
            ps.setDate(1, Date.valueOf(p.getDateRevision()));
            ps.setString(2, p.getTitre());
            ps.setInt(3, p.getIdUtilisateur());
            ps.executeUpdate();
>>>>>>> origin/gestionikram
        } catch (SQLException e) {
            System.out.println("Erreur add planning : " + e.getMessage());
        }
    }

    @Override
    public void update(Planning p) {
<<<<<<< HEAD
        String sql = "UPDATE planning SET titre=?, date_revision=?, type_activite=? WHERE id_planning=?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, p.getTitre());

            if (p.getDateRevision() == null) {
                ps.setNull(2, Types.TIMESTAMP);
            } else {
                ps.setTimestamp(2, Timestamp.valueOf(p.getDateRevision()));
            }

            if (p.getTypeActivite() == null) {
                ps.setString(3, PlanningType.REVISION.name());
            } else {
                ps.setString(3, p.getTypeActivite().toString());
            }

            ps.setInt(4, p.getId());

            ps.executeUpdate();
            System.out.println("Planning modifie avec succes.");

=======
        String req = "UPDATE planning SET titre=? WHERE id=?";
        try (PreparedStatement ps = conn.prepareStatement(req)) {
            ps.setString(1, p.getTitre());
            ps.setInt(2, p.getId());
            ps.executeUpdate();
>>>>>>> origin/gestionikram
        } catch (SQLException e) {
            System.out.println("Erreur update planning : " + e.getMessage());
        }
    }

    @Override
    public void delete(Planning p) {
<<<<<<< HEAD
        String sql = "DELETE FROM planning WHERE id_planning=?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, p.getId());
            ps.executeUpdate();
            System.out.println("Planning supprime avec succes.");

=======
        String req = "DELETE FROM planning WHERE id=?";
        try (PreparedStatement ps = conn.prepareStatement(req)) {
            ps.setInt(1, p.getId());
            ps.executeUpdate();
>>>>>>> origin/gestionikram
        } catch (SQLException e) {
            System.out.println("Erreur delete planning : " + e.getMessage());
        }
    }

    @Override
    public List<Planning> getAll() {
        List<Planning> list = new ArrayList<>();
<<<<<<< HEAD
        String sql = "SELECT * FROM planning ORDER BY date_revision ASC";

        try (Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

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

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
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

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRow(rs));
                }
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

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRow(rs));
                }
            }

        } catch (SQLException e) {
            System.out.println("Erreur getByDate planning : " + e.getMessage());
        }

        return list;
    }

    private Planning mapRow(ResultSet rs) throws SQLException {
        Planning p = new Planning();

        p.setId(rs.getInt("id_planning"));
        p.setIdUtilisateur(rs.getInt("id_utilisateur"));
        p.setTitre(rs.getString("titre"));

        Timestamp dateRevision = rs.getTimestamp("date_revision");
        if (dateRevision != null) {
            p.setDateRevision(dateRevision.toLocalDateTime());
        }

        String typeActivite = rs.getString("type_activite");
        if (typeActivite != null && !typeActivite.isBlank()) {
            p.setTypeActivite(PlanningType.valueOf(typeActivite.toUpperCase()));
        } else {
            p.setTypeActivite(PlanningType.REVISION);
        }

        Timestamp dateCreation = rs.getTimestamp("date_creation");
        if (dateCreation != null) {
            p.setDateCreation(dateCreation.toLocalDateTime());
        }

        return p;
    }
}
=======
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
>>>>>>> origin/gestionikram
