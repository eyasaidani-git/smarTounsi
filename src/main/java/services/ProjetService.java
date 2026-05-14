package services;

import enums.ProjetStatut;
import models.Projet;
import util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProjetService implements IService<Projet> {
    private final Connection conn;

    public ProjetService() {
        this.conn = DBConnection.getInstance().getConn();
    }

    @Override
    public void add(Projet p) {
        String sql = "INSERT INTO projet (nom_projet, description, id_createur, contient_code, contient_presentation, " +
                "contient_rapport, fichier_code, fichier_presentation, fichier_rapport, statut) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, p.getNomProjet());
            ps.setString(2, p.getDescription());
            ps.setInt(3, p.getIdCreateur());
            ps.setBoolean(4, p.isContientCode());
            ps.setBoolean(5, p.isContientPresentation());
            ps.setBoolean(6, p.isContientRapport());
            ps.setString(7, p.getFichierCode());
            ps.setString(8, p.getFichierPresentation());
            ps.setString(9, p.getFichierRapport());
            ps.setString(10, p.getStatut() == null ? ProjetStatut.EN_COURS.name() : p.getStatut());
            ps.executeUpdate();
            System.out.println("Projet ajoute avec succes.");
        } catch (SQLException e) {
            System.out.println("Erreur add projet : " + e.getMessage());
        }
    }

    @Override
    public void update(Projet p) {
        String sql = "UPDATE projet SET nom_projet=?, description=?, contient_code=?, contient_presentation=?, contient_rapport=?, " +
                "fichier_code=?, fichier_presentation=?, fichier_rapport=?, statut=? WHERE id_projet=?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, p.getNomProjet());
            ps.setString(2, p.getDescription());
            ps.setBoolean(3, p.isContientCode());
            ps.setBoolean(4, p.isContientPresentation());
            ps.setBoolean(5, p.isContientRapport());
            ps.setString(6, p.getFichierCode());
            ps.setString(7, p.getFichierPresentation());
            ps.setString(8, p.getFichierRapport());
            ps.setString(9, p.getStatut());
            ps.setInt(10, p.getId());
            ps.executeUpdate();
            System.out.println("Projet modifie avec succes.");
        } catch (SQLException e) {
            System.out.println("Erreur update projet : " + e.getMessage());
        }
    }

    @Override
    public void delete(Projet p) {
        String sql = "DELETE FROM projet WHERE id_projet=?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, p.getId());
            ps.executeUpdate();
            System.out.println("Projet supprime avec succes.");
        } catch (SQLException e) {
            System.out.println("Erreur delete projet : " + e.getMessage());
        }
    }

    @Override
    public List<Projet> getAll() {
        List<Projet> list = new ArrayList<>();
        String sql = "SELECT * FROM projet ORDER BY date_creation DESC";

        try (Statement st = conn.createStatement(); ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                list.add(mapRow(rs));
            }
        } catch (SQLException e) {
            System.out.println("Erreur getAll projet : " + e.getMessage());
        }

        return list;
    }

    public Projet getById(int idProjet) {
        String sql = "SELECT * FROM projet WHERE id_projet=?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idProjet);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return mapRow(rs);
            }
        } catch (SQLException e) {
            System.out.println("Erreur getById projet : " + e.getMessage());
        }

        return null;
    }

    public List<Projet> getByCreateur(int idCreateur) {
        List<Projet> list = new ArrayList<>();
        String sql = "SELECT * FROM projet WHERE id_createur=? ORDER BY date_creation DESC";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idCreateur);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(mapRow(rs));
            }
        } catch (SQLException e) {
            System.out.println("Erreur getByCreateur projet : " + e.getMessage());
        }

        return list;
    }

    public void changerStatut(int idProjet, ProjetStatut statut) {
        String sql = "UPDATE projet SET statut=? WHERE id_projet=?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, statut.name());
            ps.setInt(2, idProjet);
            ps.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Erreur changerStatut projet : " + e.getMessage());
        }
    }

    private Projet mapRow(ResultSet rs) throws SQLException {
        Projet p = new Projet();
        p.setId(rs.getInt("id_projet"));
        p.setNomProjet(rs.getString("nom_projet"));
        p.setDescription(rs.getString("description"));
        p.setIdCreateur(rs.getInt("id_createur"));
        p.setContientCode(rs.getBoolean("contient_code"));
        p.setContientPresentation(rs.getBoolean("contient_presentation"));
        p.setContientRapport(rs.getBoolean("contient_rapport"));
        p.setFichierCode(rs.getString("fichier_code"));
        p.setFichierPresentation(rs.getString("fichier_presentation"));
        p.setFichierRapport(rs.getString("fichier_rapport"));
        p.setStatut(String.valueOf(ProjetStatut.valueOf(rs.getString("statut"))));
        Timestamp dateCreation = rs.getTimestamp("date_creation");
        p.setDateCreation(dateCreation == null ? null : dateCreation.toLocalDateTime());
        return p;

    }
}