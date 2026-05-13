package services;

import models.Quiz;
import util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class QuizService implements IService<Quiz> {
    private final Connection conn;

    public QuizService() {
        this.conn = DBConnection.getInstance().getConn();
    }

    @Override
    public void add(Quiz q) {
        String sql = "INSERT INTO quiz (titre, description, id_module, id_createur, temps_limite, score_total, est_actif) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, q.getTitre());
            ps.setString(2, q.getDescription());
            ps.setInt(3, q.getIdModule());
            ps.setInt(4, q.getIdCreateur());
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

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, q.getTitre());
            ps.setString(2, q.getDescription());
            ps.setInt(3, q.getIdModule());
            setNullableInt(ps, 4, q.getTempsLimite());
            ps.setInt(5, q.getScoreTotal());
            ps.setBoolean(6, q.isEstActif());
            ps.setInt(7, q.getId());
            ps.executeUpdate();
            System.out.println("Quiz modifie avec succes.");
        } catch (SQLException e) {
            System.out.println("Erreur update quiz : " + e.getMessage());
        }
    }

    @Override
    public void delete(Quiz q) {
        String sql = "DELETE FROM quiz WHERE id_quiz=?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, q.getId());
            ps.executeUpdate();
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
        }

        return list;
    }

    public Quiz getById(int idQuiz) {
        String sql = "SELECT * FROM quiz WHERE id_quiz=?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idQuiz);
            ResultSet rs = ps.executeQuery();
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
        Quiz q = new Quiz();
        q.setId(rs.getInt("id_quiz"));
        q.setTitre(rs.getString("titre"));
        q.setDescription(rs.getString("description"));
        q.setIdModule(rs.getInt("id_module"));
        q.setIdCreateur(rs.getInt("id_createur"));

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
}
