package com.esprit.eventapp.controllers;

import com.esprit.eventapp.models.Evenement;
import com.esprit.eventapp.services.EvenementDAO;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;

public class AddEvenementController {

    @FXML private TextField titreField;
    @FXML private TextArea descField;
    @FXML private TextField typeField;
    @FXML private TextField lieuField;
    @FXML private DatePicker dateField;
    @FXML private TextField heureField;
    @FXML private Label messageLabel;

    private EvenementDAO evenementDAO = new EvenementDAO();
    private DashboardController dashboardController;

    public void setDashboardController(DashboardController dashboardController) {
        this.dashboardController = dashboardController;
    }

    @FXML
    void handleSave(ActionEvent event) {
        try {
            String titre = titreField.getText();
            String desc = descField.getText();
            String type = typeField.getText();
            String lieu = lieuField.getText();
            LocalDate date = dateField.getValue();
            String heureStr = heureField.getText();

            if (titre.isEmpty() || type.isEmpty() || lieu.isEmpty() || date == null || heureStr.isEmpty()) {
                messageLabel.setText("Veuillez remplir les champs obligatoires.");
                return;
            }

            LocalTime heure = LocalTime.parse(heureStr);

            Evenement e = new Evenement(titre, desc, type, date, heure, lieu, "A_VENIR");
            evenementDAO.ajouterEvenement(e);
            
            // Retour au Dashboard
            goBack(event);

        } catch (Exception ex) {
            messageLabel.setText("Erreur de format. Vérifiez l'heure (ex: 14:30)");
        }
    }

    @FXML
    void goBack(ActionEvent event) throws IOException {
        if (dashboardController != null) {
            dashboardController.showEventsView();
            return;
        }

        Parent root = FXMLLoader.load(getClass().getResource("/com/esprit/eventapp/views/Dashboard.fxml"));
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setMaximized(true);
        stage.setFullScreen(true);
        stage.show();
    }
}
