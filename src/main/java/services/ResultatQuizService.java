package services;

<<<<<<< HEAD
import models.ResultatQuiz;
import util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ResultatQuizService implements IService<ResultatQuiz> {
=======
import util.DBConnection;

import java.sql.*;

/**
 * ResultatQuizService — SmarTounsi
 * Sauvegarde et récupère les résultats des quiz passés.
 */
public class ResultatQuizService {
>>>>>>> origin/gestionikram

    private final Connection conn;

    public ResultatQuizService() {
        this.conn = DBConnection.getInstance().getConn();
    }

<<<<<<< HEAD
    @Override
    public void add(ResultatQuiz r) {
        String sql = "INSERT INTO resultat_quiz (id_quiz, id_utilisateur, score_obtenu, temps_passe) " +
                "VALUES (?, ?, ?, ?)";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, r.getIdQuiz());
            ps.setInt(2, r.getIdUtilisateur());
            ps.setInt(3, r.getScoreObtenu());
            setNullableInt(ps, 4, r.getTempsPasse());

            ps.executeUpdate();
            System.out.println("Résultat quiz ajouté avec succès.");

        } catch (SQLException e) {
            System.out.println("Erreur add résultat quiz : " + e.getMessage());
        }
    }

    public int addAndReturnId(ResultatQuiz r) {
        String sql = "INSERT INTO resultat_quiz (id_quiz, id_utilisateur, score_obtenu, temps_passe) " +
                "VALUES (?, ?, ?, ?)";

        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, r.getIdQuiz());
            ps.setInt(2, r.getIdUtilisateur());
            ps.setInt(3, r.getScoreObtenu());
            setNullableInt(ps, 4, r.getTempsPasse());

            ps.executeUpdate();

            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    return keys.getInt(1);
                }
            }

        } catch (SQLException e) {
            System.out.println("Erreur addAndReturnId résultat quiz : " + e.getMessage());
        }

        return -1;
    }

    @Override
    public void update(ResultatQuiz r) {
        String sql = "UPDATE resultat_quiz SET score_obtenu=?, temps_passe=? WHERE id_resultat=?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, r.getScoreObtenu());
            setNullableInt(ps, 2, r.getTempsPasse());
            ps.setInt(3, r.getId());

            ps.executeUpdate();
            System.out.println("Résultat quiz modifié avec succès.");

        } catch (SQLException e) {
            System.out.println("Erreur update résultat quiz : " + e.getMessage());
        }
    }

    @Override
    public void delete(ResultatQuiz r) {
        String sql = "DELETE FROM resultat_quiz WHERE id_resultat=?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, r.getId());

            ps.executeUpdate();
            System.out.println("Résultat quiz supprimé avec succès.");

        } catch (SQLException e) {
            System.out.println("Erreur delete résultat quiz : " + e.getMessage());
        }
    }

    @Override
    public List<ResultatQuiz> getAll() {
        List<ResultatQuiz> list = new ArrayList<>();
        String sql = "SELECT * FROM resultat_quiz ORDER BY date_passage DESC";

        try (Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                list.add(mapRow(rs));
            }

        } catch (SQLException e) {
            System.out.println("Erreur getAll résultat quiz : " + e.getMessage());
        }

        return list;
    }

    public List<ResultatQuiz> getByUtilisateur(int idUtilisateur) {
        List<ResultatQuiz> list = new ArrayList<>();
        String sql = "SELECT * FROM resultat_quiz WHERE id_utilisateur=? ORDER BY date_passage DESC";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idUtilisateur);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRow(rs));
                }
            }

        } catch (SQLException e) {
            System.out.println("Erreur getByUtilisateur résultat quiz : " + e.getMessage());
        }

        return list;
    }

    public List<ResultatQuiz> getByQuiz(int idQuiz) {
        List<ResultatQuiz> list = new ArrayList<>();
        String sql = "SELECT * FROM resultat_quiz WHERE id_quiz=? ORDER BY date_passage DESC";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idQuiz);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRow(rs));
                }
            }

        } catch (SQLException e) {
            System.out.println("Erreur getByQuiz résultat quiz : " + e.getMessage());
        }

        return list;
    }

    private ResultatQuiz mapRow(ResultSet rs) throws SQLException {
        ResultatQuiz r = new ResultatQuiz();

        r.setId(rs.getInt("id_resultat"));
        r.setIdQuiz(rs.getInt("id_quiz"));
        r.setIdUtilisateur(rs.getInt("id_utilisateur"));
        r.setScoreObtenu(rs.getInt("score_obtenu"));

        Timestamp datePassage = rs.getTimestamp("date_passage");
        if (datePassage != null) {
            r.setDatePassage(datePassage.toLocalDateTime());
        }

        int tempsPasse = rs.getInt("temps_passe");
        r.setTempsPasse(rs.wasNull() ? null : tempsPasse);

        return r;
    }

    private void setNullableInt(PreparedStatement ps, int index, Integer value) throws SQLException {
        if (value == null) {
            ps.setNull(index, Types.INTEGER);
        } else {
            ps.setInt(index, value);
        }
=======
    /**
     * Enregistre le résultat d'un quiz passé par un utilisateur.
     *
     * @param idQuiz       ID du quiz
     * @param idUtilisateur ID de l'utilisateur (1 par défaut)
     * @param scoreObtenu  score calculé
     * @param tempsPasse   temps passé en secondes
     * @return l'ID du résultat inséré (0 si échec)
     */
    public int sauvegarder(int idQuiz, int idUtilisateur, int scoreObtenu, int tempsPasse) {

        String sql =
                "INSERT INTO resultat_quiz " +
                        "(id_quiz, id_utilisateur, score_obtenu, temps_passe) " +
                        "VALUES (?, ?, ?, ?)";

        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setInt(1, idQuiz);
            ps.setInt(2, idUtilisateur);
            ps.setInt(3, scoreObtenu);
            ps.setInt(4, tempsPasse);

            ps.executeUpdate();

            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) {
                System.out.println("✅ Résultat enregistré (id=" + rs.getInt(1) + ").");
                return rs.getInt(1);
            }

        } catch (SQLException e) {
            System.out.println("❌ Erreur sauvegarde résultat : " + e.getMessage());
        }
        return 0;
    }

    /**
     * Retourne le meilleur score d'un utilisateur pour un quiz donné.
     */
    public int getMeilleurScore(int idQuiz, int idUtilisateur) {

        String sql =
                "SELECT MAX(score_obtenu) FROM resultat_quiz " +
                        "WHERE id_quiz=? AND id_utilisateur=?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idQuiz);
            ps.setInt(2, idUtilisateur);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) {
            System.out.println("❌ Erreur getMeilleurScore : " + e.getMessage());
        }
        return 0;
    }

    /**
     * Compte combien de fois un utilisateur a passé un quiz.
     */
    public int compterTentatives(int idQuiz, int idUtilisateur) {

        String sql =
                "SELECT COUNT(*) FROM resultat_quiz " +
                        "WHERE id_quiz=? AND id_utilisateur=?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idQuiz);
            ps.setInt(2, idUtilisateur);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) {
            System.out.println("❌ Erreur compterTentatives : " + e.getMessage());
        }
        return 0;
>>>>>>> origin/gestionikram
    }
}