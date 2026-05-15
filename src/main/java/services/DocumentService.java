package services;

import enums.DocumentStatut;
import enums.DocumentType;
import models.Document;
import models.Notification;
import util.DBConnection;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class DocumentService implements IService<Document> {

    private final Connection conn;

    public DocumentService() {
        this.conn = DBConnection.getInstance().getConn();
    }

    @Override
    public void add(Document d) {
        String sql = "INSERT INTO documents " +
                "(titre, description, type_document, fichier_url, id_module, id_uploadeur, statut) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, d.getTitre());
            ps.setString(2, d.getDescription());
            ps.setString(3, d.getTypeDocument().name());
            ps.setString(4, d.getFichierUrl());
            ps.setInt(5, d.getIdModule());
            ps.setInt(6, d.getIdUploadeur());

            if (d.getStatut() == null) {
                ps.setString(7, "en_attente");
            } else {
                ps.setString(7, d.getStatut().name().toLowerCase());
            }

            ps.executeUpdate();
            System.out.println("Document ajouté en attente d'approbation.");

        } catch (SQLException e) {
            System.out.println("Erreur add document : " + e.getMessage());
        }
    }

    @Override
    public void update(Document d) {
        String sql = "UPDATE documents SET titre=?, description=?, type_document=?, fichier_url=?, id_module=?, statut=? " +
                "WHERE id_document=?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, d.getTitre());
            ps.setString(2, d.getDescription());
            ps.setString(3, d.getTypeDocument().name());
            ps.setString(4, d.getFichierUrl());
            ps.setInt(5, d.getIdModule());

            if (d.getStatut() == null) {
                ps.setString(6, "en_attente");
            } else {
                ps.setString(6, d.getStatut().name().toLowerCase());
            }

            ps.setInt(7, d.getId());

            ps.executeUpdate();
            System.out.println("Document modifié avec succès.");

        } catch (SQLException e) {
            System.out.println("Erreur update document : " + e.getMessage());
        }
    }

    @Override
    public void delete(Document d) {
        String sql = "DELETE FROM documents WHERE id_document=?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, d.getId());
            ps.executeUpdate();

            System.out.println("Document supprimé avec succès.");

        } catch (SQLException e) {
            System.out.println("Erreur delete document : " + e.getMessage());
        }
    }

    @Override
    public List<Document> getAll() {
        String sql = "SELECT * FROM documents ORDER BY date_upload DESC";
        return getDocuments(sql);
    }

    public List<Document> getDocumentsApprouves() {
        String sql = "SELECT * FROM documents WHERE statut='approuve' ORDER BY date_upload DESC";
        return getDocuments(sql);
    }

    public List<Document> getDocumentsEnAttente() {
        String sql = "SELECT * FROM documents WHERE statut='en_attente' ORDER BY date_upload DESC";
        return getDocuments(sql);
    }

    public List<Document> getByModule(int idModule) {
        List<Document> documents = new ArrayList<>();

        String sql = "SELECT * FROM documents WHERE id_module=? AND statut='approuve' ORDER BY date_upload DESC";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, idModule);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    documents.add(mapRow(rs));
                }
            }

        } catch (SQLException e) {
            System.out.println("Erreur getByModule document : " + e.getMessage());
        }

        return documents;
    }

    public List<Document> getByType(DocumentType type) {
        List<Document> documents = new ArrayList<>();

        String sql = "SELECT * FROM documents WHERE type_document=? AND statut='approuve' ORDER BY date_upload DESC";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, type.name());

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    documents.add(mapRow(rs));
                }
            }

        } catch (SQLException e) {
            System.out.println("Erreur getByType document : " + e.getMessage());
        }

        return documents;
    }

    public Document getById(int idDocument) {
        String sql = "SELECT * FROM documents WHERE id_document=?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, idDocument);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
            }

        } catch (SQLException e) {
            System.out.println("Erreur getById document : " + e.getMessage());
        }

        return null;
    }

    public void approuver(int idDocument, int idAdmin) {
        changerStatut(idDocument, DocumentStatut.APPROUVE, idAdmin);

        Document d = getById(idDocument);

        if (d != null) {
            NotificationService notificationService = new NotificationService();

            Notification notification = new Notification(
                    "Document approuvé",
                    "Votre document \"" + d.getTitre() + "\" a été approuvé.",
                    "document",
                    d.getIdUploadeur()
            );

            notificationService.add(notification);
        }
    }

    public void rejeter(int idDocument, int idAdmin) {
        changerStatut(idDocument, DocumentStatut.REJETE, idAdmin);

        Document d = getById(idDocument);

        if (d != null) {
            NotificationService notificationService = new NotificationService();

            Notification notification = new Notification(
                    "Document rejeté",
                    "Votre document \"" + d.getTitre() + "\" a été rejeté.",
                    "document",
                    d.getIdUploadeur()
            );

            notificationService.add(notification);
        }
    }

    private void changerStatut(int idDocument, DocumentStatut statut, int idAdmin) {
        String sql = "UPDATE documents SET statut=?, id_approbateur=?, date_approbation=? WHERE id_document=?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, statut.name().toLowerCase());
            ps.setInt(2, idAdmin);
            ps.setTimestamp(3, Timestamp.valueOf(LocalDateTime.now()));
            ps.setInt(4, idDocument);

            ps.executeUpdate();

        } catch (SQLException e) {
            System.out.println("Erreur changerStatut document : " + e.getMessage());
        }
    }

    private List<Document> getDocuments(String sql) {
        List<Document> documents = new ArrayList<>();

        try (Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                documents.add(mapRow(rs));
            }

        } catch (SQLException e) {
            System.out.println("Erreur getDocuments : " + e.getMessage());
        }

        return documents;
    }

    private Document mapRow(ResultSet rs) throws SQLException {
        Document d = new Document();

        d.setId(rs.getInt("id_document"));
        d.setTitre(rs.getString("titre"));
        d.setDescription(rs.getString("description"));

        String typeDocument = rs.getString("type_document");
        if (typeDocument != null) {
            try {
                d.setTypeDocument(DocumentType.valueOf(typeDocument.trim().toUpperCase()));
            } catch (IllegalArgumentException e) {
                d.setTypeDocument(DocumentType.COURS);
            }
        }

        d.setFichierUrl(rs.getString("fichier_url"));
        d.setIdModule(rs.getInt("id_module"));
        d.setIdUploadeur(rs.getInt("id_uploadeur"));

        String statut = rs.getString("statut");
        if (statut != null) {
            d.setStatut(DocumentStatut.valueOf(statut.toUpperCase()));
        }

        Timestamp dateUpload = rs.getTimestamp("date_upload");
        if (dateUpload != null) {
            d.setDateUpload(dateUpload.toLocalDateTime());
        }

        Timestamp dateApprobation = rs.getTimestamp("date_approbation");
        if (dateApprobation != null) {
            d.setDateApprobation(dateApprobation.toLocalDateTime());
        }

        int idApprobateur = rs.getInt("id_approbateur");
        if (rs.wasNull()) {
            d.setIdApprobateur(null);
        } else {
            d.setIdApprobateur(idApprobateur);
        }

        return d;
    }
}
