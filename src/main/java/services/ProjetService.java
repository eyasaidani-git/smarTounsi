package services;
<<<<<<< HEAD

import enums.ProjetStatut;
=======
>>>>>>> origin/gestionikram
import models.Projet;
import util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
<<<<<<< HEAD

public class ProjetService implements IService<Projet> {

    private final Connection conn;
    private static final String TABLE_NAME = "projet";
=======
public class ProjetService implements IService<Projet> {

    private Connection conn;
>>>>>>> origin/gestionikram

    public ProjetService() {
        this.conn = DBConnection.getInstance().getConn();
    }

    @Override
    public void add(Projet p) {
<<<<<<< HEAD
        addAndReturnId(p);
    }

    public int addAndReturnId(Projet p) {
        String sql = "INSERT INTO " + TABLE_NAME + " " +
                "(nom_projet, description, id_createur, contient_code, contient_presentation, contient_rapport, " +
                "fichier_code, fichier_presentation, fichier_rapport, statut) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, p.getNomProjet());
            ps.setString(2, p.getDescription());
            ps.setInt(3, p.getIdCreateur());

            ps.setBoolean(4, p.isContientCode());
            ps.setBoolean(5, p.isContientPresentation());
            ps.setBoolean(6, p.isContientRapport());

            ps.setString(7, p.getFichierCode());
            ps.setString(8, p.getFichierPresentation());
            ps.setString(9, p.getFichierRapport());

            ps.setString(10, normalizeStatut(p.getStatut()));

            int rows = ps.executeUpdate();

            if (rows > 0) {
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        p.setId(rs.getInt(1));
                        System.out.println("Projet ajouté avec succès. ID = " + p.getId());
                        return p.getId();
                    }
                }
            }

        } catch (SQLException e) {
            System.out.println("Erreur addAndReturnId projet : " + e.getMessage());
            e.printStackTrace();
        }

        return -1;
=======
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
>>>>>>> origin/gestionikram
    }

    @Override
    public void update(Projet p) {
<<<<<<< HEAD
        String sql = "UPDATE " + TABLE_NAME + " SET " +
                "nom_projet=?, description=?, contient_code=?, contient_presentation=?, contient_rapport=?, " +
                "fichier_code=?, fichier_presentation=?, fichier_rapport=?, statut=? " +
                "WHERE id_projet=?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, p.getNomProjet());
            ps.setString(2, p.getDescription());

            ps.setBoolean(3, p.isContientCode());
            ps.setBoolean(4, p.isContientPresentation());
            ps.setBoolean(5, p.isContientRapport());

            ps.setString(6, p.getFichierCode());
            ps.setString(7, p.getFichierPresentation());
            ps.setString(8, p.getFichierRapport());

            ps.setString(9, normalizeStatut(p.getStatut()));
            ps.setInt(10, p.getId());

            ps.executeUpdate();
            System.out.println("Projet modifié avec succès.");

        } catch (SQLException e) {
            System.out.println("Erreur update projet : " + e.getMessage());
            e.printStackTrace();
=======
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
>>>>>>> origin/gestionikram
        }
    }

    @Override
    public void delete(Projet p) {
<<<<<<< HEAD
        String sql = "DELETE FROM " + TABLE_NAME + " WHERE id_projet=?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, p.getId());
            ps.executeUpdate();

            System.out.println("Projet supprimé avec succès.");

        } catch (SQLException e) {
            System.out.println("Erreur delete projet : " + e.getMessage());
            e.printStackTrace();
=======
        try (PreparedStatement ps = conn.prepareStatement("DELETE FROM projet WHERE id=?")) {
            ps.setInt(1, p.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Erreur delete projet : " + e.getMessage());
>>>>>>> origin/gestionikram
        }
    }

    @Override
    public List<Projet> getAll() {
        List<Projet> list = new ArrayList<>();
<<<<<<< HEAD
        String sql = "SELECT * FROM " + TABLE_NAME + " ORDER BY date_creation DESC";

        try (Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                list.add(mapRow(rs));
            }

        } catch (SQLException e) {
            System.out.println("Erreur getAll projet : " + e.getMessage());
            e.printStackTrace();
        }

        return list;
    }

    public Projet getById(int idProjet) {
        String sql = "SELECT * FROM " + TABLE_NAME + " WHERE id_projet=?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idProjet);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
            }

        } catch (SQLException e) {
            System.out.println("Erreur getById projet : " + e.getMessage());
            e.printStackTrace();
        }

        return null;
    }

    public List<Projet> getByCreateur(int idCreateur) {
        List<Projet> list = new ArrayList<>();
        String sql = "SELECT * FROM " + TABLE_NAME + " WHERE id_createur=? ORDER BY date_creation DESC";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idCreateur);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRow(rs));
                }
            }

        } catch (SQLException e) {
            System.out.println("Erreur getByCreateur projet : " + e.getMessage());
            e.printStackTrace();
        }

        return list;
    }

    public void changerStatut(int idProjet, String statut) {
        String sql = "UPDATE " + TABLE_NAME + " SET statut=? WHERE id_projet=?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, normalizeStatut(statut));
            ps.setInt(2, idProjet);
            ps.executeUpdate();

            System.out.println("Statut projet modifié avec succès.");

        } catch (SQLException e) {
            System.out.println("Erreur changerStatut projet : " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void changerStatut(int idProjet, ProjetStatut statut) {
        if (statut == null) {
            changerStatut(idProjet, "en_cours");
        } else {
            changerStatut(idProjet, statut.name());
=======
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
>>>>>>> origin/gestionikram
        }
    }

    private Projet mapRow(ResultSet rs) throws SQLException {
        Projet p = new Projet();
<<<<<<< HEAD

        p.setId(rs.getInt("id_projet"));
        p.setNomProjet(rs.getString("nom_projet"));
        p.setDescription(rs.getString("description"));
        p.setIdCreateur(rs.getInt("id_createur"));

        p.setContientCode(rs.getBoolean("contient_code"));
        p.setContientPresentation(rs.getBoolean("contient_presentation"));
        p.setContientRapport(rs.getBoolean("contient_rapport"));

        p.setFichierCode(rs.getString("fichier_code"));
        p.setFichierPresentation(rs.getString("fichier_presentation"));
        p.setFichierRapport(rs.getString("fichier_rapport"));

        p.setStatut(normalizeStatut(rs.getString("statut")));

        Timestamp dateCreation = rs.getTimestamp("date_creation");
        if (dateCreation != null) {
            p.setDateCreation(dateCreation.toLocalDateTime());
        }

        return p;
    }

    private String normalizeStatut(String statut) {
        if (statut == null || statut.trim().isEmpty()) {
            return "en_cours";
        }

        String value = statut.trim().toLowerCase();

        if (value.equals("en cours")) {
            return "en_cours";
        }

        if (value.equals("en_cours") || value.equals("termine") || value.equals("en_pause")) {
            return value;
        }

        if (value.equals("en_cours".toUpperCase()) || value.equals("termine".toUpperCase()) || value.equals("en_pause".toUpperCase())) {
            return value.toLowerCase();
        }

        return "en_cours";
    }
}
=======
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
>>>>>>> origin/gestionikram
