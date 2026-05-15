package controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import util.DBConnection;
import util.Navigator;
import util.Session;

import java.io.File;
import java.io.IOException;
import java.sql.*;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class Documentcontroller {

    @FXML private Label labelNomModule;
    @FXML private Label labelCompteur;
    @FXML private FlowPane flowDocuments;
    @FXML private TextField tfRecherche;
    @FXML private ComboBox<String> cbFiltreType;
    @FXML private ComboBox<String> cbFiltreChapitre;

    private Connection conn;

    // =========================================================
    // INITIALIZE
    // =========================================================

    @FXML
    public void initialize() {
        conn = DBConnection.getInstance().getConnection();
        if (conn == null) { System.out.println("❌ Connexion BD null"); return; }

        if (Session.getModuleNom() != null)
            labelNomModule.setText(Session.getModuleNom());

        // Valeurs en minuscules comme dans la BD
        cbFiltreType.getItems().addAll("Tous", "cours", "td", "examen_ds", "lien_utile", "image", "video");
        cbFiltreType.setValue("Tous");
        cbFiltreChapitre.getItems().setAll("");
        cbFiltreChapitre.setValue("");
        cbFiltreChapitre.setDisable(true);
        cbFiltreChapitre.setPromptText("Chapitre indisponible");

        // On charge sans filtre module au départ (sera rechargé par setModuleNom)
        chargerDocuments("", "Tous", "");
    }

    // =========================================================
    // SET MODULE  — appelé par ModulesController après load()
    // =========================================================

    public void setModuleNom(String nom) {
        Session.setModuleNom(nom);
        if (labelNomModule != null) labelNomModule.setText(nom);
        chargerDocuments("", "Tous", "");
    }

    // =========================================================
    // CHARGER DOCUMENTS
    // =========================================================

    private void chargerDocuments(String recherche, String type, String chapitre) {
        flowDocuments.getChildren().clear();
        recherche = recherche == null ? "" : recherche.trim();
        type = type == null ? "Tous" : type;

        // Déterminer si on filtre par module_id ou pas
        int moduleId = Session.getModuleId();
        boolean hasModuleFilter = (moduleId > 0);

        // Construction dynamique de la requête
        // On utilise les vrais noms de colonnes : id_document, type_document, chapitre
        // La colonne module peut s'appeler module_id ou id_module — on détecte au runtime
        StringBuilder sql = new StringBuilder("SELECT * FROM documents");
        boolean hasWhere = false;

        if (hasModuleFilter) {
            sql.append(" WHERE id_module = ?");
            hasWhere = true;
        }

        if (!recherche.isBlank()) {
            sql.append(hasWhere ? " AND" : " WHERE");
            sql.append(" (LOWER(titre) LIKE ? OR LOWER(description) LIKE ?)");
            hasWhere = true;
        }

        if (!type.equals("Tous")) {
            sql.append(hasWhere ? " AND" : " WHERE");
            sql.append(" LOWER(type_document) = ?");
            hasWhere = true;
        }

        sql.append(" ORDER BY date_upload DESC, titre");

        System.out.println("DEBUG SQL: " + sql);
        System.out.println("DEBUG moduleId: " + moduleId);

        int count = 0;
        try {
            PreparedStatement ps = conn.prepareStatement(sql.toString());
            int idx = 1;

            if (hasModuleFilter)     ps.setInt(idx++, moduleId);
            if (!recherche.isBlank()) {
                String like = "%" + recherche.toLowerCase() + "%";
                ps.setString(idx++, like);
                ps.setString(idx++, like);
            }
            if (!type.equals("Tous")) ps.setString(idx++, type.toLowerCase());
            ResultSet rs = ps.executeQuery();

            cbFiltreChapitre.getItems().clear();
            cbFiltreChapitre.getItems().add("");


            while (rs.next()) {
                int    id       = rs.getInt("id_document");
                String titre    = rs.getString("titre");
                String desc     = rs.getString("description");
                String chap     = "";
                String typeDoc  = rs.getString("type_document");
                String fileUrl  = rs.getString("fichier_url");
                String fileType = getExtension(fileUrl);
                long   taille   = getFileSize(fileUrl);

                if (chap != null && !chap.isBlank() && !cbFiltreChapitre.getItems().contains(chap))
                    cbFiltreChapitre.getItems().add(chap);

                flowDocuments.getChildren().add(
                        creerCarte(id, titre, desc, chap, typeDoc, fileType, taille)
                );
                count++;
            }
            ps.close(); rs.close();

        } catch (SQLSyntaxErrorException e) {
            // Si module_id n'existe pas, réessayer sans ce filtre
            System.err.println("⚠ Erreur SQL (probablement colonne inconnue): " + e.getMessage());
            System.err.println("  → Vérifier le nom de la colonne module dans ta table 'documents'");
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        labelCompteur.setText(count + " document(s)");
    }

    // =========================================================
    // CARTE DOCUMENT
    // =========================================================

    private VBox creerCarte(int id, String titre, String desc,
                            String chapitre, String typeDoc,
                            String fileType, long taille) {

        String borderColor = getBorderColor(typeDoc);
        String badgeText   = (fileType != null && !fileType.isBlank()) ? fileType.toUpperCase() : "FILE";
        String badgeColor  = getBadgeColor(badgeText);

        Label badge = new Label(badgeText);
        badge.setStyle(
                "-fx-background-color:" + badgeColor + ";" +
                        "-fx-text-fill:white;" +
                        "-fx-font-size:11px;" +
                        "-fx-font-weight:bold;" +
                        "-fx-padding:3 10;" +
                        "-fx-background-radius:20;"
        );
        HBox badgeRow = new HBox(badge);
        badgeRow.setAlignment(Pos.TOP_LEFT);

        Label lblTitre = new Label(titre);
        lblTitre.setWrapText(true);
        lblTitre.setStyle("-fx-font-size:15px;-fx-font-weight:bold;-fx-text-fill:#023047;");

        Label lblDesc = new Label(desc != null ? desc : "");
        lblDesc.setWrapText(true);
        lblDesc.setStyle("-fx-font-size:12px;-fx-text-fill:#229DBC;");

        Label lblTaille = new Label((taille / 1024) + " Ko");
        lblTaille.setStyle("-fx-font-size:11px;-fx-text-fill:#8ECBE6;");

        // ── Cercle de progression ──
        double progression = calculerProgression(id);
        int pct = (int) Math.round(progression * 100);

        int size = 56;
        Canvas canvas = new Canvas(size, size);
        GraphicsContext gc = canvas.getGraphicsContext2D();

        // Fond gris
        gc.setStroke(Color.web("#E0F0F8"));
        gc.setLineWidth(5);
        gc.strokeArc(5, 5, size - 10, size - 10, 90, 360, javafx.scene.shape.ArcType.OPEN);

        // Arc coloré
        String arcHex = pct == 100 ? "#2d6a4f" : pct >= 50 ? "#229DBC" : "#FEB707";
        gc.setStroke(Color.web(arcHex));
        gc.setLineWidth(5);
        double angle = -(pct / 100.0 * 360);
        gc.strokeArc(5, 5, size - 10, size - 10, 90, angle, javafx.scene.shape.ArcType.OPEN);

        // Texte centré
        Label lblPct = new Label(pct + "%");
        lblPct.setStyle(
                "-fx-font-size:12px;" +
                        "-fx-font-weight:bold;" +
                        "-fx-text-fill:" + arcHex + ";"
        );
        lblPct.setPrefWidth(size);
        lblPct.setAlignment(Pos.CENTER);

        StackPane circlePane = new StackPane(canvas, lblPct);
        circlePane.setPrefSize(size, size);

        Label lblProgressLabel = new Label("Progression");
        lblProgressLabel.setStyle("-fx-font-size:10px;-fx-text-fill:#8ECBE6;");

        VBox progressCol = new VBox(2, circlePane, lblProgressLabel);
        progressCol.setAlignment(Pos.CENTER);

        HBox progressRow = new HBox(progressCol);
        progressRow.setAlignment(Pos.CENTER_RIGHT);

        VBox card = new VBox(10, badgeRow, lblTitre, lblDesc, lblTaille, progressRow);
        card.setPrefWidth(260);
        card.setPrefHeight(240);
        card.setPadding(new Insets(16));
        card.setStyle(
                "-fx-background-color:white;" +
                        "-fx-border-color:" + borderColor + ";" +
                        "-fx-border-width:2;" +
                        "-fx-border-radius:12;" +
                        "-fx-background-radius:12;" +
                        "-fx-effect:dropshadow(gaussian,rgba(0,0,0,0.07),8,0,0,2);" +
                        "-fx-cursor:hand;"
        );
        card.setOnMouseClicked(e -> ouvrirDetail(id, titre, card));
        return card;
    }

    // =========================================================
    // CALCUL PROGRESSION MODULE
    // =========================================================

    private double calculerProgression(int docId) {
        if (docId <= 0) return 0.0;
        try {
            // Récupérer l'id_module de ce document
            PreparedStatement ps0 = conn.prepareStatement(
                    "SELECT id_module FROM documents WHERE id_document = ?"
            );
            ps0.setInt(1, docId);
            ResultSet rs0 = ps0.executeQuery();
            int moduleId = rs0.next() ? rs0.getInt(1) : 0;
            ps0.close(); rs0.close();
            if (moduleId <= 0) return 0.0;

            // La table documents actuelle n'a pas de colonne chapitre : progression par document.
            PreparedStatement ps1 = conn.prepareStatement(
                    "SELECT COUNT(*) FROM documents WHERE id_module = ?"
            );
            ps1.setInt(1, moduleId);
            ResultSet rs1 = ps1.executeQuery();
            int total = rs1.next() ? rs1.getInt(1) : 0;
            ps1.close(); rs1.close();
            if (total == 0) return 0.0;

            // Nombre de chapitres lus par le user connecté dans ce module
            PreparedStatement ps2 = conn.prepareStatement(
                    "SELECT COUNT(*) FROM progression " +
                            "WHERE id_user = ? AND id_module = ?"
            );
            ps2.setInt(1, Session.getUserId());
            ps2.setInt(2, moduleId);
            ResultSet rs2 = ps2.executeQuery();
            int lus = rs2.next() ? rs2.getInt(1) : 0;
            ps2.close(); rs2.close();

            return Math.min(1.0, (double) lus / total);
        } catch (SQLException e) {
            e.printStackTrace();
            return 0.0;
        }
    }

    private String getExtension(String path) {
        if (path == null || path.isBlank() || !path.contains(".")) return "FILE";
        return path.substring(path.lastIndexOf('.') + 1).toUpperCase();
    }

    private long getFileSize(String path) {
        if (path == null || path.isBlank()) return 0;
        File file = new File(path);
        return file.exists() ? file.length() : 0;
    }

    private void ouvrirDetail(int docId, String docNom, Node source) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/DetailDocument.fxml"));
            Parent root = loader.load();
            DetailDocumentController ctrl = loader.getController();
            ctrl.initData(docId, docNom);
            Navigator.applySessionRoleLabels(root);
            Stage stage = (Stage) source.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) { e.printStackTrace(); }
    }

    // =========================================================
    // HANDLERS
    // =========================================================

    @FXML public void handleRechercher(ActionEvent e) {
        String recherche = tfRecherche.getText();
        String type      = cbFiltreType.getValue();
        String chapitre  = cbFiltreChapitre.getValue();
        if (chapitre == null) chapitre = "";
        chargerDocuments(recherche, type, chapitre);
    }

    @FXML public void handleRetour(ActionEvent e)       { naviguerVers("Modules.fxml", e); }
    @FXML public void handleDashboard(ActionEvent e)     { Navigator.goDashboard((Node) e.getSource()); }
    @FXML public void handleModules(ActionEvent e)       { naviguerVers("Modules.fxml", e); }
    @FXML public void handleDocuments(ActionEvent e)     { }
    @FXML public void handleUpload(ActionEvent e)        { naviguerVers("UploadDocument.fxml", e); }
    @FXML public void handleDeconnexion(ActionEvent e)   { Session.clear(); naviguerVers("Connexion.fxml", e); }
    @FXML public void handleQuiz(ActionEvent e)          { naviguerVers("quiz.fxml", e); }
    @FXML public void handlePlanning(ActionEvent e)      { naviguerVers("Planning.fxml", e); }
    @FXML public void handleJira(ActionEvent e)          { }
    @FXML public void handleFavoris(ActionEvent e)       { naviguerVers("Favoris.fxml", e); }
    @FXML public void handleProjets(ActionEvent e)       { naviguerVers("ProjectView.fxml", e); }
    @FXML public void handleEvenements(ActionEvent e)    { naviguerVers("Evenements.fxml", e); }
    @FXML public void handleProfil(ActionEvent e)        { naviguerVers("Profil.fxml", e); }
    @FXML public void handleNotification(ActionEvent e)  { naviguerVers("Notification.fxml", e); }
    @FXML public void handleNotifications(ActionEvent e) { naviguerVers("Notification.fxml", e); }

    private void naviguerVers(String fxml, ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/" + fxml));
            Parent root = loader.load();
            Navigator.applySessionRoleLabels(root);
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) { e.printStackTrace(); }
    }

    // =========================================================
    // COULEURS
    // =========================================================

    private String getBorderColor(String type) {
        if (type == null) return "#229DBC";
        switch (type.toLowerCase()) {
            case "td":                      return "#FEB707";
            case "ds": case "examen":       return "#FB8402";
            case "image": case "video":     return "#2d6a4f";
            default:                        return "#229DBC";
        }
    }

    private String getBadgeColor(String ft) {
        switch (ft) {
            case "PDF":  return "#229DBC";
            case "PPTX": return "#FEB707";
            case "DOCX": return "#FB8402";
            default:     return "#8ECBE6";
        }
    }
}
