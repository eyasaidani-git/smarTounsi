package services;
<<<<<<< HEAD

=======
>>>>>>> origin/gestionikram
import models.Module;
import util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
<<<<<<< HEAD

public class ModuleService implements IService<Module> {

    private final Connection conn;

    public ModuleService() {
=======
public class ModuleService implements IService<Module> {
    private Connection conn;

    public ModuleService(Connection conn) {
>>>>>>> origin/gestionikram
        this.conn = DBConnection.getInstance().getConn();
    }

    @Override
    public void add(Module m) {
<<<<<<< HEAD
        String sql = "INSERT INTO modules (nom_module, description, id_createur) VALUES (?, ?, ?)";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, m.getNomModule());
            ps.setString(2, m.getDescription());
            setNullableInt(ps, 3, m.getIdCreateur());

            ps.executeUpdate();
            System.out.println("Module ajouté avec succès.");
=======
        String req = "INSERT INTO modules (nom, description, icone) VALUES (?, ?, ?)";

        try (PreparedStatement ps = conn.prepareStatement(req)) {

            ps.setString(1, m.getNom());
            ps.setString(2, m.getDescription());
            ps.setString(3, m.getIcone());

            ps.executeUpdate();
            System.out.println("Module ajoute avec succes.");
>>>>>>> origin/gestionikram

        } catch (SQLException e) {
            System.out.println("Erreur add module : " + e.getMessage());
        }
<<<<<<< HEAD
=======

>>>>>>> origin/gestionikram
    }

    @Override
    public void update(Module m) {
<<<<<<< HEAD
        String sql = "UPDATE modules SET nom_module=?, description=?, id_createur=? WHERE id_module=?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, m.getNomModule());
            ps.setString(2, m.getDescription());
            setNullableInt(ps, 3, m.getIdCreateur());
            ps.setInt(4, m.getId());

            ps.executeUpdate();
            System.out.println("Module modifié avec succès.");
=======
        String req = "UPDATE modules SET nom=?, description=?, icone=? WHERE id=?";

        try (PreparedStatement ps = conn.prepareStatement(req)) {

            ps.setString(1, m.getNom());
            ps.setString(2, m.getDescription());
            ps.setString(3, m.getIcone());
            ps.setInt(4, m.getId());

            ps.executeUpdate();
            System.out.println("Module modifie avec succes.");
>>>>>>> origin/gestionikram

        } catch (SQLException e) {
            System.out.println("Erreur update module : " + e.getMessage());
        }
<<<<<<< HEAD
=======

>>>>>>> origin/gestionikram
    }

    @Override
    public void delete(Module m) {
<<<<<<< HEAD
        String sql = "DELETE FROM modules WHERE id_module=?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, m.getId());
            ps.executeUpdate();

            System.out.println("Module supprimé avec succès.");
=======
        String req = "DELETE FROM modules WHERE id=?";

        try (PreparedStatement ps = conn.prepareStatement(req)) {

            ps.setInt(1, m.getId());

            ps.executeUpdate();
            System.out.println("Module supprime avec succes.");
>>>>>>> origin/gestionikram

        } catch (SQLException e) {
            System.out.println("Erreur delete module : " + e.getMessage());
        }
    }

    @Override
    public List<Module> getAll() {
<<<<<<< HEAD
        List<Module> list = new ArrayList<>();
        String sql = "SELECT * FROM modules ORDER BY nom_module ASC";

        try (Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

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

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
=======
        return getModules("SELECT * FROM modules");    }

    public Module getById(int id) {
        String req = "SELECT * FROM modules WHERE id=?";

        try (PreparedStatement ps = conn.prepareStatement(req)) {

            ps.setInt(1, id);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return mapRow(rs);
>>>>>>> origin/gestionikram
            }

        } catch (SQLException e) {
            System.out.println("Erreur getById module : " + e.getMessage());
        }

        return null;
    }

<<<<<<< HEAD
    public List<Module> searchByNom(String motCle) {
        List<Module> list = new ArrayList<>();
        String sql = "SELECT * FROM modules WHERE nom_module LIKE ? ORDER BY nom_module ASC";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, "%" + motCle + "%");

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRow(rs));
                }
=======
    public List<Module> searchByNom(String keyword) {
        List<Module> list = new ArrayList<>();

        String req = "SELECT * FROM modules WHERE nom LIKE ?";

        try (PreparedStatement ps = conn.prepareStatement(req)) {

            ps.setString(1, "%" + keyword + "%");

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                list.add(mapRow(rs));
>>>>>>> origin/gestionikram
            }

        } catch (SQLException e) {
            System.out.println("Erreur searchByNom module : " + e.getMessage());
        }

        return list;
    }

<<<<<<< HEAD
    private Module mapRow(ResultSet rs) throws SQLException {
        Module m = new Module();

        m.setId(rs.getInt("id_module"));
        m.setNomModule(rs.getString("nom_module"));
        m.setDescription(rs.getString("description"));

        int idCreateur = rs.getInt("id_createur");
        m.setIdCreateur(rs.wasNull() ? null : idCreateur);

        Timestamp dateCreation = rs.getTimestamp("date_creation");
        if (dateCreation != null) {
            m.setDateCreation(dateCreation.toLocalDateTime());
=======
    private List<Module> getModules(String sql) {
        List<Module> list = new ArrayList<>();

        try (Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                list.add(mapRow(rs));
            }

        } catch (SQLException e) {
            System.out.println("Erreur getModules : " + e.getMessage());
        }

        return list;
    }

    private Module mapRow(ResultSet rs) throws SQLException {
        Module m = new Module();

        m.setId(rs.getInt("id"));
        m.setNom(rs.getString("nom"));
        m.setDescription(rs.getString("description"));
        m.setIcone(rs.getString("icone"));

        Timestamp timestamp = rs.getTimestamp("date_creation");
        if (timestamp != null) {
            m.setDateCreation(timestamp.toLocalDateTime());
>>>>>>> origin/gestionikram
        }

        return m;
    }
<<<<<<< HEAD

    private void setNullableInt(PreparedStatement ps, int index, Integer value) throws SQLException {
        if (value == null) {
            ps.setNull(index, Types.INTEGER);
        } else {
            ps.setInt(index, value);
        }
    }
}
=======
}

>>>>>>> origin/gestionikram
