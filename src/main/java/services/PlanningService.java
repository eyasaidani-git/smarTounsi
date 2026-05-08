package services;

import models.Planning;
import util.DBConnection;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class PlanningService implements IService<Planning> {

    private Connection conn;

    public PlanningService() {
        this.conn = DBConnection.getInstance().getConn();
    }

    @Override
    public void add(Planning p) {
        String req = "INSERT INTO planning " +
                "(date_revision, titre, id_utilisateur, type, heure, module, nom_fichier, chemin_fichier) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement ps = conn.prepareStatement(req)) {

            ps.setDate(1, Date.valueOf(p.getDateRevision()));
            ps.setString(2, p.getTitre());
            ps.setInt(3, p.getIdUtilisateur());
            ps.setString(4, p.getType());
            ps.setString(5, p.getHeure());
            ps.setString(6, p.getModule());
            ps.setString(7, p.getNomFichier());
            ps.setString(8, p.getCheminFichier());

            ps.executeUpdate();

        } catch (SQLException e) {
            System.out.println("Erreur add planning : " + e.getMessage());
        }
    }

    @Override
    public void update(Planning p) {
        String req = "UPDATE planning SET " +
                "date_revision=?, titre=?, id_utilisateur=?, type=?, heure=?, module=?, nom_fichier=?, chemin_fichier=? " +
                "WHERE id=?";

        try (PreparedStatement ps = conn.prepareStatement(req)) {

            ps.setDate(1, Date.valueOf(p.getDateRevision()));
            ps.setString(2, p.getTitre());
            ps.setInt(3, p.getIdUtilisateur());
            ps.setString(4, p.getType());
            ps.setString(5, p.getHeure());
            ps.setString(6, p.getModule());
            ps.setString(7, p.getNomFichier());
            ps.setString(8, p.getCheminFichier());
            ps.setInt(9, p.getId());

            ps.executeUpdate();

        } catch (SQLException e) {
            System.out.println("Erreur update planning : " + e.getMessage());
        }
    }

    @Override
    public void delete(Planning p) {
        String req = "DELETE FROM planning WHERE id=?";

        try (PreparedStatement ps = conn.prepareStatement(req)) {

            ps.setInt(1, p.getId());
            ps.executeUpdate();

        } catch (SQLException e) {
            System.out.println("Erreur delete planning : " + e.getMessage());
        }
    }

    @Override
    public List<Planning> getAll() {
        List<Planning> list = new ArrayList<>();

        String req = "SELECT * FROM planning";

        try (Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(req)) {

            while (rs.next()) {
                Planning p = mapResultSetToPlanning(rs);
                list.add(p);
            }

        } catch (SQLException e) {
            System.out.println("Erreur getAll planning : " + e.getMessage());
        }

        return list;
    }

    public List<Planning> getAllByUtilisateur(int idUtilisateur) {
        List<Planning> list = new ArrayList<>();

        String req = "SELECT * FROM planning WHERE id_utilisateur=? ORDER BY date_revision ASC, heure ASC";

        try (PreparedStatement ps = conn.prepareStatement(req)) {

            ps.setInt(1, idUtilisateur);

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Planning p = mapResultSetToPlanning(rs);
                list.add(p);
            }

        } catch (SQLException e) {
            System.out.println("Erreur getAllByUtilisateur planning : " + e.getMessage());
        }

        return list;
    }

    public List<Planning> getByDate(int idUtilisateur, LocalDate date) {
        List<Planning> list = new ArrayList<>();

        String req = "SELECT * FROM planning WHERE id_utilisateur=? AND date_revision=? ORDER BY heure ASC";

        try (PreparedStatement ps = conn.prepareStatement(req)) {

            ps.setInt(1, idUtilisateur);
            ps.setDate(2, Date.valueOf(date));

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Planning p = mapResultSetToPlanning(rs);
                list.add(p);
            }

        } catch (SQLException e) {
            System.out.println("Erreur getByDate planning : " + e.getMessage());
        }

        return list;
    }

    private Planning mapResultSetToPlanning(ResultSet rs) throws SQLException {
        Planning p = new Planning(titre, type, heure, module, selectedDate, selectedFile);

        p.setId(rs.getInt("id"));
        p.setIdUtilisateur(rs.getInt("id_utilisateur"));
        p.setDateRevision(rs.getDate("date_revision").toLocalDate());
        p.setTitre(rs.getString("titre"));
        p.setType(rs.getString("type"));
        p.setHeure(rs.getString("heure"));
        p.setModule(rs.getString("module"));
        p.setNomFichier(rs.getString("nom_fichier"));
        p.setCheminFichier(rs.getString("chemin_fichier"));

        return p;
    }
}