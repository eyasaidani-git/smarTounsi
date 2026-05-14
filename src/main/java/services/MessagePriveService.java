package services;

import models.MessagePrive;
import util.DBConnection;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class MessagePriveService implements IService<MessagePrive> {
    private final Connection conn;

    public MessagePriveService() {
        this.conn = DBConnection.getInstance().getConn();
    }

    @Override
    public void add(MessagePrive m) {
        String sql = "INSERT INTO message_prive (id_expediteur, id_destinataire, contenu) VALUES (?, ?, ?)";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, m.getIdExpediteur());
            ps.setInt(2, m.getIdDestinataire());
            ps.setString(3, m.getContenu());
            ps.executeUpdate();
            System.out.println("Message envoye avec succes.");
        } catch (SQLException e) {
            System.out.println("Erreur add message prive : " + e.getMessage());
        }
    }

    @Override
    public void update(MessagePrive m) {
        String sql = "UPDATE message_prive SET contenu=? WHERE id_message=?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, m.getContenu());
            ps.setInt(2, m.getId());
            ps.executeUpdate();
            System.out.println("Message modifie avec succes.");
        } catch (SQLException e) {
            System.out.println("Erreur update message prive : " + e.getMessage());
        }
    }

    @Override
    public void delete(MessagePrive m) {
        String sql = "DELETE FROM message_prive WHERE id_message=?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, m.getId());
            ps.executeUpdate();
            System.out.println("Message supprime avec succes.");
        } catch (SQLException e) {
            System.out.println("Erreur delete message prive : " + e.getMessage());
        }
    }

    @Override
    public List<MessagePrive> getAll() {
        List<MessagePrive> list = new ArrayList<>();
        String sql = "SELECT * FROM message_prive ORDER BY date_envoi DESC";

        try (Statement st = conn.createStatement(); ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                list.add(mapRow(rs));
            }
        } catch (SQLException e) {
            System.out.println("Erreur getAll message prive : " + e.getMessage());
        }

        return list;
    }

    public List<MessagePrive> getConversation(int idUser1, int idUser2) {
        List<MessagePrive> list = new ArrayList<>();
        String sql = "SELECT * FROM message_prive " +
                "WHERE (id_expediteur=? AND id_destinataire=?) OR (id_expediteur=? AND id_destinataire=?) " +
                "ORDER BY date_envoi ASC";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idUser1);
            ps.setInt(2, idUser2);
            ps.setInt(3, idUser2);
            ps.setInt(4, idUser1);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(mapRow(rs));
            }
        } catch (SQLException e) {
            System.out.println("Erreur getConversation : " + e.getMessage());
        }

        return list;
    }

    public List<MessagePrive> getMessagesRecus(int idDestinataire) {
        List<MessagePrive> list = new ArrayList<>();
        String sql = "SELECT * FROM message_prive WHERE id_destinataire=? ORDER BY date_envoi DESC";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idDestinataire);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(mapRow(rs));
            }
        } catch (SQLException e) {
            System.out.println("Erreur getMessagesRecus : " + e.getMessage());
        }

        return list;
    }

    public List<MessagePrive> getMessagesEnvoyes(int idExpediteur) {
        List<MessagePrive> list = new ArrayList<>();
        String sql = "SELECT * FROM message_prive WHERE id_expediteur=? ORDER BY date_envoi DESC";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idExpediteur);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(mapRow(rs));
            }
        } catch (SQLException e) {
            System.out.println("Erreur getMessagesEnvoyes : " + e.getMessage());
        }

        return list;
    }

    public void marquerCommeLu(int idMessage) {
        String sql = "UPDATE message_prive SET est_lu=1, date_lecture=? WHERE id_message=?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setTimestamp(1, Timestamp.valueOf(LocalDateTime.now()));
            ps.setInt(2, idMessage);
            ps.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Erreur marquerCommeLu message : " + e.getMessage());
        }
    }

    private MessagePrive mapRow(ResultSet rs) throws SQLException {
        MessagePrive m = new MessagePrive();
        m.setId(rs.getInt("id_message"));
        m.setIdExpediteur(rs.getInt("id_expediteur"));
        m.setIdDestinataire(rs.getInt("id_destinataire"));
        m.setContenu(rs.getString("contenu"));
        Timestamp dateEnvoi = rs.getTimestamp("date_envoi");
        m.setDateEnvoi(dateEnvoi == null ? null : dateEnvoi.toLocalDateTime());
        m.setEstLu(rs.getBoolean("est_lu"));
        Timestamp dateLecture = rs.getTimestamp("date_lecture");
        m.setDateLecture(dateLecture == null ? null : dateLecture.toLocalDateTime());
        return m;
    }
<<<<<<< HEAD
}
=======
}
>>>>>>> origin/GestionNour
