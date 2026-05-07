package services;
import models.Projet;
import util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
public class ProjetService implements IService<Projet> {

    private Connection conn;

    public ProjetService() {
        this.conn = DBConnection.getInstance().getConn();
    }

    @Override
    public void add(Projet p) {
        String req = "INSERT INTO projet (nom, description, a_code, a_presentation, " +
                "a_rapport, id_createur) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(req)) {
            ps.setString(1, p.getNom());
            ps.setString(2, p.getDescription());
            ps.setBoolean(3, p.isaCode());
            ps.setBoolean(4, p.isaPresentation());
            ps.setBoolean(5, p.isaRapport());
            ps.setInt(6, p.getIdCreateur());
            ps.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Erreur add projet : " + e.getMessage());
        }
    }

    @Override
    public void update(Projet p) {
        String req = "UPDATE projet SET nom=?, description=?, fichier_code=?, " +
                "fichier_presentation=?, fichier_rapport=? WHERE id=?";
        try (PreparedStatement ps = conn.prepareStatement(req)) {
            ps.setString(1, p.getNom());
            ps.setString(2, p.getDescription());
            ps.setString(3, p.getFichierCode());
            ps.setString(4, p.getFichierPresentation());
            ps.setString(5, p.getFichierRapport());
            ps.setInt(6, p.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Erreur update projet : " + e.getMessage());
        }
    }

    @Override
    public void delete(Projet p) {
        try (PreparedStatement ps = conn.prepareStatement("DELETE FROM projet WHERE id=?")) {
            ps.setInt(1, p.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Erreur delete projet : " + e.getMessage());
        }
    }

    @Override
    public List<Projet> getAll() {
        List<Projet> list = new ArrayList<>();
        try (Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery("SELECT * FROM projet")) {
            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) {
            System.out.println("Erreur getAll projets : " + e.getMessage());
        }
        return list;
    }

    public List<Projet> search(String keyword) {
        List<Projet> list = new ArrayList<>();
        String req = "SELECT * FROM projet WHERE nom LIKE ?";
        try (PreparedStatement ps = conn.prepareStatement(req)) {
            ps.setString(1, "%" + keyword + "%");
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) {
            System.out.println("Erreur search projet : " + e.getMessage());
        }
        return list;
    }

    public void addCollaborateur(int idProjet, int idUtilisateur) {
        String req = "INSERT IGNORE INTO projet_collaborateur (id_projet, id_utilisateur) VALUES (?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(req)) {
            ps.setInt(1, idProjet);
            ps.setInt(2, idUtilisateur);
            ps.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Erreur addCollaborateur : " + e.getMessage());
        }
    }

    private Projet mapRow(ResultSet rs) throws SQLException {
        Projet p = new Projet();
        p.setId(rs.getInt("id"));
        p.setNom(rs.getString("nom"));
        p.setDescription(rs.getString("description"));
        p.setaCode(rs.getBoolean("a_code"));
        p.setaPresentation(rs.getBoolean("a_presentation"));
        p.setaRapport(rs.getBoolean("a_rapport"));
        p.setFichierCode(rs.getString("fichier_code"));
        p.setFichierPresentation(rs.getString("fichier_presentation"));
        p.setFichierRapport(rs.getString("fichier_rapport"));
        p.setIdCreateur(rs.getInt("id_createur"));
        return p;
    }
}
