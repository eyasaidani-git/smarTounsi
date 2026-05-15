package controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import util.Navigator;

import java.awt.Desktop;
import java.net.URI;

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

    @FXML
    private void openFacebook() {
        openExternalLink("https://www.facebook.com/profile.php?id=61581797906674&mibextid=wwXIfr");
    }

    @FXML
    private void openInstagram() {
        openExternalLink("https://www.instagram.com/smartounsi?igsh=MXN3cDB5aW93NW50NQ%3D%3D&utm_source=qr");
    }

    private void scrollTo(double value) {
        mainScroll.setVvalue(value);
    }

    private void ouvrirPage(String fxmlPath, String title) {
        if (mainScroll == null || mainScroll.getScene() == null) {
            afficherAlerte(Alert.AlertType.ERROR,
                    "Erreur navigation",
                    "Impossible d'ouvrir : " + fxmlPath);
            return;
        }

        Navigator.go(mainScroll, fxmlPath, title);
    }

    private void afficherAlerte(Alert.AlertType type, String titre, String contenu) {
        Alert alert = new Alert(type);
        alert.setTitle(titre);
        alert.setHeaderText(null);
        alert.setContentText(contenu);
        alert.showAndWait();
    }

    private void openExternalLink(String url) {
        try {
            if (!Desktop.isDesktopSupported()) {
                throw new IllegalStateException("Ouverture navigateur non supportée.");
            }

            Desktop.getDesktop().browse(new URI(url));
        } catch (Exception e) {
            afficherAlerte(Alert.AlertType.ERROR,
                    "Lien indisponible",
                    "Impossible d'ouvrir le lien :\n" + url);
        }
    }
}
