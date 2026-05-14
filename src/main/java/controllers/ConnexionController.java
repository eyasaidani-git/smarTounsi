package controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import models.Utilisateur;
import services.UtilisateurService;
import util.Session;

import java.io.IOException;
import util.CaptchaUtil;
public class ConnexionController {

    @FXML
    private Button btnEtudiant;

    @FXML
    private Button btnProf;

    @FXML
    private TextField emailField;

    @FXML
    private PasswordField passwordField;

    private String selectedRole = "etudiant";

    private final UtilisateurService utilisateurService = new UtilisateurService();
    @FXML
    private Label captchaLabel;

    @FXML
    private TextField captchaField;

    private final CaptchaUtil captchaUtil = new CaptchaUtil();

    @FXML
    private void initialize() {
        captchaLabel.setText(captchaUtil.getQuestion());
    }

    @FXML
    private void choisirEtudiant() {
        selectedRole = "etudiant";
        btnEtudiant.getStyleClass().setAll("role-button-selected");
        btnProf.getStyleClass().setAll("role-button");
    }

    @FXML
    private void choisirProf() {
        selectedRole = "prof";
        btnProf.getStyleClass().setAll("role-button-selected");
        btnEtudiant.getStyleClass().setAll("role-button");
    }

    @FXML
    private void connecter() {
        String email = emailField.getText().trim();
        String password = passwordField.getText().trim();
        if (!captchaUtil.validate(captchaField.getText())) {
            afficherAlerte(Alert.AlertType.ERROR, "Captcha incorrect", "Veuillez vérifier que vous n'êtes pas un robot.");
            captchaUtil.generate();
            captchaLabel.setText(captchaUtil.getQuestion());
            captchaField.clear();
            return;
        }
        if (email.isBlank() || password.isBlank()) {
            afficherAlerte(Alert.AlertType.WARNING, "Champs manquants", "Veuillez saisir votre email et mot de passe.");
            return;
        }

        Utilisateur u = utilisateurService.login(email, password, selectedRole);

        if (u == null) {
            afficherAlerte(Alert.AlertType.ERROR, "Connexion échouée", "Email, mot de passe ou rôle incorrect.");
            return;
        }

        Session.setCurrentUser(u);
        ouvrirPage("/Profil.fxml", "Profil - SmarTounsi");
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

    private void ouvrirPage(String path, String title) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource(path));
            Stage stage = (Stage) emailField.getScene().getWindow();
            stage.setScene(new Scene(root, 1400, 850));
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
