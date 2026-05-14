package services;

import models.Reponse;
import util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ReponseService implements IService<Reponse> {
    private final Connection conn;

    public ReponseService() {
        this.conn = DBConnection.getInstance().getConn();
    }

    @Override
    public void add(Reponse r) {
        String sql = "INSERT INTO reponse (id_question, texte_reponse, est_correcte) VALUES (?, ?, ?)";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, r.getIdQuestion());
            ps.setString(2, r.getTexteReponse());
            ps.setBoolean(3, r.isEstCorrecte());
            ps.executeUpdate();
            System.out.println("Reponse ajoutee avec succes.");
        } catch (SQLException e) {
            System.out.println("Erreur add reponse : " + e.getMessage());
        }
    }

    @Override
    public void update(Reponse r) {
        String sql = "UPDATE reponse SET texte_reponse=?, est_correcte=? WHERE id_reponse=?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, r.getTexteReponse());
            ps.setBoolean(2, r.isEstCorrecte());
            ps.setInt(3, r.getId());
            ps.executeUpdate();
            System.out.println("Reponse modifiee avec succes.");
        } catch (SQLException e) {
            System.out.println("Erreur update reponse : " + e.getMessage());
        }
    }

    @Override
    public void delete(Reponse r) {
        String sql = "DELETE FROM reponse WHERE id_reponse=?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, r.getId());
            ps.executeUpdate();
            System.out.println("Reponse supprimee avec succes.");
        } catch (SQLException e) {
            System.out.println("Erreur delete reponse : " + e.getMessage());
        }
    }

    @Override
    public List<Reponse> getAll() {
        List<Reponse> list = new ArrayList<>();
        String sql = "SELECT * FROM reponse ORDER BY id_question ASC, id_reponse ASC";

        try (Statement st = conn.createStatement(); ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                list.add(mapRow(rs));
            }
        } catch (SQLException e) {
            System.out.println("Erreur getAll reponse : " + e.getMessage());
        }

        return list;
    }

    public List<Reponse> getByQuestion(int idQuestion) {
        List<Reponse> list = new ArrayList<>();
        String sql = "SELECT * FROM reponse WHERE id_question=? ORDER BY id_reponse ASC";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idQuestion);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(mapRow(rs));
            }
        } catch (SQLException e) {
            System.out.println("Erreur getByQuestion reponse : " + e.getMessage());
        }

        return list;
    }

    private Reponse mapRow(ResultSet rs) throws SQLException {
        Reponse r = new Reponse();
        r.setId(rs.getInt("id_reponse"));
        r.setIdQuestion(rs.getInt("id_question"));
        r.setTexteReponse(rs.getString("texte_reponse"));
        r.setEstCorrecte(rs.getBoolean("est_correcte"));
        return r;
    }
}