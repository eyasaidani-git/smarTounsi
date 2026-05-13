package com.esprit.eventapp.controllers;

import com.esprit.eventapp.models.Evenement;
import com.esprit.eventapp.services.EvenementDAO;
import com.esprit.eventapp.utils.SessionManager;
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
import java.time.LocalTime;

public class EditEvenementController {

    @FXML private TextField titreField;
    @FXML private TextArea descField;
    @FXML private TextField typeField;
    @FXML private TextField lieuField;
    @FXML private DatePicker dateDebutPicker;
    @FXML private TextField heureDebutField;
    @FXML private DatePicker dateFinPicker;
    @FXML private TextField heureFinField;
    @FXML private TextField prixField;
    @FXML private TextField placesField;
    @FXML private Label messageLabel;

    private EvenementDAO evenementDAO = new EvenementDAO();
    private DashboardController dashboardController;
    private Evenement evenement;

    public void setDashboardController(DashboardController dashboardController) {
        this.dashboardController = dashboardController;
    }

    /** Pré-remplir les champs avec les données existantes de l'événement */
    public void setEvenement(Evenement e) {
        this.evenement = e;
        titreField.setText(e.getTitre());
        descField.setText(e.getDescription() != null ? e.getDescription() : "");
        typeField.setText(e.getTypeEvenement() != null ? e.getTypeEvenement() : "");
        lieuField.setText(e.getLieu() != null ? e.getLieu() : "");
        
        if (e.getDateDebut() != null) {
            dateDebutPicker.setValue(e.getDateDebut().toLocalDate());
            heureDebutField.setText(e.getDateDebut().toLocalTime().toString());
        }
        if (e.getDateFin() != null) {
            dateFinPicker.setValue(e.getDateFin().toLocalDate());
            heureFinField.setText(e.getDateFin().toLocalTime().toString());
        }
        
        prixField.setText(String.valueOf(e.getPrix()));
        placesField.setText(String.valueOf(e.getCapacity()));
    }

    @FXML
    void handleSave(ActionEvent event) {
        try {
            String titre = titreField.getText();
            String desc = descField.getText();
            String type = typeField.getText();
            String lieu = lieuField.getText();
            
            java.time.LocalDate dateDeb = dateDebutPicker.getValue();
            String hDebStr = heureDebutField.getText();
            java.time.LocalDate dateFin = dateFinPicker.getValue();
            String hFinStr = heureFinField.getText();
            
            String prixStr = prixField.getText();
            String placesStr = placesField.getText();

            if (titre.isEmpty() || type.isEmpty() || lieu.isEmpty() || 
                dateDeb == null || hDebStr.isEmpty() || 
                dateFin == null || hFinStr.isEmpty() ||
                prixStr.isEmpty() || placesStr.isEmpty()) {
                messageLabel.setText("Veuillez remplir les champs obligatoires.");
                return;
            }

            java.time.LocalDateTime now = java.time.LocalDateTime.now();
            java.time.LocalDateTime start = java.time.LocalDateTime.of(dateDeb, java.time.LocalTime.parse(hDebStr));
            java.time.LocalDateTime end = java.time.LocalDateTime.of(dateFin, java.time.LocalTime.parse(hFinStr));

            if (start.isBefore(now)) {
                messageLabel.setText("La date de début doit être aujourd'hui ou dans le futur.");
                return;
            }

            if (end.isBefore(start)) {
                messageLabel.setText("La date de fin doit être après le début.");
                return;
            }

            double prix = Double.parseDouble(prixStr);
            int places = Integer.parseInt(placesStr);

            evenement.setTitre(titre);
            evenement.setDescription(desc);
            evenement.setTypeEvenement(type);
            evenement.setLieu(lieu);
            evenement.setDateDebut(start);
            evenement.setDateFin(end);
            evenement.setPrix(prix);
            evenement.setCapacity(places);

            System.out.println("[FRONTEND] Envoi de la modification au DAO...");
            boolean success = evenementDAO.modifierEvenement(evenement);

            if (success) {
                System.out.println("[FRONTEND] Modification réussie !");
                javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.INFORMATION);
                alert.setTitle("Succès");
                alert.setHeaderText("Événement Modifié");
                alert.setContentText("L'événement '" + titre + "' a été mis à jour avec succès.");
                alert.showAndWait();
                goBack(event);
            } else {
                System.out.println("[FRONTEND] Échec de la modification en base.");
                messageLabel.setText("Erreur : Impossible de modifier l'événement.");
            }

        } catch (Exception ex) {
            System.err.println("[FRONTEND ERROR] Erreur lors de l'édition : " + ex.getMessage());
            messageLabel.setText("Erreur : vérifiez l'heure (ex: 14:30) et les nombres.");
            ex.printStackTrace();
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
