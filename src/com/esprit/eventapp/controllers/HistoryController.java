package com.esprit.eventapp.controllers;

import com.esprit.eventapp.models.Evenement;
import com.esprit.eventapp.models.User;
import com.esprit.eventapp.services.EvenementDAO;
import com.esprit.eventapp.services.UserDAO;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.geometry.Pos;
import javafx.geometry.Insets;

import java.time.LocalDate;
import java.util.List;

public class HistoryController {

    @FXML
    private FlowPane futureEventsGrid;

    @FXML
    private FlowPane pastEventsGrid;

    private EvenementDAO evenementDAO = new EvenementDAO();
    private DashboardController dashboardController;

    public void setDashboardController(DashboardController dashboardController) {
        this.dashboardController = dashboardController;
    }

    @FXML
    public void initialize() {
        loadHistory();
    }

    private void loadHistory() {
        futureEventsGrid.getChildren().clear();
        pastEventsGrid.getChildren().clear();

        List<Evenement> evenements = evenementDAO.afficherEvenements();
        java.time.LocalDateTime now = java.time.LocalDateTime.now();

        int futureCount = 0;
        int pastCount = 0;

        for (Evenement event : evenements) {
            if (event.getDateDebut().isBefore(now)) {
                VBox card = createEventCard(event, pastEventsGrid);
                pastEventsGrid.getChildren().add(card);
                pastCount++;
            } else {
                VBox card = createEventCard(event, futureEventsGrid);
                futureEventsGrid.getChildren().add(card);
                futureCount++;
            }
        }

        if (futureCount == 0) {
            showEmptyState(futureEventsGrid, "Aucun événement futur disponible", "🚀 Prévoyez quelque chose de nouveau !");
        }
        if (pastCount == 0) {
            showEmptyState(pastEventsGrid, "Aucun historique disponible", "⌛ Vos événements passés s'afficheront ici.");
        }
    }

    private void showEmptyState(FlowPane grid, String title, String subtitle) {
        VBox empty = new VBox(15);
        empty.setAlignment(Pos.CENTER);
        empty.setPadding(new Insets(60, 30, 60, 30));
        empty.prefWidthProperty().bind(grid.widthProperty().subtract(40));
        
        Label icon = new Label("🏜️");
        icon.setStyle("-fx-font-size: 60px; -fx-text-fill: #dfe6e9;");
        
        Label lblTitle = new Label(title);
        lblTitle.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: #b2bec3;");
        
        Label lblSubtitle = new Label(subtitle);
        lblSubtitle.setStyle("-fx-font-size: 14px; -fx-text-fill: #b2bec3;");
        
        empty.getChildren().addAll(icon, lblTitle, lblSubtitle);
        grid.getChildren().add(empty);
    }

    private VBox createEventCard(Evenement event, FlowPane parentGrid) {
        VBox card = new VBox();
        card.getStyleClass().add("event-card");
        card.prefWidthProperty().bind(parentGrid.widthProperty().subtract(80).divide(2));
        card.setMinWidth(400);
        card.setStyle("-fx-background-color: white; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.15), 10, 0, 0, 2); -fx-background-radius: 10;");

        // --- Top part: Image and Overlay ---
        javafx.scene.layout.StackPane imageContainer = new javafx.scene.layout.StackPane();
        imageContainer.setPrefHeight(380); // Agrandir la hauteur de l'image
        
        // Clip pour l'image
        javafx.scene.shape.Rectangle imgClip = new javafx.scene.shape.Rectangle();
        imgClip.setArcWidth(10);
        imgClip.setArcHeight(10);
        imgClip.widthProperty().bind(card.widthProperty());
        imgClip.setHeight(380);
        imageContainer.setClip(imgClip);

        javafx.scene.image.ImageView imageView = new javafx.scene.image.ImageView();
        imageView.fitWidthProperty().bind(card.widthProperty());
        imageView.setFitHeight(380);
        imageView.setPreserveRatio(false);

        if (event.getImage() != null && !event.getImage().isEmpty()) {
            try {
                javafx.scene.image.Image img;
                if (event.getImage().startsWith("file:") || event.getImage().startsWith("http")) {
                    img = new javafx.scene.image.Image(event.getImage());
                } else if (event.getImage().startsWith("/")) {
                    img = new javafx.scene.image.Image(getClass().getResourceAsStream(event.getImage()));
                } else {
                    img = new javafx.scene.image.Image("file:" + event.getImage());
                }
                imageView.setImage(img);
            } catch (Exception ex) {
                System.out.println("Erreur chargement image : " + ex.getMessage());
                imageView.setStyle("-fx-background-color: #bdc3c7;");
            }
        } else {
            imageView.setStyle("-fx-background-color: #bdc3c7;");
        }

        // Gradient au bas de l'image
        Region gradient = new Region();
        gradient.setStyle("-fx-background-color: linear-gradient(to top, rgba(0,0,0,0.8), transparent);");
        gradient.setPrefHeight(100);
        javafx.scene.layout.StackPane.setAlignment(gradient, Pos.BOTTOM_CENTER);

        // Lieu sur l'image
        Label lieu = new Label("📍 " + event.getLieu());
        lieu.setStyle("-fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 16px;");
        javafx.scene.layout.StackPane.setAlignment(lieu, Pos.BOTTOM_LEFT);
        javafx.scene.layout.StackPane.setMargin(lieu, new Insets(0, 0, 15, 15));

        imageContainer.getChildren().addAll(imageView, gradient, lieu);

        // --- Bottom part: Info ---
        VBox infoBox = new VBox(5);
        infoBox.setPadding(new Insets(10, 15, 10, 15));
        infoBox.setStyle("-fx-background-color: white; -fx-background-radius: 0 0 10 10;");

        Label title = new Label(event.getTitre().toUpperCase());
        title.setStyle("-fx-font-weight: bold; -fx-font-size: 18px; -fx-text-fill: #1e272e;");
        title.setWrapText(true);

        java.time.format.DateTimeFormatter formatter = java.time.format.DateTimeFormatter.ofPattern("d MMMM yyyy", java.util.Locale.FRENCH);
        String formattedDate = event.getDateDebut().format(formatter);
        
        HBox dateBox = new HBox(5);
        dateBox.setAlignment(Pos.CENTER_LEFT);
        Label iconLbl = new Label("📅");
        iconLbl.setStyle("-fx-text-fill: #e84393; -fx-font-size: 14px;"); 
        Label dateTextLbl = new Label(formattedDate + " à " + event.getDateDebut().toLocalTime().toString());
        dateTextLbl.setStyle("-fx-text-fill: #95a5a6; -fx-font-size: 14px;");
        dateBox.getChildren().addAll(iconLbl, dateTextLbl);

        // Créateur de l'événement
        UserDAO userDAO = new UserDAO();
        User creator = userDAO.getUserById(event.getCreateurId());
        String creatorName = (creator != null) ? creator.getPrenom() + " " + creator.getNom() : "Inconnu";
        Label creatorLbl = new Label("👤 Par : " + creatorName);
        creatorLbl.setStyle("-fx-text-fill: #7f8c8d; -fx-font-size: 12px; -fx-font-style: italic;");

        infoBox.getChildren().addAll(title, dateBox, creatorLbl);
        
        card.getChildren().addAll(imageContainer, infoBox);

        // Clic sur la carte -> voir les détails
        card.setOnMouseClicked(e -> openEventDetails(event));

        return card;
    }

    private void openEventDetails(Evenement event) {
        try {
            javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(getClass().getResource("/com/esprit/eventapp/views/EventDetails.fxml"));
            javafx.scene.Parent root = loader.load();
            EventDetailsController controller = loader.getController();
            controller.setDashboardController(dashboardController);
            controller.setEvent(event);
            if (dashboardController != null) {
                dashboardController.loadNode(root);
            }
        } catch (java.io.IOException ex) { ex.printStackTrace(); }
    }
}
