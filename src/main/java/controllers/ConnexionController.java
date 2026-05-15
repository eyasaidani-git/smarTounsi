package controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import models.Utilisateur;
import services.UtilisateurService;
import util.CaptchaUtil;
import util.Session;

import java.io.IOException;
import java.net.URL;

public class ConnexionController {

    @FXML
    private Button btnEtudiant;

    @FXML
    private Button btnProf;

    @FXML
    private Button btnAdmin;

    @FXML
    private TextField emailField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Label captchaLabel;

    @FXML
    private TextField captchaField;

    private String selectedRole = "etudiant";

    private UtilisateurService utilisateurService;
    private final CaptchaUtil captchaUtil = new CaptchaUtil();

    @FXML
    private void initialize() {
        choisirEtudiant();

        if (captchaLabel != null) {
            captchaLabel.setText(captchaUtil.getQuestion());
        }
    }

    @FXML
    private void choisirEtudiant() {
        selectedRole = "etudiant";

        if (btnEtudiant != null && btnProf != null) {
            btnEtudiant.getStyleClass().removeAll("role-button", "role-button-selected");
            btnEtudiant.getStyleClass().add("role-button-selected");

            btnProf.getStyleClass().removeAll("role-button", "role-button-selected");
            btnProf.getStyleClass().add("role-button");

            if (btnAdmin != null) {
                btnAdmin.getStyleClass().removeAll("role-button", "role-button-selected");
                btnAdmin.getStyleClass().add("role-button");
            }
        }
    }

    @FXML
    private void choisirProf() {
        /*
         * Important :
         * Cette valeur doit être identique à celle enregistrée dans XAMPP.
         * Si dans la base tu as role='prof', mets "prof".
         * Si dans la base tu as role='professeur', garde "professeur".
         */
        selectedRole = "prof";

        if (btnEtudiant != null && btnProf != null) {
            btnProf.getStyleClass().removeAll("role-button", "role-button-selected");
            btnProf.getStyleClass().add("role-button-selected");

            btnEtudiant.getStyleClass().removeAll("role-button", "role-button-selected");
            btnEtudiant.getStyleClass().add("role-button");

            if (btnAdmin != null) {
                btnAdmin.getStyleClass().removeAll("role-button", "role-button-selected");
                btnAdmin.getStyleClass().add("role-button");
            }
        }
    }

    @FXML
    private void choisirAdmin() {
        selectedRole = "admin";

        if (btnEtudiant != null && btnProf != null && btnAdmin != null) {
            btnAdmin.getStyleClass().removeAll("role-button", "role-button-selected");
            btnAdmin.getStyleClass().add("role-button-selected");

            btnEtudiant.getStyleClass().removeAll("role-button", "role-button-selected");
            btnEtudiant.getStyleClass().add("role-button");

            btnProf.getStyleClass().removeAll("role-button", "role-button-selected");
            btnProf.getStyleClass().add("role-button");
        }
    }

    @FXML
    private void connecter() {
        String email = emailField.getText() == null ? "" : emailField.getText().trim();
        String password = passwordField.getText() == null ? "" : passwordField.getText().trim();
        String captcha = captchaField.getText() == null ? "" : captchaField.getText().trim();

        if (email.isBlank() || password.isBlank()) {
            afficherAlerte(Alert.AlertType.WARNING, "Champs manquants", "Veuillez saisir votre email et votre mot de passe.");
            return;
        }

        if (!captchaUtil.validate(captcha)) {
            afficherAlerte(Alert.AlertType.ERROR, "Captcha incorrect", "Veuillez vérifier que vous n'êtes pas un robot.");
            renouvelerCaptcha();
            return;
        }

        Utilisateur u;
        try {
            u = getUtilisateurService().login(email, password, selectedRole);

            if (u == null) {
                u = getUtilisateurService().login(email, password);
            }
        } catch (RuntimeException e) {
            afficherAlerte(Alert.AlertType.ERROR, "Erreur base de données", e.getMessage());
            renouvelerCaptcha();
            return;
        }

        if (u == null) {
            afficherAlerte(Alert.AlertType.ERROR, "Connexion échouée", "Email, mot de passe ou rôle incorrect.");
            renouvelerCaptcha();
            return;
        }

        Session.setCurrentUser(u);

        if ("admin".equalsIgnoreCase(u.getRole())) {
            ouvrirPage("/AdminDashboard.fxml", "Admin - SmarTounsi");
        } else {
            ouvrirPage("/Profil.fxml", "Profil - SmarTounsi");
        }
    }

    @FXML
    private void goMotDePasseOublie() {
        ouvrirPage("/MotDePasseOublie.fxml", "Mot de passe oublié - SmarTounsi");
    }

    @FXML
    private void goInscription() {
        ouvrirPage("/Inscription.fxml", "Inscription - SmarTounsi");
    }

    @FXML
    private void goAccueil() {
        ouvrirPage("/Acceuil.fxml", "Accueil - SmarTounsi");
    }

    private void renouvelerCaptcha() {
        captchaUtil.generate();

        if (captchaLabel != null) {
            captchaLabel.setText(captchaUtil.getQuestion());
        }

        if (captchaField != null) {
            captchaField.clear();
        }
    }

    private UtilisateurService getUtilisateurService() {
        if (utilisateurService == null) {
            utilisateurService = new UtilisateurService();
        }

        return utilisateurService;
    }

    private void ouvrirPage(String path, String title) {
        try {
            URL resource = getClass().getResource(path);

            if (resource == null) {
                afficherAlerte(Alert.AlertType.ERROR, "FXML introuvable", "Impossible de trouver : " + path);
                return;
            }

            Parent root = FXMLLoader.load(resource);
            Stage stage = (Stage) emailField.getScene().getWindow();

            Scene scene = new Scene(root, 1400, 850);
            stage.setScene(scene);
            stage.setTitle(title);
            stage.show();

        } catch (IOException e) {
            afficherAlerte(Alert.AlertType.ERROR, "Erreur navigation", e.getMessage());
        }
    }

    private void afficherAlerte(Alert.AlertType type, String titre, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(titre);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
