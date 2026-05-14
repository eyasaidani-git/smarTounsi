package services;

import models.Quiz;
import util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

<<<<<<< HEAD
public class QuizService implements IService<Quiz> {
=======
/**
 * QuizService — SmarTounsi
 * Corrigé : getAll() charge les quiz depuis la base de données.
 */
public class QuizService implements IService<Quiz> {

>>>>>>> origin/gestionikram
    private final Connection conn;

    public QuizService() {
        this.conn = DBConnection.getInstance().getConn();
    }

<<<<<<< HEAD
    @Override
    public void add(Quiz q) {
        String sql = "INSERT INTO quiz (titre, description, id_module, id_createur, temps_limite, score_total, est_actif) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
=======
    // ================================================
    // ADD
    // ================================================
    @Override
    public void add(Quiz q) {

        String sql =
                "INSERT INTO quiz " +
                        "(titre, description, id_module, id_createur, temps_limite, score_total, est_actif) " +
                        "VALUES (?, ?, ?, ?, ?, ?, ?)";

        try {
            PreparedStatement ps =
                    conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

>>>>>>> origin/gestionikram
            ps.setString(1, q.getTitre());
            ps.setString(2, q.getDescription());
            ps.setInt(3, q.getIdModule());
            ps.setInt(4, q.getIdCreateur());
<<<<<<< HEAD
            setNullableInt(ps, 5, q.getTempsLimite());
            ps.setInt(6, q.getScoreTotal());
            ps.setBoolean(7, q.isEstActif());
            ps.executeUpdate();
            System.out.println("Quiz ajoute avec succes.");
        } catch (SQLException e) {
            System.out.println("Erreur add quiz : " + e.getMessage());
        }
    }

    @Override
    public void update(Quiz q) {
        String sql = "UPDATE quiz SET titre=?, description=?, id_module=?, temps_limite=?, score_total=?, est_actif=? " +
                "WHERE id_quiz=?";
=======
            ps.setInt(5, q.getTempsLimite());
            ps.setInt(6, q.getScoreTotal());
            ps.setBoolean(7, q.isEstActif());

            ps.executeUpdate();

            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) {
                q.setId(rs.getInt(1));
            }

            System.out.println("✅ Quiz ajouté avec succès. ID=" + q.getId());

        } catch (SQLException e) {
            System.out.println("❌ Erreur add quiz : " + e.getMessage());
        }
    }

    // ================================================
    // UPDATE
    // ================================================
    @Override
    public void update(Quiz q) {

        String sql =
                "UPDATE quiz SET titre=?, description=?, id_module=?, " +
                        "temps_limite=?, score_total=?, est_actif=? WHERE id_quiz=?";
>>>>>>> origin/gestionikram

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, q.getTitre());
            ps.setString(2, q.getDescription());
            ps.setInt(3, q.getIdModule());
<<<<<<< HEAD
            setNullableInt(ps, 4, q.getTempsLimite());
=======
            ps.setInt(4, q.getTempsLimite());
>>>>>>> origin/gestionikram
            ps.setInt(5, q.getScoreTotal());
            ps.setBoolean(6, q.isEstActif());
            ps.setInt(7, q.getId());
            ps.executeUpdate();
<<<<<<< HEAD
            System.out.println("Quiz modifie avec succes.");
        } catch (SQLException e) {
            System.out.println("Erreur update quiz : " + e.getMessage());
        }
    }

    @Override
    public void delete(Quiz q) {
=======
            System.out.println("✅ Quiz modifié avec succès.");
        } catch (SQLException e) {
            System.out.println("❌ Erreur update quiz : " + e.getMessage());
        }
    }

    // ================================================
    // DELETE
    // ================================================
    @Override
    public void delete(Quiz q) {

>>>>>>> origin/gestionikram
        String sql = "DELETE FROM quiz WHERE id_quiz=?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, q.getId());
            ps.executeUpdate();
<<<<<<< HEAD
            System.out.println("Quiz supprime avec succes.");
        } catch (SQLException e) {
            System.out.println("Erreur delete quiz : " + e.getMessage());
        }
    }

    @Override
    public List<Quiz> getAll() {
        List<Quiz> list = new ArrayList<>();
        String sql = "SELECT * FROM quiz ORDER BY date_creation DESC";

        try (Statement st = conn.createStatement(); ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                list.add(mapRow(rs));
            }
        } catch (SQLException e) {
            System.out.println("Erreur getAll quiz : " + e.getMessage());
=======
            System.out.println("✅ Quiz supprimé avec succès.");
        } catch (SQLException e) {
            System.out.println("❌ Erreur delete quiz : " + e.getMessage());
        }
    }

    // ================================================
    // GET ALL  ← LA CORRECTION PRINCIPALE
    // Charge tous les quiz actifs depuis la BDD
    // avec le nom du module
    // ================================================
    @Override
    public List<Quiz> getAll() {

        List<Quiz> list = new ArrayList<>();

        // JOIN avec modules pour récupérer le nom de la matière
        String sql =
                "SELECT q.*, m.nom_module " +
                        "FROM quiz q " +
                        "LEFT JOIN modules m ON q.id_module = m.id_module " +
                        "WHERE q.est_actif = 1 " +
                        "ORDER BY q.date_creation DESC";

        try (Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                Quiz q = mapRow(rs);
                list.add(q);
            }

        } catch (SQLException e) {
            System.out.println("❌ Erreur getAll quiz : " + e.getMessage());
>>>>>>> origin/gestionikram
        }

        return list;
    }

<<<<<<< HEAD
    public Quiz getById(int idQuiz) {
        String sql = "SELECT * FROM quiz WHERE id_quiz=?";
=======
    // ================================================
    // GET BY ID
    // ================================================
    public Quiz getById(int id) {

        String sql =
                "SELECT q.*, m.nom_module " +
                        "FROM quiz q " +
                        "LEFT JOIN modules m ON q.id_module = m.id_module " +
                        "WHERE q.id_quiz = ?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return mapRow(rs);
        } catch (SQLException e) {
            System.out.println("❌ Erreur getById quiz : " + e.getMessage());
        }
        return null;
    }

    // ================================================
    // RECHERCHE PAR TITRE OU MODULE
    // ================================================
    public List<Quiz> rechercher(String terme, String nomModule) {

        List<Quiz> list = new ArrayList<>();

        String sql =
                "SELECT q.*, m.nom_module " +
                        "FROM quiz q " +
                        "LEFT JOIN modules m ON q.id_module = m.id_module " +
                        "WHERE q.est_actif = 1 " +
                        "AND (? IS NULL OR q.titre LIKE ? OR m.nom_module LIKE ?) " +
                        "AND (? IS NULL OR m.nom_module = ?) " +
                        "ORDER BY q.date_creation DESC";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {

            String like = (terme != null && !terme.isEmpty()) ? "%" + terme + "%" : null;
            String mod  = (nomModule != null && !nomModule.isEmpty()
                    && !nomModule.equals("Toutes les matières")) ? nomModule : null;

            ps.setString(1, like);
            ps.setString(2, like);
            ps.setString(3, like);
            ps.setString(4, mod);
            ps.setString(5, mod);

            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(mapRow(rs));

        } catch (SQLException e) {
            System.out.println("❌ Erreur recherche quiz : " + e.getMessage());
        }
        return list;
    }

    // ================================================
    // COMPTER LES QUESTIONS D'UN QUIZ
    // ================================================
    public int compterQuestions(int idQuiz) {

        String sql = "SELECT COUNT(*) FROM question WHERE id_quiz=?";
>>>>>>> origin/gestionikram

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idQuiz);
            ResultSet rs = ps.executeQuery();
<<<<<<< HEAD
            if (rs.next()) {
                return mapRow(rs);
            }
        } catch (SQLException e) {
            System.out.println("Erreur getById quiz : " + e.getMessage());
        }

        return null;
    }

    public List<Quiz> getByModule(int idModule) {
        List<Quiz> list = new ArrayList<>();
        String sql = "SELECT * FROM quiz WHERE id_module=? AND est_actif=1 ORDER BY date_creation DESC";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idModule);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(mapRow(rs));
            }
        } catch (SQLException e) {
            System.out.println("Erreur getByModule quiz : " + e.getMessage());
        }

        return list;
    }

    private Quiz mapRow(ResultSet rs) throws SQLException {
=======
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) {
            System.out.println("❌ Erreur comptage questions : " + e.getMessage());
        }
        return 0;
    }

    // ================================================
    // MAPPER UNE LIGNE ResultSet → Quiz
    // ================================================
    private Quiz mapRow(ResultSet rs) throws SQLException {

>>>>>>> origin/gestionikram
        Quiz q = new Quiz();
        q.setId(rs.getInt("id_quiz"));
        q.setTitre(rs.getString("titre"));
        q.setDescription(rs.getString("description"));
        q.setIdModule(rs.getInt("id_module"));
        q.setIdCreateur(rs.getInt("id_createur"));
<<<<<<< HEAD

        int tempsLimite = rs.getInt("temps_limite");
        q.setTempsLimite(rs.wasNull() ? null : tempsLimite);

        q.setScoreTotal(rs.getInt("score_total"));
        Timestamp dateCreation = rs.getTimestamp("date_creation");
        q.setDateCreation(dateCreation == null ? null : dateCreation.toLocalDateTime());
        q.setEstActif(rs.getBoolean("est_actif"));
        return q;
    }

    private void setNullableInt(PreparedStatement ps, int index, Integer value) throws SQLException {
        if (value == null) {
            ps.setNull(index, Types.INTEGER);
        } else {
            ps.setInt(index, value);
        }
    }
=======
        q.setTempsLimite(rs.getInt("temps_limite"));
        q.setScoreTotal(rs.getInt("score_total"));
        q.setEstActif(rs.getBoolean("est_actif"));

        // Nom du module (depuis le JOIN)
        try {
            q.setNomModule(rs.getString("nom_module"));
        } catch (SQLException ignored) {}

        return q;
    }
>>>>>>> origin/gestionikram
}