package controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import models.Notification;
import models.Utilisateur;
import services.NotificationService;
import util.Navigator;
import util.Session;

import java.time.format.DateTimeFormatter;
import java.util.List;

public class NotificationController {

    @FXML
    private BorderPane rootPane;

    @FXML
    private VBox listBox;

    @FXML
    private Label titleLabel;

    private final NotificationService notificationService = new NotificationService();

    @FXML
    private void initialize() {
        Utilisateur user = Session.getCurrentUser();

        if (user == null) {
            Navigator.go(rootPane, "/Connexion.fxml", "Connexion - SmarTounsi");
            return;
        }

        chargerNotifications(user.getId());
    }

    private void chargerNotifications(int idUtilisateur) {
        listBox.getChildren().clear();

        List<Notification> notifications = notificationService.getByUtilisateur(idUtilisateur);

        if (notifications.isEmpty()) {
            Label empty = new Label("Aucune notification pour le moment.");
            empty.setStyle("-fx-font-size: 18px; -fx-text-fill: #64748b;");
            listBox.getChildren().add(empty);
            return;
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

        for (Notification n : notifications) {
            VBox card = new VBox(6);
            card.setStyle("""
                    -fx-background-color: white;
                    -fx-background-radius: 14;
                    -fx-border-color: #d7e4f2;
                    -fx-border-radius: 14;
                    -fx-padding: 18;
                    """);

            Label titre = new Label((n.isLu() ? "✅ " : "🔔 ") + n.getTitre());
            titre.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #061d33;");

            Label message = new Label(n.getMessage());
            message.setWrapText(true);
            message.setStyle("-fx-font-size: 14px; -fx-text-fill: #475569;");

            String dateText = n.getDateCreation() == null ? "" : n.getDateCreation().format(formatter);
            Label date = new Label(dateText);
            date.setStyle("-fx-font-size: 12px; -fx-text-fill: #94a3b8;");

            card.getChildren().addAll(titre, message, date);
            listBox.getChildren().add(card);
        }
    }

    @FXML
    private void marquerToutCommeLu() {
        Utilisateur user = Session.getCurrentUser();

        if (user == null) {
            return;
        }

        notificationService.marquerToutCommeLu(user.getId());
        chargerNotifications(user.getId());

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Notifications");
        alert.setHeaderText(null);
        alert.setContentText("Toutes les notifications sont marquées comme lues.");
        alert.showAndWait();
    }

    @FXML private void goDashboard() { Navigator.goDashboard(rootPane); }
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
