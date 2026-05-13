package com.esprit.eventapp.controllers;

import com.esprit.eventapp.models.Evenement;
import com.esprit.eventapp.models.Participation;
import com.esprit.eventapp.models.User;
import com.esprit.eventapp.services.EvenementDAO;
import com.esprit.eventapp.services.ParticipationDAO;
import com.esprit.eventapp.utils.SessionManager;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class BuyTicketController {

    @FXML private Label lblEventTitle;
    @FXML private TextField txtName;
    @FXML private TextField txtEmail;
    @FXML private Label lblQty;
    @FXML private Label lblTotalPrice;

    private Evenement event;
    private int quantity = 1;
    private final EvenementDAO evenementDAO = new EvenementDAO();
    private final ParticipationDAO participationDAO = new ParticipationDAO();
    private EventDetailsController parentController;

    public void setEvent(Evenement event, EventDetailsController parentController) {
        this.event = event;
        this.parentController = parentController;
        lblEventTitle.setText(event.getTitre());
        updatePrice();

        User currentUser = SessionManager.getInstance().getCurrentUser();
        if (currentUser != null) {
            txtName.setText(currentUser.getPrenom() + " " + currentUser.getNom());
            txtEmail.setText(currentUser.getEmail());
        }
    }

    @FXML
    void incrementQty() {
        if (quantity < event.getCapacity()) {
            quantity++;
            updateUI();
        }
    }

    @FXML
    void decrementQty() {
        if (quantity > 1) {
            quantity--;
            updateUI();
        }
    }

    private void updateUI() {
        lblQty.setText(String.valueOf(quantity));
        updatePrice();
    }

    private void updatePrice() {
        double total = event.getPrix() * quantity;
        lblTotalPrice.setText(String.format("%.2f TND", total));
    }

    @FXML
    void handleConfirmPurchase() {
        User currentUser = SessionManager.getInstance().getCurrentUser();
        if (currentUser == null) {
            showAlert("Erreur", "Vous devez être connecté pour réserver.");
            return;
        }

        if (event.getCapacity() < quantity) {
            showAlert("Erreur", "Désolé, il n'y a plus assez de places.");
            return;
        }

        // Créer la participation
        Participation p = new Participation(currentUser.getId(), event.getIdEvenement());
        boolean success = participationDAO.ajouterParticipation(p);

        if (success) {
            String msg = p.getStatut().equals("waitlist") 
                ? "L'événement est complet. Vous avez été ajouté à la LISTE D'ATTENTE."
                : "Votre réservation a été CONFIRMÉE avec succès !";
            
            showAlert("Succès", msg);
        } else {
            showAlert("Erreur", "Une erreur est survenue lors de la réservation.");
        }
        
        if (parentController != null) {
            parentController.setEvent(event); // Rafraîchir l'affichage
        }
        
        closeStage();
    }

    @FXML
    void handleCancel() {
        closeStage();
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private void closeStage() {
        Stage stage = (Stage) lblEventTitle.getScene().getWindow();
        stage.close();
    }
}
