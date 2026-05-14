package services;
<<<<<<< HEAD

import enums.TodoStatus;
=======
>>>>>>> origin/gestionikram
import models.ToDoItem;
import util.DBConnection;

import java.sql.*;
<<<<<<< HEAD
import java.util.ArrayList;
import java.util.List;

public class ToDoItemService implements IService<ToDoItem> {
    private final Connection conn;
=======
import java.util.List;
import java.util.ArrayList;
public class ToDoItemService implements IService<ToDoItem> {
    private Connection conn;
>>>>>>> origin/gestionikram

    public ToDoItemService() {
        this.conn = DBConnection.getInstance().getConn();
    }

    @Override
    public void add(ToDoItem t) {
<<<<<<< HEAD
        String sql = "INSERT INTO todo_item (id_planning, id_document, lien_externe, fichier_url, nom_document, description, heure, ordre, statut) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, t.getIdPlanning());
            setNullableInt(ps, 2, t.getIdDocument());
            ps.setString(3, t.getLienExterne());
            ps.setString(4, t.getFichierUrl());
            ps.setString(5, t.getNomDocument());
            ps.setString(6, t.getDescription());
            if (t.getHeure() == null) {
                ps.setNull(7, Types.TIME);
            } else {
                ps.setTime(7, Time.valueOf(t.getHeure()));
            }
            ps.setInt(8, t.getOrdre());
            ps.setString(9, t.getStatut() == null ? TodoStatus.A_FAIRE.name() : t.getStatut().name());
            ps.executeUpdate();
            System.out.println("ToDo ajoute avec succes.");
=======
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
>>>>>>> origin/gestionikram
        } catch (SQLException e) {
            System.out.println("Erreur add todo : " + e.getMessage());
        }
    }

    @Override
    public void update(ToDoItem t) {
<<<<<<< HEAD
        String sql = "UPDATE todo_item SET id_document=?, lien_externe=?, fichier_url=?, nom_document=?, " +
                "description=?, heure=?, ordre=?, statut=? WHERE id_todo=?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            setNullableInt(ps, 1, t.getIdDocument());
            ps.setString(2, t.getLienExterne());
            ps.setString(3, t.getFichierUrl());
            ps.setString(4, t.getNomDocument());
            ps.setString(5, t.getDescription());
            if (t.getHeure() == null) {
                ps.setNull(6, Types.TIME);
            } else {
                ps.setTime(6, Time.valueOf(t.getHeure()));
            }
            ps.setInt(7, t.getOrdre());
            ps.setString(8, t.getStatut().name());
            ps.setInt(9, t.getId());
            ps.executeUpdate();
            System.out.println("ToDo modifie avec succes.");
=======
        String req = "UPDATE todo_item SET titre=?, statut=?, duree_minutes=? WHERE id=?";
        try (PreparedStatement ps = conn.prepareStatement(req)) {
            ps.setString(1, t.getTitre());
            ps.setString(2, t.getStatut());
            ps.setInt(3, t.getDureeMinutes());
            ps.setInt(4, t.getId());
            ps.executeUpdate();
>>>>>>> origin/gestionikram
        } catch (SQLException e) {
            System.out.println("Erreur update todo : " + e.getMessage());
        }
    }

    @Override
    public void delete(ToDoItem t) {
<<<<<<< HEAD
        String sql = "DELETE FROM todo_item WHERE id_todo=?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, t.getId());
            ps.executeUpdate();
            System.out.println("ToDo supprime avec succes.");
=======
        try (PreparedStatement ps = conn.prepareStatement("DELETE FROM todo_item WHERE id=?")) {
            ps.setInt(1, t.getId());
            ps.executeUpdate();
>>>>>>> origin/gestionikram
        } catch (SQLException e) {
            System.out.println("Erreur delete todo : " + e.getMessage());
        }
    }

    @Override
    public List<ToDoItem> getAll() {
<<<<<<< HEAD
        List<ToDoItem> list = new ArrayList<>();
        String sql = "SELECT * FROM todo_item ORDER BY id_planning ASC, heure ASC, ordre ASC";

        try (Statement st = conn.createStatement(); ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                list.add(mapRow(rs));
            }
        } catch (SQLException e) {
            System.out.println("Erreur getAll todo : " + e.getMessage());
        }

        return list;
    }

    public List<ToDoItem> getByPlanning(int idPlanning) {
        List<ToDoItem> list = new ArrayList<>();
        String sql = "SELECT * FROM todo_item WHERE id_planning=? ORDER BY heure ASC, ordre ASC";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idPlanning);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(mapRow(rs));
            }
        } catch (SQLException e) {
            System.out.println("Erreur getByPlanning todo : " + e.getMessage());
        }

        return list;
    }

    public void changerStatut(int idTodo, TodoStatus statut) {
        String sql = "UPDATE todo_item SET statut=? WHERE id_todo=?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, statut.name());
            ps.setInt(2, idTodo);
            ps.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Erreur changerStatut todo : " + e.getMessage());
        }
    }

    private ToDoItem mapRow(ResultSet rs) throws SQLException {
        ToDoItem t = new ToDoItem();
        t.setId(rs.getInt("id_todo"));
        t.setIdPlanning(rs.getInt("id_planning"));

        int idDocument = rs.getInt("id_document");
        t.setIdDocument(rs.wasNull() ? null : idDocument);

        t.setLienExterne(rs.getString("lien_externe"));
        t.setFichierUrl(rs.getString("fichier_url"));
        t.setNomDocument(rs.getString("nom_document"));
        t.setDescription(rs.getString("description"));

        Time heure = rs.getTime("heure");
        t.setHeure(heure == null ? null : heure.toLocalTime());

        t.setOrdre(rs.getInt("ordre"));
        t.setStatut(TodoStatus.valueOf(rs.getString("statut")));
        return t;
    }

    private void setNullableInt(PreparedStatement ps, int index, Integer value) throws SQLException {
        if (value == null) {
            ps.setNull(index, Types.INTEGER);
        } else {
            ps.setInt(index, value);
        }
    }
}
=======
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
>>>>>>> origin/gestionikram
