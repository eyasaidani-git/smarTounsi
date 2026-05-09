package services;

import models.Question;
import models.Quiz;
import models.Reponse;
import util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class QuizService implements IService<Quiz> {

    private Connection conn;

    public QuizService() {
        this.conn = DBConnection.getInstance().getConn();
    }

    @Override
    public void add(Quiz q) {
        String req = "INSERT INTO quiz (titre, description, id_module, id_createur) VALUES (?, ?, ?, ?)";

        try (PreparedStatement ps = conn.prepareStatement(req)) {

            ps.setString(1, q.getTitre());
            ps.setString(2, q.getDescription());
            ps.setInt(3, q.getIdModule());
            ps.setInt(4, q.getIdCreateur());

            ps.executeUpdate();

        } catch (SQLException e) {
            System.out.println("Erreur add quiz : " + e.getMessage());
        }
    }

    public int addAndReturnId(Quiz q) {
        String req = "INSERT INTO quiz (titre, description, id_module, id_createur) VALUES (?, ?, ?, ?)";

        try (PreparedStatement ps = conn.prepareStatement(req, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, q.getTitre());
            ps.setString(2, q.getDescription());
            ps.setInt(3, q.getIdModule());
            ps.setInt(4, q.getIdCreateur());

            ps.executeUpdate();

            ResultSet rs = ps.getGeneratedKeys();

            if (rs.next()) {
                return rs.getInt(1);
            }

        } catch (SQLException e) {
            System.out.println("Erreur addAndReturnId quiz : " + e.getMessage());
        }

        return -1;
    }

    @Override
    public void update(Quiz q) {
        String req = "UPDATE quiz SET titre = ?, description = ?, id_module = ?, id_createur = ? WHERE id = ?";

        try (PreparedStatement ps = conn.prepareStatement(req)) {

            ps.setString(1, q.getTitre());
            ps.setString(2, q.getDescription());
            ps.setInt(3, q.getIdModule());
            ps.setInt(4, q.getIdCreateur());
            ps.setInt(5, q.getId());

            ps.executeUpdate();

        } catch (SQLException e) {
            System.out.println("Erreur update quiz : " + e.getMessage());
        }
    }

    @Override
    public void delete(Quiz q) {
        String req = "DELETE FROM quiz WHERE id = ?";

        try (PreparedStatement ps = conn.prepareStatement(req)) {

            ps.setInt(1, q.getId());
            ps.executeUpdate();

        } catch (SQLException e) {
            System.out.println("Erreur delete quiz : " + e.getMessage());
        }
    }

    @Override
    public List<Quiz> getAll() {
        List<Quiz> list = new ArrayList<>();

        String req = "SELECT * FROM quiz";

        try (Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(req)) {

            while (rs.next()) {
                Quiz q = mapResultSetToQuiz(rs);
                list.add(q);
            }

        } catch (SQLException e) {
            System.out.println("Erreur getAll quiz : " + e.getMessage());
        }

        return list;
    }

    public Quiz getById(int idQuiz) {
        String req = "SELECT * FROM quiz WHERE id = ?";

        try (PreparedStatement ps = conn.prepareStatement(req)) {

            ps.setInt(1, idQuiz);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return mapResultSetToQuiz(rs);
            }

        } catch (SQLException e) {
            System.out.println("Erreur getById quiz : " + e.getMessage());
        }

        return null;
    }

    private Quiz mapResultSetToQuiz(ResultSet rs) throws SQLException {
        Quiz q = new Quiz();

        q.setId(rs.getInt("id"));
        q.setTitre(rs.getString("titre"));
        q.setDescription(rs.getString("description"));
        q.setIdModule(rs.getInt("id_module"));
        q.setIdCreateur(rs.getInt("id_createur"));

        q.setMatiere(getMatiereByModuleId(q.getIdModule()));
        q.setIcone(getIconeByModuleId(q.getIdModule()));
        q.setNombreQuestions(countQuestions(q.getId()));
        q.setType(getQuizType(q.getId()));

        return q;
    }

    private int countQuestions(int idQuiz) {
        String req = "SELECT COUNT(*) FROM question WHERE id_quiz = ?";

        try (PreparedStatement ps = conn.prepareStatement(req)) {

            ps.setInt(1, idQuiz);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return rs.getInt(1);
            }

        } catch (SQLException e) {
            System.out.println("Erreur countQuestions : " + e.getMessage());
        }

        return 0;
    }

    private String getQuizType(int idQuiz) {
        String req = "SELECT DISTINCT type FROM question WHERE id_quiz = ?";

        List<String> types = new ArrayList<>();

        try (PreparedStatement ps = conn.prepareStatement(req)) {

            ps.setInt(1, idQuiz);

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                types.add(rs.getString("type"));
            }

        } catch (SQLException e) {
            System.out.println("Erreur getQuizType : " + e.getMessage());
        }

        if (types.isEmpty()) {
            return "QCM";
        }

        if (types.size() == 1) {
            return types.get(0);
        }

        return "Mixte";
    }

    private String getMatiereByModuleId(int idModule) {
        switch (idModule) {
            case 1:
                return "Mathématiques";
            case 2:
                return "Réseaux";
            case 3:
                return "Java";
            case 4:
                return "Electronique";
            case 5:
                return "Base de données";
            case 6:
                return "Web";
            case 7:
                return "Français";
            case 8:
                return "Anglais";
            default:
                return "Module " + idModule;
        }
    }

    private String getIconeByModuleId(int idModule) {
        switch (idModule) {
            case 1:
                return "📐";
            case 2:
                return "🌐";
            case 3:
                return "☕";
            case 4:
                return "⚡";
            case 5:
                return "🗄";
            case 6:
                return "💻";
            case 7:
                return "📘";
            case 8:
                return "📝";
            default:
                return "📝";
        }
    }

    public void addQuestion(Question q) {
        String req = "INSERT INTO question (enonce, type, id_quiz) VALUES (?, ?, ?)";

        try (PreparedStatement ps = conn.prepareStatement(req)) {

            ps.setString(1, q.getEnonce());
            ps.setString(2, q.getType());
            ps.setInt(3, q.getIdQuiz());

            ps.executeUpdate();

        } catch (SQLException e) {
            System.out.println("Erreur addQuestion : " + e.getMessage());
        }
    }

    public void addReponse(Reponse r) {
        String req = "INSERT INTO reponse (contenu, est_correcte, id_question) VALUES (?, ?, ?)";

        try (PreparedStatement ps = conn.prepareStatement(req)) {

            ps.setString(1, r.getContenu());
            ps.setBoolean(2, r.isEstCorrecte());
            ps.setInt(3, r.getIdQuestion());

            ps.executeUpdate();

        } catch (SQLException e) {
            System.out.println("Erreur addReponse : " + e.getMessage());
        }
    }

    public List<Question> getQuestions(int idQuiz) {
        List<Question> list = new ArrayList<>();

        String req = "SELECT * FROM question WHERE id_quiz = ?";

        try (PreparedStatement ps = conn.prepareStatement(req)) {

            ps.setInt(1, idQuiz);

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Question q = new Question();

                q.setId(rs.getInt("id"));
                q.setEnonce(rs.getString("enonce"));
                q.setType(rs.getString("type"));
                q.setIdQuiz(idQuiz);

                list.add(q);
            }

        } catch (SQLException e) {
            System.out.println("Erreur getQuestions : " + e.getMessage());
        }

        return list;
    }

    public void saveResultat(int idUtilisateur, int idQuiz, float score) {
        String req = "INSERT INTO resultat_quiz (score, id_quiz, id_utilisateur) VALUES (?, ?, ?)";

        try (PreparedStatement ps = conn.prepareStatement(req)) {

            ps.setFloat(1, score);
            ps.setInt(2, idQuiz);
            ps.setInt(3, idUtilisateur);

            ps.executeUpdate();

        } catch (SQLException e) {
            System.out.println("Erreur saveResultat : " + e.getMessage());
        }
    }
}