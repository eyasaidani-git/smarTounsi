package services;
import models.ToDoItem;
import util.DBConnection;

import java.sql.*;
import java.util.List;
import java.util.ArrayList;
public class ToDoItemService implements IService<ToDoItem> {
    private Connection conn;

    public ToDoItemService() {
        this.conn = DBConnection.getInstance().getConn();
    }

    @Override
    public void add(ToDoItem t) {
        String req = "INSERT INTO todo_item (titre, description, duree_minutes, " +
                "heure_debut, id_planning, id_document) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(req)) {
            ps.setString(1, t.getTitre());
            ps.setString(2, t.getDescription());
            ps.setInt(3, t.getDureeMinutes());
            ps.setString(4, t.getHeureDebut());
            ps.setInt(5, t.getIdPlanning());
            if (t.getIdDocument() > 0) ps.setInt(6, t.getIdDocument());
            else ps.setNull(6, Types.INTEGER);
            ps.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Erreur add todo : " + e.getMessage());
        }
    }

    @Override
    public void update(ToDoItem t) {
        String req = "UPDATE todo_item SET titre=?, statut=?, duree_minutes=? WHERE id=?";
        try (PreparedStatement ps = conn.prepareStatement(req)) {
            ps.setString(1, t.getTitre());
            ps.setString(2, t.getStatut());
            ps.setInt(3, t.getDureeMinutes());
            ps.setInt(4, t.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Erreur update todo : " + e.getMessage());
        }
    }

    @Override
    public void delete(ToDoItem t) {
        try (PreparedStatement ps = conn.prepareStatement("DELETE FROM todo_item WHERE id=?")) {
            ps.setInt(1, t.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Erreur delete todo : " + e.getMessage());
        }
    }

    @Override
    public List<ToDoItem> getAll() {
        return getByPlanning(-1);
    }
    public List<ToDoItem> getByPlanning(int idPlanning) {
        List<ToDoItem> list = new ArrayList<>();
        String req = "SELECT * FROM todo_item WHERE id_planning=? ORDER BY heure_debut";
        try (PreparedStatement ps = conn.prepareStatement(req)) {
            ps.setInt(1, idPlanning);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                ToDoItem t = new ToDoItem();
                t.setId(rs.getInt("id"));
                t.setTitre(rs.getString("titre"));
                t.setDescription(rs.getString("description"));
                t.setDureeMinutes(rs.getInt("duree_minutes"));
                t.setHeureDebut(rs.getString("heure_debut"));
                t.setStatut(rs.getString("statut"));
                t.setIdPlanning(rs.getInt("id_planning"));
                t.setIdDocument(rs.getInt("id_document"));
                list.add(t);
            }
        } catch (SQLException e) {
            System.out.println("Erreur getByPlanning : " + e.getMessage());
        }
        return list;
    }
}