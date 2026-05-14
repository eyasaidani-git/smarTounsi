package services;
<<<<<<< HEAD

import enums.EvenementType;
import models.Evenement;
import util.DBConnection;

import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
=======
import models.Evenement;
import util.DBConnection;

import java.sql.*;
>>>>>>> origin/gestionikram
import java.util.ArrayList;
import java.util.List;

public class EvenementService implements IService<Evenement> {
<<<<<<< HEAD

    private final Connection conn;
=======
    private Connection conn;
>>>>>>> origin/gestionikram

    public EvenementService() {
        this.conn = DBConnection.getInstance().getConn();
    }

    @Override
    public void add(Evenement e) {
<<<<<<< HEAD
        String sql = "INSERT INTO evenement " +
                "(titre, description, type_evenement, organise_par_site, lieu, date_debut, date_fin, tarif, capacity, statut, id_createur, image_evenement) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, e.getTitre());
            ps.setString(2, e.getDescription());

            ps.setString(3, e.getTypeEvenement() == null
                    ? EvenementType.AUTRE.name()
                    : e.getTypeEvenement().name());

            ps.setBoolean(4, e.isOrganiseParSite());
            ps.setString(5, e.getLieu());

            setDateTime(ps, 6, e.getDateDebut());
            setDateTime(ps, 7, e.getDateFin());

            ps.setBigDecimal(8, e.getTarif() == null ? BigDecimal.ZERO : e.getTarif());
            ps.setInt(9, e.getCapacity());
            ps.setString(10, e.getStatut() == null || e.getStatut().isBlank() ? "a_venir" : e.getStatut());
            ps.setInt(11, e.getIdCreateur());
            ps.setString(12, e.getImageEvenement());

            ps.executeUpdate();

            try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    e.setId(generatedKeys.getInt(1));
                }
            }

            System.out.println("Evenement ajoute avec succes. ID = " + e.getId());

        } catch (SQLException ex) {
            System.out.println("Erreur add evenement : " + ex.getMessage());
=======
        String req = "INSERT INTO evenement (nom, description, type, tarif, " +
                "emplacement, date_evenement, heure_evenement, id_organisateur) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(req)) {
            ps.setString(1, e.getNom());
            ps.setString(2, e.getDescription());
            ps.setString(3, e.getType());
            ps.setFloat(4, e.getTarif());
            ps.setString(5, e.getEmplacement());
            ps.setDate(6, Date.valueOf(e.getDateEvenement()));
            ps.setTime(7, Time.valueOf(e.getHeureEvenement()));
            ps.setInt(8, e.getIdOrganisateur());
            ps.executeUpdate();
            System.out.println("Événement ajouté ✔");
        } catch (SQLException ex) {
            System.out.println("Erreur add événement : " + ex.getMessage());
>>>>>>> origin/gestionikram
        }
    }

    @Override
    public void update(Evenement e) {
<<<<<<< HEAD
        String sql = "UPDATE evenement SET " +
                "titre=?, description=?, type_evenement=?, organise_par_site=?, lieu=?, date_debut=?, date_fin=?, " +
                "tarif=?, capacity=?, statut=?, id_createur=?, image_evenement=? " +
                "WHERE id_evenement=?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, e.getTitre());
            ps.setString(2, e.getDescription());

            ps.setString(3, e.getTypeEvenement() == null
                    ? EvenementType.AUTRE.name()
                    : e.getTypeEvenement().name());

            ps.setBoolean(4, e.isOrganiseParSite());
            ps.setString(5, e.getLieu());

            setDateTime(ps, 6, e.getDateDebut());
            setDateTime(ps, 7, e.getDateFin());

            ps.setBigDecimal(8, e.getTarif() == null ? BigDecimal.ZERO : e.getTarif());
            ps.setInt(9, e.getCapacity());
            ps.setString(10, e.getStatut() == null || e.getStatut().isBlank() ? "a_venir" : e.getStatut());
            ps.setInt(11, e.getIdCreateur());
            ps.setString(12, e.getImageEvenement());
            ps.setInt(13, e.getId());

            ps.executeUpdate();

            System.out.println("Evenement modifie avec succes.");

        } catch (SQLException ex) {
            System.out.println("Erreur update evenement : " + ex.getMessage());
=======
        String req = "UPDATE evenement SET nom=?, description=?, tarif=?, " +
                "emplacement=?, date_evenement=?, heure_evenement=? WHERE id=?";
        try (PreparedStatement ps = conn.prepareStatement(req)) {
            ps.setString(1, e.getNom());
            ps.setString(2, e.getDescription());
            ps.setFloat(3, e.getTarif());
            ps.setString(4, e.getEmplacement());
            ps.setDate(5, Date.valueOf(e.getDateEvenement()));
            ps.setTime(6, Time.valueOf(e.getHeureEvenement()));
            ps.setInt(7, e.getId());
            ps.executeUpdate();
        } catch (SQLException ex) {
            System.out.println("Erreur update événement : " + ex.getMessage());
>>>>>>> origin/gestionikram
        }
    }

    @Override
    public void delete(Evenement e) {
<<<<<<< HEAD
        String sql = "DELETE FROM evenement WHERE id_evenement=?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, e.getId());
            ps.executeUpdate();

            System.out.println("Evenement supprime avec succes.");

        } catch (SQLException ex) {
            System.out.println("Erreur delete evenement : " + ex.getMessage());
=======
        try (PreparedStatement ps = conn.prepareStatement("DELETE FROM evenement WHERE id=?")) {
            ps.setInt(1, e.getId());
            ps.executeUpdate();
        } catch (SQLException ex) {
            System.out.println("Erreur delete événement : " + ex.getMessage());
>>>>>>> origin/gestionikram
        }
    }

    @Override
    public List<Evenement> getAll() {
<<<<<<< HEAD
        String sql = "SELECT * FROM evenement ORDER BY date_debut ASC";
        return getEvenements(sql);
    }

    public Evenement getById(int idEvenement) {
        String sql = "SELECT * FROM evenement WHERE id_evenement=?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idEvenement);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
            }

        } catch (SQLException ex) {
            System.out.println("Erreur getById evenement : " + ex.getMessage());
        }

        return null;
    }

    public List<Evenement> getEvenementsOrganisesParSite() {
        String sql = "SELECT * FROM evenement WHERE organise_par_site=1 ORDER BY date_debut ASC";
        return getEvenements(sql);
    }

    public List<Evenement> getEvenementsExternes() {
        String sql = "SELECT * FROM evenement WHERE organise_par_site=0 ORDER BY date_debut ASC";
        return getEvenements(sql);
    }

    public List<Evenement> getEvenementsAVenir() {
        String sql = "SELECT * FROM evenement WHERE statut='a_venir' ORDER BY date_debut ASC";
        return getEvenements(sql);
    }

    public List<Evenement> getEvenementsPayantsDuSite() {
        String sql = "SELECT * FROM evenement " +
                "WHERE organise_par_site=1 AND tarif > 0 AND statut='a_venir' " +
                "ORDER BY date_debut ASC";

        return getEvenements(sql);
    }

    public List<Evenement> getEvenementsGratuitsDuSite() {
        String sql = "SELECT * FROM evenement " +
                "WHERE organise_par_site=1 AND tarif = 0 AND statut='a_venir' " +
                "ORDER BY date_debut ASC";

        return getEvenements(sql);
    }

    public List<Evenement> getByType(EvenementType type) {
        List<Evenement> list = new ArrayList<>();

        String sql = "SELECT * FROM evenement WHERE type_evenement=? ORDER BY date_debut ASC";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, type.name());

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRow(rs));
                }
            }

        } catch (SQLException ex) {
            System.out.println("Erreur getByType evenement : " + ex.getMessage());
        }

        return list;
    }

    public List<Evenement> getByDate(LocalDate date) {
        List<Evenement> list = new ArrayList<>();

        String sql = "SELECT * FROM evenement WHERE DATE(date_debut)=? ORDER BY date_debut ASC";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDate(1, Date.valueOf(date));

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRow(rs));
                }
            }

        } catch (SQLException ex) {
            System.out.println("Erreur getByDate evenement : " + ex.getMessage());
        }

        return list;
    }

    public List<Evenement> getByStatut(String statut) {
        List<Evenement> list = new ArrayList<>();

        String sql = "SELECT * FROM evenement WHERE statut=? ORDER BY date_debut ASC";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, statut);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRow(rs));
                }
            }

        } catch (SQLException ex) {
            System.out.println("Erreur getByStatut evenement : " + ex.getMessage());
        }

        return list;
    }

    public void changerStatut(int idEvenement, String nouveauStatut) {
        String sql = "UPDATE evenement SET statut=? WHERE id_evenement=?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, nouveauStatut);
            ps.setInt(2, idEvenement);

            ps.executeUpdate();

            System.out.println("Statut evenement modifie avec succes.");

        } catch (SQLException ex) {
            System.out.println("Erreur changerStatut evenement : " + ex.getMessage());
        }
    }

    public void annulerEvenement(int idEvenement) {
        changerStatut(idEvenement, "annule");
    }

    public void terminerEvenement(int idEvenement) {
        changerStatut(idEvenement, "termine");
    }

    public void marquerComplet(int idEvenement) {
        changerStatut(idEvenement, "complet");
    }

    public int compterParticipations(int idEvenement) {
        String sql = "SELECT COUNT(*) FROM participation_evenement " +
                "WHERE id_evenement=? AND type_participation='participation' " +
                "AND statut IN ('confirmee', 'present')";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idEvenement);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }

        } catch (SQLException ex) {
            System.out.println("Erreur compterParticipations : " + ex.getMessage());
        }

        return 0;
    }

    public int compterInteresses(int idEvenement) {
        String sql = "SELECT COUNT(*) FROM participation_evenement " +
                "WHERE id_evenement=? AND type_participation='interesse'";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idEvenement);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }

        } catch (SQLException ex) {
            System.out.println("Erreur compterInteresses : " + ex.getMessage());
        }

        return 0;
    }

    public boolean estComplet(int idEvenement) {
        Evenement e = getById(idEvenement);

        if (e == null) {
            return false;
        }

        if (e.getCapacity() <= 0) {
            return false;
        }

        int nbParticipants = compterParticipations(idEvenement);

        return nbParticipants >= e.getCapacity();
    }

    public List<Evenement> rechercher(String motCle) {
        List<Evenement> list = new ArrayList<>();

        String sql = "SELECT * FROM evenement " +
                "WHERE titre LIKE ? OR description LIKE ? OR lieu LIKE ? " +
                "ORDER BY date_debut ASC";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            String search = "%" + motCle + "%";

            ps.setString(1, search);
            ps.setString(2, search);
            ps.setString(3, search);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRow(rs));
                }
            }

        } catch (SQLException ex) {
            System.out.println("Erreur rechercher evenement : " + ex.getMessage());
        }

        return list;
    }

    private List<Evenement> getEvenements(String sql) {
        List<Evenement> list = new ArrayList<>();

        try (Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                list.add(mapRow(rs));
            }

        } catch (SQLException ex) {
            System.out.println("Erreur getEvenements : " + ex.getMessage());
        }

=======
        List<Evenement> list = new ArrayList<>();
        String req = "SELECT * FROM evenement ORDER BY date_evenement ASC";
        try (Statement st = conn.createStatement(); ResultSet rs = st.executeQuery(req)) {
            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) {
            System.out.println("Erreur getAll événements : " + e.getMessage());
        }
        return list;
    }
    public List<Evenement> getByType(String type) {
        List<Evenement> list = new ArrayList<>();
        String req = "SELECT * FROM evenement WHERE type=? ORDER BY date_evenement ASC";
        try (PreparedStatement ps = conn.prepareStatement(req)) {
            ps.setString(1, type);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) {
            System.out.println("Erreur getByType : " + e.getMessage());
        }
>>>>>>> origin/gestionikram
        return list;
    }

    private Evenement mapRow(ResultSet rs) throws SQLException {
        Evenement e = new Evenement();
<<<<<<< HEAD

        e.setId(rs.getInt("id_evenement"));
        e.setTitre(rs.getString("titre"));
        e.setDescription(rs.getString("description"));

        String type = rs.getString("type_evenement");
        if (type != null && !type.isBlank()) {
            e.setTypeEvenement(EvenementType.valueOf(type.toUpperCase()));
        } else {
            e.setTypeEvenement(EvenementType.AUTRE);
        }

        e.setOrganiseParSite(rs.getBoolean("organise_par_site"));
        e.setLieu(rs.getString("lieu"));

        Timestamp dateDebut = rs.getTimestamp("date_debut");
        if (dateDebut != null) {
            e.setDateDebut(dateDebut.toLocalDateTime());
        }

        Timestamp dateFin = rs.getTimestamp("date_fin");
        if (dateFin != null) {
            e.setDateFin(dateFin.toLocalDateTime());
        }

        BigDecimal tarif = rs.getBigDecimal("tarif");
        e.setTarif(tarif == null ? BigDecimal.ZERO : tarif);

        e.setCapacity(rs.getInt("capacity"));
        e.setStatut(rs.getString("statut"));
        e.setIdCreateur(rs.getInt("id_createur"));
        e.setImageEvenement(rs.getString("image_evenement"));

        Timestamp dateCreation = rs.getTimestamp("date_creation");
        if (dateCreation != null) {
            e.setDateCreation(dateCreation.toLocalDateTime());
        }

        return e;
    }

    private void setDateTime(PreparedStatement ps, int index, LocalDateTime value) throws SQLException {
        if (value == null) {
            ps.setNull(index, Types.TIMESTAMP);
        } else {
            ps.setTimestamp(index, Timestamp.valueOf(value));
        }
    }
}
=======
        e.setId(rs.getInt("id"));
        e.setNom(rs.getString("nom"));
        e.setDescription(rs.getString("description"));
        e.setType(rs.getString("type"));
        e.setTarif(rs.getFloat("tarif"));
        e.setEmplacement(rs.getString("emplacement"));
        e.setDateEvenement(rs.getDate("date_evenement").toLocalDate());
        Time t = rs.getTime("heure_evenement");
        if (t != null) e.setHeureEvenement(t.toLocalTime());
        e.setIdOrganisateur(rs.getInt("id_organisateur"));
        return e;
    }
}
>>>>>>> origin/gestionikram
