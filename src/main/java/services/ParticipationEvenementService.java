package services;

import models.Evenement;
import models.ParticipationEvenement;
import util.DBConnection;

import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ParticipationEvenementService implements IService<ParticipationEvenement> {

    private final EvenementService evenementService = new EvenementService();

    public ParticipationEvenementService() {
    }

    private Connection getConn() {
        return DBConnection.getInstance().getConn();
    }

    @Override
    public void add(ParticipationEvenement p) {
        String sql = "INSERT INTO participation_evenement " +
                "(id_utilisateur, id_evenement, type_participation, statut, montant_paye, mode_paiement, reference_paiement, date_paiement, checkin_time) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement ps = getConn().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, p.getIdUtilisateur());
            ps.setInt(2, p.getIdEvenement());
            ps.setString(3, normalizeTypeParticipation(p.getTypeParticipation()));
            ps.setString(4, normalizeStatut(p.getStatut()));
            ps.setBigDecimal(5, p.getMontantPaye() == null ? BigDecimal.ZERO : p.getMontantPaye());
            ps.setString(6, p.getModePaiement());
            ps.setString(7, p.getReferencePaiement());
            setDateTime(ps, 8, p.getDatePaiement());
            setDateTime(ps, 9, p.getCheckinTime());

            ps.executeUpdate();

            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    p.setId(keys.getInt(1));
                }
            }

            System.out.println("Participation ajoutée avec succès. ID = " + p.getId());
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Erreur add participation : " + e.getMessage());
        }
    }

    @Override
    public void update(ParticipationEvenement p) {
        String sql = "UPDATE participation_evenement SET " +
                "type_participation=?, statut=?, montant_paye=?, mode_paiement=?, reference_paiement=?, date_paiement=?, checkin_time=? " +
                "WHERE id_participation=?";

        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setString(1, normalizeTypeParticipation(p.getTypeParticipation()));
            ps.setString(2, normalizeStatut(p.getStatut()));
            ps.setBigDecimal(3, p.getMontantPaye() == null ? BigDecimal.ZERO : p.getMontantPaye());
            ps.setString(4, p.getModePaiement());
            ps.setString(5, p.getReferencePaiement());
            setDateTime(ps, 6, p.getDatePaiement());
            setDateTime(ps, 7, p.getCheckinTime());
            ps.setInt(8, p.getId());

            ps.executeUpdate();
            System.out.println("Participation modifiée avec succès.");
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Erreur update participation : " + e.getMessage());
        }
    }

    @Override
    public void delete(ParticipationEvenement p) {
        String sql = "DELETE FROM participation_evenement WHERE id_participation=?";

        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setInt(1, p.getId());
            ps.executeUpdate();
            System.out.println("Participation supprimée avec succès.");
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Erreur delete participation : " + e.getMessage());
        }
    }

    @Override
    public List<ParticipationEvenement> getAll() {
        String sql = baseSelect() + " ORDER BY p.date_participation DESC";
        return executeList(sql);
    }

    public ParticipationEvenement getById(int idParticipation) {
        String sql = baseSelect() + " WHERE p.id_participation=?";

        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setInt(1, idParticipation);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Erreur getById participation : " + e.getMessage());
        }

        return null;
    }

    public List<ParticipationEvenement> getByUtilisateur(int idUtilisateur) {
        List<ParticipationEvenement> list = new ArrayList<>();
        String sql = baseSelect() + " WHERE p.id_utilisateur=? ORDER BY p.date_participation DESC";

        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setInt(1, idUtilisateur);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Erreur getByUtilisateur participation : " + e.getMessage());
        }

        return list;
    }

    public List<ParticipationEvenement> getByEvenement(int idEvenement) {
        List<ParticipationEvenement> list = new ArrayList<>();
        String sql = baseSelect() + " WHERE p.id_evenement=? ORDER BY p.date_participation DESC";

        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setInt(1, idEvenement);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Erreur getByEvenement participation : " + e.getMessage());
        }

        return list;
    }

    public List<ParticipationEvenement> getParticipants(int idEvenement) {
        return getByEvenementAndType(idEvenement, "participation");
    }

    public List<ParticipationEvenement> getInteresses(int idEvenement) {
        return getByEvenementAndType(idEvenement, "interesse");
    }

    public boolean estDejaInscrit(int idUtilisateur, int idEvenement) {
        String sql = "SELECT COUNT(*) FROM participation_evenement WHERE id_utilisateur=? AND id_evenement=?";

        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setInt(1, idUtilisateur);
            ps.setInt(2, idEvenement);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() && rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Erreur vérification participation : " + e.getMessage());
        }
    }

    public ParticipationEvenement getByUtilisateurAndEvenement(int idUtilisateur, int idEvenement) {
        String sql = baseSelect() + " WHERE p.id_utilisateur=? AND p.id_evenement=? LIMIT 1";

        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setInt(1, idUtilisateur);
            ps.setInt(2, idEvenement);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Erreur lecture participation : " + e.getMessage());
        }

        return null;
    }

    public void participer(int idUtilisateur, int idEvenement) {
        if (estDejaInscrit(idUtilisateur, idEvenement)) {
            throw new RuntimeException("Vous avez déjà une participation ou un intérêt pour cet événement.");
        }

        Evenement evenement = evenementService.getById(idEvenement);
        if (evenement == null) {
            throw new RuntimeException("Événement introuvable.");
        }

        if (!evenement.isOrganiseParSite()) {
            interesser(idUtilisateur, idEvenement);
            return;
        }

        String statut;
        if (evenementService.estComplet(evenement)) {
            statut = "waitlist";
        } else if (evenement.getTarif() != null && evenement.getTarif().compareTo(BigDecimal.ZERO) > 0) {
            statut = "en_attente_paiement";
        } else {
            statut = "confirmee";
        }

        ParticipationEvenement p = new ParticipationEvenement(idUtilisateur, idEvenement, "participation", statut);
        p.setMontantPaye(BigDecimal.ZERO);
        add(p);
    }

    public void interesser(int idUtilisateur, int idEvenement) {
        if (estDejaInscrit(idUtilisateur, idEvenement)) {
            throw new RuntimeException("Vous avez déjà marqué cet événement.");
        }

        ParticipationEvenement p = new ParticipationEvenement(idUtilisateur, idEvenement, "interesse", "interesse");
        p.setMontantPaye(BigDecimal.ZERO);
        add(p);
    }

    public void confirmerPaiement(int idParticipation, String modePaiement, String referencePaiement) {
        ParticipationEvenement p = getById(idParticipation);
        if (p == null) {
            throw new RuntimeException("Participation introuvable.");
        }

        Evenement e = evenementService.getById(p.getIdEvenement());
        BigDecimal montant = e == null || e.getTarif() == null ? BigDecimal.ZERO : e.getTarif();

        String sql = "UPDATE participation_evenement SET statut='confirmee', montant_paye=?, mode_paiement=?, reference_paiement=?, date_paiement=? WHERE id_participation=?";

        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setBigDecimal(1, montant);
            ps.setString(2, modePaiement);
            ps.setString(3, referencePaiement);
            ps.setTimestamp(4, Timestamp.valueOf(LocalDateTime.now()));
            ps.setInt(5, idParticipation);
            ps.executeUpdate();
        } catch (SQLException ex) {
            ex.printStackTrace();
            throw new RuntimeException("Erreur confirmation paiement : " + ex.getMessage());
        }
    }

    public void marquerPresent(int idParticipation) {
        String sql = "UPDATE participation_evenement SET statut='present', checkin_time=? WHERE id_participation=?";

        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setTimestamp(1, Timestamp.valueOf(LocalDateTime.now()));
            ps.setInt(2, idParticipation);
            ps.executeUpdate();
        } catch (SQLException ex) {
            ex.printStackTrace();
            throw new RuntimeException("Erreur check-in : " + ex.getMessage());
        }
    }

    public void marquerAbsent(int idParticipation) {
        changerStatut(idParticipation, "absent");
    }

    public void annuler(int idParticipation) {
        changerStatut(idParticipation, "annulee");
    }

    public void changerStatut(int idParticipation, String statut) {
        String sql = "UPDATE participation_evenement SET statut=? WHERE id_participation=?";

        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setString(1, normalizeStatut(statut));
            ps.setInt(2, idParticipation);
            ps.executeUpdate();
        } catch (SQLException ex) {
            ex.printStackTrace();
            throw new RuntimeException("Erreur changement statut : " + ex.getMessage());
        }
    }

    public int compterParticipantsConfirmes(int idEvenement) {
        String sql = "SELECT COUNT(*) FROM participation_evenement " +
                "WHERE id_evenement=? AND type_participation='participation' AND statut IN ('confirmee', 'present')";

        return countByEvent(sql, idEvenement);
    }

    public int compterInteresses(int idEvenement) {
        String sql = "SELECT COUNT(*) FROM participation_evenement WHERE id_evenement=? AND type_participation='interesse'";
        return countByEvent(sql, idEvenement);
    }

    private int countByEvent(String sql, int idEvenement) {
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setInt(1, idEvenement);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getInt(1) : 0;
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            throw new RuntimeException("Erreur count participation : " + ex.getMessage());
        }
    }

    private List<ParticipationEvenement> getByEvenementAndType(int idEvenement, String type) {
        List<ParticipationEvenement> list = new ArrayList<>();
        String sql = baseSelect() + " WHERE p.id_evenement=? AND p.type_participation=? ORDER BY p.date_participation DESC";

        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setInt(1, idEvenement);
            ps.setString(2, type);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Erreur getByEvenementAndType : " + e.getMessage());
        }

        return list;
    }

    private List<ParticipationEvenement> executeList(String sql) {
        List<ParticipationEvenement> list = new ArrayList<>();

        try (Statement st = getConn().createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                list.add(mapRow(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Erreur liste participation : " + e.getMessage());
        }

        return list;
    }

    private String baseSelect() {
        return "SELECT p.*, CONCAT(COALESCE(u.prenom,''), ' ', COALESCE(u.nom,'')) AS nom_utilisateur, e.titre AS titre_evenement " +
                "FROM participation_evenement p " +
                "LEFT JOIN utilisateur u ON p.id_utilisateur = u.id_utilisateur " +
                "LEFT JOIN evenement e ON p.id_evenement = e.id_evenement";
    }

    private ParticipationEvenement mapRow(ResultSet rs) throws SQLException {
        ParticipationEvenement p = new ParticipationEvenement();

        p.setId(rs.getInt("id_participation"));
        p.setIdUtilisateur(rs.getInt("id_utilisateur"));
        p.setIdEvenement(rs.getInt("id_evenement"));
        p.setTypeParticipation(rs.getString("type_participation"));
        p.setStatut(rs.getString("statut"));
        p.setMontantPaye(rs.getBigDecimal("montant_paye"));
        p.setModePaiement(rs.getString("mode_paiement"));
        p.setReferencePaiement(rs.getString("reference_paiement"));

        Timestamp datePaiement = rs.getTimestamp("date_paiement");
        if (datePaiement != null) {
            p.setDatePaiement(datePaiement.toLocalDateTime());
        }

        Timestamp dateParticipation = rs.getTimestamp("date_participation");
        if (dateParticipation != null) {
            p.setDateParticipation(dateParticipation.toLocalDateTime());
        }

        Timestamp checkin = rs.getTimestamp("checkin_time");
        if (checkin != null) {
            p.setCheckinTime(checkin.toLocalDateTime());
        }

        try {
            p.setNomUtilisateur(rs.getString("nom_utilisateur"));
        } catch (SQLException ignored) {
        }

        try {
            p.setTitreEvenement(rs.getString("titre_evenement"));
        } catch (SQLException ignored) {
        }

        return p;
    }

    private void setDateTime(PreparedStatement ps, int index, LocalDateTime value) throws SQLException {
        if (value == null) {
            ps.setNull(index, Types.TIMESTAMP);
        } else {
            ps.setTimestamp(index, Timestamp.valueOf(value));
        }
    }

    private String normalizeTypeParticipation(String type) {
        if (type == null || type.isBlank()) {
            return "participation";
        }

        String value = type.trim().toLowerCase();
        return "interesse".equals(value) ? "interesse" : "participation";
    }

    private String normalizeStatut(String statut) {
        if (statut == null || statut.isBlank()) {
            return "confirmee";
        }

        String value = statut.trim().toLowerCase();
        return switch (value) {
            case "interesse", "en_attente_paiement", "confirmee", "waitlist", "present", "absent", "annulee" -> value;
            default -> "confirmee";
        };
    }
}
