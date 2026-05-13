package services;

import models.Utilisateur;
import util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UtilisateurService implements IService<Utilisateur> {

    private final Connection conn;

    public UtilisateurService() {
        this.conn = DBConnection.getInstance().getConn();
    }

    @Override
    public void add(Utilisateur u) {
        String req = "INSERT INTO utilisateur " +
                "(nom, prenom, email, mot_de_passe, role, photo_profil, est_actif) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement ps = conn.prepareStatement(req)) {
            ps.setString(1, u.getNom());
            ps.setString(2, u.getPrenom());
            ps.setString(3, u.getEmail());
            ps.setString(4, u.getMotDePasse());
            ps.setString(5, u.getRole());
            ps.setString(6, u.getPhotoProfil());
            ps.setBoolean(7, u.isEstActif());

            ps.executeUpdate();
            System.out.println("Utilisateur ajouté avec succès.");

        } catch (SQLException e) {
            System.out.println("Erreur add utilisateur : " + e.getMessage());
        }
    }

    @Override
    public void update(Utilisateur u) {
        String req = "UPDATE utilisateur SET nom=?, prenom=?, email=?, mot_de_passe=?, role=?, photo_profil=?, est_actif=? " +
                "WHERE id_utilisateur=?";

        try (PreparedStatement ps = conn.prepareStatement(req)) {
            ps.setString(1, u.getNom());
            ps.setString(2, u.getPrenom());
            ps.setString(3, u.getEmail());
            ps.setString(4, u.getMotDePasse());
            ps.setString(5, u.getRole());
            ps.setString(6, u.getPhotoProfil());
            ps.setBoolean(7, u.isEstActif());
            ps.setInt(8, u.getId());

            ps.executeUpdate();
            System.out.println("Utilisateur modifié avec succès.");

        } catch (SQLException e) {
            System.out.println("Erreur update utilisateur : " + e.getMessage());
        }
    }

    @Override
    public void delete(Utilisateur u) {
        String req = "DELETE FROM utilisateur WHERE id_utilisateur=?";

        try (PreparedStatement ps = conn.prepareStatement(req)) {
            ps.setInt(1, u.getId());
            ps.executeUpdate();
            System.out.println("Utilisateur supprimé avec succès.");

        } catch (SQLException e) {
            System.out.println("Erreur delete utilisateur : " + e.getMessage());
        }
    }

    @Override
    public List<Utilisateur> getAll() {
        List<Utilisateur> list = new ArrayList<>();
        String req = "SELECT * FROM utilisateur";

        try (Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(req)) {

            while (rs.next()) {
                list.add(mapRow(rs));
            }

        } catch (SQLException e) {
            System.out.println("Erreur getAll utilisateurs : " + e.getMessage());
        }

        return list;
    }

    public Utilisateur login(String email, String motDePasse) {
        String req = "SELECT * FROM utilisateur WHERE email=? AND mot_de_passe=? AND est_actif=1";

        try (PreparedStatement ps = conn.prepareStatement(req)) {
            ps.setString(1, email);
            ps.setString(2, motDePasse);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
            }

        } catch (SQLException e) {
            System.out.println("Erreur login : " + e.getMessage());
        }

        return null;
    }

    public Utilisateur getById(int idUtilisateur) {
        String req = "SELECT * FROM utilisateur WHERE id_utilisateur=?";

        try (PreparedStatement ps = conn.prepareStatement(req)) {
            ps.setInt(1, idUtilisateur);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
            }

        } catch (SQLException e) {
            System.out.println("Erreur getById utilisateur : " + e.getMessage());
        }

        return null;
    }

    public void desactiverCompte(int idUtilisateur) {
        String req = "UPDATE utilisateur SET est_actif=0 WHERE id_utilisateur=?";

        try (PreparedStatement ps = conn.prepareStatement(req)) {
            ps.setInt(1, idUtilisateur);
            ps.executeUpdate();
            System.out.println("Compte désactivé avec succès.");

        } catch (SQLException e) {
            System.out.println("Erreur désactivation utilisateur : " + e.getMessage());
        }
    }

    public void activerCompte(int idUtilisateur) {
        String req = "UPDATE utilisateur SET est_actif=1 WHERE id_utilisateur=?";

        try (PreparedStatement ps = conn.prepareStatement(req)) {
            ps.setInt(1, idUtilisateur);
            ps.executeUpdate();
            System.out.println("Compte activé avec succès.");

        } catch (SQLException e) {
            System.out.println("Erreur activation utilisateur : " + e.getMessage());
        }
    }

    private Utilisateur mapRow(ResultSet rs) throws SQLException {
        Utilisateur u = new Utilisateur();

        u.setId(rs.getInt("id_utilisateur"));
        u.setNom(rs.getString("nom"));
        u.setPrenom(rs.getString("prenom"));
        u.setEmail(rs.getString("email"));
        u.setMotDePasse(rs.getString("mot_de_passe"));
        u.setRole(rs.getString("role"));
        u.setPhotoProfil(rs.getString("photo_profil"));
        u.setEstActif(rs.getBoolean("est_actif"));

        Timestamp dateInscription = rs.getTimestamp("date_inscription");
        if (dateInscription != null) {
            u.setDateInscription(dateInscription.toLocalDateTime());
        }

        return u;
    }
}