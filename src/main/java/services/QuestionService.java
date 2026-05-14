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

        String sql =
                "INSERT INTO question " +
                        "(id_quiz, enonce, type_question, points, ordre) " +
                        "VALUES (?, ?, ?, ?, ?)";

        try {

            PreparedStatement ps =
                    conn.prepareStatement(
                            sql,
                            Statement.RETURN_GENERATED_KEYS
                    );

            ps.setInt(1, q.getIdQuiz());
            ps.setString(2, q.getEnonce());
            ps.setString(3, q.getTypeQuestion().name());
            ps.setInt(4, q.getPoints());
            ps.setInt(5, q.getOrdre());

            ps.executeUpdate();

            ResultSet rs =
                    ps.getGeneratedKeys();

            if(rs.next()){

                q.setId(rs.getInt(1));
            }

            System.out.println(
                    "Question ajoutée avec succès."
            );

        } catch (SQLException e) {

            System.out.println(
                    "Erreur add question : "
                            + e.getMessage()
            );
        }
    }


    @Override
    public void update(Question q) {

    }

    @Override
    public void delete(Question q) {

    }

    @Override
    public List<Question> getAll() {

        return new ArrayList<>();
    }

    public List<Question> getByQuiz(int idQuiz) {

        List<Question> list =
                new ArrayList<>();

        String sql =
                "SELECT * FROM question WHERE id_quiz=?";

        try {

            PreparedStatement ps =
                    conn.prepareStatement(sql);

            ps.setInt(1, idQuiz);

            ResultSet rs =
                    ps.executeQuery();

            while(rs.next()){

                Question q =
                        new Question();

                q.setId(
                        rs.getInt("id_question")
                );

                q.setIdQuiz(
                        rs.getInt("id_quiz")
                );

                q.setEnonce(
                        rs.getString("enonce")
                );

                q.setTypeQuestion(
                        QuestionType.valueOf(
                                rs.getString("type_question")
                                        .toUpperCase()
                        )
                );

                q.setPoints(
                        rs.getInt("points")
                );

                q.setOrdre(
                        rs.getInt("ordre")
                );

                list.add(q);
            }

        } catch (SQLException e) {

            System.out.println(
                    e.getMessage()
            );
        }

        return list;
    }
}