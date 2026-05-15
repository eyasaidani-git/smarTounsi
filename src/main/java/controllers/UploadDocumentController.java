package controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import util.DBConnection;
import util.Navigator;
import util.Session;

import java.io.File;
import java.io.IOException;
import java.sql.*;

public class UploadDocumentController {

    @FXML private ComboBox<String> cbModule;
    @FXML private TextField        tfChapitre;
    @FXML private TextField        tfTitre;
    @FXML private TextArea         tfDescription;
    @FXML private Label            lblFichier;
    @FXML private Label            lblTailleFichier;

    @FXML private ToggleButton tbCours;
    @FXML private ToggleButton tbTD;
    @FXML private ToggleButton tbExamenDS;
    @FXML private ToggleButton tbLien;
    @FXML private ToggleButton tbImage;

    private File        fichier;
    private ToggleGroup typeGroup;
    private Connection  conn;

    // ─────────────────────────────────────────────────────────────────
    @FXML
    public void initialize() {
        conn = DBConnection.getInstance().getConnection();

        typeGroup = new ToggleGroup();
        tbCours.setToggleGroup(typeGroup);
        tbTD.setToggleGroup(typeGroup);
        tbExamenDS.setToggleGroup(typeGroup);
        tbLien.setToggleGroup(typeGroup);
        tbImage.setToggleGroup(typeGroup);
        tbCours.setSelected(true);

        chargerModules();
        tfChapitre.setDisable(true);
        tfChapitre.setPromptText("Chapitre indisponible avec la table actuelle");

        if (Session.getModuleNom() != null) {
            cbModule.setValue(Session.getModuleNom());
        }
    }

    // ─── Charger les modules (colonne nom_module) ─────────────────────
    private void chargerModules() {
        cbModule.getItems().clear();
        String sql = "SELECT nom_module FROM modules ORDER BY nom_module";
        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                cbModule.getItems().add(rs.getString("nom_module"));
            }
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur BD",
                    "Impossible de charger les modules :\n" + e.getMessage());
        }
    }

    // ─── Choisir un fichier ───────────────────────────────────────────
    @FXML
    public void choisirFichier() {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Choisir un document");
        chooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Documents", "*.pdf", "*.docx", "*.pptx", "*.txt"),
                new FileChooser.ExtensionFilter("Tous les fichiers", "*.*")
        );
        Stage stage = (Stage) lblFichier.getScene().getWindow();
        fichier = chooser.showOpenDialog(stage);
        if (fichier != null) {
            lblFichier.setText(fichier.getName());
            lblFichier.setStyle("-fx-text-fill: #023047; -fx-font-style: normal;");
            if (lblTailleFichier != null) {
                long ko = fichier.length() / 1024;
                lblTailleFichier.setText(ko > 1024
                        ? String.format("%.1f Mo", ko / 1024.0)
                        : ko + " Ko");
            }
        }
    }

    // ─── Publier le document ──────────────────────────────────────────
    @FXML
    public void ajouterDocument() {

        if (cbModule.getValue() == null || cbModule.getValue().isBlank()) {
            showAlert(Alert.AlertType.WARNING, "Champ manquant", "Veuillez choisir un module.");
            return;
        }
        if (tfTitre.getText().isBlank()) {
            showAlert(Alert.AlertType.WARNING, "Champ manquant", "Veuillez saisir un titre.");
            return;
        }
        if (typeGroup.getSelectedToggle() == null) {
            showAlert(Alert.AlertType.WARNING, "Champ manquant", "Veuillez choisir un type de document.");
            return;
        }

        String moduleNom   = cbModule.getValue();
        String titre       = tfTitre.getText().trim();
        String description = tfDescription.getText().trim();
        String type        = getTypeSelectionne();
        String fichierUrl  = fichier != null ? fichier.getAbsolutePath() : "";

        int moduleId = getModuleId(moduleNom);
        if (moduleId == -1) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Module introuvable en base.");
            return;
        }
        if (!Session.isLoggedIn() || Session.getCurrentUser().getId() <= 0) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Utilisateur non connecté. Veuillez vous reconnecter.");
            return;
        }

        String sql =
                "INSERT INTO documents " +
                        "(titre, description, type_document, fichier_url, id_module, id_uploadeur, statut) " +
                        "VALUES (?, ?, ?, ?, ?, ?, 'en_attente')";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, titre);
            ps.setString(2, description.isEmpty() ? null : description);
            ps.setString(3, type);
            ps.setString(4, fichierUrl);
            ps.setInt   (5, moduleId);
            ps.setInt   (6, Session.getCurrentUser().getId());
            ps.executeUpdate();

            showAlert(Alert.AlertType.INFORMATION, "Succès",
                    "Document \"" + titre + "\" publié avec succès !");

            Session.setModuleId(moduleId);
            Session.setModuleNom(moduleNom);
            naviguerVersDocuments();

        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur BD",
                    "Impossible d'insérer le document :\n" + e.getMessage());
        }
    }

    // ─── selectType ───────────────────────────────────────────────────
    @FXML
    public void selectType() { }

    // ─── Annuler ──────────────────────────────────────────────────────
    @FXML
    public void annuler() {
        naviguerVers("Document.fxml");
    }

    // ─────────────────── NAVIGATION SIDEBAR ──────────────────────────

    @FXML public void handleDashboard(ActionEvent event) {
        Navigator.go((Node) event.getSource(), "/Acceuil.fxml", "Dashboard - SmarTounsi");
    }
    @FXML public void handleModules(ActionEvent event) {
        Navigator.go((Node) event.getSource(), "/Modules.fxml", "Modules - SmarTounsi");
    }
    @FXML public void handleDocuments(ActionEvent event) {
        Navigator.go((Node) event.getSource(), "/Document.fxml", "Documents - SmarTounsi");
    }
    @FXML public void handleUpload(ActionEvent event) {
        Navigator.go((Node) event.getSource(), "/UploadDocument.fxml", "Upload - SmarTounsi");
    }
    @FXML public void handleQuiz(ActionEvent event) {
        Navigator.go((Node) event.getSource(), "/quiz.fxml", "Quiz - SmarTounsi");
    }
    @FXML public void handleFavoris(ActionEvent event) {
        Navigator.go((Node) event.getSource(), "/Favoris.fxml", "Favoris - SmarTounsi");
    }

    @FXML public void handlePlanning(ActionEvent event) {
        Navigator.go((Node) event.getSource(), "/Planning.fxml", "Planning - SmarTounsi");
    }

    @FXML public void handleProjets(ActionEvent event) {
        Navigator.go((Node) event.getSource(), "/ProjectView.fxml", "Projets - SmarTounsi");
    }

    @FXML public void handleEvenements(ActionEvent event) {
        Navigator.go((Node) event.getSource(), "/Evenements.fxml", "Evenements - SmarTounsi");
    }

    @FXML public void handleProfil(ActionEvent event) {
        Navigator.go((Node) event.getSource(), "/Profil.fxml", "Profil - SmarTounsi");
    }

    @FXML public void handleNotifications(ActionEvent event) {
        Navigator.go((Node) event.getSource(), "/Notification.fxml", "Notifications - SmarTounsi");
    }

    // ─── Helpers ─────────────────────────────────────────────────────

    private String getTypeSelectionne() {
        if (typeGroup.getSelectedToggle() == null) return "cours";
        ToggleButton selected = (ToggleButton) typeGroup.getSelectedToggle();
        String text = selected.getText();
        if (text.contains("Cours"))   return "cours";
        if (text.contains("TD"))      return "td";
        if (text.contains("Examen") || text.contains("DS")) return "examen_ds";
        if (text.contains("Lien"))    return "lien_utile";
        if (text.contains("Image"))   return "image";
        return "cours";
    }

    private int getModuleId(String nomModule) {
        String sql = "SELECT id_module FROM modules WHERE nom_module = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, nomModule);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getInt("id_module");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    private String getExtension(String nom) {
        if (nom == null || !nom.contains(".")) return "PDF";
        return nom.substring(nom.lastIndexOf('.') + 1).toUpperCase();
    }

    private void naviguerVersDocuments() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Document.fxml"));
            Parent root = loader.load();
            Documentcontroller controller = loader.getController();
            controller.setModuleNom(Session.getModuleNom());
            Stage stage = (Stage) cbModule.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private void naviguerVers(String fxml) {
        try {
            var url = getClass().getResource("/" + fxml);
            if (url == null) {
                System.err.println("FXML introuvable : " + fxml);
                return;  // ne plante plus, affiche juste un message dans la console
            }
            FXMLLoader loader = new FXMLLoader(url);
            Parent root = loader.load();
            if (fxml.equals("Document.fxml") && Session.getModuleNom() != null) {
                Documentcontroller controller = loader.getController();
                controller.setModuleNom(Session.getModuleNom());
            }
            Stage stage = (Stage) cbModule.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void showAlert(Alert.AlertType type, String titre, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(titre);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    @FXML public void handleDeconnexion(ActionEvent event) {
        Navigator.logout((Node) event.getSource());
    }
}
