package services;
import models.Module;
import util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import services.NotificationService;
public class ModuleService implements IService<Module> {
    private Connection conn;

    public ModuleService(Connection conn) {
        this.conn = DBConnection.getInstance().getConn();
    }

    @Override
    public void add(Module m) {
        String req = "INSERT INTO modules (nom, description, icone) VALUES (?, ?, ?)";

        try (PreparedStatement ps = conn.prepareStatement(req)) {

            ps.setString(1, m.getNom());
            ps.setString(2, m.getDescription());
            ps.setString(3, m.getIcone());

            ps.executeUpdate();
            NotificationService notificationService = new NotificationService(conn);

            notificationService.notifierTousLesUtilisateurs(
                    "Nouveau module",
                    "Un nouveau module a ete ajoute : " + m.getNom(),
                    "MODULE"
            );
            System.out.println("Module ajoute avec succes.");

        } catch (SQLException e) {
            System.out.println("Erreur add module : " + e.getMessage());
        }

    }

    @Override
    public void update(Module m) {
        String req = "UPDATE modules SET nom=?, description=?, icone=? WHERE id=?";

        try (PreparedStatement ps = conn.prepareStatement(req)) {

            ps.setString(1, m.getNom());
            ps.setString(2, m.getDescription());
            ps.setString(3, m.getIcone());
            ps.setInt(4, m.getId());

            ps.executeUpdate();
            System.out.println("Module modifie avec succes.");
            NotificationService notificationService = new NotificationService(conn);
            notificationService.notifierTousLesUtilisateurs(
                    "Nouveau module",
                    "Un nouveau module a ete ajoute : " + m.getNom(),
                    "MODULE"
            );
        } catch (SQLException e) {
            System.out.println("Erreur update module : " + e.getMessage());
        }

    }

    @Override
    public void delete(Module m) {
        String req = "DELETE FROM modules WHERE id=?";

        try (PreparedStatement ps = conn.prepareStatement(req)) {

            ps.setInt(1, m.getId());

            ps.executeUpdate();
            System.out.println("Module supprime avec succes.");

        } catch (SQLException e) {
            System.out.println("Erreur delete module : " + e.getMessage());
        }
    }

    @Override
    public List<Module> getAll() {
        return getModules("SELECT * FROM modules");    }

    public Module getById(int id) {
        String req = "SELECT * FROM modules WHERE id=?";

        try (PreparedStatement ps = conn.prepareStatement(req)) {

            ps.setInt(1, id);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return mapRow(rs);
            }

        } catch (SQLException e) {
            System.out.println("Erreur getById module : " + e.getMessage());
        }

        return null;
    }

    public List<Module> searchByNom(String keyword) {
        List<Module> list = new ArrayList<>();

        String req = "SELECT * FROM modules WHERE nom LIKE ?";

        try (PreparedStatement ps = conn.prepareStatement(req)) {

            ps.setString(1, "%" + keyword + "%");

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                list.add(mapRow(rs));
            }

        } catch (SQLException e) {
            System.out.println("Erreur searchByNom module : " + e.getMessage());
        }

        return list;
    }

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
        }

        return m;
    }
}

