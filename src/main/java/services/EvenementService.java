package services;

import enums.EvenementType;
import models.Evenement;
import util.DBConnection;

import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class EvenementService implements IService<Evenement> {

    public EvenementService() {
    }

    private Connection getConn() {
        return DBConnection.getInstance().getConn();
    }

    @Override
    public void add(Evenement e) {
        String sql = "INSERT INTO evenement " +
                "(titre, description, type_evenement, organise_par_site, lieu, date_debut, date_fin, tarif, capacity, statut, id_createur, image_evenement) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement ps = getConn().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            fillStatement(ps, e, false);
            ps.executeUpdate();

            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    e.setId(keys.getInt(1));
                }
            }

            System.out.println("Événement ajouté avec succès. ID = " + e.getId());
        } catch (SQLException ex) {
            ex.printStackTrace();
            throw new RuntimeException("Erreur add événement : " + ex.getMessage());
        }
    }

    public int addAndReturnId(Evenement e) {
        add(e);
        return e.getId();
    }

    @Override
    public void update(Evenement e) {
        String sql = "UPDATE evenement SET " +
                "titre=?, description=?, type_evenement=?, organise_par_site=?, lieu=?, date_debut=?, date_fin=?, " +
                "tarif=?, capacity=?, statut=?, id_createur=?, image_evenement=? " +
                "WHERE id_evenement=?";

        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            fillStatement(ps, e, true);
            ps.executeUpdate();
            System.out.println("Événement modifié avec succès.");
        } catch (SQLException ex) {
            ex.printStackTrace();
            throw new RuntimeException("Erreur update événement : " + ex.getMessage());
        }
    }

    @Override
    public void delete(Evenement e) {
        String sql = "DELETE FROM evenement WHERE id_evenement=?";

        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setInt(1, e.getId());
            ps.executeUpdate();
            System.out.println("Événement supprimé avec succès.");
        } catch (SQLException ex) {
            ex.printStackTrace();
            throw new RuntimeException("Erreur delete événement : " + ex.getMessage());
        }
    }

    @Override
    public List<Evenement> getAll() {
        String sql = "SELECT * FROM evenement ORDER BY date_debut ASC";
        return getEvenements(sql);
    }

    public Evenement getById(int idEvenement) {
        String sql = "SELECT * FROM evenement WHERE id_evenement=?";

        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setInt(1, idEvenement);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            throw new RuntimeException("Erreur getById événement : " + ex.getMessage());
        }

        return null;
    }

    public List<Evenement> getByCreateur(int idCreateur) {
        List<Evenement> list = new ArrayList<>();
        String sql = "SELECT * FROM evenement WHERE id_createur=? ORDER BY date_debut ASC";

        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setInt(1, idCreateur);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRow(rs));
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            throw new RuntimeException("Erreur getByCreateur événement : " + ex.getMessage());
        }

        return list;
    }

    public List<Evenement> search(String keyword, String type, String statut, String organiseFilter) {
        List<Evenement> list = new ArrayList<>();

        StringBuilder sql = new StringBuilder("SELECT * FROM evenement WHERE 1=1");
        List<Object> params = new ArrayList<>();

        if (keyword != null && !keyword.isBlank()) {
            sql.append(" AND (LOWER(titre) LIKE ? OR LOWER(description) LIKE ? OR LOWER(lieu) LIKE ?)");
            String k = "%" + keyword.trim().toLowerCase() + "%";
            params.add(k);
            params.add(k);
            params.add(k);
        }

        if (type != null && !type.isBlank() && !"TOUS".equalsIgnoreCase(type)) {
            sql.append(" AND type_evenement=?");
            params.add(type.trim().toLowerCase());
        }

        if (statut != null && !statut.isBlank() && !"TOUS".equalsIgnoreCase(statut)) {
            sql.append(" AND statut=?");
            params.add(statut.trim().toLowerCase());
        }

        if (organiseFilter != null && !organiseFilter.isBlank() && !"TOUS".equalsIgnoreCase(organiseFilter)) {
            sql.append(" AND organise_par_site=?");
            params.add("SITE".equalsIgnoreCase(organiseFilter) ? 1 : 0);
        }

        sql.append(" ORDER BY date_debut ASC");

        try (PreparedStatement ps = getConn().prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) {
                Object value = params.get(i);
                if (value instanceof Integer) {
                    ps.setInt(i + 1, (Integer) value);
                } else {
                    ps.setString(i + 1, String.valueOf(value));
                }
            }

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRow(rs));
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            throw new RuntimeException("Erreur search événement : " + ex.getMessage());
        }

        return list;
    }

    public int countParticipantsConfirmes(int idEvenement) {
        String sql = "SELECT COUNT(*) FROM participation_evenement " +
                "WHERE id_evenement=? AND type_participation='participation' AND statut IN ('confirmee', 'present')";

        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setInt(1, idEvenement);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            throw new RuntimeException("Erreur count participants : " + ex.getMessage());
        }

        return 0;
    }

    public int placesRestantes(Evenement e) {
        if (e == null || e.getCapacity() <= 0) {
            return 0;
        }

        return Math.max(0, e.getCapacity() - countParticipantsConfirmes(e.getId()));
    }

    public boolean estComplet(Evenement e) {
        return e != null && e.getCapacity() > 0 && countParticipantsConfirmes(e.getId()) >= e.getCapacity();
    }

    private void fillStatement(PreparedStatement ps, Evenement e, boolean update) throws SQLException {
        ps.setString(1, e.getTitre());
        ps.setString(2, e.getDescription());
        ps.setString(3, e.getTypeEvenement() == null ? EvenementType.AUTRE.toDbValue() : e.getTypeEvenement().toDbValue());
        ps.setBoolean(4, e.isOrganiseParSite());
        ps.setString(5, e.getLieu());
        setDateTime(ps, 6, e.getDateDebut());
        setDateTime(ps, 7, e.getDateFin());
        ps.setBigDecimal(8, e.getTarif() == null ? BigDecimal.ZERO : e.getTarif());
        ps.setInt(9, e.getCapacity());
        ps.setString(10, normalizeStatut(e.getStatut()));
        ps.setInt(11, e.getIdCreateur());
        ps.setString(12, e.getImageEvenement());

        if (update) {
            ps.setInt(13, e.getId());
        }
    }

    private List<Evenement> getEvenements(String sql) {
        List<Evenement> list = new ArrayList<>();

        try (Statement st = getConn().createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                list.add(mapRow(rs));
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            throw new RuntimeException("Erreur liste événements : " + ex.getMessage());
        }

        return list;
    }

    private Evenement mapRow(ResultSet rs) throws SQLException {
        Evenement e = new Evenement();

        e.setId(rs.getInt("id_evenement"));
        e.setTitre(rs.getString("titre"));
        e.setDescription(rs.getString("description"));
        e.setTypeEvenement(EvenementType.fromDb(rs.getString("type_evenement")));
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

        e.setTarif(rs.getBigDecimal("tarif"));
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

    private String normalizeStatut(String statut) {
        if (statut == null || statut.isBlank()) {
            return "a_venir";
        }

        String value = statut.trim().toLowerCase();
        return switch (value) {
            case "a_venir", "complet", "annule", "termine" -> value;
            default -> "a_venir";
        };
    }
}
