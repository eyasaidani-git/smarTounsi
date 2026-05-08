package services;
import models.Notification;
import util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class NotificationService {
    private Connection conn;

    public NotificationService(Connection conn) {
        this.conn = DBConnection.getInstance().getConn();
    }
    public void envoyer(Notification n) {
        String req = "INSERT INTO notification (titre, message, type, id_utilisateur) VALUES (?, ?, ?, ?)";

        try (PreparedStatement ps = conn.prepareStatement(req)) {

            ps.setString(1, n.getTitre());
            ps.setString(2, n.getMessage());
            ps.setString(3, n.getType());
            ps.setInt(4, n.getIdUtilisateur());

            ps.executeUpdate();
            System.out.println("Notification envoyee.");

        } catch (SQLException e) {
            System.out.println("Erreur envoyer notification : " + e.getMessage());
        }
    }

    public void notifierAdmins(String titre, String message, String type) {
        String req = "SELECT id FROM utilisateur WHERE role='admin' AND actif=1";

        try (Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(req)) {

            while (rs.next()) {
                envoyer(new Notification(
                        titre,
                        message,
                        type,
                        rs.getInt("id")
                ));
            }

        } catch (SQLException e) {
            System.out.println("Erreur notifierAdmins : " + e.getMessage());
        }
    }

    public void notifierEtudiants(String titre, String message, String type) {
        String req = "SELECT id FROM utilisateur WHERE role='etudiant' AND actif=1";

        try (Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(req)) {

            while (rs.next()) {
                envoyer(new Notification(
                        titre,
                        message,
                        type,
                        rs.getInt("id")
                ));
            }

        } catch (SQLException e) {
            System.out.println("Erreur notifierEtudiants : " + e.getMessage());
        }
    }

    public void notifierEnseignants(String titre, String message, String type) {
        String req = "SELECT id FROM utilisateur WHERE role='enseignant' AND actif=1";

        try (Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(req)) {

            while (rs.next()) {
                envoyer(new Notification(
                        titre,
                        message,
                        type,
                        rs.getInt("id")
                ));
            }

        } catch (SQLException e) {
            System.out.println("Erreur notifierEnseignants : " + e.getMessage());
        }
    }

    public void notifierTousLesUtilisateurs(String titre, String message, String type) {
        String req = "SELECT id FROM utilisateur WHERE actif=1";

        try (Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(req)) {

            while (rs.next()) {
                envoyer(new Notification(
                        titre,
                        message,
                        type,
                        rs.getInt("id")
                ));
            }

        } catch (SQLException e) {
            System.out.println("Erreur notifierTousLesUtilisateurs : " + e.getMessage());
        }
    }

    public List<Notification> getNotificationsUtilisateur(int idUtilisateur) {
        List<Notification> list = new ArrayList<>();

        String req = "SELECT * FROM notification WHERE id_utilisateur=? ORDER BY date_creation DESC";

        try (PreparedStatement ps = conn.prepareStatement(req)) {

            ps.setInt(1, idUtilisateur);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                list.add(mapRow(rs));
            }

        } catch (SQLException e) {
            System.out.println("Erreur getNotificationsUtilisateur : " + e.getMessage());
        }

        return list;
    }

    public List<Notification> getNotificationsNonLues(int idUtilisateur) {
        List<Notification> list = new ArrayList<>();

        String req = "SELECT * FROM notification WHERE id_utilisateur=? AND lu=0 ORDER BY date_creation DESC";

        try (PreparedStatement ps = conn.prepareStatement(req)) {

            ps.setInt(1, idUtilisateur);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                list.add(mapRow(rs));
            }

        } catch (SQLException e) {
            System.out.println("Erreur getNotificationsNonLues : " + e.getMessage());
        }

        return list;
    }

    public int countNonLues(int idUtilisateur) {
        String req = "SELECT COUNT(*) AS total FROM notification WHERE id_utilisateur=? AND lu=0";

        try (PreparedStatement ps = conn.prepareStatement(req)) {

            ps.setInt(1, idUtilisateur);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return rs.getInt("total");
            }

        } catch (SQLException e) {
            System.out.println("Erreur countNonLues : " + e.getMessage());
        }

        return 0;
    }

    public void marquerCommeLue(int idNotification) {
        String req = "UPDATE notification SET lu=1 WHERE id=?";

        try (PreparedStatement ps = conn.prepareStatement(req)) {

            ps.setInt(1, idNotification);
            ps.executeUpdate();

            System.out.println("Notification marquee comme lue.");

        } catch (SQLException e) {
            System.out.println("Erreur marquerCommeLue : " + e.getMessage());
        }
    }

    public void marquerToutesCommeLues(int idUtilisateur) {
        String req = "UPDATE notification SET lu=1 WHERE id_utilisateur=?";

        try (PreparedStatement ps = conn.prepareStatement(req)) {

            ps.setInt(1, idUtilisateur);
            ps.executeUpdate();

            System.out.println("Toutes les notifications sont marquees comme lues.");

        } catch (SQLException e) {
            System.out.println("Erreur marquerToutesCommeLues : " + e.getMessage());
        }
    }

    public void notifierTodosAujourdhui() {
        String req = "SELECT t.titre, p.id_utilisateur " +
                "FROM todo_item t " +
                "JOIN planning p ON t.id_planning = p.id " +
                "WHERE p.date_revision = CURDATE() " +
                "AND t.statut <> 'termine'";

        try (Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(req)) {

            while (rs.next()) {
                String titreTodo = rs.getString("titre");
                int idUtilisateur = rs.getInt("id_utilisateur");

                envoyer(new Notification(
                        "Rappel revision",
                        "Vous avez une revision prevue aujourd hui : " + titreTodo,
                        "TODO",
                        idUtilisateur
                ));
            }

        } catch (SQLException e) {
            System.out.println("Erreur notifierTodosAujourdhui : " + e.getMessage());
        }
    }

    public void notifierTodosProches() {
        String req = "SELECT t.titre, p.date_revision, p.id_utilisateur " +
                "FROM todo_item t " +
                "JOIN planning p ON t.id_planning = p.id " +
                "WHERE p.date_revision BETWEEN CURDATE() AND DATE_ADD(CURDATE(), INTERVAL 2 DAY) " +
                "AND t.statut <> 'termine'";

        try (Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(req)) {

            while (rs.next()) {
                String titreTodo = rs.getString("titre");
                Date dateRevision = rs.getDate("date_revision");
                int idUtilisateur = rs.getInt("id_utilisateur");

                envoyer(new Notification(
                        "Revision proche",
                        "Vous avez une revision proche : " + titreTodo + " le " + dateRevision,
                        "TODO",
                        idUtilisateur
                ));
            }

        } catch (SQLException e) {
            System.out.println("Erreur notifierTodosProches : " + e.getMessage());
        }
    }

    private Notification mapRow(ResultSet rs) throws SQLException {
        Notification n = new Notification();

        n.setId(rs.getInt("id"));
        n.setTitre(rs.getString("titre"));
        n.setMessage(rs.getString("message"));
        n.setType(rs.getString("type"));
        n.setLu(rs.getBoolean("lu"));

        Timestamp timestamp = rs.getTimestamp("date_creation");
        if (timestamp != null) {
            n.setDateCreation(timestamp.toLocalDateTime());
        }

        n.setIdUtilisateur(rs.getInt("id_utilisateur"));

        return n;
    }

}