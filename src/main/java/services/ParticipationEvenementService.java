package services;

import models.ParticipationEvenement;
import util.DBConnection;

import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ParticipationEvenementService implements IService<ParticipationEvenement> {

    private final Connection conn;

    public ParticipationEvenementService() {
        this.conn = DBConnection.getInstance().getConn();
    }

    @Override
    public void add(ParticipationEvenement p) {
        String sql = "INSERT INTO participation_evenement " +
                "(id_utilisateur, id_evenement, type_participation, statut, montant_paye, mode_paiement, reference_paiement, date_paiement, checkin_time) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, p.getIdUtilisateur());
            ps.setInt(2, p.getIdEvenement());
            ps.setString(3, p.getTypeParticipation());
            ps.setString(4, p.getStatut());

            if (p.getMontantPaye() == null) {
                ps.setBigDecimal(5, BigDecimal.ZERO);
            } else {
                ps.setBigDecimal(5, p.getMontantPaye());
            }

            ps.setString(6, p.getModePaiement());
            ps.setString(7, p.getReferencePaiement());

            setDateTime(ps, 8, p.getDatePaiement());
            setDateTime(ps, 9, p.getCheckinTime());

            ps.executeUpdate();
            System.out.println("Participation ajoutée avec succès.");

        } catch (SQLException e) {
            System.out.println("Erreur add participation : " + e.getMessage());
        }
    }

    @Override
    public void update(ParticipationEvenement p) {
        String sql = "UPDATE participation_evenement SET " +
                "type_participation=?, statut=?, montant_paye=?, mode_paiement=?, reference_paiement=?, date_paiement=?, checkin_time=? " +
                "WHERE id_participation=?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, p.getTypeParticipation());
            ps.setString(2, p.getStatut());

            if (p.getMontantPaye() == null) {
                ps.setBigDecimal(3, BigDecimal.ZERO);
            } else {
                ps.setBigDecimal(3, p.getMontantPaye());
            }

            ps.setString(4, p.getModePaiement());
            ps.setString(5, p.getReferencePaiement());

            setDateTime(ps, 6, p.getDatePaiement());
            setDateTime(ps, 7, p.getCheckinTime());

            ps.setInt(8, p.getId());

            ps.executeUpdate();
            System.out.println("Participation modifiée avec succès.");

        } catch (SQLException e) {
            System.out.println("Erreur update participation : " + e.getMessage());
        }
    }

    @Override
    public void delete(ParticipationEvenement p) {
        String sql = "DELETE FROM participation_evenement WHERE id_participation=?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, p.getId());
            ps.executeUpdate();
            System.out.println("Participation supprimée avec succès.");

        } catch (SQLException e) {
            System.out.println("Erreur delete participation : " + e.getMessage());
        }
    }

    @Override
    public List<ParticipationEvenement> getAll() {
        String sql = baseSelect() + " ORDER BY p.date_participation DESC";
        return getParticipations(sql);
    }

    public ParticipationEvenement getById(int idParticipation) {
        String sql = baseSelect() + " WHERE p.id_participation=?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idParticipation);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
            }

        } catch (SQLException e) {
            System.out.println("Erreur getById participation : " + e.getMessage());
        }

        return null;
    }

    public List<ParticipationEvenement> getByUtilisateur(int idUtilisateur) {
        List<ParticipationEvenement> list = new ArrayList<>();

        String sql = baseSelect() +
                " WHERE p.id_utilisateur=? ORDER BY p.date_participation DESC";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idUtilisateur);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRow(rs));
                }
            }

        } catch (SQLException e) {
            System.out.println("Erreur getByUtilisateur participation : " + e.getMessage());
        }

        return list;
    }

    public List<ParticipationEvenement> getByEvenement(int idEvenement) {
        List<ParticipationEvenement> list = new ArrayList<>();

        String sql = baseSelect() +
                " WHERE p.id_evenement=? ORDER BY p.date_participation DESC";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idEvenement);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRow(rs));
                }
            }

        } catch (SQLException e) {
            System.out.println("Erreur getByEvenement participation : " + e.getMessage());
        }

        return list;
    }

    public boolean estDejaInscrit(int idUtilisateur, int idEvenement) {
        String sql = "SELECT COUNT(*) FROM participation_evenement WHERE id_utilisateur=? AND id_evenement=?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idUtilisateur);
            ps.setInt(2, idEvenement);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }

        } catch (SQLException e) {
            System.out.println("Erreur estDejaInscrit : " + e.getMessage());
        }

        return false;
    }

    public void participerEvenementSite(int idUtilisateur, int idEvenement , int capacity) {
        if (estDejaInscrit(idUtilisateur, idEvenement)) {
            System.out.println("Utilisateur déjà inscrit ou intéressé à cet événement.");
            return;
        }
        String sql = "SELECT organise_par_site, tarif, capacity FROM evenement WHERE id_evenement=?";
        EvenementInfo info = getEvenementInfo(idEvenement);

        if (info == null) {
            System.out.println("Événement introuvable.");
            return;
        }

        if (!info.organiseParSite) {
            interesserEvenementExterne(idUtilisateur, idEvenement);
            return;
        }

        String statut;
        BigDecimal montantPaye = BigDecimal.ZERO;

        if (info.tarif.compareTo(BigDecimal.ZERO) > 0) {
            statut = "en_attente_paiement";
        } else {
            statut = "confirmee";
        }

        ParticipationEvenement p = new ParticipationEvenement(
                idUtilisateur,
                idEvenement,
                "participation",
                statut,
                montantPaye
        );

        add(p);
    }

    public void interesserEvenementExterne(int idUtilisateur, int idEvenement) {
        if (estDejaInscrit(idUtilisateur, idEvenement)) {
            System.out.println("Utilisateur déjà intéressé à cet événement.");
            return;
        }

        ParticipationEvenement p = new ParticipationEvenement(
                idUtilisateur,
                idEvenement,
                "interesse",
                "interesse",
                BigDecimal.ZERO
        );

        add(p);
    }

    public void confirmerPaiement(int idParticipation, BigDecimal montantPaye, String modePaiement, String referencePaiement) {
        String sql = "UPDATE participation_evenement SET statut='confirmee', montant_paye=?, mode_paiement=?, reference_paiement=?, date_paiement=? " +
                "WHERE id_participation=?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setBigDecimal(1, montantPaye);
            ps.setString(2, modePaiement);
            ps.setString(3, referencePaiement);
            ps.setTimestamp(4, Timestamp.valueOf(LocalDateTime.now()));
            ps.setInt(5, idParticipation);

            ps.executeUpdate();
            System.out.println("Paiement confirmé avec succès.");

        } catch (SQLException e) {
            System.out.println("Erreur confirmerPaiement : " + e.getMessage());
        }
    }

    public void annulerParticipation(int idParticipation) {
        String sql = "UPDATE participation_evenement SET statut='annulee' WHERE id_participation=?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idParticipation);
            ps.executeUpdate();
            System.out.println("Participation annulée.");

        } catch (SQLException e) {
            System.out.println("Erreur annulerParticipation : " + e.getMessage());
        }
    }

    public void marquerPresent(int idParticipation) {
        String sql = "UPDATE participation_evenement SET statut='present', checkin_time=? WHERE id_participation=?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setTimestamp(1, Timestamp.valueOf(LocalDateTime.now()));
            ps.setInt(2, idParticipation);

            ps.executeUpdate();
            System.out.println("Participant marqué présent.");

        } catch (SQLException e) {
            System.out.println("Erreur marquerPresent : " + e.getMessage());
        }
    }

    public void marquerAbsent(int idParticipation) {
        String sql = "UPDATE participation_evenement SET statut='absent' WHERE id_participation=?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idParticipation);
            ps.executeUpdate();
            System.out.println("Participant marqué absent.");

        } catch (SQLException e) {
            System.out.println("Erreur marquerAbsent : " + e.getMessage());
        }
    }

    private List<ParticipationEvenement> getParticipations(String sql) {
        List<ParticipationEvenement> list = new ArrayList<>();

        try (Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                list.add(mapRow(rs));
            }

        } catch (SQLException e) {
            System.out.println("Erreur getParticipations : " + e.getMessage());
        }

        return list;
    }

    private String baseSelect() {
        return "SELECT p.*, CONCAT(u.nom, ' ', u.prenom) AS nom_utilisateur, e.titre AS titre_evenement " +
                "FROM participation_evenement p " +
                "JOIN utilisateur u ON p.id_utilisateur = u.id_utilisateur " +
                "JOIN evenement e ON p.id_evenement = e.id_evenement";
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

        Timestamp dateParticipation = rs.getTimestamp("date_participation");
        if (dateParticipation != null) {
            p.setDateParticipation(dateParticipation.toLocalDateTime());
        }

        Timestamp datePaiement = rs.getTimestamp("date_paiement");
        if (datePaiement != null) {
            p.setDatePaiement(datePaiement.toLocalDateTime());
        }

        Timestamp checkinTime = rs.getTimestamp("checkin_time");
        if (checkinTime != null) {
            p.setCheckinTime(checkinTime.toLocalDateTime());
        }

        p.setNomUtilisateur(rs.getString("nom_utilisateur"));
        p.setTitreEvenement(rs.getString("titre_evenement"));

        return p;
    }

    private void setDateTime(PreparedStatement ps, int index, LocalDateTime value) throws SQLException {
        if (value == null) {
            ps.setNull(index, Types.TIMESTAMP);
        } else {
            ps.setTimestamp(index, Timestamp.valueOf(value));
        }
    }

    private EvenementInfo getEvenementInfo(int idEvenement) {
        String sql = "SELECT organise_par_site, tarif FROM evenement WHERE id_evenement=?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idEvenement);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    EvenementInfo info = new EvenementInfo();
                    info.organiseParSite = rs.getBoolean("organise_par_site");
                    info.tarif = rs.getBigDecimal("tarif");

                    if (info.tarif == null) {
                        info.tarif = BigDecimal.ZERO;
                    }

                    return info;
                }
            }

        } catch (SQLException e) {
            System.out.println("Erreur getEvenementInfo : " + e.getMessage());
        }

        return null;
    }

    private static class EvenementInfo {
        boolean organiseParSite;
        BigDecimal tarif;
    }
    public List<ParticipationEvenement> getParticipantsByEvenement(int idEvenement) {
        List<ParticipationEvenement> list = new ArrayList<>();

        String sql = baseSelect() +
                " WHERE p.id_evenement=? AND p.type_participation='participation' " +
                " ORDER BY p.date_participation DESC";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idEvenement);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRow(rs));
                }
            }

        } catch (SQLException e) {
            System.out.println("Erreur getParticipantsByEvenement : " + e.getMessage());
        }

        return list;
    }
    public List<ParticipationEvenement> getInteressesByEvenement(int idEvenement) {
        List<ParticipationEvenement> list = new ArrayList<>();

        String sql = baseSelect() +
                " WHERE p.id_evenement=? AND p.type_participation='interesse' " +
                " ORDER BY p.date_participation DESC";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idEvenement);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRow(rs));
                }
            }

        } catch (SQLException e) {
            System.out.println("Erreur getInteressesByEvenement : " + e.getMessage());
        }

        return list;
    }
    public int compterParticipantsConfirmes(int idEvenement) {
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

        } catch (SQLException e) {
            System.out.println("Erreur compterParticipantsConfirmes : " + e.getMessage());
        }

        return 0;
    }
}