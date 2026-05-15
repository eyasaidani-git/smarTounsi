package controllers;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import models.Utilisateur;
import services.UtilisateurService;
import util.Navigator;
import util.Session;

public class ProfilController {

    @FXML
    private BorderPane rootPane;

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
            Navigator.go(rootPane, "/Connexion.fxml", "Connexion - SmarTounsi");
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

        boolean isProf = "prof".equalsIgnoreCase(currentUser.getRole())
                || "professeur".equalsIgnoreCase(currentUser.getRole());

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

        String nomComplet = nomCompletField.getText() == null ? "" : nomCompletField.getText().trim();
        String email = emailField.getText() == null ? "" : emailField.getText().trim();

        if (nomComplet.isBlank() || email.isBlank()) {
            afficherAlerte(Alert.AlertType.WARNING, "Champs manquants", "Le nom complet et l'email sont obligatoires.");
            return;
        }

        currentUser.setNomComplet(nomComplet);
        currentUser.setEmail(email);
        currentUser.setFiliere(filiereField.getText() == null ? "" : filiereField.getText().trim());
        currentUser.setUniversite(universiteField.getText() == null ? "" : universiteField.getText().trim());

        boolean isProf = "prof".equalsIgnoreCase(currentUser.getRole())
                || "professeur".equalsIgnoreCase(currentUser.getRole());

        if (isProf) {
            currentUser.setAnnee(null);
            currentUser.setNumeroEtudiant(null);
        } else {
            currentUser.setAnnee(anneeField.getText() == null ? "" : anneeField.getText().trim());
            currentUser.setNumeroEtudiant(numeroEtudiantField.getText() == null ? "" : numeroEtudiantField.getText().trim());
        }

        utilisateurService.update(currentUser);
        Session.setCurrentUser(currentUser);

        remplirChamps();

        afficherAlerte(Alert.AlertType.INFORMATION, "Profil sauvegardé", "Vos informations ont été mises à jour.");
    }

    @FXML
    private void goDashboard() {
        Navigator.go(rootPane, "/Acceuil.fxml", "Dashboard - SmarTounsi");
    }

    @FXML
    private void goModules() {
        Navigator.go(rootPane, "/Modules.fxml", "Modules - SmarTounsi");
    }

    @FXML
    private void goBibliotheque() {
        Navigator.go(rootPane, "/Document.fxml", "Bibliothèque - SmarTounsi");
    }

    @FXML
    private void goUpload() {
        Navigator.go(rootPane, "/UploadDocument.fxml", "Upload - SmarTounsi");
    }

    @FXML
    private void goCalendrier() {
        Navigator.go(rootPane, "/Planning.fxml", "Calendrier - SmarTounsi");
    }

    @FXML
    private void goFavoris() {
        Navigator.go(rootPane, "/Favoris.fxml", "Favoris - SmarTounsi");
    }

    @FXML
    private void goQuiz() {
        Navigator.go(rootPane, "/quiz.fxml", "Quiz - SmarTounsi");
    }

    @FXML
    private void goProjets() {
        Navigator.go(rootPane, "/ProjectView.fxml", "Projets - SmarTounsi");
    }

    @FXML
    private void goEvenements() {
        Navigator.go(rootPane, "/Evenements.fxml", "Événements - SmarTounsi");
    }

    @FXML
    private void goProfil() {
        Navigator.go(rootPane, "/Profil.fxml", "Profil - SmarTounsi");
    }

    @FXML
    private void goNotifications() {
        Navigator.go(rootPane, "/Notification.fxml", "Notifications - SmarTounsi");
    }

    @FXML
    private void deconnexion() {
        Navigator.logout(rootPane);
    }

    private void afficherAlerte(Alert.AlertType type, String titre, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(titre);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
