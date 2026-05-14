package services;

<<<<<<< HEAD
import enums.ProjetStatut;
=======
>>>>>>> origin/gestionmohamed
import models.Projet;
import util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
<<<<<<< HEAD

public class ProjetService implements IService<Projet> {
    private final Connection conn;
=======

public class ProjetService implements IService<Projet> {

    private final Connection conn;

    private static final String TABLE_NAME = "projet";
>>>>>>> origin/gestionmohamed

    public ProjetService() {
        this.conn = DBConnection.getInstance().getConn();
    }

    @Override
    public void add(Projet p) {
<<<<<<< HEAD
        String sql = "INSERT INTO projet (nom_projet, description, id_createur, contient_code, contient_presentation, " +
                "contient_rapport, fichier_code, fichier_presentation, fichier_rapport, statut) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, p.getNomProjet());
            ps.setString(2, p.getDescription());
            ps.setInt(3, p.getIdCreateur());
            ps.setBoolean(4, p.isContientCode());
            ps.setBoolean(5, p.isContientPresentation());
            ps.setBoolean(6, p.isContientRapport());
            ps.setString(7, p.getFichierCode());
            ps.setString(8, p.getFichierPresentation());
            ps.setString(9, p.getFichierRapport());
            ps.setString(10, p.getStatut() == null ? ProjetStatut.EN_COURS.name() : p.getStatut());
            ps.executeUpdate();
            System.out.println("Projet ajoute avec succes.");
=======
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

            // TODO : plus tard remplacer 1 par l'id de l'utilisateur connecté
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
                ResultSet rs = ps.getGeneratedKeys();
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }

>>>>>>> origin/gestionmohamed
        } catch (SQLException e) {
            System.out.println("Erreur addAndReturnId projet : " + e.getMessage());
            e.printStackTrace();
        }

        return -1;
    }

    @Override
    public void update(Projet p) {
<<<<<<< HEAD
        String sql = "UPDATE projet SET nom_projet=?, description=?, contient_code=?, contient_presentation=?, contient_rapport=?, " +
                "fichier_code=?, fichier_presentation=?, fichier_rapport=?, statut=? WHERE id_projet=?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, p.getNomProjet());
            ps.setString(2, p.getDescription());
            ps.setBoolean(3, p.isContientCode());
            ps.setBoolean(4, p.isContientPresentation());
            ps.setBoolean(5, p.isContientRapport());
            ps.setString(6, p.getFichierCode());
            ps.setString(7, p.getFichierPresentation());
            ps.setString(8, p.getFichierRapport());
            ps.setString(9, p.getStatut());
            ps.setInt(10, p.getId());
            ps.executeUpdate();
            System.out.println("Projet modifie avec succes.");
=======
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

>>>>>>> origin/gestionmohamed
        } catch (SQLException e) {
            System.out.println("Erreur update projet : " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public void delete(Projet p) {
<<<<<<< HEAD
        String sql = "DELETE FROM projet WHERE id_projet=?";
=======
        String sql = "DELETE FROM " + TABLE_NAME + " WHERE id_projet=?";
>>>>>>> origin/gestionmohamed

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, p.getId());
            ps.executeUpdate();
<<<<<<< HEAD
            System.out.println("Projet supprime avec succes.");
=======

>>>>>>> origin/gestionmohamed
        } catch (SQLException e) {
            System.out.println("Erreur delete projet : " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public List<Projet> getAll() {
        List<Projet> list = new ArrayList<>();
<<<<<<< HEAD
        String sql = "SELECT * FROM projet ORDER BY date_creation DESC";

        try (Statement st = conn.createStatement(); ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                list.add(mapRow(rs));
            }
        } catch (SQLException e) {
            System.out.println("Erreur getAll projet : " + e.getMessage());
=======
        String sql = "SELECT * FROM " + TABLE_NAME + " ORDER BY date_creation DESC";

        try (Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                list.add(mapRow(rs));
            }

        } catch (SQLException e) {
            System.out.println("Erreur getAll projet : " + e.getMessage());
            e.printStackTrace();
>>>>>>> origin/gestionmohamed
        }

        return list;
    }

    public Projet getById(int idProjet) {
<<<<<<< HEAD
        String sql = "SELECT * FROM projet WHERE id_projet=?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idProjet);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return mapRow(rs);
            }
        } catch (SQLException e) {
            System.out.println("Erreur getById projet : " + e.getMessage());
        }

        return null;
    }

    public List<Projet> getByCreateur(int idCreateur) {
        List<Projet> list = new ArrayList<>();
        String sql = "SELECT * FROM projet WHERE id_createur=? ORDER BY date_creation DESC";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idCreateur);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(mapRow(rs));
            }
        } catch (SQLException e) {
            System.out.println("Erreur getByCreateur projet : " + e.getMessage());
        }

        return list;
    }

    public void changerStatut(int idProjet, ProjetStatut statut) {
        String sql = "UPDATE projet SET statut=? WHERE id_projet=?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, statut.name());
            ps.setInt(2, idProjet);
            ps.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Erreur changerStatut projet : " + e.getMessage());
=======
        String sql = "SELECT * FROM " + TABLE_NAME + " WHERE id_projet=?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idProjet);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return mapRow(rs);
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

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                list.add(mapRow(rs));
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

        } catch (SQLException e) {
            System.out.println("Erreur changerStatut projet : " + e.getMessage());
            e.printStackTrace();
>>>>>>> origin/gestionmohamed
        }
    }

    private Projet mapRow(ResultSet rs) throws SQLException {
        Projet p = new Projet();
<<<<<<< HEAD
=======

>>>>>>> origin/gestionmohamed
        p.setId(rs.getInt("id_projet"));
        p.setNomProjet(rs.getString("nom_projet"));
        p.setDescription(rs.getString("description"));
        p.setIdCreateur(rs.getInt("id_createur"));
<<<<<<< HEAD
        p.setContientCode(rs.getBoolean("contient_code"));
        p.setContientPresentation(rs.getBoolean("contient_presentation"));
        p.setContientRapport(rs.getBoolean("contient_rapport"));
        p.setFichierCode(rs.getString("fichier_code"));
        p.setFichierPresentation(rs.getString("fichier_presentation"));
        p.setFichierRapport(rs.getString("fichier_rapport"));
        p.setStatut(String.valueOf(ProjetStatut.valueOf(rs.getString("statut"))));
        Timestamp dateCreation = rs.getTimestamp("date_creation");
        p.setDateCreation(dateCreation == null ? null : dateCreation.toLocalDateTime());
=======

        p.setContientCode(rs.getBoolean("contient_code"));
        p.setContientPresentation(rs.getBoolean("contient_presentation"));
        p.setContientRapport(rs.getBoolean("contient_rapport"));

        p.setFichierCode(rs.getString("fichier_code"));
        p.setFichierPresentation(rs.getString("fichier_presentation"));
        p.setFichierRapport(rs.getString("fichier_rapport"));

        p.setStatut(rs.getString("statut"));

        Timestamp dateCreation = rs.getTimestamp("date_creation");
        p.setDateCreation(dateCreation == null ? null : dateCreation.toLocalDateTime());

>>>>>>> origin/gestionmohamed
        return p;

    }
<<<<<<< HEAD
=======

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

        return "en_cours";
    }
>>>>>>> origin/gestionmohamed
}