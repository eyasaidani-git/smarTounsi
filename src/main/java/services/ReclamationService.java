package services;

import models.Reclamation;
import util.DBConnection;

import java.sql.*;
import java.text.Normalizer;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ReclamationService implements IService<Reclamation> {
    public static final String STATUT_EN_ATTENTE = "en_attente";
    public static final String STATUT_TRAITEE = "traitee";
    public static final String STATUT_REJETEE = "rejetee";

    private static final String AUTO_REJECT_MESSAGE = "Contenu rejeté automatiquement: mots interdits.";
    private static final String[] BAD_WORDS = {
            "merde", "connard", "connasse", "salope", "pute", "batard",
            "fuck", "shit", "bitch", "asshole"
    };

    private final Connection conn;

    public ReclamationService() {
        this.conn = DBConnection.getInstance().getConn();
        ensureTable();
    }

    private void ensureTable() {
        String sql = "CREATE TABLE IF NOT EXISTS reclamation (" +
                "id_reclamation INT AUTO_INCREMENT PRIMARY KEY, " +
                "id_utilisateur INT NOT NULL, " +
                "sujet VARCHAR(150) NOT NULL, " +
                "contenu TEXT NOT NULL, " +
                "statut VARCHAR(20) NOT NULL DEFAULT 'en_attente', " +
                "motif_rejet VARCHAR(255) NULL, " +
                "reponse_admin TEXT NULL, " +
                "id_admin INT NULL, " +
                "date_creation TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
                "date_traitement TIMESTAMP NULL, " +
                "INDEX idx_reclamation_user (id_utilisateur), " +
                "INDEX idx_reclamation_statut (statut)" +
                ")";

        try (Statement st = conn.createStatement()) {
            st.execute(sql);
        } catch (SQLException e) {
            throw new RuntimeException("Erreur création table reclamation : " + e.getMessage(), e);
        }
    }

    @Override
    public void add(Reclamation r) {
        boolean rejected = containsBadWords(r.getSujet() + " " + r.getContenu());
        String statut = rejected ? STATUT_REJETEE : STATUT_EN_ATTENTE;
        String motif = rejected ? AUTO_REJECT_MESSAGE : null;
        LocalDateTime dateTraitement = rejected ? LocalDateTime.now() : null;

        String sql = "INSERT INTO reclamation " +
                "(id_utilisateur, sujet, contenu, statut, motif_rejet, date_traitement) " +
                "VALUES (?, ?, ?, ?, ?, ?)";

        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, r.getIdUtilisateur());
            ps.setString(2, r.getSujet());
            ps.setString(3, r.getContenu());
            ps.setString(4, statut);
            ps.setString(5, motif);
            setDateTime(ps, 6, dateTraitement);
            ps.executeUpdate();

            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    r.setId(keys.getInt(1));
                }
            }

            r.setStatut(statut);
            r.setMotifRejet(motif);
            r.setDateTraitement(dateTraitement);
        } catch (SQLException e) {
            throw new RuntimeException("Erreur ajout réclamation : " + e.getMessage(), e);
        }
    }

    @Override
    public void update(Reclamation r) {
        String sql = "UPDATE reclamation SET sujet=?, contenu=?, statut=?, motif_rejet=?, reponse_admin=?, id_admin=?, date_traitement=? " +
                "WHERE id_reclamation=?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, r.getSujet());
            ps.setString(2, r.getContenu());
            ps.setString(3, normalizeStatut(r.getStatut()));
            ps.setString(4, r.getMotifRejet());
            ps.setString(5, r.getReponseAdmin());
            setNullableInt(ps, 6, r.getIdAdmin());
            setDateTime(ps, 7, r.getDateTraitement());
            ps.setInt(8, r.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erreur modification réclamation : " + e.getMessage(), e);
        }
    }

    @Override
    public void delete(Reclamation r) {
        String sql = "DELETE FROM reclamation WHERE id_reclamation=?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, r.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erreur suppression réclamation : " + e.getMessage(), e);
        }
    }

    @Override
    public List<Reclamation> getAll() {
        String sql = baseSelect() + " ORDER BY r.date_creation DESC";
        return executeList(sql);
    }

    public List<Reclamation> getByUtilisateur(int idUtilisateur) {
        String sql = baseSelect() + " WHERE r.id_utilisateur=? ORDER BY r.date_creation DESC";
        List<Reclamation> list = new ArrayList<>();

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idUtilisateur);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur liste réclamations utilisateur : " + e.getMessage(), e);
        }

        return list;
    }

    public void traiter(int idReclamation, int idAdmin, String reponseAdmin) {
        changerStatut(idReclamation, STATUT_TRAITEE, idAdmin, reponseAdmin, null, LocalDateTime.now());
    }

    public void remettreEnAttente(int idReclamation, int idAdmin) {
        changerStatut(idReclamation, STATUT_EN_ATTENTE, idAdmin, null, null, null);
    }

    public void rejeter(int idReclamation, int idAdmin, String motif) {
        changerStatut(idReclamation, STATUT_REJETEE, idAdmin, null,
                motif == null || motif.isBlank() ? "Réclamation rejetée par l'admin." : motif.trim(),
                LocalDateTime.now());
    }

    private void changerStatut(int idReclamation, String statut, int idAdmin, String reponseAdmin,
                               String motifRejet, LocalDateTime dateTraitement) {
        String sql = "UPDATE reclamation SET statut=?, id_admin=?, reponse_admin=?, motif_rejet=?, date_traitement=? " +
                "WHERE id_reclamation=?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, normalizeStatut(statut));
            ps.setInt(2, idAdmin);
            ps.setString(3, reponseAdmin);
            ps.setString(4, motifRejet);
            setDateTime(ps, 5, dateTraitement);
            ps.setInt(6, idReclamation);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erreur traitement réclamation : " + e.getMessage(), e);
        }
    }

    private List<Reclamation> executeList(String sql) {
        List<Reclamation> list = new ArrayList<>();

        try (Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                list.add(mapRow(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur liste réclamations : " + e.getMessage(), e);
        }

        return list;
    }

    private String baseSelect() {
        return "SELECT r.*, CONCAT(COALESCE(u.prenom,''), ' ', COALESCE(u.nom,'')) AS nom_utilisateur, " +
                "u.email AS email_utilisateur " +
                "FROM reclamation r LEFT JOIN utilisateur u ON r.id_utilisateur = u.id_utilisateur";
    }

    private Reclamation mapRow(ResultSet rs) throws SQLException {
        Reclamation r = new Reclamation();
        r.setId(rs.getInt("id_reclamation"));
        r.setIdUtilisateur(rs.getInt("id_utilisateur"));
        r.setSujet(rs.getString("sujet"));
        r.setContenu(rs.getString("contenu"));
        r.setStatut(rs.getString("statut"));
        r.setMotifRejet(rs.getString("motif_rejet"));
        r.setReponseAdmin(rs.getString("reponse_admin"));

        int idAdmin = rs.getInt("id_admin");
        r.setIdAdmin(rs.wasNull() ? null : idAdmin);

        Timestamp dateCreation = rs.getTimestamp("date_creation");
        r.setDateCreation(dateCreation == null ? null : dateCreation.toLocalDateTime());

        Timestamp dateTraitement = rs.getTimestamp("date_traitement");
        r.setDateTraitement(dateTraitement == null ? null : dateTraitement.toLocalDateTime());

        try {
            r.setNomUtilisateur(rs.getString("nom_utilisateur"));
            r.setEmailUtilisateur(rs.getString("email_utilisateur"));
        } catch (SQLException ignored) {
        }

        return r;
    }

    private boolean containsBadWords(String value) {
        String text = normalizeText(value);

        for (String badWord : BAD_WORDS) {
            String word = normalizeText(badWord);
            if (text.matches(".*\\b" + java.util.regex.Pattern.quote(word) + "\\b.*")) {
                return true;
            }
        }

        return false;
    }

    private String normalizeText(String value) {
        if (value == null) {
            return "";
        }

        String normalized = Normalizer.normalize(value, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "");
        return normalized.toLowerCase(Locale.ROOT);
    }

    private String normalizeStatut(String statut) {
        if (statut == null || statut.isBlank()) {
            return STATUT_EN_ATTENTE;
        }

        String value = statut.trim().toLowerCase(Locale.ROOT);
        return switch (value) {
            case STATUT_TRAITEE, STATUT_REJETEE -> value;
            default -> STATUT_EN_ATTENTE;
        };
    }

    private void setDateTime(PreparedStatement ps, int index, LocalDateTime value) throws SQLException {
        if (value == null) {
            ps.setNull(index, Types.TIMESTAMP);
        } else {
            ps.setTimestamp(index, Timestamp.valueOf(value));
        }
    }

    private void setNullableInt(PreparedStatement ps, int index, Integer value) throws SQLException {
        if (value == null) {
            ps.setNull(index, Types.INTEGER);
        } else {
            ps.setInt(index, value);
        }
    }
}
