package services;

import models.Utilisateur;
import util.DBConnection;
import util.PasswordUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
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
            ps.setString(4, PasswordUtil.hashPassword(u.getMotDePasse()));
            ps.setString(5, u.getRole());
            ps.setString(6, u.getPhotoProfil());
            ps.setBoolean(7, u.isEstActif());
            ps.setString(8, u.getFiliere());
            ps.setString(9, u.getAnnee());
            ps.setString(10, u.getUniversite());
            ps.setString(11, u.getNumeroEtudiant());

            int rows = ps.executeUpdate();

            if (rows == 0) {
                throw new SQLException("Aucune ligne inseree.");
            }

            try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    u.setId(generatedKeys.getInt(1));
                }
            }

            System.out.println("Utilisateur ajoute avec succes. ID = " + u.getId());
        } catch (SQLException e) {
            throw new RuntimeException("Erreur add utilisateur : " + e.getMessage(), e);
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
            System.out.println("Utilisateur modifie avec succes.");
        } catch (SQLException e) {
            throw new RuntimeException("Erreur update utilisateur : " + e.getMessage(), e);
        }
    }

    public void updateMotDePasse(int idUtilisateur, String nouveauMotDePasse) {
        if (!PasswordUtil.isStrongPassword(nouveauMotDePasse)) {
            throw new RuntimeException(PasswordUtil.getPasswordRulesMessage());
        }

        changerMotDePasseHash(idUtilisateur, nouveauMotDePasse);
        System.out.println("Mot de passe modifie avec succes.");
    }

    private void changerMotDePasseHash(int idUtilisateur, String motDePasseClair) {
        String req = "UPDATE utilisateur SET mot_de_passe=? WHERE id_utilisateur=?";

        try (PreparedStatement ps = conn.prepareStatement(req)) {
            ps.setString(1, PasswordUtil.hashPassword(motDePasseClair));
            ps.setInt(2, idUtilisateur);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erreur update mot de passe : " + e.getMessage(), e);
        }
    }

    @Override
    public void delete(Utilisateur u) {
        String req = "DELETE FROM utilisateur WHERE id_utilisateur=?";

        try (PreparedStatement ps = conn.prepareStatement(req)) {
            ps.setInt(1, u.getId());
            ps.executeUpdate();
            System.out.println("Utilisateur supprime avec succes.");
        } catch (SQLException e) {
            throw new RuntimeException("Erreur delete utilisateur : " + e.getMessage(), e);
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
            throw new RuntimeException("Erreur getAll utilisateurs : " + e.getMessage(), e);
        }

        return list;
    }

    public Utilisateur login(String email, String motDePasse, String role) {
        String req = "SELECT * FROM utilisateur " +
                "WHERE LOWER(email)=LOWER(?) AND LOWER(role)=LOWER(?) AND est_actif=1";

        try (PreparedStatement ps = conn.prepareStatement(req)) {
            ps.setString(1, email.trim());
            ps.setString(2, role.trim());

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next() && motDePasseValide(rs, motDePasse)) {
                    return mapRow(rs);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur login : " + e.getMessage(), e);
        }

        return null;
    }

    public Utilisateur login(String email, String motDePasse) {
        String req = "SELECT * FROM utilisateur WHERE LOWER(email)=LOWER(?) AND est_actif=1";

        try (PreparedStatement ps = conn.prepareStatement(req)) {
            ps.setString(1, email.trim());

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next() && motDePasseValide(rs, motDePasse)) {
                    return mapRow(rs);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur login : " + e.getMessage(), e);
        }

        return null;
    }

    private boolean motDePasseValide(ResultSet rs, String motDePasse) throws SQLException {
        String storedPassword = rs.getString("mot_de_passe");

        if (storedPassword == null || motDePasse == null) {
            return false;
        }

        if (PasswordUtil.isBCryptHash(storedPassword)) {
            return PasswordUtil.checkPassword(motDePasse, storedPassword);
        }

        boolean ancienMotDePasseValide = storedPassword.equals(motDePasse);
        if (ancienMotDePasseValide) {
            changerMotDePasseHash(rs.getInt("id_utilisateur"), motDePasse);
        }

        return ancienMotDePasseValide;
    }

    public boolean emailExiste(String email) {
        String req = "SELECT COUNT(*) FROM utilisateur WHERE LOWER(email)=LOWER(?)";

        try (PreparedStatement ps = conn.prepareStatement(req)) {
            ps.setString(1, email.trim());

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() && rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur emailExiste : " + e.getMessage(), e);
        }
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
            throw new RuntimeException("Erreur getByEmail utilisateur : " + e.getMessage(), e);
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
            throw new RuntimeException("Erreur getById utilisateur : " + e.getMessage(), e);
        }

        return null;
    }

    public void desactiverCompte(int idUtilisateur) {
        changerStatutCompte(idUtilisateur, false);
    }

    public void activerCompte(int idUtilisateur) {
        changerStatutCompte(idUtilisateur, true);
    }

    private void changerStatutCompte(int idUtilisateur, boolean actif) {
        String req = "UPDATE utilisateur SET est_actif=? WHERE id_utilisateur=?";

        try (PreparedStatement ps = conn.prepareStatement(req)) {
            ps.setBoolean(1, actif);
            ps.setInt(2, idUtilisateur);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erreur changement statut utilisateur : " + e.getMessage(), e);
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
            Timestamp date = rs.getTimestamp("date_inscription");
            if (date != null) {
                u.setDateInscription(date.toLocalDateTime());
            }
        } catch (SQLException ignored) {
        }

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

        return u;
    }
}
