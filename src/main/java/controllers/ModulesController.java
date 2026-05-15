package controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import util.Navigator;
import util.Session;

import java.io.IOException;

public class ModulesController {

    @FXML private TextField tfRecherche;

    // ================= MODULES (onMouseClicked sur VBox) =================

    @FXML
    public void handleMathematiques(MouseEvent event) throws IOException {
        ouvrirDocuments((Node) event.getSource(), 1, "Mathématiques");
    }

    @FXML
    public void handlePhysique(MouseEvent event) throws IOException {
        ouvrirDocuments((Node) event.getSource(), 2, "Physique");
    }

    @FXML
    public void handleReseaux(MouseEvent event) throws IOException {
        ouvrirDocuments((Node) event.getSource(), 3, "Réseaux");
    }

    @FXML
    public void handleUnix(MouseEvent event) throws IOException {
        ouvrirDocuments((Node) event.getSource(), 4, "Unix");
    }

    @FXML
    public void handleJava(MouseEvent event) throws IOException {
        ouvrirDocuments((Node) event.getSource(), 5, "Java");
    }

    @FXML
    public void handleMachineLearning(MouseEvent event) throws IOException {
        ouvrirDocuments((Node) event.getSource(), 6, "Machine Learning");
    }

    // ================= NAVIGATION INTERNE =================

    private void ouvrirDocuments(Node source, int moduleId, String moduleNom) throws IOException {
        Session.setModuleId(moduleId);
        Session.setModuleNom(moduleNom);

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/Document.fxml"));
        Parent root = loader.load();

        Documentcontroller controller = loader.getController();
        controller.setModuleNom(moduleNom);
        Navigator.applySessionRoleLabels(root);

        Stage stage = (Stage) source.getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.show();
    }

    private void naviguerVers(String fxml, Node source) {
        try {
            var url = getClass().getResource("/" + fxml);
            if (url == null) {
                System.err.println("FXML introuvable : " + fxml);
                return;
            }
            FXMLLoader loader = new FXMLLoader(url);
            Parent root = loader.load();
            Navigator.applySessionRoleLabels(root);
            Stage stage = (Stage) source.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // ================= SIDEBAR (onMouseClicked sur Button) =================

    @FXML
    public void handleDashboard(MouseEvent event) {
        Navigator.goDashboard((Node) event.getSource());
    }

    @FXML
    public void handleModules(MouseEvent event) {
        naviguerVers("Modules.fxml", (Node) event.getSource());
    }

    @FXML
    public void handleDocuments(MouseEvent event) {
        if (Session.getModuleId() > 0 && Session.getModuleNom() != null) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/Document.fxml"));
                Parent root = loader.load();
                Documentcontroller ctrl = loader.getController();
                ctrl.setModuleNom(Session.getModuleNom());
                Navigator.applySessionRoleLabels(root);
                Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                stage.setScene(new Scene(root));
                stage.show();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @FXML
    public void handleUpload(MouseEvent event) {
        naviguerVers("UploadDocument.fxml", (Node) event.getSource());
    }

    @FXML
    public void handleDeconnexion(MouseEvent event) {
        Session.clear();
        naviguerVers("Connexion.fxml", (Node) event.getSource());
    }

    @FXML public void handleQuiz(MouseEvent event)         { naviguerVers("quiz.fxml", (Node) event.getSource()); }
    @FXML public void handleProjets(MouseEvent event)      { naviguerVers("ProjectView.fxml", (Node) event.getSource()); }
    @FXML public void handlePlanning(MouseEvent event)     { naviguerVers("Planning.fxml", (Node) event.getSource()); }
    @FXML public void handleJira(MouseEvent event)         { System.out.println("Jira"); }
    @FXML public void handleFavoris(MouseEvent event)      { naviguerVers("Favoris.fxml", (Node) event.getSource()); }
    @FXML public void handleEvenements(MouseEvent event)   { naviguerVers("Evenements.fxml", (Node) event.getSource()); }
    @FXML public void handleProfil(MouseEvent event)       { naviguerVers("Profil.fxml", (Node) event.getSource()); }
    @FXML public void handleNotification(MouseEvent event) { naviguerVers("Notification.fxml", (Node) event.getSource()); }

    // ================= RECHERCHE (onAction sur Button) =================
    // Le bouton Rechercher utilise onAction → on garde ActionEvent ici uniquement
    @FXML
    public void handleRechercher(javafx.event.ActionEvent event) {
        String recherche = (tfRecherche != null) ? tfRecherche.getText().toLowerCase().trim() : "";
        System.out.println("Recherche : " + recherche);
    }
}
