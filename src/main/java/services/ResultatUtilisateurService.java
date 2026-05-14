package services;

import models.ReponseUtilisateur;
import util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ReponseUtilisateurService implements IService<ReponseUtilisateur> {
    private final Connection conn;

    public ReponseUtilisateurService() {
        this.conn = DBConnection.getInstance().getConn();
    }

    @Override
    public void add(ReponseUtilisateur r) {
        String sql = "INSERT INTO reponse_utilisateur (id_resultat, id_question, id_reponse, texte_libre, est_correcte) " +
                "VALUES (?, ?, ?, ?, ?)";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, r.getIdResultat());
            ps.setInt(2, r.getIdQuestion());
            setNullableInt(ps, 3, r.getIdReponse());
            ps.setString(4, r.getTexteLibre());
            setNullableBoolean(ps, 5, r.getEstCorrecte());
            ps.executeUpdate();
            System.out.println("Reponse utilisateur ajoutee avec succes.");
        } catch (SQLException e) {
            System.out.println("Erreur add reponse utilisateur : " + e.getMessage());
        }
    }

    @Override
    public void update(ReponseUtilisateur r) {
        String sql = "UPDATE reponse_utilisateur SET id_reponse=?, texte_libre=?, est_correcte=? WHERE id=?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            setNullableInt(ps, 1, r.getIdReponse());
            ps.setString(2, r.getTexteLibre());
            setNullableBoolean(ps, 3, r.getEstCorrecte());
            ps.setInt(4, r.getId());
            ps.executeUpdate();
            System.out.println("Reponse utilisateur modifiee avec succes.");
        } catch (SQLException e) {
            System.out.println("Erreur update reponse utilisateur : " + e.getMessage());
        }
    }

    @Override
    public void delete(ReponseUtilisateur r) {
        String sql = "DELETE FROM reponse_utilisateur WHERE id=?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, r.getId());
            ps.executeUpdate();
            System.out.println("Reponse utilisateur supprimee avec succes.");
        } catch (SQLException e) {
            System.out.println("Erreur delete reponse utilisateur : " + e.getMessage());
        }
    }

    @Override
    public List<ReponseUtilisateur> getAll() {
        List<ReponseUtilisateur> list = new ArrayList<>();
        String sql = "SELECT * FROM reponse_utilisateur ORDER BY id DESC";

        try (Statement st = conn.createStatement(); ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                list.add(mapRow(rs));
            }
        } catch (SQLException e) {
            System.out.println("Erreur getAll reponse utilisateur : " + e.getMessage());
        }

        return list;
    }

    public List<ReponseUtilisateur> getByResultat(int idResultat) {
        List<ReponseUtilisateur> list = new ArrayList<>();
        String sql = "SELECT * FROM reponse_utilisateur WHERE id_resultat=?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idResultat);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(mapRow(rs));
            }
        } catch (SQLException e) {
            System.out.println("Erreur getByResultat reponse utilisateur : " + e.getMessage());
        }

        return list;
    }

    private ReponseUtilisateur mapRow(ResultSet rs) throws SQLException {
        ReponseUtilisateur r = new ReponseUtilisateur();
        r.setId(rs.getInt("id"));
        r.setIdResultat(rs.getInt("id_resultat"));
        r.setIdQuestion(rs.getInt("id_question"));

        int idReponse = rs.getInt("id_reponse");
        r.setIdReponse(rs.wasNull() ? null : idReponse);

        r.setTexteLibre(rs.getString("texte_libre"));

        boolean estCorrecte = rs.getBoolean("est_correcte");
        r.setEstCorrecte(rs.wasNull() ? null : estCorrecte);
        return r;
    }

    private void setNullableInt(PreparedStatement ps, int index, Integer value) throws SQLException {
        if (value == null) {
            ps.setNull(index, Types.INTEGER);
        } else {
            ps.setInt(index, value);
        }
    }

    private void setNullableBoolean(PreparedStatement ps, int index, Boolean value) throws SQLException {
        if (value == null) {
            ps.setNull(index, Types.BOOLEAN);
        } else {
            ps.setBoolean(index, value);
        }
    }
}