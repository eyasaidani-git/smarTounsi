package controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import services.PasswordResetService;
import util.CaptchaUtil;
import util.PasswordUtil;

import java.io.IOException;

public class MotDePasseOublieController {

    @FXML
    private TextField emailField;

    @FXML
    private TextField codeField;

    @FXML
    private PasswordField newPasswordField;

    @FXML
    private PasswordField confirmPasswordField;

    @FXML
    private Label captchaLabel;

    @FXML
    private TextField captchaField;

    private final PasswordResetService passwordResetService = new PasswordResetService();

    private final CaptchaUtil captchaUtil = new CaptchaUtil();

    @FXML
    private void initialize() {
        captchaLabel.setText(captchaUtil.getQuestion());
    }

    @FXML
    private void envoyerCode() {
        String email = emailField.getText().trim();

        if (email.isBlank()) {
            afficherAlerte(Alert.AlertType.WARNING, "Email manquant", "Veuillez saisir votre email.");
            return;
        }

        try {
            passwordResetService.envoyerCode(email);
            afficherAlerte(Alert.AlertType.INFORMATION, "Code envoyé", "Un code a été envoyé à votre email.");

        } catch (RuntimeException e) {
            afficherAlerte(Alert.AlertType.ERROR, "Erreur", e.getMessage());
        }
    }

    @FXML
    private void reinitialiser() {
        String email = emailField.getText().trim();
        String code = codeField.getText().trim();
        String newPassword = newPasswordField.getText().trim();
        String confirmPassword = confirmPasswordField.getText().trim();

        if (email.isBlank() || code.isBlank() || newPassword.isBlank() || confirmPassword.isBlank()) {
            afficherAlerte(Alert.AlertType.WARNING, "Champs manquants", "Veuillez remplir tous les champs.");
            return;
        }

        if (!captchaUtil.validate(captchaField.getText())) {
            afficherAlerte(Alert.AlertType.ERROR, "Captcha incorrect", "Veuillez vérifier que vous n'êtes pas un robot.");
            captchaUtil.generate();
            captchaLabel.setText(captchaUtil.getQuestion());
            captchaField.clear();
            return;
        }

        if (!newPassword.equals(confirmPassword)) {
            afficherAlerte(Alert.AlertType.WARNING, "Mot de passe différent", "Les deux mots de passe ne correspondent pas.");
            return;
        }

        if (!PasswordUtil.isStrongPassword(newPassword)) {
            afficherAlerte(Alert.AlertType.WARNING, "Mot de passe faible", PasswordUtil.getPasswordRulesMessage());
            return;
        }

        try {
            passwordResetService.resetPassword(email, code, newPassword);

            afficherAlerte(Alert.AlertType.INFORMATION, "Succès", "Votre mot de passe a été changé.");
            ouvrirPage("/Connexion.fxml", "Connexion - SmarTounsi");

        } catch (RuntimeException e) {
            afficherAlerte(Alert.AlertType.ERROR, "Erreur", e.getMessage());
        }
    }

    @FXML
    private void goConnexion() {
        ouvrirPage("/Connexion.fxml", "Connexion - SmarTounsi");
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