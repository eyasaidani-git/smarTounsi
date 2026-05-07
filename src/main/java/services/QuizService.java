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
        String req = "INSERT INTO quiz (titre, description, id_module, id_createur) VALUES (?,?,?,?)";
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

    @Override
    public void update(Quiz q) {
        String req = "UPDATE quiz SET titre=?, description=? WHERE id=?";
        try (PreparedStatement ps = conn.prepareStatement(req)) {
            ps.setString(1, q.getTitre());
            ps.setString(2, q.getDescription());
            ps.setInt(3, q.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Erreur update quiz : " + e.getMessage());
        }
    }

    @Override
    public void delete(Quiz q) {
        try (PreparedStatement ps = conn.prepareStatement("DELETE FROM quiz WHERE id=?")) {
            ps.setInt(1, q.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Erreur delete quiz : " + e.getMessage());
        }
    }

    @Override
    public List<Quiz> getAll() {
        List<Quiz> list = new ArrayList<>();
        try (Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery("SELECT * FROM quiz")) {
            while (rs.next()) {
                Quiz q = new Quiz();
                q.setId(rs.getInt("id"));
                q.setTitre(rs.getString("titre"));
                q.setDescription(rs.getString("description"));
                q.setIdModule(rs.getInt("id_module"));
                q.setIdCreateur(rs.getInt("id_createur"));
                list.add(q);
            }
        } catch (SQLException e) {
            System.out.println("Erreur getAll quiz : " + e.getMessage());
        }
        return list;
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
        String req = "SELECT * FROM question WHERE id_quiz=?";
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
