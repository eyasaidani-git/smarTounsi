package controllers;

import javafx.fxml.FXML;
import javafx.scene.layout.BorderPane;
import util.Navigator;

public class EvenementsController {

    @FXML
    private BorderPane rootPane;

    @FXML private void goDashboard() { Navigator.go(rootPane, "/Acceuil.fxml", "Dashboard - SmarTounsi"); }
    @FXML private void goModules() { Navigator.go(rootPane, "/Modules.fxml", "Modules - SmarTounsi"); }
    @FXML private void goBibliotheque() { Navigator.go(rootPane, "/Document.fxml", "Bibliothèque - SmarTounsi"); }
    @FXML private void goUpload() { Navigator.go(rootPane, "/UploadDocument.fxml", "Upload - SmarTounsi"); }
    @FXML private void goCalendrier() { Navigator.go(rootPane, "/Planning.fxml", "Calendrier - SmarTounsi"); }
    @FXML private void goFavoris() { Navigator.go(rootPane, "/Favoris.fxml", "Favoris - SmarTounsi"); }
    @FXML private void goQuiz() { Navigator.go(rootPane, "/quiz.fxml", "Quiz - SmarTounsi"); }
    @FXML private void goProjets() { Navigator.go(rootPane, "/ProjectView.fxml", "Projets - SmarTounsi"); }
    @FXML private void goEvenements() { Navigator.go(rootPane, "/Evenements.fxml", "Événements - SmarTounsi"); }
    @FXML private void goProfil() { Navigator.go(rootPane, "/Profil.fxml", "Profil - SmarTounsi"); }
    @FXML private void goNotifications() { Navigator.go(rootPane, "/Notification.fxml", "Notifications - SmarTounsi"); }
    @FXML private void deconnexion() { Navigator.logout(rootPane); }
}
