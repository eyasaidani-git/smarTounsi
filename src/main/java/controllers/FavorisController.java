package controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import models.Favori;
import models.Utilisateur;
import services.FavoriService;
import util.Navigator;
import util.Session;

import java.util.List;

public class FavorisController {

    @FXML
    private BorderPane rootPane;

    @FXML
    private VBox listBox;

    private final FavoriService favoriService = new FavoriService();

    @FXML
    private void initialize() {
        Utilisateur user = Session.getCurrentUser();

        if (user == null) {
            Navigator.go(rootPane, "/Connexion.fxml", "Connexion - SmarTounsi");
            return;
        }

        chargerFavoris(user.getId());
    }

    private void chargerFavoris(int idUtilisateur) {
        listBox.getChildren().clear();

        List<Favori> favoris = favoriService.getByUtilisateur(idUtilisateur);

        if (favoris.isEmpty()) {
            Label empty = new Label("Aucun favori pour le moment.");
            empty.setStyle("-fx-font-size: 18px; -fx-text-fill: #64748b;");
            listBox.getChildren().add(empty);
            return;
        }

        for (Favori f : favoris) {
            VBox card = new VBox(6);
            card.setStyle("""
                    -fx-background-color: white;
                    -fx-background-radius: 14;
                    -fx-border-color: #d7e4f2;
                    -fx-border-radius: 14;
                    -fx-padding: 18;
                    """);

            Label titre = new Label("⭐ Document favori");
            titre.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #061d33;");

            Label info = new Label("ID document : " + f.getIdDocument());
            info.setStyle("-fx-font-size: 14px; -fx-text-fill: #475569;");

            card.getChildren().addAll(titre, info);
            listBox.getChildren().add(card);
        }
    }

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
