package services;
import models.Utilisateur;
import util.DBConnection;
import java.util.List;
import java.util.ArrayList;
import java.sql.*;
public class UtilisateurService implements IService<Utilisateur> {
    private Connection conn;
    public UtilisateurService() {
        this.conn = DBConnection.getInstance().getConn();
    }

    @Override
    public void add(Utilisateur u) {
        String req = "INSERT INTO utilisateur (nom, prenom, email, mot_de_passe, role) " +
                "VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(req)) {
            ps.setString(1, u.getNom());
            ps.setString(2, u.getPrenom());
            ps.setString(3, u.getEmail());
            ps.setString(4, u.getMotDePasse()); // hashé avant d'appeler
            ps.setString(5, u.getRole());
            ps.executeUpdate();
            System.out.println("User added successfully");
        } catch (SQLException e) {
            System.out.println("Error cannot be add user  : " + e.getMessage());
        }
    }

    @Override
    public void update(Utilisateur u) {
        String req = "UPDATE utilisateur SET nom=?, prenom=?, email=?, role=? WHERE id=?";
        try (PreparedStatement ps = conn.prepareStatement(req)) {
            ps.setString(1, u.getNom());
            ps.setString(2, u.getPrenom());
            ps.setString(3, u.getEmail());
            ps.setString(4, u.getRole());
            ps.setInt(5, u.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Erreur update utilisateur : " + e.getMessage());
        }


    }

    @Override
    public void delete(Utilisateur u) {
        String req = "DELETE FROM utilisateur WHERE id=?";
        try (PreparedStatement ps = conn.prepareStatement(req)) {
            ps.setInt(1, u.getId());
            ps.executeUpdate();
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
                Utilisateur u = new Utilisateur();
                u.setId(rs.getInt("id"));
                u.setNom(rs.getString("nom"));
                u.setPrenom(rs.getString("prenom"));
                u.setEmail(rs.getString("email"));
                u.setRole(rs.getString("role"));
                u.setActif(rs.getBoolean("actif"));
                list.add(u);
            }
        } catch (SQLException e) {
            System.out.println("Erreur getAll utilisateurs : " + e.getMessage());
        }
        return list;
    }
    public Utilisateur login(String email, String motDePasse) {
        String req = "SELECT * FROM utilisateur WHERE email=? AND mot_de_passe=? AND actif=1";

        try (PreparedStatement ps = conn.prepareStatement(req)) {

            ps.setString(1, email);
            ps.setString(2, motDePasse);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                Utilisateur u = new Utilisateur();

                u.setId(rs.getInt("id"));
                u.setNom(rs.getString("nom"));
                u.setPrenom(rs.getString("prenom"));
                u.setEmail(rs.getString("email"));
                u.setMotDePasse(rs.getString("mot_de_passe"));
                u.setRole(rs.getString("role"));
                u.setPhotoProfil(rs.getString("photo_profil"));
                u.setActif(rs.getBoolean("actif"));

                return u;
            }

        } catch (SQLException e) {
            System.out.println("Erreur login : " + e.getMessage());
        }

        return null;
    }

}

