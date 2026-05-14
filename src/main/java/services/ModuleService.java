package services;

import models.Module;
import util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ModuleService implements IService<Module> {
    private final Connection conn;

    public ModuleService() {
        this.conn = DBConnection.getInstance().getConn();
    }

    @Override
    public void add(Module m) {
        String sql = "INSERT INTO modules (nom_module, description, id_createur) VALUES (?, ?, ?)";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, m.getNom());
            ps.setString(2, m.getDescription());
            setNullableInt(ps, 3, m.getIdCreateur());
            ps.executeUpdate();
            System.out.println("Module ajoute avec succes.");
        } catch (SQLException e) {
            System.out.println("Erreur add module : " + e.getMessage());
        }
    }

    @Override
    public void update(Module m) {
        String sql = "UPDATE modules SET nom_module=?, description=?, id_createur=? WHERE id_module=?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, m.getNom());
            ps.setString(2, m.getDescription());
            setNullableInt(ps, 3, m.getIdCreateur());
            ps.setInt(4, m.getId());
            ps.executeUpdate();
            System.out.println("Module modifie avec succes.");
        } catch (SQLException e) {
            System.out.println("Erreur update module : " + e.getMessage());
        }
    }

    @Override
    public void delete(Module m) {
        String sql = "DELETE FROM modules WHERE id_module=?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, m.getId());
            ps.executeUpdate();
            System.out.println("Module supprime avec succes.");
        } catch (SQLException e) {
            System.out.println("Erreur delete module : " + e.getMessage());
        }
    }

    @Override
    public List<Module> getAll() {
        List<Module> list = new ArrayList<>();
        String sql = "SELECT * FROM modules ORDER BY nom_module ASC";

        try (Statement st = conn.createStatement(); ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                list.add(mapRow(rs));
            }
        } catch (SQLException e) {
            System.out.println("Erreur getAll modules : " + e.getMessage());
        }

        return list;
    }

    public Module getById(int idModule) {
        String sql = "SELECT * FROM modules WHERE id_module=?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idModule);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return mapRow(rs);
            }
        } catch (SQLException e) {
            System.out.println("Erreur getById module : " + e.getMessage());
        }

        return null;
    }

    public List<Module> searchByNom(String motCle) {
        List<Module> list = new ArrayList<>();
        String sql = "SELECT * FROM modules WHERE nom_module LIKE ? ORDER BY nom_module ASC";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, "%" + motCle + "%");
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(mapRow(rs));
            }
        } catch (SQLException e) {
            System.out.println("Erreur searchByNom module : " + e.getMessage());
        }

        return list;
    }

    private Module mapRow(ResultSet rs) throws SQLException {
        Module m = new Module();
        m.setId(rs.getInt("id_module"));
        m.setNom(rs.getString("nom_module"));
        m.setDescription(rs.getString("description"));
        int idCreateur = rs.getInt("id_createur");
        m.setIdCreateur(rs.wasNull() ? null : idCreateur);
        Timestamp dateCreation = rs.getTimestamp("date_creation");
        m.setDateCreation(dateCreation == null ? null : dateCreation.toLocalDateTime());
        return m;
    }

    private void setNullableInt(PreparedStatement ps, int index, Integer value) throws SQLException {
        if (value == null) {
            ps.setNull(index, Types.INTEGER);
        } else {
            ps.setInt(index, value);
        }
    }
}
