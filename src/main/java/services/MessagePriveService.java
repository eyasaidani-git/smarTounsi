package services;
import models.MessagePrive;
import util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
public class MessagePriveService implements IService<MessagePrive>{
    private Connection conn;

    public MessagePriveService() {
        this.conn = DBConnection.getInstance().getConn();
    }

    @Override
    public void add(MessagePrive m) {
        String req = "INSERT INTO message_prive (contenu, id_expediteur, id_destinataire) " +
                "VALUES (?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(req)) {
            ps.setString(1, m.getContenu());
            ps.setInt(2, m.getIdExpediteur());
            ps.setInt(3, m.getIdDestinataire());
            ps.executeUpdate();
            System.out.println("Message envoyé ✔");
        } catch (SQLException e) {
            System.out.println("Erreur envoi message : " + e.getMessage());
        }

    }

    @Override
    public void update(MessagePrive m) {
        String req = "UPDATE message_prive SET contenu=? WHERE id=? AND lu=0";
        try (PreparedStatement ps = conn.prepareStatement(req)) {
            ps.setString(1, m.getContenu());
            ps.setInt(2, m.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Erreur update message : " + e.getMessage());
        }

    }

    @Override
    public void delete(MessagePrive m) {
        try (PreparedStatement ps = conn.prepareStatement(
                "DELETE FROM message_prive WHERE id=?")) {
            ps.setInt(1, m.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Erreur delete message : " + e.getMessage());
        }

    }

    @Override
    public List<MessagePrive> getAll() {
        List<MessagePrive> list = new ArrayList<>();
        try (Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(
                     "SELECT * FROM message_prive ORDER BY date_envoi DESC")) {
            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) {
            System.out.println("Erreur getAll messages : " + e.getMessage());
        }
        return list;
    }
    public List<MessagePrive> getConversation(int idUser1, int idUser2) {
        List<MessagePrive> list = new ArrayList<>();
        String req = "SELECT * FROM message_prive " +
                "WHERE (id_expediteur=? AND id_destinataire=?) " +
                "   OR (id_expediteur=? AND id_destinataire=?) " +
                "ORDER BY date_envoi ASC";
        try (PreparedStatement ps = conn.prepareStatement(req)) {
            ps.setInt(1, idUser1); ps.setInt(2, idUser2);
            ps.setInt(3, idUser2); ps.setInt(4, idUser1);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) {
            System.out.println("Erreur getConversation : " + e.getMessage());
        }
        return list;
    }
    public List<MessagePrive> getNonLus(int idDestinataire) {
        List<MessagePrive> list = new ArrayList<>();
        String req = "SELECT * FROM message_prive " +
                "WHERE id_destinataire=? AND lu=0 ORDER BY date_envoi DESC";
        try (PreparedStatement ps = conn.prepareStatement(req)) {
            ps.setInt(1, idDestinataire);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) {
            System.out.println("Erreur getNonLus : " + e.getMessage());
        }
        return list;
    }
    public int countNonLus(int idDestinataire) {
        String req = "SELECT COUNT(*) FROM message_prive " +
                "WHERE id_destinataire=? AND lu=0";
        try (PreparedStatement ps = conn.prepareStatement(req)) {
            ps.setInt(1, idDestinataire);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) {
            System.out.println("Erreur countNonLus : " + e.getMessage());
        }
        return 0;
    }
    public void marquerConversationLue(int idExpediteur, int idDestinataire) {
        String req = "UPDATE message_prive SET lu=1 " +
                "WHERE id_expediteur=? AND id_destinataire=?";
        try (PreparedStatement ps = conn.prepareStatement(req)) {
            ps.setInt(1, idExpediteur);
            ps.setInt(2, idDestinataire);
            ps.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Erreur marquerConversationLue : " + e.getMessage());
        }
    }




    private MessagePrive mapRow(ResultSet rs)throws SQLException {
        MessagePrive m = new MessagePrive();
        m.setId(rs.getInt("id"));
        m.setContenu(rs.getString("contenu"));
        m.setLu(rs.getBoolean("lu"));
        m.setIdExpediteur(rs.getInt("id_expediteur"));
        m.setIdDestinataire(rs.getInt("id_destinataire"));
        Timestamp ts = rs.getTimestamp("date_envoi");
        if (ts != null) m.setDateEnvoi(ts.toLocalDateTime());
        return m;
    }
    }

