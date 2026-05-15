package controllers;

import enums.EvenementType;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import models.Evenement;
import services.EvenementService;
import services.ParticipationEvenementService;
import util.Navigator;
import util.Session;

import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class EvenementsController {

    @FXML private BorderPane rootPane;
    @FXML private TextField searchField;
    @FXML private ComboBox<String> typeFilter;
    @FXML private ComboBox<String> statutFilter;
    @FXML private ComboBox<String> organiseFilter;
    @FXML private FlowPane eventsGrid;
    @FXML private Label countLabel;

    private final EvenementService evenementService = new EvenementService();
    private final ParticipationEvenementService participationService = new ParticipationEvenementService();
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    @FXML
    private void initialize() {
        typeFilter.getItems().setAll("TOUS", "forum_stage", "fete", "hackathon", "conference", "autre");
        typeFilter.setValue("TOUS");

        statutFilter.getItems().setAll("TOUS", "a_venir", "complet", "annule", "termine");
        statutFilter.setValue("TOUS");

        organiseFilter.getItems().setAll("TOUS", "SITE", "EXTERNE");
        organiseFilter.setValue("TOUS");

        chargerEvenements();
    }

    @FXML
    private void chargerEvenements() {
        String keyword = searchField.getText();
        String type = typeFilter.getValue();
        String statut = statutFilter.getValue();
        String organise = organiseFilter.getValue();

        List<Evenement> evenements = evenementService.search(keyword, type, statut, organise);
        eventsGrid.getChildren().clear();

        for (Evenement event : evenements) {
            eventsGrid.getChildren().add(createEventCard(event));
        }

        countLabel.setText(evenements.size() + " événement(s)");
    }

    private VBox createEventCard(Evenement e) {
        VBox card = new VBox(12);
        card.setPrefWidth(360);
        card.setStyle("""
                -fx-background-color: white;
                -fx-background-radius: 18;
                -fx-border-color: #d7e4f2;
                -fx-border-radius: 18;
                -fx-effect: dropshadow(gaussian, rgba(2,48,71,0.12), 14, 0, 0, 4);
                """);

        StackPane imagePane = new StackPane();
        imagePane.setPrefHeight(170);
        imagePane.setStyle("-fx-background-color: #dce8f4; -fx-background-radius: 18 18 0 0;");

        ImageView imageView = new ImageView();
        imageView.setFitWidth(360);
        imageView.setFitHeight(170);
        imageView.setPreserveRatio(false);
        imageView.setSmooth(true);

        if (e.getImageEvenement() != null && !e.getImageEvenement().isBlank()) {
            try {
                imageView.setImage(new Image(e.getImageEvenement(), true));
            } catch (Exception ignored) {
            }
        }

        Label typeBadge = new Label(e.getTypeEvenement() == null ? "AUTRE" : e.getTypeEvenement().name());
        typeBadge.setStyle("-fx-background-color: #023047cc; -fx-text-fill: white; -fx-padding: 6 10; -fx-background-radius: 10; -fx-font-weight: bold;");
        StackPane.setAlignment(typeBadge, Pos.TOP_LEFT);
        StackPane.setMargin(typeBadge, new Insets(12));

        Label price = new Label(formatPrice(e.getTarif()));
        price.setStyle("-fx-background-color: #FEB707; -fx-text-fill: #061d33; -fx-padding: 6 10; -fx-background-radius: 10; -fx-font-weight: bold;");
        StackPane.setAlignment(price, Pos.TOP_RIGHT);
        StackPane.setMargin(price, new Insets(12));

        imagePane.getChildren().addAll(imageView, typeBadge, price);

        VBox info = new VBox(8);
        info.setPadding(new Insets(16));

        Label title = new Label(e.getTitre());
        title.setWrapText(true);
        title.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: #061d33;");

        Label details = new Label(
                "📍 " + safe(e.getLieu()) + "\n" +
                        "📅 " + (e.getDateDebut() == null ? "" : e.getDateDebut().format(formatter)) + "\n" +
                        "👥 " + participationService.compterParticipantsConfirmes(e.getId()) + "/" + e.getCapacity() + " confirmé(s)"
        );
        details.setStyle("-fx-font-size: 13px; -fx-text-fill: #475569;");
        details.setWrapText(true);

        Label statut = new Label("Statut : " + e.getStatut() + " · " + (e.isOrganiseParSite() ? "Organisé par site" : "Événement externe"));
        statut.setStyle("-fx-font-size: 12px; -fx-text-fill: #64748b;");

        HBox buttons = new HBox(8);
        buttons.setAlignment(Pos.CENTER_LEFT);

        Button participateBtn = new Button(e.isOrganiseParSite() ? "Participer" : "Intéressé");
        participateBtn.setStyle("-fx-background-color: #FEB707; -fx-text-fill: black; -fx-font-weight: bold; -fx-background-radius: 10;");
        participateBtn.setOnAction(event -> participer(e));

        Button participantsBtn = new Button("Participants");
        participantsBtn.setStyle("-fx-background-color: #168aad; -fx-text-fill: white; -fx-background-radius: 10;");
        participantsBtn.setOnAction(event -> ouvrirParticipations(e));

        Button editBtn = new Button("Modifier");
        editBtn.setStyle("-fx-background-color: #023047; -fx-text-fill: white; -fx-background-radius: 10;");
        editBtn.setOnAction(event -> ouvrirForm(e));

        Button deleteBtn = new Button("Supprimer");
        deleteBtn.setStyle("-fx-background-color: #ef4444; -fx-text-fill: white; -fx-background-radius: 10;");
        deleteBtn.setOnAction(event -> supprimer(e));

        buttons.getChildren().addAll(participateBtn, participantsBtn, editBtn, deleteBtn);
        info.getChildren().addAll(title, details, statut, buttons);

        card.getChildren().addAll(imagePane, info);
        return card;
    }

    private void participer(Evenement e) {
        try {
            int userId = Session.getUserId();

            if (e.isOrganiseParSite()) {
                participationService.participer(userId, e.getId());
                afficherInfo("Participation", "Votre demande de participation est enregistrée.");
            } else {
                participationService.interesser(userId, e.getId());
                afficherInfo("Intérêt", "Vous êtes marqué comme intéressé par cet événement externe.");
            }

            chargerEvenements();
        } catch (Exception ex) {
            afficherErreur(ex.getMessage());
        }
    }

    @FXML
    private void ouvrirAjout() {
        ouvrirForm(null);
    }

    private void ouvrirForm(Evenement evenement) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/EvenementForm.fxml"));
            Parent root = loader.load();

            EvenementFormController controller = loader.getController();
            controller.setEvenement(evenement);

            Stage stage = (Stage) rootPane.getScene().getWindow();
            stage.setScene(new Scene(root, 1400, 850));
            stage.setTitle(evenement == null ? "Créer événement" : "Modifier événement");
            stage.show();
        } catch (Exception ex) {
            ex.printStackTrace();
            afficherErreur("Impossible d'ouvrir le formulaire : " + ex.getMessage());
        }
    }

    private void ouvrirParticipations(Evenement evenement) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ParticipationEvenement.fxml"));
            Parent root = loader.load();

            ParticipationEvenementController controller = loader.getController();
            controller.setEvenement(evenement);

            Stage stage = (Stage) rootPane.getScene().getWindow();
            stage.setScene(new Scene(root, 1400, 850));
            stage.setTitle("Participants - " + evenement.getTitre());
            stage.show();
        } catch (Exception ex) {
            ex.printStackTrace();
            afficherErreur("Impossible d'ouvrir les participations : " + ex.getMessage());
        }
    }

    private void supprimer(Evenement e) {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Suppression");
        confirm.setHeaderText(null);
        confirm.setContentText("Supprimer l'événement : " + e.getTitre() + " ?");

        if (confirm.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
            evenementService.delete(e);
            chargerEvenements();
        }
    }

    private String formatPrice(BigDecimal price) {
        if (price == null || price.compareTo(BigDecimal.ZERO) == 0) {
            return "Gratuit";
        }
        return price + " TND";
    }

    private String safe(String value) {
        return value == null ? "" : value;
    }

    private void afficherInfo(String titre, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titre);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void afficherErreur(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Erreur");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML private void goDashboard() { Navigator.go(rootPane, "/Profil.fxml", "Dashboard - SmarTounsi"); }
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
