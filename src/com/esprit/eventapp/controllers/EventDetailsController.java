package com.esprit.eventapp.controllers;

import com.esprit.eventapp.models.AvisEvenement;
import com.esprit.eventapp.models.Evenement;
import com.esprit.eventapp.services.AvisEvenementDAO;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

import java.sql.Timestamp;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class EventDetailsController {

    @FXML
    private Label lblTitre;

    @FXML
    private Label lblType;

    @FXML
    private Label lblDate;

    @FXML
    private Label lblHeure;

    @FXML
    private Label lblLieu;

    @FXML
    private Label lblDescription;

    @FXML
    private VBox avisContainer;

    @FXML
    private TextField txtNom;

    @FXML
    private ComboBox<Integer> comboNote;

    @FXML
    private TextArea txtCommentaire;

    private final AvisEvenementDAO avisDAO = new AvisEvenementDAO();
    private Evenement event;

    @FXML
    public void initialize() {
        comboNote.setItems(FXCollections.observableArrayList(1, 2, 3, 4, 5));
    }

    public void setEvent(Evenement event) {
        this.event = event;
        renderEvent();
        loadAvis();
    }

    private void renderEvent() {
        if (event == null) return;

        lblTitre.setText(event.getTitre());
        lblType.setText("Type: " + nullSafe(event.getTypeEvenement()));
        lblDate.setText("Date: " + (event.getDateEvenement() != null ? event.getDateEvenement().toString() : ""));
        lblHeure.setText("Heure: " + (event.getHeureEvenement() != null ? event.getHeureEvenement().toString() : ""));
        lblLieu.setText("Lieu: " + nullSafe(event.getLieu()));
        lblDescription.setText(nullSafe(event.getDescription()));
    }

    private void loadAvis() {
        avisContainer.getChildren().clear();
        if (event == null) return;

        List<AvisEvenement> avis = avisDAO.afficherAvisParEvenement(event.getIdEvenement());
        for (AvisEvenement a : avis) {
            avisContainer.getChildren().add(createAvisCard(a));
        }
    }

    private VBox createAvisCard(AvisEvenement avis) {
        VBox card = new VBox(6);
        card.getStyleClass().add("event-card");
        card.setStyle("-fx-padding: 14; -fx-background-radius: 14; -fx-border-radius: 14;");

        Label header = new Label(
                nullSafe(avis.getNomAuteur()) + " — " + avis.getNote() + "/5"
        );
        header.setStyle("-fx-font-weight: bold; -fx-font-size: 16px; -fx-text-fill: #00334E;");

        Label date = new Label(formatTimestamp(avis.getDateAvis()));
        date.setStyle("-fx-text-fill: #6B7280;");

        Label body = new Label(nullSafe(avis.getCommentaire()));
        body.setWrapText(true);

        card.getChildren().addAll(header, date, body);
        return card;
    }

    @FXML
    public void submitAvis() {
        if (event == null) return;

        String nom = txtNom.getText();
        Integer note = comboNote.getValue();
        String commentaire = txtCommentaire.getText();

        if (nom == null || nom.isEmpty() || note == null || commentaire == null || commentaire.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Champs manquants");
            alert.setHeaderText(null);
            alert.setContentText("Veuillez remplir tous les champs.");
            alert.showAndWait();
            return;
        }

        AvisEvenement nouvelAvis = new AvisEvenement(event.getIdEvenement(), nom, commentaire, note);
        // date_avis est gérée par la DB, mais on garde un objet complet si besoin
        nouvelAvis.setDateAvis(new Timestamp(System.currentTimeMillis()));
        avisDAO.ajouterAvis(nouvelAvis);

        txtNom.clear();
        txtCommentaire.clear();
        comboNote.setValue(null);

        loadAvis();
    }

    private static String nullSafe(String s) {
        return s == null ? "" : s;
    }

    private static String formatTimestamp(Timestamp ts) {
        if (ts == null) return "";
        return ts.toLocalDateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
    }
}

