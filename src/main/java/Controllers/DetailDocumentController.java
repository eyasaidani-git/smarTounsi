package Controllers;

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
import util.Session;

import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DetailDocumentController {

    @FXML private Label labelBreadcrumb;
    @FXML private Label labelTitreDoc;
    @FXML private Label labelDescDoc;
    @FXML private HBox  hboxFiltresType;
    @FXML private Label labelSectionTitre;
    @FXML private Label labelCompteur;
    @FXML private FlowPane flowFichiers;

    private Connection conn;
    private int    documentId;
    private String documentNom;

    // Données brutes de la BD pour ce document
    private List<FichierRow> tousLesFichiers = new ArrayList<>();
    private String filtreActuel = "cours";

    // ─────────────────────────────────────────
    //  INIT
    // ─────────────────────────────────────────

    public void initData(int docId, String docNom) {
        this.documentId  = docId;
        this.documentNom = docNom;

        conn = DBConnection.getInstance().getConnection();
        if (conn == null) return;

        chargerInfoDocument();
        chargerFichiers();
        construireFiltres();
        afficherFiltres(filtreActuel);
    }

    // ─────────────────────────────────────────
    //  CHARGER INFO PRINCIPALE DU DOCUMENT
    // ─────────────────────────────────────────

    private void chargerInfoDocument() {
        try {
            PreparedStatement ps = conn.prepareStatement(
                    "SELECT titre, description FROM documents WHERE id_document = ?"
            );
            ps.setInt(1, documentId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                String titre = rs.getString("titre");
                String desc  = rs.getString("description");
                labelTitreDoc.setText(titre);
                labelDescDoc.setText(desc);
                labelBreadcrumb.setText(
                        "Modules  ›  " + Session.getModuleNom() + "  ›  " + titre
                );
            }
            ps.close(); rs.close();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    // ─────────────────────────────────────────
    //  CHARGER TOUS LES FICHIERS DU DOC
    //  (On récupère tous les enregistrements du
    //   même titre/module pour simuler les sous-fichiers.
    //   Si ta table a une FK parent_doc_id, adapte ici.)
    // ─────────────────────────────────────────

    private void chargerFichiers() {
        tousLesFichiers.clear();
        try {
            // On charge tous les docs du même module et même chapitre parent
            // comme "fichiers" du document cliqué.
            // Si tu as une relation parent/enfant, remplace par la vraie FK.
            PreparedStatement ps = conn.prepareStatement(
                    "SELECT id_document AS id, titre, description, type_document, fichier_type, " +
                            "taille_fichier, chapitre, fichier_nom, fichier_url " +
                            "FROM documents " +
                            "WHERE id_module = ? " +
                            "ORDER BY chapitre, titre"
            );
            ps.setInt(1, Session.getModuleId());
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                FichierRow f = new FichierRow();
                f.id         = rs.getInt("id");
                f.titre      = rs.getString("titre");
                f.desc       = rs.getString("description");
                f.typeDoc    = rs.getString("type_document");
                f.fileType   = rs.getString("fichier_type");
                f.taille     = rs.getLong("taille_fichier");
                f.chapitre   = rs.getString("chapitre");
                f.fichierNom = rs.getString("fichier_nom");
                f.path       = rs.getString("fichier_url");
                tousLesFichiers.add(f);
            }
            ps.close(); rs.close();

            // Types distincts disponibles
            filtreActuel = tousLesFichiers.isEmpty() ? "Cours"
                    : tousLesFichiers.get(0).typeDoc;

        } catch (SQLException e) { e.printStackTrace(); }
    }

    // ─────────────────────────────────────────
    //  CONSTRUIRE LES BOUTONS FILTRE (Cours / TD / DS …)
    // ─────────────────────────────────────────

    private void construireFiltres() {
        hboxFiltresType.getChildren().clear();
        hboxFiltresType.setSpacing(8);

        List<String> types = new ArrayList<>();
        for (FichierRow f : tousLesFichiers) {
            if (f.typeDoc != null && !types.contains(f.typeDoc))
                types.add(f.typeDoc);
        }

        for (String t : types) {
            Button btn = new Button(getIconType(t) + "  " + t);
            btn.setStyle(styleFiltreBouton(t.equalsIgnoreCase(filtreActuel)));
            btn.setOnAction(e -> {
                filtreActuel = t;
                // reset styles
                hboxFiltresType.getChildren().forEach(node -> {
                    if (node instanceof Button)
                        ((Button) node).setStyle(styleFiltreBouton(false));
                });
                btn.setStyle(styleFiltreBouton(true));
                afficherFiltres(t);
            });
            hboxFiltresType.getChildren().add(btn);
        }
    }

    private String styleFiltreBouton(boolean actif) {
        if (actif) return
                "-fx-background-color:#229DBC;" +
                        "-fx-text-fill:white;" +
                        "-fx-font-size:13px;" +
                        "-fx-font-weight:bold;" +
                        "-fx-padding:8 18;" +
                        "-fx-background-radius:8;" +
                        "-fx-cursor:hand;";
        return
                "-fx-background-color:white;" +
                        "-fx-text-fill:#023047;" +
                        "-fx-font-size:13px;" +
                        "-fx-border-color:#8ECBE6;" +
                        "-fx-border-width:1.5;" +
                        "-fx-padding:8 18;" +
                        "-fx-background-radius:8;" +
                        "-fx-border-radius:8;" +
                        "-fx-cursor:hand;";
    }

    // ─────────────────────────────────────────
    //  AFFICHER LES CARTES FILTRÉES
    // ─────────────────────────────────────────

    private void afficherFiltres(String type) {
        flowFichiers.getChildren().clear();
        int count = 0;
        for (FichierRow f : tousLesFichiers) {
            if (type.equalsIgnoreCase(f.typeDoc)) {
                flowFichiers.getChildren().add(creerCarteFichier(f));
                count++;
            }
        }
        labelSectionTitre.setText(getIconType(type) + "  " + type + "s disponibles");
        labelCompteur.setText(count + " fichier" + (count > 1 ? "s" : ""));
    }

    // ─────────────────────────────────────────
    //  CARTE FICHIER  (style image 2)
    // ─────────────────────────────────────────

    private VBox creerCarteFichier(FichierRow f) {
        String cardColor  = getCardColor(f.typeDoc);
        String badgeColor = getBadgeColor(f.fileType);
        String fileLabel  = (f.fileType != null) ? f.fileType.toUpperCase() : "FILE";
        String chapNum    = (f.chapitre != null) ? f.chapitre : "";

        // Header coloré
        VBox header = new VBox(4);
        header.setPadding(new Insets(14, 14, 10, 14));
        header.setStyle("-fx-background-color:" + cardColor + ";-fx-background-radius:10 10 0 0;");

        Label lblTypeIcon = new Label(getIconType(f.typeDoc) + "  " + f.typeDoc);
        lblTypeIcon.setStyle("-fx-text-fill:white;-fx-font-size:12px;-fx-font-weight:bold;");
        header.getChildren().add(lblTypeIcon);

        // Body
        VBox body = new VBox(6);
        body.setPadding(new Insets(12, 14, 14, 14));

        Label lblTitre = new Label(f.titre);
        lblTitre.setWrapText(true);
        lblTitre.setStyle("-fx-font-size:14px;-fx-font-weight:bold;-fx-text-fill:#023047;");

        String sousTitre = Session.getModuleNom()
                + (chapNum.isBlank() ? "" : " — " + chapNum);
        Label lblSous = new Label(sousTitre);
        lblSous.setStyle("-fx-font-size:11px;-fx-text-fill:#229DBC;");

        // Badge type fichier + taille
        Label badge = new Label(fileLabel);
        badge.setStyle(
                "-fx-background-color:" + badgeColor + ";" +
                        "-fx-text-fill:white;" +
                        "-fx-font-size:10px;" +
                        "-fx-font-weight:bold;" +
                        "-fx-padding:2 8;" +
                        "-fx-background-radius:12;"
        );
        Label lblKo = new Label((f.taille / 1024) + " Ko");
        lblKo.setStyle("-fx-font-size:11px;-fx-text-fill:#8ECBE6;");

        HBox badgeRow = new HBox(8, badge, lblKo);
        badgeRow.setAlignment(Pos.CENTER_LEFT);

        // Bouton télécharger
        Button btnDl = new Button("⬇  Télécharger");
        btnDl.setMaxWidth(Double.MAX_VALUE);
        btnDl.setStyle(
                "-fx-background-color:#023047;" +
                        "-fx-text-fill:white;" +
                        "-fx-font-size:13px;" +
                        "-fx-font-weight:bold;" +
                        "-fx-padding:9 0;" +
                        "-fx-background-radius:8;" +
                        "-fx-cursor:hand;"
        );
        btnDl.setOnAction(e -> ouvrirFichier(f.path, f.titre, btnDl));

        // Bouton "Marquer comme lu"
        boolean dejaLu = estChapitreLu(f.chapitre);
        Button btnLu = new Button(dejaLu ? "\u2705  Lu" : "\u2611  Marquer comme lu");
        btnLu.setMaxWidth(Double.MAX_VALUE);
        btnLu.setStyle(styleBtnLu(dejaLu));
        btnLu.setOnAction(e -> {
            boolean nowLu = marquerChapitreLu(f.chapitre);
            btnLu.setText(nowLu ? "\u2705  Lu" : "\u2611  Marquer comme lu");
            btnLu.setStyle(styleBtnLu(nowLu));
        });

        body.getChildren().addAll(lblTitre, lblSous, badgeRow, btnDl, btnLu);

        VBox card = new VBox(header, body);
        card.setPrefWidth(270);
        card.setStyle(
                "-fx-background-color:white;" +
                        "-fx-border-color:" + cardColor + ";" +
                        "-fx-border-width:2;" +
                        "-fx-border-radius:12;" +
                        "-fx-background-radius:12;" +
                        "-fx-effect:dropshadow(gaussian,rgba(0,0,0,0.08),8,0,0,2);"
        );
        return card;
    }

    // ─────────────────────────────────────────
    //  PROGRESSION : MARQUER CHAPITRE LU
    // ─────────────────────────────────────────

    private boolean estChapitreLu(String chapitre) {
        if (chapitre == null || chapitre.isBlank()) return false;
        try {
            PreparedStatement ps = conn.prepareStatement(
                    "SELECT 1 FROM progression WHERE id_user = ? AND id_module = ? AND chapitre = ?"
            );
            ps.setInt(1, Session.getUserId());
            ps.setInt(2, Session.getModuleId());
            ps.setString(3, chapitre);
            ResultSet rs = ps.executeQuery();
            boolean lu = rs.next();
            ps.close(); rs.close();
            return lu;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    /** Bascule l'état lu/non-lu et retourne le nouvel état. */
    private boolean marquerChapitreLu(String chapitre) {
        if (chapitre == null || chapitre.isBlank()) return false;
        boolean etaitLu = estChapitreLu(chapitre);
        try {
            if (etaitLu) {
                PreparedStatement ps = conn.prepareStatement(
                        "DELETE FROM progression WHERE id_user = ? AND id_module = ? AND chapitre = ?"
                );
                ps.setInt(1, Session.getUserId());
                ps.setInt(2, Session.getModuleId());
                ps.setString(3, chapitre);
                ps.executeUpdate(); ps.close();
            } else {
                PreparedStatement ps = conn.prepareStatement(
                        "INSERT IGNORE INTO progression (id_user, id_module, chapitre) VALUES (?, ?, ?)"
                );
                ps.setInt(1, Session.getUserId());
                ps.setInt(2, Session.getModuleId());
                ps.setString(3, chapitre);
                ps.executeUpdate(); ps.close();
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return !etaitLu;
    }

    private String styleBtnLu(boolean lu) {
        if (lu) return
                "-fx-background-color:#2d6a4f;" +
                        "-fx-text-fill:white;" +
                        "-fx-font-size:13px;" +
                        "-fx-font-weight:bold;" +
                        "-fx-padding:9 0;" +
                        "-fx-background-radius:8;" +
                        "-fx-cursor:hand;";
        return
                "-fx-background-color:#8ECBE6;" +
                        "-fx-text-fill:#023047;" +
                        "-fx-font-size:13px;" +
                        "-fx-font-weight:bold;" +
                        "-fx-padding:9 0;" +
                        "-fx-background-radius:8;" +
                        "-fx-cursor:hand;";
    }

    // ─────────────────────────────────────────
    //  TÉLÉCHARGEMENT / OUVERTURE FICHIER
    // ─────────────────────────────────────────

    private void ouvrirFichier(String path, String titre, Button btn) {
        if (path == null || path.isBlank()) {
            afficherAlerte("Fichier introuvable", "Aucun chemin enregistré pour : " + titre);
            return;
        }

        java.io.File fichier = new java.io.File(path);
        if (!fichier.exists()) {
            afficherAlerte("Fichier introuvable", "Le fichier n'existe pas :\n" + path);
            return;
        }

        // Désactiver le bouton pendant l'ouverture
        btn.setDisable(true);
        btn.setText("⏳  Ouverture...");

        new Thread(() -> {
            try {
                java.awt.Desktop.getDesktop().open(fichier);
                javafx.application.Platform.runLater(() -> {
                    btn.setDisable(false);
                    btn.setText("⬇  Télécharger");
                });
            } catch (java.io.IOException ex) {
                javafx.application.Platform.runLater(() -> {
                    btn.setDisable(false);
                    btn.setText("⬇  Télécharger");
                    afficherAlerte("Erreur", "Impossible d'ouvrir le fichier :\n" + ex.getMessage());
                });
            }
        }).start();
    }

    private void afficherAlerte(String titre, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(titre);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    // ─────────────────────────────────────────
    //  NAVIGATION SIDEBAR
    // ─────────────────────────────────────────

    @FXML public void handleRetour(ActionEvent e)       { naviguerVers("Document.fxml", e); }
    @FXML public void handleDashboard(ActionEvent e)    { naviguerVers("Dashboard.fxml", e); }
    @FXML public void handleModules(ActionEvent e)      { naviguerVers("Modules.fxml", e); }
    @FXML public void handleDocuments(ActionEvent e)    { naviguerVers("Document.fxml", e); }
    @FXML public void handleUpload(ActionEvent e)       { naviguerVers("UploadDocument.fxml", e); }
    @FXML public void handleDeconnexion(ActionEvent e)  { Session.clear(); naviguerVers("Connexion.fxml", e); }
    @FXML public void handleQuiz(ActionEvent e)         { }
    @FXML public void handlePlanning(ActionEvent e)     { }
    @FXML public void handleJira(ActionEvent e)         { }
    @FXML public void handleProfil(ActionEvent e)       { }
    @FXML public void handleFavoris(ActionEvent e)      { }
    @FXML public void handleNotification(ActionEvent e) { }

    private void naviguerVers(String fxml, ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/" + fxml));
            Parent root = loader.load();
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) { e.printStackTrace(); }
    }

    // ─────────────────────────────────────────
    //  HELPERS
    // ─────────────────────────────────────────

    private String getIconType(String type) {
        if (type == null) return "📄";
        switch (type.toLowerCase()) {
            case "cours":  return "📖";
            case "td":     return "📝";
            case "ds":     return "📋";
            case "examen": return "📜";
            case "image":  return "🖼";
            case "video":  return "🎬";
            default:       return "📄";
        }
    }

    private String getCardColor(String type) {
        if (type == null) return "#229DBC";
        switch (type.toLowerCase()) {
            case "td":                   return "#FEB707";
            case "ds": case "examen":    return "#FB8402";
            default:                     return "#229DBC";
        }
    }

    private String getBadgeColor(String ft) {
        if (ft == null) return "#8ECBE6";
        switch (ft.toUpperCase()) {
            case "PDF":  return "#229DBC";
            case "PPTX": return "#FEB707";
            case "DOCX": return "#FB8402";
            default:     return "#8ECBE6";
        }
    }

    // ─────────────────────────────────────────
    //  DATA CLASS
    // ─────────────────────────────────────────

    private static class FichierRow {
        int    id;
        String titre, desc, typeDoc, fileType, chapitre, fichierNom, path;
        long   taille;
    }
}
