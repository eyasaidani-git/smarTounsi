package services;
import models.AvisEvenement;
import util.DBConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
public class AvisEvenementService implements IService<AvisEvenement> {
    private Connection conn;

    public AvisEvenementService() {
        conn = DBConnection.getInstance().getConn();
    }

    @Override
    public void add(AvisEvenement a) {
        String req = "INSERT INTO avis_evenement (note, commentaire, id_evenement, id_utilisateur) " +
                "VALUES (?, ?, ?, ?)";

        try (PreparedStatement ps = conn.prepareStatement(req)) {

            if (a.getNote() < 1 || a.getNote() > 5) {
                System.out.println("Erreur : la note doit etre entre 1 et 5.");
                return;
            }

            ps.setInt(1, a.getNote());
            ps.setString(2, a.getCommentaire());
            ps.setInt(3, a.getIdEvenement());
            ps.setInt(4, a.getIdUtilisateur());

            ps.executeUpdate();
            System.out.println("Avis evenement ajoute avec succes.");

        } catch (SQLException e) {
            System.out.println("Erreur add avis evenement : " + e.getMessage());
        }
    }

    @Override
    public void update(AvisEvenement a) {
        String req = "UPDATE avis_evenement SET note=?, commentaire=? WHERE id=?";

        try (PreparedStatement ps = conn.prepareStatement(req)) {

            if (a.getNote() < 1 || a.getNote() > 5) {
                System.out.println("Erreur : la note doit etre entre 1 et 5.");
                return;
            }

            ps.setInt(1, a.getNote());
            ps.setString(2, a.getCommentaire());
            ps.setInt(3, a.getId());

            ps.executeUpdate();
            System.out.println("Avis evenement modifie avec succes.");

        } catch (SQLException e) {
            System.out.println("Erreur update avis evenement : " + e.getMessage());
        }
    }

    @Override
    public void delete(AvisEvenement a) {
        String req = "DELETE FROM avis_evenement WHERE id=?";

        try (PreparedStatement ps = conn.prepareStatement(req)) {

            ps.setInt(1, a.getId());
            ps.executeUpdate();
            System.out.println("Avis evenement supprime avec succes.");

        } catch (SQLException e) {
            System.out.println("Erreur delete avis evenement : " + e.getMessage());
        }
    }

    @Override
    public List<AvisEvenement> getAll() {
        return getAvisEvenements("SELECT * FROM avis_evenement");
    }
    public List<AvisEvenement> getByEvenement(int idEvenement) {
        List<AvisEvenement> list = new ArrayList<>();

        String req = "SELECT * FROM avis_evenement WHERE id_evenement=? ORDER BY date_avis DESC";

        try (PreparedStatement ps = conn.prepareStatement(req)) {

            ps.setInt(1, idEvenement);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                list.add(mapRow(rs));
            }

        } catch (SQLException e) {
            System.out.println("Erreur getByEvenement : " + e.getMessage());
        }

        return list;
    }

    public List<AvisEvenement> getByUtilisateur(int idUtilisateur) {
        List<AvisEvenement> list = new ArrayList<>();

        String req = "SELECT * FROM avis_evenement WHERE id_utilisateur=? ORDER BY date_avis DESC";

        try (PreparedStatement ps = conn.prepareStatement(req)) {

            ps.setInt(1, idUtilisateur);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                list.add(mapRow(rs));
            }

        } catch (SQLException e) {
            System.out.println("Erreur getByUtilisateur : " + e.getMessage());
        }

        return list;
    }

    public boolean avisExiste(int idEvenement, int idUtilisateur) {
        String req = "SELECT * FROM avis_evenement WHERE id_evenement=? AND id_utilisateur=?";

        try (PreparedStatement ps = conn.prepareStatement(req)) {

            ps.setInt(1, idEvenement);
            ps.setInt(2, idUtilisateur);

            ResultSet rs = ps.executeQuery();

            return rs.next();

        } catch (SQLException e) {
            System.out.println("Erreur avisExiste : " + e.getMessage());
        }

        return false;
    }

    public double getMoyenneByEvenement(int idEvenement) {
        String req = "SELECT AVG(note) AS moyenne FROM avis_evenement WHERE id_evenement=?";

        try (PreparedStatement ps = conn.prepareStatement(req)) {

            ps.setInt(1, idEvenement);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return rs.getDouble("moyenne");
            }

        } catch (SQLException e) {
            System.out.println("Erreur getMoyenneByEvenement : " + e.getMessage());
        }

        return 0;
    }

    public int getNombreAvisByEvenement(int idEvenement) {
        String req = "SELECT COUNT(*) AS total FROM avis_evenement WHERE id_evenement=?";

        try (PreparedStatement ps = conn.prepareStatement(req)) {

            ps.setInt(1, idEvenement);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return rs.getInt("total");
            }

        } catch (SQLException e) {
            System.out.println("Erreur getNombreAvisByEvenement : " + e.getMessage());
        }

        return 0;
    }

    private List<AvisEvenement> getAvisEvenements(String sql) {
        List<AvisEvenement> list = new ArrayList<>();

        try (Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                list.add(mapRow(rs));
            }

        } catch (SQLException e) {
            System.out.println("Erreur getAvisEvenements : " + e.getMessage());
        }

        return list;
    }

    private AvisEvenement mapRow(ResultSet rs) throws SQLException {
        AvisEvenement a = new AvisEvenement();

        a.setId(rs.getInt("id"));
        a.setNote(rs.getInt("note"));
        a.setCommentaire(rs.getString("commentaire"));

        Timestamp timestamp = rs.getTimestamp("date_avis");
        if (timestamp != null) {
            a.setDateAvis(timestamp.toLocalDateTime());
        }

        a.setIdEvenement(rs.getInt("id_evenement"));
        a.setIdUtilisateur(rs.getInt("id_utilisateur"));

        return a;
    }
}
