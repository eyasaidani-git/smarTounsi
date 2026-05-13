package services;

import enums.QuestionType;
import models.Question;
import util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class QuestionService implements IService<Question> {
    private final Connection conn;

    public QuestionService() {
        this.conn = DBConnection.getInstance().getConn();
    }

    @Override
    public void add(Question q) {
        String sql = "INSERT INTO question (id_quiz, enonce, type_question, points, ordre) VALUES (?, ?, ?, ?, ?)";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, q.getIdQuiz());
            ps.setString(2, q.getEnonce());
            ps.setString(3, q.getTypeQuestion().name());
            ps.setInt(4, q.getPoints());
            ps.setInt(5, q.getOrdre());
            ps.executeUpdate();
            System.out.println("Question ajoutee avec succes.");
        } catch (SQLException e) {
            System.out.println("Erreur add question : " + e.getMessage());
        }
    }

    @Override
    public void update(Question q) {
        String sql = "UPDATE question SET enonce=?, type_question=?, points=?, ordre=? WHERE id_question=?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, q.getEnonce());
            ps.setString(2, q.getTypeQuestion().name());
            ps.setInt(3, q.getPoints());
            ps.setInt(4, q.getOrdre());
            ps.setInt(5, q.getId());
            ps.executeUpdate();
            System.out.println("Question modifiee avec succes.");
        } catch (SQLException e) {
            System.out.println("Erreur update question : " + e.getMessage());
        }
    }

    @Override
    public void delete(Question q) {
        String sql = "DELETE FROM question WHERE id_question=?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, q.getId());
            ps.executeUpdate();
            System.out.println("Question supprimee avec succes.");
        } catch (SQLException e) {
            System.out.println("Erreur delete question : " + e.getMessage());
        }
    }

    @Override
    public List<Question> getAll() {
        List<Question> list = new ArrayList<>();
        String sql = "SELECT * FROM question ORDER BY id_quiz ASC, ordre ASC";

        try (Statement st = conn.createStatement(); ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                list.add(mapRow(rs));
            }
        } catch (SQLException e) {
            System.out.println("Erreur getAll question : " + e.getMessage());
        }

        return list;
    }

    public List<Question> getByQuiz(int idQuiz) {
        List<Question> list = new ArrayList<>();
        String sql = "SELECT * FROM question WHERE id_quiz=? ORDER BY ordre ASC";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idQuiz);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(mapRow(rs));
            }
        } catch (SQLException e) {
            System.out.println("Erreur getByQuiz question : " + e.getMessage());
        }

        return list;
    }

    private Question mapRow(ResultSet rs) throws SQLException {
        Question q = new Question();
        q.setId(rs.getInt("id_question"));
        q.setIdQuiz(rs.getInt("id_quiz"));
        q.setEnonce(rs.getString("enonce"));
        q.setTypeQuestion(QuestionType.valueOf(rs.getString("type_question")));
        q.setPoints(rs.getInt("points"));
        q.setOrdre(rs.getInt("ordre"));
        return q;
    }
}
