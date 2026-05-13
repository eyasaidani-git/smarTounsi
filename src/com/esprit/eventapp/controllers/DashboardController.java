package com.esprit.eventapp.controllers;

import com.esprit.eventapp.models.Evenement;
import com.esprit.eventapp.models.User;
import com.esprit.eventapp.services.EvenementDAO;
import com.esprit.eventapp.services.UserDAO;
import com.esprit.eventapp.utils.SessionManager;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import javafx.geometry.Insets;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Region;
import javafx.scene.shape.Rectangle;

public class DashboardController {

    @FXML private FlowPane eventsGrid;
    @FXML private StackPane contentArea;
    @FXML private VBox eventsView;

    private final EvenementDAO evenementDAO = new EvenementDAO();

    @FXML
    public void initialize() {
        loadEvents();
    }

    private void loadEvents() {
        eventsGrid.getChildren().clear();

        // Carte d'ajout (+)
        VBox addCard = new VBox();
        addCard.getStyleClass().add("add-event-card");
        addCard.setAlignment(Pos.CENTER);
        addCard.prefWidthProperty().bind(eventsGrid.widthProperty().subtract(80).divide(2));
        addCard.setMinWidth(400);
        addCard.setMinHeight(430);
        addCard.setStyle("-fx-background-color: #f8f9fa; -fx-border-color: #bdc3c7; -fx-border-style: dashed; -fx-border-width: 2; -fx-border-radius: 10; -fx-background-radius: 10; -fx-cursor: hand;");
        Label plusLabel = new Label("+");
        plusLabel.setStyle("-fx-font-size: 60px; -fx-text-fill: #bdc3c7;");
        Label addText = new Label("Ajouter un événement");
        addText.setStyle("-fx-font-size: 18px; -fx-text-fill: #bdc3c7; -fx-font-weight: bold;");
        addCard.getChildren().addAll(plusLabel, addText);
        addCard.setOnMouseClicked(e -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/esprit/eventapp/views/AddEvenement.fxml"));
                Parent root = loader.load();
                AddEvenementController controller = loader.getController();
                controller.setDashboardController(this);
                contentArea.getChildren().clear();
                contentArea.getChildren().add(root);
            } catch (IOException ex) { ex.printStackTrace(); }
        });
        eventsGrid.getChildren().add(addCard);

        // Vérifier le rôle de l'utilisateur connecté
        boolean isAdmin = SessionManager.getInstance().getCurrentUser() != null
                && "ADMIN".equalsIgnoreCase(SessionManager.getInstance().getCurrentUser().getRole());
        int currentUserId = SessionManager.getInstance().getCurrentUser() != null
                ? SessionManager.getInstance().getCurrentUser().getId() : 0;

        // Charger les événements depuis la DB
        List<Evenement> evenements = evenementDAO.afficherEvenements();
        
        if (evenements.isEmpty()) {
            VBox emptyState = new VBox(20);
            emptyState.setAlignment(Pos.CENTER);
            emptyState.setPadding(new Insets(100, 50, 100, 50));
            emptyState.prefWidthProperty().bind(eventsGrid.widthProperty().subtract(100));
            
            Label emptyIcon = new Label("📅");
            emptyIcon.setStyle("-fx-font-size: 100px; -fx-text-fill: #dfe6e9;");
            
            Label emptyTitle = new Label("Aucun événement disponible");
            emptyTitle.setStyle("-fx-font-size: 28px; -fx-font-weight: bold; -fx-text-fill: #b2bec3;");
            
            Label emptySubtitle = new Label("Commencez par créer votre premier événement en cliquant sur le bouton '+' à gauche.");
            emptySubtitle.setStyle("-fx-font-size: 18px; -fx-text-fill: #b2bec3;");
            
            emptyState.getChildren().addAll(emptyIcon, emptyTitle, emptySubtitle);
            eventsGrid.getChildren().add(emptyState);
        }

        for (Evenement event : evenements) {
            VBox card = new VBox();
            card.getStyleClass().add("event-card");
            card.prefWidthProperty().bind(eventsGrid.widthProperty().subtract(80).divide(2));
            card.setMinWidth(400);
            card.setSpacing(0);

            // --- Top part: Image and Overlay ---
            StackPane imageContainer = new StackPane();
            imageContainer.getStyleClass().add("event-image-container");
            imageContainer.setPrefHeight(280);
            
            // Clip pour l'image
            Rectangle imgClip = new Rectangle();
            imgClip.setArcWidth(40);
            imgClip.setArcHeight(40);
            imgClip.widthProperty().bind(card.widthProperty());
            imgClip.setHeight(280);
            imageContainer.setClip(imgClip);

            ImageView imageView = new ImageView();
            imageView.getStyleClass().add("event-image-view");
            imageView.fitWidthProperty().bind(card.widthProperty());
            imageView.setFitHeight(280);
            imageView.setPreserveRatio(false);

            if (event.getImage() != null && !event.getImage().isEmpty()) {
                try {
                    Image img;
                    if (event.getImage().startsWith("file:") || event.getImage().startsWith("http")) {
                        img = new Image(event.getImage());
                    } else if (event.getImage().startsWith("/")) {
                        img = new Image(getClass().getResourceAsStream(event.getImage()));
                    } else {
                        img = new Image("file:" + event.getImage());
                    }
                    imageView.setImage(img);
                } catch (Exception ex) {
                    imageView.setStyle("-fx-background-color: #bdc3c7;");
                }
            } else {
                imageView.setStyle("-fx-background-color: #bdc3c7;");
            }

            // Gradient Overlay
            Region overlay = new Region();
            overlay.getStyleClass().add("event-overlay");
            overlay.setPrefHeight(150);
            StackPane.setAlignment(overlay, Pos.BOTTOM_CENTER);

            // Type Badge on Top Left
            Label typeBadge = new Label(event.getTypeEvenement().toUpperCase());
            typeBadge.getStyleClass().add("event-date-badge");
            StackPane.setAlignment(typeBadge, Pos.TOP_LEFT);
            StackPane.setMargin(typeBadge, new Insets(15));

            // Price on Top Right
            Label priceLbl = new Label(String.format("%.0f TND", event.getPrix()));
            priceLbl.setStyle("-fx-background-color: rgba(0,0,0,0.6); -fx-text-fill: white; -fx-padding: 8 15; -fx-background-radius: 10; -fx-font-weight: bold;");
            StackPane.setAlignment(priceLbl, Pos.TOP_RIGHT);
            StackPane.setMargin(priceLbl, new Insets(15));

            imageContainer.getChildren().addAll(imageView, overlay, typeBadge, priceLbl);

            // --- Bottom part: Info ---
            VBox infoBox = new VBox(12);
            infoBox.setPadding(new Insets(20, 25, 25, 25));

            Label title = new Label(event.getTitre());
            title.getStyleClass().add("event-title");
            title.setWrapText(true);

            HBox dateLocBox = new HBox(15);
            dateLocBox.setAlignment(Pos.CENTER_LEFT);
            
            java.time.format.DateTimeFormatter dtFormatter = java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
            String startStr = event.getDateDebut().format(dtFormatter);
            String endStr = event.getDateFin().format(dtFormatter);
            
            Label dateLbl = new Label("📅 " + startStr + " → " + endStr);
            dateLbl.setStyle("-fx-text-fill: #636E72; -fx-font-size: 13px; -fx-font-weight: bold;");
            
            Label locLbl = new Label("📍 " + event.getLieu());
            locLbl.setStyle("-fx-text-fill: #636E72; -fx-font-size: 14px;");
            
            dateLocBox.getChildren().addAll(dateLbl, locLbl);

            // Bottom row: Creator and Actions
            HBox bottomRow = new HBox();
            bottomRow.setAlignment(Pos.CENTER_LEFT);
            
            UserDAO userDAO = new UserDAO();
            User creator = userDAO.getUserById(event.getCreateurId());
            String creatorName = (creator != null) ? creator.getPrenom() + " " + creator.getNom() : "Admin";
            Label creatorLbl = new Label("👤 Par " + creatorName);
            creatorLbl.setStyle("-fx-text-fill: #B2BEC3; -fx-font-size: 12px;");

            Region spacer = new Region();
            HBox.setHgrow(spacer, Priority.ALWAYS);

            Button btnDetails = new Button("Voir Détails →");
            btnDetails.getStyleClass().add("button-link");
            btnDetails.setStyle("-fx-font-size: 14px; -fx-padding: 0;");
            btnDetails.setOnAction(e -> openEventDetails(event));

            bottomRow.getChildren().addAll(creatorLbl, spacer, btnDetails);

            infoBox.getChildren().addAll(title, dateLocBox, bottomRow);
            
            card.getChildren().addAll(imageContainer, infoBox);

            // Clic sur la carte → voir les détails
            card.setOnMouseClicked(e -> {
                if (e.getClickCount() == 1) openEventDetails(event);
            });
            
            eventsGrid.getChildren().add(card);
        }
    }

    /** Ouvrir la vue de modification directement depuis le Dashboard */
    private void openEditView(Evenement event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/esprit/eventapp/views/EditEvenement.fxml"));
            Parent root = loader.load();
            EditEvenementController controller = loader.getController();
            controller.setEvenement(event);
            controller.setDashboardController(this);
            contentArea.getChildren().clear();
            contentArea.getChildren().add(root);
        } catch (IOException ex) { ex.printStackTrace(); }
    }

    /** Demander confirmation puis supprimer */
    private void confirmDelete(Evenement event) {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirmer la suppression");
        confirm.setHeaderText("Supprimer « " + event.getTitre() + " » ?");
        confirm.setContentText("Cette action est irréversible. Tous les avis associés seront également supprimés.");
        Optional<ButtonType> result = confirm.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            evenementDAO.supprimerEvenement(event.getIdEvenement());
            showEventsView(); // Rafraîchir
        }
    }

    private void openEventDetails(Evenement event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/esprit/eventapp/views/EventDetails.fxml"));
            Parent root = loader.load();
            EventDetailsController controller = loader.getController();
            controller.setDashboardController(this);
            controller.setEvent(event);
            contentArea.getChildren().clear();
            contentArea.getChildren().add(root);
        } catch (IOException ex) { ex.printStackTrace(); }
    }

    public void loadNode(Parent root) {
        contentArea.getChildren().clear();
        contentArea.getChildren().add(root);
    }

    @FXML void showEvents(ActionEvent event) { showEventsView(); }

    public void showEventsView() {
        contentArea.getChildren().clear();
        contentArea.getChildren().add(eventsView);
        loadEvents();
    }

    @FXML void showHistory(ActionEvent event) { loadView("/com/esprit/eventapp/views/History.fxml"); }
    @FXML void showParticipation(ActionEvent event) { loadView("/com/esprit/eventapp/views/Participation.fxml"); }

    private void loadView(String fxmlPath) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();
            Object controller = loader.getController();
            if (controller instanceof HistoryController) {
                ((HistoryController) controller).setDashboardController(this);
            }
            contentArea.getChildren().clear();
            contentArea.getChildren().add(root);
        } catch (IOException e) { e.printStackTrace(); }
    }

    @FXML
    void logout(ActionEvent event) {
        SessionManager.getInstance().cleanSession();
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/com/esprit/eventapp/views/SignIn.fxml"));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root, 1000, 700));
            stage.setFullScreen(true);
        } catch (IOException e) { e.printStackTrace(); }
    }
}
