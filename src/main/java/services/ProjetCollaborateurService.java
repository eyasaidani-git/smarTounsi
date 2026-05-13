package services;

import models.ProjetCollaborateur;
import util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProjetCollaborateurService implements IService<ProjetCollaborateur> {
    private final Connection conn;

    public ProjetCollaborateurService() {
        this.conn = DBConnection.getInstance().getConn();
    }

    @Override
    public void add(ProjetCollaborateur pc) {
        String sql = "INSERT INTO projet_collaborateur (id_projet, id_utilisateur, role_projet) VALUES (?, ?, ?)";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, pc.getIdProjet());
            ps.setInt(2, pc.getIdUtilisateur());
            ps.setString(3, pc.getRoleProjet());
            ps.executeUpdate();
            System.out.println("Collaborateur ajoute au projet.");
        } catch (SQLException e) {
            System.out.println("Erreur add projet collaborateur : " + e.getMessage());
        }
    }

    @Override
    public void update(ProjetCollaborateur pc) {
        String sql = "UPDATE projet_collaborateur SET role_projet=? WHERE id=?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, pc.getRoleProjet());
            ps.setInt(2, pc.getId());
            ps.executeUpdate();
            System.out.println("Collaborateur modifie avec succes.");
        } catch (SQLException e) {
            System.out.println("Erreur update projet collaborateur : " + e.getMessage());
        }
    }

    @Override
    public void delete(ProjetCollaborateur pc) {
        String sql = "DELETE FROM projet_collaborateur WHERE id_projet=? AND id_utilisateur=?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, pc.getIdProjet());
            ps.setInt(2, pc.getIdUtilisateur());
            ps.executeUpdate();
            System.out.println("Collaborateur supprime du projet.");
        } catch (SQLException e) {
            System.out.println("Erreur delete projet collaborateur : " + e.getMessage());
        }
    }

    public void deleteById(int id) {
        String sql = "DELETE FROM projet_collaborateur WHERE id=?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Erreur deleteById projet collaborateur : " + e.getMessage());
        }
    }

    @Override
    public List<ProjetCollaborateur> getAll() {
        List<ProjetCollaborateur> list = new ArrayList<>();
        String sql = "SELECT * FROM projet_collaborateur ORDER BY date_ajout DESC";

        try (Statement st = conn.createStatement(); ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                list.add(mapRow(rs));
            }
        } catch (SQLException e) {
            System.out.println("Erreur getAll projet collaborateur : " + e.getMessage());
        }

        return list;
    }

    public List<ProjetCollaborateur> getByProjet(int idProjet) {
        List<ProjetCollaborateur> list = new ArrayList<>();
        String sql = "SELECT * FROM projet_collaborateur WHERE id_projet=? ORDER BY date_ajout ASC";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idProjet);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(mapRow(rs));
            }
        } catch (SQLException e) {
            System.out.println("Erreur getByProjet collaborateur : " + e.getMessage());
        }

        return list;
    }

    public List<ProjetCollaborateur> getByUtilisateur(int idUtilisateur) {
        List<ProjetCollaborateur> list = new ArrayList<>();
        String sql = "SELECT * FROM projet_collaborateur WHERE id_utilisateur=? ORDER BY date_ajout DESC";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idUtilisateur);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(mapRow(rs));
            }
        } catch (SQLException e) {
            System.out.println("Erreur getByUtilisateur collaborateur : " + e.getMessage());
        }

        return list;
    }

    public boolean existe(int idProjet, int idUtilisateur) {
        String sql = "SELECT id FROM projet_collaborateur WHERE id_projet=? AND id_utilisateur=?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idProjet);
            ps.setInt(2, idUtilisateur);
            ResultSet rs = ps.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            System.out.println("Erreur existe projet collaborateur : " + e.getMessage());
        }

        return false;
    }

    private ProjetCollaborateur mapRow(ResultSet rs) throws SQLException {
        ProjetCollaborateur pc = new ProjetCollaborateur();
        pc.setId(rs.getInt("id"));
        pc.setIdProjet(rs.getInt("id_projet"));
        pc.setIdUtilisateur(rs.getInt("id_utilisateur"));
        pc.setRoleProjet(rs.getString("role_projet"));
        Timestamp dateAjout = rs.getTimestamp("date_ajout");
        pc.setDateAjout(dateAjout == null ? null : dateAjout.toLocalDateTime());
        return pc;
    }
}