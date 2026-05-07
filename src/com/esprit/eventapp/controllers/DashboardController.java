package com.esprit.eventapp.controllers;

import com.esprit.eventapp.models.Evenement;
import com.esprit.eventapp.services.EvenementDAO;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.List;

public class DashboardController {

    @FXML
    private FlowPane eventsGrid;

    @FXML
    private StackPane contentArea;

    @FXML
    private VBox eventsView;

    private EvenementDAO evenementDAO = new EvenementDAO();

    @FXML
    public void initialize() {
        loadEvents();
    }

    private void loadEvents() {
        eventsGrid.getChildren().clear();

        // Carte d'ajout (+)
        VBox addCard = new VBox();
        addCard.getStyleClass().add("add-event-card");
        Label plusLabel = new Label("+");
        plusLabel.setStyle("-fx-font-size: 60px; -fx-text-fill: #00334E;");
        addCard.getChildren().add(plusLabel);
        
        addCard.setOnMouseClicked(e -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/esprit/eventapp/views/AddEvenement.fxml"));
                Parent root = loader.load();
                AddEvenementController controller = loader.getController();
                controller.setDashboardController(this);
                contentArea.getChildren().clear();
                contentArea.getChildren().add(root);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        });
        
        eventsGrid.getChildren().add(addCard);

        // Charger les événements depuis la DB
        List<Evenement> evenements = evenementDAO.afficherEvenements();
        for (Evenement event : evenements) {
            VBox card = new VBox(10);
            card.getStyleClass().add("event-card");

            Label title = new Label(event.getTitre());
            title.setStyle("-fx-font-weight: bold; -fx-font-size: 20px; -fx-text-fill: #00334E;");

            Label type = new Label("Type: " + event.getTypeEvenement());
            Label date = new Label("Date: " + event.getDateEvenement().toString());
            Label lieu = new Label("Lieu: " + event.getLieu());

            card.getChildren().addAll(title, type, date, lieu);
            
            // Clic sur un événement pour voir les détails
            card.setOnMouseClicked(e -> openEventDetails(event));

            eventsGrid.getChildren().add(card);
        }
    }

    private void openEventDetails(Evenement event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/esprit/eventapp/views/EventDetails.fxml"));
            Parent root = loader.load();
            
            EventDetailsController controller = loader.getController();
            controller.setEvent(event);
            
            contentArea.getChildren().clear();
            contentArea.getChildren().add(root);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    @FXML
    void showEvents(ActionEvent event) {
        showEventsView();
    }

    public void showEventsView() {
        contentArea.getChildren().clear();
        contentArea.getChildren().add(eventsView);
        loadEvents(); // Reload in case new events were added
    }

    @FXML
    void showHistory(ActionEvent event) {
        loadView("/com/esprit/eventapp/views/History.fxml");
    }
    
    @FXML
    void showAvis(ActionEvent event) {
        loadView("/com/esprit/eventapp/views/Avis.fxml");
    }

    private void loadView(String fxmlPath) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource(fxmlPath));
            contentArea.getChildren().clear();
            contentArea.getChildren().add(root);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void logout(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/com/esprit/eventapp/views/SignIn.fxml"));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Scene scene = new Scene(root, 1000, 700);
            stage.setScene(scene);
            stage.setFullScreen(true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
