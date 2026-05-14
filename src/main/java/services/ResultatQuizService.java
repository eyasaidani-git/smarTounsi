package services;

import util.DBConnection;

import java.sql.*;

/**
 * ResultatQuizService — SmarTounsi
 * Sauvegarde et récupère les résultats des quiz passés.
 */
public class ResultatQuizService {

    private final Connection conn;

    public ResultatQuizService() {
        this.conn = DBConnection.getInstance().getConn();
    }

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
    }
}