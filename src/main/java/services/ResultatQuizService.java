package services;

import models.ResultatQuiz;
import util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ResultatQuizService implements IService<ResultatQuiz> {
    private final Connection conn;
    public ResultatQuizService() {
        this.conn = DBConnection.getInstance().getConn();
    }

    @Override
    public void add(ResultatQuiz r) {
        String sql = "INSERT INTO resultat_quiz (id_quiz, id_utilisateur, score_obtenu, temps_passe) VALUES (?, ?, ?, ?)";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, r.getIdQuiz());
            ps.setInt(2, r.getIdUtilisateur());
            ps.setInt(3, r.getScoreObtenu());
            setNullableInt(ps, 4, r.getTempsPasse());
            ps.executeUpdate();
            System.out.println("Resultat quiz ajoute avec succes.");
        } catch (SQLException e) {
            System.out.println("Erreur add resultat quiz : " + e.getMessage());
        }
    }

    public int addAndReturnId(ResultatQuiz r) {
        String sql = "INSERT INTO resultat_quiz (id_quiz, id_utilisateur, score_obtenu, temps_passe) VALUES (?, ?, ?, ?)";

        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, r.getIdQuiz());
            ps.setInt(2, r.getIdUtilisateur());
            ps.setInt(3, r.getScoreObtenu());
            setNullableInt(ps, 4, r.getTempsPasse());
            ps.executeUpdate();

            ResultSet keys = ps.getGeneratedKeys();
            if (keys.next()) {
                return keys.getInt(1);
            }
        } catch (SQLException e) {
            System.out.println("Erreur addAndReturnId resultat quiz : " + e.getMessage());
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
            System.out.println("Resultat quiz modifie avec succes.");
        } catch (SQLException e) {
            System.out.println("Erreur update resultat quiz : " + e.getMessage());
        }
    }

    @Override
    public void delete(ResultatQuiz r) {
        String sql = "DELETE FROM resultat_quiz WHERE id_resultat=?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, r.getId());
            ps.executeUpdate();
            System.out.println("Resultat quiz supprime avec succes.");
        } catch (SQLException e) {
            System.out.println("Erreur delete resultat quiz : " + e.getMessage());
        }
    }

    @Override
    public List<ResultatQuiz> getAll() {
        List<ResultatQuiz> list = new ArrayList<>();
        String sql = "SELECT * FROM resultat_quiz ORDER BY date_passage DESC";

        try (Statement st = conn.createStatement(); ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                list.add(mapRow(rs));
            }
        } catch (SQLException e) {
            System.out.println("Erreur getAll resultat quiz : " + e.getMessage());
        }

        return list;
    }

    public List<ResultatQuiz> getByUtilisateur(int idUtilisateur) {
        List<ResultatQuiz> list = new ArrayList<>();
        String sql = "SELECT * FROM resultat_quiz WHERE id_utilisateur=? ORDER BY date_passage DESC";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idUtilisateur);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(mapRow(rs));
            }
        } catch (SQLException e) {
            System.out.println("Erreur getByUtilisateur resultat quiz : " + e.getMessage());
        }

        return list;
    }

    public List<ResultatQuiz> getByQuiz(int idQuiz) {
        List<ResultatQuiz> list = new ArrayList<>();
        String sql = "SELECT * FROM resultat_quiz WHERE id_quiz=? ORDER BY date_passage DESC";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idQuiz);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(mapRow(rs));
            }
        } catch (SQLException e) {
            System.out.println("Erreur getByQuiz resultat quiz : " + e.getMessage());
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
        r.setDatePassage(datePassage == null ? null : datePassage.toLocalDateTime());
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
    }
}
