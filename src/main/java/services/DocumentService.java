package services;
import models.Document;
import util.DBConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DocumentService implements IService<Document> {
    private Connection conn;
    public DocumentService(Connection conn) {
        this.conn = DBConnection.getInstance().getConn();
    }
    @Override
    public void add(Document d) {
        String req = "INSERT INTO document (titre, type, contenu, id_module, id_utilisateur) " +
                "VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(req)) {
            ps.setString(1, d.getTitre());
            ps.setString(2, d.getType());
            ps.setString(3, d.getContenu());
            ps.setInt(4, d.getIdModule());
            ps.setInt(5, d.getIdUtilisateur());
            ps.executeUpdate();
            System.out.println("Document ajouté (en attente d'approbation).");
        } catch (SQLException e) {
            System.out.println("Erreur add document : " + e.getMessage());
        }

    }

    @Override
    public void update(Document d) {
        String req = "UPDATE document SET titre=?, type=?, contenu=?, id_module=? WHERE id=?";
        try (PreparedStatement ps = conn.prepareStatement(req)) {
            ps.setString(1, d.getTitre());
            ps.setString(2, d.getType());
            ps.setString(3, d.getContenu());
            ps.setInt(4, d.getIdModule());
            ps.setInt(5, d.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Erreur update document : " + e.getMessage());
        }

    }

    @Override
    public void delete(Document d) {
        String req = "DELETE FROM document WHERE id=?";
        try (PreparedStatement ps = conn.prepareStatement(req)) {
            ps.setInt(1, d.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Erreur delete document : " + e.getMessage());
        }

    }

    @Override
    public List<Document> getAll() {
        return getDocuments("SELECT * FROM document");

    }
    public List<Document> getByModule(int idModule) {
        List<Document> list = new ArrayList<>();
        String req = "SELECT * FROM document WHERE id_module=? AND approuve=1";
        try (PreparedStatement ps = conn.prepareStatement(req)) {
            ps.setInt(1, idModule);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) {
            System.out.println("Erreur getByModule : " + e.getMessage());
        }
        return list;
    }
    public void approuver(int idDocument) {
        String req = "UPDATE document SET approuve=1 WHERE id=?";
        try (PreparedStatement ps = conn.prepareStatement(req)) {
            ps.setInt(1, idDocument);
            ps.executeUpdate();
            System.out.println("Document approuvé ✔");
        } catch (SQLException e) {
            System.out.println("Erreur approbation : " + e.getMessage());
        }
    }



    private List<Document> getDocuments(String sql) {
        List<Document> list = new ArrayList<>();
        try (Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) {
            System.out.println("Erreur getDocuments : " + e.getMessage());
        }
        return list;
    }
    private Document mapRow(ResultSet rs) throws SQLException {
        Document d = new Document();
        d.setId(rs.getInt("id"));
        d.setTitre(rs.getString("titre"));
        d.setType(rs.getString("type"));
        d.setContenu(rs.getString("contenu"));
        d.setIdModule(rs.getInt("id_module"));
        d.setIdUtilisateur(rs.getInt("id_utilisateur"));
        d.setApprouve(rs.getBoolean("approuve"));
        return d;
    }
}
