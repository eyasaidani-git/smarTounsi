package controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import models.Utilisateur;
import services.UtilisateurService;
import util.Session;

import java.io.IOException;

public class ProfilController {

    @FXML
    private Label roleHeaderLabel;

    @FXML
    private Label avatarLabel;

    @FXML
    private Label nomHeaderLabel;

    @FXML
    private Label infoHeaderLabel;

    @FXML
    private TextField nomCompletField;

    @FXML
    private TextField emailField;

    @FXML
    private TextField filiereField;

    @FXML
    private TextField anneeField;

    @FXML
    private TextField universiteField;

    @FXML
    private TextField numeroEtudiantField;

    @FXML
    private VBox anneeBox;

    @FXML
    private VBox numeroBox;

    private final UtilisateurService utilisateurService = new UtilisateurService();

    private Utilisateur currentUser;

    @FXML
    private void initialize() {
        currentUser = Session.getCurrentUser();

        if (currentUser == null) {
            ouvrirPage("/Connexion.fxml", "Connexion - SmarTounsi");
            return;
        }

        remplirChamps();
    }

    private void remplirChamps() {
        String nomComplet = currentUser.getNomComplet();

        nomCompletField.setText(nomComplet);
        emailField.setText(currentUser.getEmail());
        filiereField.setText(currentUser.getFiliere());
        anneeField.setText(currentUser.getAnnee());
        universiteField.setText(currentUser.getUniversite());
        numeroEtudiantField.setText(currentUser.getNumeroEtudiant());

        nomHeaderLabel.setText(nomComplet == null || nomComplet.isBlank() ? "Utilisateur" : nomComplet);

        if (nomComplet != null && !nomComplet.isBlank()) {
            avatarLabel.setText(nomComplet.substring(0, 1).toUpperCase());
        } else {
            avatarLabel.setText("U");
        }

        boolean isProf = "prof".equalsIgnoreCase(currentUser.getRole());

        if (isProf) {
            roleHeaderLabel.setText("👨‍🏫 Professeur");
            infoHeaderLabel.setText("👨‍🏫 Professeur · " + currentUser.getEmail());

            anneeBox.setVisible(false);
            anneeBox.setManaged(false);

            numeroBox.setVisible(false);
            numeroBox.setManaged(false);
        } else {
            roleHeaderLabel.setText("🎓 Étudiant");

            String annee = currentUser.getAnnee() == null ? "" : currentUser.getAnnee();
            infoHeaderLabel.setText("🎓 Étudiant — " + annee + " · " + currentUser.getEmail());

            anneeBox.setVisible(true);
            anneeBox.setManaged(true);

            numeroBox.setVisible(true);
            numeroBox.setManaged(true);
        }
    }

    @FXML
    private void activerModification() {
        nomCompletField.requestFocus();
    }

    @FXML
    private void sauvegarder() {
        if (currentUser == null) {
            return;
        }

        String nomComplet = nomCompletField.getText().trim();
        String email = emailField.getText().trim();

        if (nomComplet.isBlank() || email.isBlank()) {
            afficherAlerte(Alert.AlertType.WARNING, "Champs manquants", "Le nom complet et l'email sont obligatoires.");
            return;
        }

        currentUser.setNomComplet(nomComplet);
        currentUser.setEmail(email);
        currentUser.setFiliere(filiereField.getText().trim());
        currentUser.setUniversite(universiteField.getText().trim());

        if ("prof".equalsIgnoreCase(currentUser.getRole())) {
            currentUser.setAnnee(null);
            currentUser.setNumeroEtudiant(null);
        } else {
            currentUser.setAnnee(anneeField.getText().trim());
            currentUser.setNumeroEtudiant(numeroEtudiantField.getText().trim());
        }

        utilisateurService.update(currentUser);
        Session.setCurrentUser(currentUser);

        remplirChamps();

        afficherAlerte(Alert.AlertType.INFORMATION, "Profil sauvegardé", "Vos informations ont été mises à jour.");
    }

    @FXML
    private void deconnexion() {
        Session.clear();
        ouvrirPage("/Acceuil.fxml", "Accueil - SmarTounsi");
    }

    private void ouvrirPage(String path, String title) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource(path));
            Stage stage;

            if (nomCompletField != null && nomCompletField.getScene() != null) {
                stage = (Stage) nomCompletField.getScene().getWindow();
            } else {
                stage = new Stage();
            }

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
