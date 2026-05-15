package services;

import models.Quiz;
import util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class QuizService implements IService<Quiz> {

    public QuizService() {
    }

    private Connection getConn() {
        return DBConnection.getInstance().getConn();
    }

    @Override
    public void add(Quiz q) {
        String sql = "INSERT INTO quiz " +
                "(titre, description, id_module, id_createur, temps_limite, score_total, est_actif) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement ps = getConn().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, q.getTitre());
            ps.setString(2, q.getDescription());
            ps.setInt(3, q.getIdModule());
            ps.setInt(4, q.getIdCreateur());
            ps.setInt(5, q.getTempsLimite());
            ps.setInt(6, q.getScoreTotal());
            ps.setBoolean(7, q.isEstActif());

            int rows = ps.executeUpdate();

            if (rows == 0) {
                throw new SQLException("Aucune ligne insérée dans quiz.");
            }

            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    q.setId(keys.getInt(1));
                }
            }

            System.out.println("Quiz ajouté avec succès. ID = " + q.getId());

        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Erreur add quiz : " + e.getMessage());
        }
    }

    public int addAndReturnId(Quiz q) {
        add(q);
        return q.getId();
    }

    @Override
    public void update(Quiz q) {
        String sql = "UPDATE quiz SET titre=?, description=?, id_module=?, temps_limite=?, score_total=?, est_actif=? " +
                "WHERE id_quiz=?";

        try (PreparedStatement ps = getConn().prepareStatement(sql)) {

            ps.setString(1, q.getTitre());
            ps.setString(2, q.getDescription());
            ps.setInt(3, q.getIdModule());
            ps.setInt(4, q.getTempsLimite());
            ps.setInt(5, q.getScoreTotal());
            ps.setBoolean(6, q.isEstActif());
            ps.setInt(7, q.getId());

            ps.executeUpdate();
            System.out.println("Quiz modifié avec succès.");

        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Erreur update quiz : " + e.getMessage());
        }
    }

    @Override
    public void delete(Quiz q) {
        String sql = "DELETE FROM quiz WHERE id_quiz=?";

        try (PreparedStatement ps = getConn().prepareStatement(sql)) {

            ps.setInt(1, q.getId());
            ps.executeUpdate();
            System.out.println("Quiz supprimé avec succès.");

        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Erreur delete quiz : " + e.getMessage());
        }
    }

    @Override
    public List<Quiz> getAll() {
        List<Quiz> list = new ArrayList<>();
        String sql = "SELECT q.*, m.nom_module " +
                "FROM quiz q " +
                "LEFT JOIN modules m ON q.id_module = m.id_module " +
                "WHERE q.est_actif = 1 " +
                "ORDER BY q.date_creation DESC";

        try (Statement st = getConn().createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                list.add(mapRow(rs));
            }

        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Erreur getAll quiz : " + e.getMessage());
        }

        return list;
    }

    public Quiz getById(int idQuiz) {
        String sql = "SELECT q.*, m.nom_module " +
                "FROM quiz q " +
                "LEFT JOIN modules m ON q.id_module = m.id_module " +
                "WHERE q.id_quiz=?";

        try (PreparedStatement ps = getConn().prepareStatement(sql)) {

            ps.setInt(1, idQuiz);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Erreur getById quiz : " + e.getMessage());
        }

        return null;
    }

    public List<Quiz> rechercher(String terme, String nomModule) {
        List<Quiz> list = new ArrayList<>();
        String sql = "SELECT q.*, m.nom_module " +
                "FROM quiz q " +
                "LEFT JOIN modules m ON q.id_module = m.id_module " +
                "WHERE q.est_actif = 1 " +
                "AND (? IS NULL OR q.titre LIKE ? OR m.nom_module LIKE ?) " +
                "AND (? IS NULL OR m.nom_module = ?) " +
                "ORDER BY q.date_creation DESC";

        String like = (terme == null || terme.isBlank()) ? null : "%" + terme.trim() + "%";
        String module = (nomModule == null || nomModule.isBlank()
                || "Toutes les matières".equals(nomModule)) ? null : nomModule;

        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setString(1, like);
            ps.setString(2, like);
            ps.setString(3, like);
            ps.setString(4, module);
            ps.setString(5, module);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRow(rs));
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Erreur recherche quiz : " + e.getMessage());
        }

        return list;
    }

    public int compterQuestions(int idQuiz) {
        String sql = "SELECT COUNT(*) FROM question WHERE id_quiz=?";

        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setInt(1, idQuiz);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Erreur comptage questions : " + e.getMessage());
        }

        return 0;
    }

    public List<Quiz> getByCreateur(int idCreateur) {
        List<Quiz> list = new ArrayList<>();
        String sql = "SELECT q.*, m.nom_module " +
                "FROM quiz q " +
                "LEFT JOIN modules m ON q.id_module = m.id_module " +
                "WHERE q.id_createur=? " +
                "ORDER BY q.date_creation DESC";

        try (PreparedStatement ps = getConn().prepareStatement(sql)) {

            ps.setInt(1, idCreateur);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRow(rs));
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Erreur getByCreateur quiz : " + e.getMessage());
        }

        return list;
    }

    private Quiz mapRow(ResultSet rs) throws SQLException {
        Quiz q = new Quiz();

        q.setId(rs.getInt("id_quiz"));
        q.setTitre(rs.getString("titre"));
        q.setDescription(rs.getString("description"));
        q.setIdModule(rs.getInt("id_module"));
        q.setIdCreateur(rs.getInt("id_createur"));
        q.setTempsLimite(rs.getInt("temps_limite"));
        q.setScoreTotal(rs.getInt("score_total"));
        q.setEstActif(rs.getBoolean("est_actif"));

        Timestamp dateCreation = rs.getTimestamp("date_creation");
        if (dateCreation != null) {
            q.setDateCreation(dateCreation.toLocalDateTime());
        }

        try {
            q.setNomModule(rs.getString("nom_module"));
        } catch (SQLException ignored) {
            // Some queries do not select the module name.
        }

        return q;
    }
}
