package services;

import enums.EvenementType;
import models.Evenement;
import util.DBConnection;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class EvenementService implements IService<Evenement> {
    private final Connection conn;

    public EvenementService() {
        this.conn = DBConnection.getInstance().getConn();
    }

    @Override
    public void add(Evenement e) {
        String sql = "INSERT INTO evenement (titre, description, type_evenement, lieu, date_debut, date_fin, tarif, id_createur, image_evenement) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, e.getTitre());
            ps.setString(2, e.getDescription());
            ps.setString(3, e.getTypeEvenement() == null ? EvenementType.AUTRE.name() : e.getTypeEvenement().name());
            ps.setString(4, e.getLieu());
            ps.setTimestamp(5, Timestamp.valueOf(e.getDateDebut()));
            if (e.getDateFin() == null) {
                ps.setNull(6, Types.TIMESTAMP);
            } else {
                ps.setTimestamp(6, Timestamp.valueOf(e.getDateFin()));
            }
            ps.setBigDecimal(7, e.getTarif());
            ps.setInt(8, e.getIdCreateur());
            ps.setString(9, e.getImageEvenement());
            ps.executeUpdate();
            System.out.println("Evenement ajoute avec succes.");
        } catch (SQLException ex) {
            System.out.println("Erreur add evenement : " + ex.getMessage());
        }
    }

    @Override
    public void update(Evenement e) {
        String sql = "UPDATE evenement SET titre=?, description=?, type_evenement=?, lieu=?, date_debut=?, date_fin=?, " +
                "tarif=?, image_evenement=? WHERE id_evenement=?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, e.getTitre());
            ps.setString(2, e.getDescription());
            ps.setString(3, e.getTypeEvenement().name());
            ps.setString(4, e.getLieu());
            ps.setTimestamp(5, Timestamp.valueOf(e.getDateDebut()));
            if (e.getDateFin() == null) {
                ps.setNull(6, Types.TIMESTAMP);
            } else {
                ps.setTimestamp(6, Timestamp.valueOf(e.getDateFin()));
            }
            ps.setBigDecimal(7, e.getTarif());
            ps.setString(8, e.getImageEvenement());
            ps.setInt(9, e.getId());
            ps.executeUpdate();
            System.out.println("Evenement modifie avec succes.");
        } catch (SQLException ex) {
            System.out.println("Erreur update evenement : " + ex.getMessage());
        }
    }

    @Override
    public void delete(Evenement e) {
        String sql = "DELETE FROM evenement WHERE id_evenement=?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, e.getId());
            ps.executeUpdate();
            System.out.println("Evenement supprime avec succes.");
        } catch (SQLException ex) {
            System.out.println("Erreur delete evenement : " + ex.getMessage());
        }
    }

    @Override
    public List<Evenement> getAll() {
        List<Evenement> list = new ArrayList<>();
        String sql = "SELECT * FROM evenement ORDER BY date_debut ASC";

        try (Statement st = conn.createStatement(); ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                list.add(mapRow(rs));
            }
        } catch (SQLException e) {
            System.out.println("Erreur getAll evenement : " + e.getMessage());
        }

        return list;
    }

    public Evenement getById(int idEvenement) {
        String sql = "SELECT * FROM evenement WHERE id_evenement=?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idEvenement);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return mapRow(rs);
            }
        } catch (SQLException e) {
            System.out.println("Erreur getById evenement : " + e.getMessage());
        }

        return null;
    }

    public List<Evenement> getByType(EvenementType type) {
        List<Evenement> list = new ArrayList<>();
        String sql = "SELECT * FROM evenement WHERE type_evenement=? ORDER BY date_debut ASC";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, type.name());
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(mapRow(rs));
            }
        } catch (SQLException e) {
            System.out.println("Erreur getByType evenement : " + e.getMessage());
        }

        return list;
    }

    public List<Evenement> getByDate(LocalDate date) {
        List<Evenement> list = new ArrayList<>();
        String sql = "SELECT * FROM evenement WHERE DATE(date_debut)=? ORDER BY date_debut ASC";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDate(1, Date.valueOf(date));
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(mapRow(rs));
            }
        } catch (SQLException e) {
            System.out.println("Erreur getByDate evenement : " + e.getMessage());
        }

        return list;
    }

    private Evenement mapRow(ResultSet rs) throws SQLException {
        Evenement e = new Evenement();
        e.setId(rs.getInt("id_evenement"));
        e.setTitre(rs.getString("titre"));
        e.setDescription(rs.getString("description"));
        e.setTypeEvenement(EvenementType.valueOf(rs.getString("type_evenement")));
        e.setLieu(rs.getString("lieu"));
        Timestamp dateDebut = rs.getTimestamp("date_debut");
        e.setDateDebut(dateDebut == null ? null : dateDebut.toLocalDateTime());
        Timestamp dateFin = rs.getTimestamp("date_fin");
        e.setDateFin(dateFin == null ? null : dateFin.toLocalDateTime());
        e.setTarif(rs.getBigDecimal("tarif"));
        e.setIdCreateur(rs.getInt("id_createur"));
        e.setImageEvenement(rs.getString("image_evenement"));
        Timestamp dateCreation = rs.getTimestamp("date_creation");
        e.setDateCreation(dateCreation == null ? null : dateCreation.toLocalDateTime());
        return e;
    }
}