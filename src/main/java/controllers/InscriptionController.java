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
import util.PasswordUtil;
import java.io.IOException;
import util.CaptchaUtil;
import services.EmailVerificationService;
import util.EmailValidator;
import util.PasswordUtil;
public class InscriptionController {

    @FXML
    private Button btnEtudiant;

    @FXML
    private Button btnProf;

    @FXML
    private TextField emailField;

    @FXML
    private TextField nomCompletField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private TextField filiereField;

    private String selectedRole = "etudiant";

    private final UtilisateurService utilisateurService = new UtilisateurService();
    @FXML
    private Label captchaLabel;

    @FXML
    private TextField captchaField;

    private final CaptchaUtil captchaUtil = new CaptchaUtil();
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
    private TextField emailCodeField;

    private final EmailVerificationService emailVerificationService = new EmailVerificationService();

    @FXML
    private void inscrire() {
        String email = emailField.getText().trim();
        String nomComplet = nomCompletField.getText().trim();
        String password = passwordField.getText().trim();
        String filiere = filiereField.getText().trim();
        String codeEmail = emailCodeField.getText().trim();

        if (codeEmail.isBlank()) {
            afficherAlerte(Alert.AlertType.WARNING, "Code email manquant", "Veuillez entrer le code reçu par email.");
            return;
        }

        if (!emailVerificationService.verifierCode(email, codeEmail)) {
            afficherAlerte(Alert.AlertType.ERROR, "Code incorrect", "Le code email est incorrect ou expiré.");
            return;
        }
        if (!captchaUtil.validate(captchaField.getText())) {
            afficherAlerte(Alert.AlertType.ERROR, "Captcha incorrect", "Veuillez vérifier que vous n'êtes pas un robot.");
            captchaUtil.generate();
            captchaLabel.setText(captchaUtil.getQuestion());
            captchaField.clear();
            return;
        }
        if (email.isBlank() || nomComplet.isBlank() || password.isBlank() || filiere.isBlank()) {
            afficherAlerte(Alert.AlertType.WARNING, "Champs manquants", "Veuillez remplir tous les champs.");
            return;
        }

        if (!EmailValidator.isValidEmailFormat(email)) {
            afficherAlerte(Alert.AlertType.WARNING, "Email invalide", EmailValidator.getEmailRulesMessage());
            return;
        }

        if (utilisateurService.emailExiste(email)) {
            afficherAlerte(Alert.AlertType.ERROR, "Email existe déjà", "Un compte existe déjà avec cet email.");
            return;
        }

        Utilisateur u = new Utilisateur();
        u.setNomComplet(nomComplet);
        u.setEmail(email);
        u.setMotDePasse(password);
        u.setRole(selectedRole);
        u.setFiliere(filiere);
        u.setUniversite("ESPRIT");
        u.setEstActif(true);

        if ("etudiant".equals(selectedRole)) {
            u.setAnnee("1ère année");
            u.setNumeroEtudiant("");
        } else {
            u.setAnnee(null);
            u.setNumeroEtudiant(null);
        }
        if (!PasswordUtil.isStrongPassword(password)) {
            afficherAlerte(Alert.AlertType.WARNING, "Mot de passe faible", PasswordUtil.getPasswordRulesMessage());
            return;
        }
        try {
            utilisateurService.add(u);

            Utilisateur connected = utilisateurService.login(email, password, selectedRole);

            if (connected != null) {
                Session.setCurrentUser(connected);
                ouvrirPage("/Profil.fxml", "Profil - SmarTounsi");
            } else {
                afficherAlerte(Alert.AlertType.ERROR, "Erreur connexion", "Compte créé, mais connexion impossible. Vérifiez le rôle dans la base.");
            }

        } catch (RuntimeException e) {
            afficherAlerte(Alert.AlertType.ERROR, "Erreur inscription", e.getMessage());
        }
    }

    @FXML
    private void goConnexion() {
        ouvrirPage("/Connexion.fxml", "Connexion - SmarTounsi");
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
    @FXML
    private void initialize() {
        captchaLabel.setText(captchaUtil.getQuestion());
    }
    private void afficherAlerte(Alert.AlertType type, String titre, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(titre);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    @FXML
    private void envoyerCodeEmail() {
        String email = emailField.getText().trim();

        if (email.isBlank()) {
            afficherAlerte(Alert.AlertType.WARNING, "Email manquant", "Veuillez saisir votre email.");
            return;
        }

        if (!EmailValidator.isValidEmailFormat(email)) {
            afficherAlerte(Alert.AlertType.WARNING, "Email invalide", EmailValidator.getEmailRulesMessage());
            return;
        }

        if (utilisateurService.emailExiste(email)) {
            afficherAlerte(Alert.AlertType.ERROR, "Email existe déjà", "Un compte existe déjà avec cet email.");
            return;
        }

        try {
            emailVerificationService.envoyerCodeVerification(email);
            afficherAlerte(Alert.AlertType.INFORMATION, "Code envoyé", "Un code de vérification a été envoyé à votre email.");

        } catch (RuntimeException e) {
            afficherAlerte(Alert.AlertType.ERROR, "Erreur email", e.getMessage());
        }
    }
}
