package com.esprit.eventapp.controllers;

import com.esprit.eventapp.models.Evenement;
import com.esprit.eventapp.services.EvenementDAO;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;

import java.time.LocalDate;
import java.util.List;

public class HistoryController {

    @FXML
    private FlowPane futureEventsGrid;

    @FXML
    private FlowPane pastEventsGrid;

    private EvenementDAO evenementDAO = new EvenementDAO();

    @FXML
    public void initialize() {
        loadHistory();
    }

    private void loadHistory() {
        futureEventsGrid.getChildren().clear();
        pastEventsGrid.getChildren().clear();

        List<Evenement> evenements = evenementDAO.afficherEvenements();
        LocalDate today = LocalDate.now();

        for (Evenement event : evenements) {
            LocalDate eventDate = event.getDateEvenement();
            VBox card = createEventCard(event);

            if (eventDate.isBefore(today)) {
                pastEventsGrid.getChildren().add(card);
            } else {
                futureEventsGrid.getChildren().add(card);
            }
        }
    }

    private VBox createEventCard(Evenement event) {
        VBox card = new VBox(10);
        card.getStyleClass().add("event-card");

        Label title = new Label(event.getTitre());
        title.setStyle("-fx-font-weight: bold; -fx-font-size: 20px; -fx-text-fill: #00334E;");

        Label type = new Label("Type: " + event.getTypeEvenement());
        Label date = new Label("Date: " + event.getDateEvenement().toString());
        Label lieu = new Label("Lieu: " + event.getLieu());

        card.getChildren().addAll(title, type, date, lieu);

        return card;
    }
}
