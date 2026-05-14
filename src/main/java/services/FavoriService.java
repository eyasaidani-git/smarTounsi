package services;

import models.Favori;
import util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class FavoriService implements IService<Favori> {
    private final Connection conn;

    public FavoriService() {
        this.conn = DBConnection.getInstance().getConn();
    }

    @Override
    public void add(Favori f) {
        String sql = "INSERT IGNORE INTO favoris (id_utilisateur, id_document) VALUES (?, ?)";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, f.getIdUtilisateur());
            ps.setInt(2, f.getIdDocument());
            ps.executeUpdate();
            System.out.println("Favori ajoute avec succes.");
        } catch (SQLException e) {
            System.out.println("Erreur add favori : " + e.getMessage());
        }
    }

    @Override
    public void update(Favori f) {
        // Un favori ne se modifie pas vraiment : on l'ajoute ou on le supprime.
        // Cette methode est gardee pour respecter IService.
    }

    @Override
    public void delete(Favori f) {
        String sql = "DELETE FROM favoris WHERE id_utilisateur=? AND id_document=?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, f.getIdUtilisateur());
            ps.setInt(2, f.getIdDocument());
            ps.executeUpdate();
            System.out.println("Favori supprime avec succes.");
        } catch (SQLException e) {
            System.out.println("Erreur delete favori : " + e.getMessage());
        }
    }

    public void deleteById(int idFavori) {
        String sql = "DELETE FROM favoris WHERE id_favori=?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idFavori);
            ps.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Erreur deleteById favori : " + e.getMessage());
        }
    }

    @Override
    public List<Favori> getAll() {
        List<Favori> list = new ArrayList<>();
        String sql = "SELECT * FROM favoris ORDER BY date_ajout DESC";

        try (Statement st = conn.createStatement(); ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                list.add(mapRow(rs));
            }
        } catch (SQLException e) {
            System.out.println("Erreur getAll favoris : " + e.getMessage());
        }

        return list;
    }

    public List<Favori> getByUtilisateur(int idUtilisateur) {
        List<Favori> list = new ArrayList<>();
        String sql = "SELECT * FROM favoris WHERE id_utilisateur=? ORDER BY date_ajout DESC";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idUtilisateur);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(mapRow(rs));
            }
        } catch (SQLException e) {
            System.out.println("Erreur getByUtilisateur favoris : " + e.getMessage());
        }

        return list;
    }

    public boolean existe(int idUtilisateur, int idDocument) {
        String sql = "SELECT id_favori FROM favoris WHERE id_utilisateur=? AND id_document=?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idUtilisateur);
            ps.setInt(2, idDocument);
            ResultSet rs = ps.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            System.out.println("Erreur existe favori : " + e.getMessage());
        }

        return false;
    }

    private Favori mapRow(ResultSet rs) throws SQLException {
        Favori f = new Favori();
        f.setId(rs.getInt("id_favori"));
        f.setIdUtilisateur(rs.getInt("id_utilisateur"));
        f.setIdDocument(rs.getInt("id_document"));
        Timestamp dateAjout = rs.getTimestamp("date_ajout");
        f.setDateAjout(dateAjout == null ? null : dateAjout.toLocalDateTime());
        return f;
    }
<<<<<<< HEAD
}

=======
}
>>>>>>> origin/gestionmohamed
