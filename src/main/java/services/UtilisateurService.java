package services;

import models.Utilisateur;
import util.DBConnection;
import util.PasswordUtil;

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
                "(nom, prenom, email, mot_de_passe, role, photo_profil, est_actif, filiere, annee, universite, numero_etudiant) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement ps = conn.prepareStatement(req, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, u.getNom());
            ps.setString(2, u.getPrenom());
            ps.setString(3, u.getEmail());

            String hashedPassword = PasswordUtil.hashPassword(u.getMotDePasse());
            ps.setString(4, hashedPassword);

            ps.setString(5, u.getRole());
            ps.setString(6, u.getPhotoProfil());
            ps.setBoolean(7, true);
            ps.setString(8, u.getFiliere());
            ps.setString(9, u.getAnnee());
            ps.setString(10, u.getUniversite());
            ps.setString(11, u.getNumeroEtudiant());

            int rows = ps.executeUpdate();

            if (rows == 0) {
                throw new SQLException("Aucune ligne insérée.");
            }

            try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    u.setId(generatedKeys.getInt(1));
                }
            }

            System.out.println("Utilisateur ajouté avec succès. ID = " + u.getId());

        } catch (SQLException e) {
            throw new RuntimeException("Erreur add utilisateur : " + e.getMessage());
        }
    }

    @Override
    public void update(Utilisateur u) {
        String req = "UPDATE utilisateur SET nom=?, prenom=?, email=?, role=?, photo_profil=?, est_actif=?, " +
                "filiere=?, annee=?, universite=?, numero_etudiant=? WHERE id_utilisateur=?";

        try (PreparedStatement ps = conn.prepareStatement(req)) {

            ps.setString(1, u.getNom());
            ps.setString(2, u.getPrenom());
            ps.setString(3, u.getEmail());
            ps.setString(4, u.getRole());
            ps.setString(5, u.getPhotoProfil());
            ps.setBoolean(6, u.isEstActif());
            ps.setString(7, u.getFiliere());
            ps.setString(8, u.getAnnee());
            ps.setString(9, u.getUniversite());
            ps.setString(10, u.getNumeroEtudiant());
            ps.setInt(11, u.getId());

            ps.executeUpdate();
            System.out.println("Utilisateur modifié avec succès.");

        } catch (SQLException e) {
            throw new RuntimeException("Erreur update utilisateur : " + e.getMessage());
        }
    }

    public void updateMotDePasse(int idUtilisateur, String nouveauMotDePasse) {
        if (!PasswordUtil.isStrongPassword(nouveauMotDePasse)) {
            throw new RuntimeException(PasswordUtil.getPasswordRulesMessage());
        }

        String req = "UPDATE utilisateur SET mot_de_passe=? WHERE id_utilisateur=?";

        try (PreparedStatement ps = conn.prepareStatement(req)) {
            ps.setString(1, PasswordUtil.hashPassword(nouveauMotDePasse));
            ps.setInt(2, idUtilisateur);

            ps.executeUpdate();
            System.out.println("Mot de passe modifié avec succès.");

        } catch (SQLException e) {
            throw new RuntimeException("Erreur update mot de passe : " + e.getMessage());
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

    public Utilisateur login(String email, String motDePasse, String role) {
        String req = "SELECT * FROM utilisateur WHERE LOWER(email)=LOWER(?) AND LOWER(role)=LOWER(?) AND est_actif=1";

        try (PreparedStatement ps = conn.prepareStatement(req)) {

            ps.setString(1, email.trim());
            ps.setString(2, role.trim());

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Utilisateur u = mapRow(rs);
                    String storedPassword = rs.getString("mot_de_passe");

                    if (PasswordUtil.isBCryptHash(storedPassword)) {
                        if (PasswordUtil.checkPassword(motDePasse, storedPassword)) {
                            return u;
                        }
                    } else {
                        // Compatibilité avec les anciens comptes dont le mot de passe était stocké en clair
                        if (storedPassword != null && storedPassword.equals(motDePasse)) {
                            updateMotDePasse(u.getId(), motDePasse);
                            return u;
                        }
                    }
                }
            }

        } catch (SQLException e) {
            System.out.println("Erreur login : " + e.getMessage());
        }

        return null;
    }

    public Utilisateur login(String email, String motDePasse) {
        String req = "SELECT * FROM utilisateur WHERE LOWER(email)=LOWER(?) AND est_actif=1";

        try (PreparedStatement ps = conn.prepareStatement(req)) {

            ps.setString(1, email.trim());

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Utilisateur u = mapRow(rs);
                    String storedPassword = rs.getString("mot_de_passe");

                    if (PasswordUtil.isBCryptHash(storedPassword)) {
                        if (PasswordUtil.checkPassword(motDePasse, storedPassword)) {
                            return u;
                        }
                    } else {
                        if (storedPassword != null && storedPassword.equals(motDePasse)) {
                            updateMotDePasse(u.getId(), motDePasse);
                            return u;
                        }
                    }
                }
            }

        } catch (SQLException e) {
            System.out.println("Erreur login : " + e.getMessage());
        }

        return null;
    }

    public boolean emailExiste(String email) {
        String req = "SELECT COUNT(*) FROM utilisateur WHERE LOWER(email)=LOWER(?)";

        try (PreparedStatement ps = conn.prepareStatement(req)) {
            ps.setString(1, email.trim());

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }

        } catch (SQLException e) {
            System.out.println("Erreur emailExiste : " + e.getMessage());
        }

        return false;
    }

    public Utilisateur getByEmail(String email) {
        String req = "SELECT * FROM utilisateur WHERE LOWER(email)=LOWER(?)";

        try (PreparedStatement ps = conn.prepareStatement(req)) {
            ps.setString(1, email.trim());

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
            }

        } catch (SQLException e) {
            System.out.println("Erreur getByEmail utilisateur : " + e.getMessage());
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

        try {
            u.setFiliere(rs.getString("filiere"));
        } catch (SQLException ignored) {
        }

        try {
            u.setAnnee(rs.getString("annee"));
        } catch (SQLException ignored) {
        }

        try {
            u.setUniversite(rs.getString("universite"));
        } catch (SQLException ignored) {
        }

        try {
            u.setNumeroEtudiant(rs.getString("numero_etudiant"));
        } catch (SQLException ignored) {
        }

        try {
            Timestamp date = rs.getTimestamp("date_inscription");
            if (date != null) {
                u.setDateInscription(date.toLocalDateTime());
            }
        } catch (SQLException ignored) {
        }

        return u;
    }
}