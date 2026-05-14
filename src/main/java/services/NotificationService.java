package services;

import models.Notification;
import util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class NotificationService implements IService<Notification> {

    private final Connection conn;

    public NotificationService() {
        this.conn = DBConnection.getInstance().getConn();
    }

    @Override
    public void add(Notification n) {
        String sql = "INSERT INTO notification " +
                "(id_utilisateur, titre, message, type_notification, est_lue) " +
                "VALUES (?, ?, ?, ?, ?)";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, n.getIdUtilisateur());
            ps.setString(2, n.getTitre());
            ps.setString(3, n.getMessage());
            ps.setString(4, n.getType());
            ps.setBoolean(5, n.isLu());

            ps.executeUpdate();
            System.out.println("Notification ajoutée avec succès.");

        } catch (SQLException e) {
            System.out.println("Erreur add notification : " + e.getMessage());
        }
    }

    @Override
    public void update(Notification n) {
        String sql = "UPDATE notification SET titre=?, message=?, type_notification=?, est_lue=? " +
                "WHERE id_notification=?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, n.getTitre());
            ps.setString(2, n.getMessage());
            ps.setString(3, n.getType());
            ps.setBoolean(4, n.isLu());
            ps.setInt(5, n.getId());

            ps.executeUpdate();
            System.out.println("Notification modifiée avec succès.");

        } catch (SQLException e) {
            System.out.println("Erreur update notification : " + e.getMessage());
        }
    }

    @Override
    public void delete(Notification n) {
        String sql = "DELETE FROM notification WHERE id_notification=?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, n.getId());

            ps.executeUpdate();
            System.out.println("Notification supprimée avec succès.");

        } catch (SQLException e) {
            System.out.println("Erreur delete notification : " + e.getMessage());
        }
    }

    @Override
    public List<Notification> getAll() {
        List<Notification> notifications = new ArrayList<>();
        String sql = "SELECT * FROM notification ORDER BY date_creation DESC";

        try (Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                notifications.add(mapRow(rs));
            }

        } catch (SQLException e) {
            System.out.println("Erreur getAll notification : " + e.getMessage());
        }

        return notifications;
    }

    public List<Notification> getByUtilisateur(int idUtilisateur) {
        List<Notification> notifications = new ArrayList<>();
        String sql = "SELECT * FROM notification WHERE id_utilisateur=? ORDER BY date_creation DESC";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idUtilisateur);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    notifications.add(mapRow(rs));
                }
            }

        } catch (SQLException e) {
            System.out.println("Erreur getByUtilisateur notification : " + e.getMessage());
        }

        return notifications;
    }

    public List<Notification> getNonLuesByUtilisateur(int idUtilisateur) {
        List<Notification> notifications = new ArrayList<>();
        String sql = "SELECT * FROM notification WHERE id_utilisateur=? AND est_lue=0 ORDER BY date_creation DESC";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idUtilisateur);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    notifications.add(mapRow(rs));
                }
            }

        } catch (SQLException e) {
            System.out.println("Erreur getNonLuesByUtilisateur notification : " + e.getMessage());
        }

        return notifications;
    }

    public void marquerCommeLue(int idNotification) {
        String sql = "UPDATE notification SET est_lue=1 WHERE id_notification=?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idNotification);

            ps.executeUpdate();
            System.out.println("Notification marquée comme lue.");

        } catch (SQLException e) {
            System.out.println("Erreur marquerCommeLue notification : " + e.getMessage());
        }
    }

    public void marquerToutCommeLu(int idUtilisateur) {
        String sql = "UPDATE notification SET est_lue=1 WHERE id_utilisateur=?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idUtilisateur);

            ps.executeUpdate();
            System.out.println("Toutes les notifications sont marquées comme lues.");

        } catch (SQLException e) {
            System.out.println("Erreur marquerToutCommeLu notification : " + e.getMessage());
        }
    }

    public int compterNonLues(int idUtilisateur) {
        String sql = "SELECT COUNT(*) FROM notification WHERE id_utilisateur=? AND est_lue=0";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idUtilisateur);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }

        } catch (SQLException e) {
            System.out.println("Erreur compterNonLues notification : " + e.getMessage());
        }

        return 0;
    }

    private Notification mapRow(ResultSet rs) throws SQLException {
        Notification n = new Notification();

        n.setId(rs.getInt("id_notification"));
        n.setIdUtilisateur(rs.getInt("id_utilisateur"));
        n.setTitre(rs.getString("titre"));
        n.setMessage(rs.getString("message"));
        n.setType(rs.getString("type_notification"));
        n.setLu(rs.getBoolean("est_lue"));

        Timestamp dateCreation = rs.getTimestamp("date_creation");
        if (dateCreation != null) {
            n.setDateCreation(dateCreation.toLocalDateTime());
        }

        return n;
    }
}
