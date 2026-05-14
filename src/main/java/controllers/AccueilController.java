package controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.scene.Scene;

import java.io.IOException;

public class AccueilController {

    @FXML
    private ScrollPane mainScroll;

    @FXML
    private VBox aproposSection;

    @FXML
    private VBox fonctionnalitesSection;

    @FXML
    private VBox contactSection;

    @FXML
    private TextField nomField;

    @FXML
    private TextField emailField;

    @FXML
    private TextField sujetField;

    @FXML
    private TextArea messageArea;

    @FXML
    private void goToApropos() {
        scrollTo(0.32);
    }

    @FXML
    private void goToFonctionnalites() {
        scrollTo(0.57);
    }

    @FXML
    private void goToContact() {
        scrollTo(0.82);
    }

    @FXML
    private void ouvrirConnexion() {
        ouvrirPage("/Connexion.fxml", "Connexion - SmarTounsi");
    }

    @FXML
    private void ouvrirInscription() {
        ouvrirPage("/Inscription.fxml", "Inscription - SmarTounsi");
    }

    @FXML
    private void ouvrirModules() {
        ouvrirPage("/Modules.fxml", "Modules - SmarTounsi");
    }

    @FXML
    private void envoyerMessage() {
        String nom = nomField.getText();
        String email = emailField.getText();
        String sujet = sujetField.getText();
        String message = messageArea.getText();

        if (nom.isBlank() || email.isBlank() || sujet.isBlank() || message.isBlank()) {
            afficherAlerte(Alert.AlertType.WARNING,
                    "Champs manquants",
                    "Veuillez remplir tous les champs du formulaire.");
            return;
        }

        afficherAlerte(Alert.AlertType.INFORMATION,
                "Message envoyé",
                "Merci " + nom + ", votre message a été envoyé avec succès.");

        nomField.clear();
        emailField.clear();
        sujetField.clear();
        messageArea.clear();
    }

    private void scrollTo(double value) {
        mainScroll.setVvalue(value);
    }

    private void ouvrirPage(String fxmlPath, String title) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource(fxmlPath));

            Stage stage = (Stage) mainScroll.getScene().getWindow();
            Scene scene = new Scene(root);

            stage.setTitle(title);
            stage.setScene(scene);
            stage.show();

        } catch (IOException e) {
            afficherAlerte(Alert.AlertType.ERROR,
                    "Page introuvable",
                    "La page " + fxmlPath + " n'existe pas encore.\nElle sera créée dans l'étape suivante.");
        }
    }

    private void afficherAlerte(Alert.AlertType type, String titre, String contenu) {
        Alert alert = new Alert(type);
        alert.setTitle(titre);
        alert.setHeaderText(null);
        alert.setContentText(contenu);
        alert.showAndWait();
    }
}
