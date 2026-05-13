package com.esprit.eventapp.controllers;

import com.esprit.eventapp.models.AvisEvenement;
import com.esprit.eventapp.models.Evenement;
import com.esprit.eventapp.services.AvisEvenementDAO;
import com.esprit.eventapp.services.EvenementDAO;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.util.List;

public class AvisController {

    @FXML
    private ComboBox<Evenement> comboEvenement;

    @FXML
    private TextField txtNom;

    @FXML
    private ComboBox<Integer> comboNote;

    @FXML
    private TextArea txtCommentaire;

    @FXML
    private TableView<Evenement> tableEvenements;

    @FXML
    private TableColumn<Evenement, Integer> colId;

    @FXML
    private TableColumn<Evenement, String> colTitre;

    @FXML
    private TableColumn<Evenement, String> colType;

    @FXML
    private TableColumn<Evenement, String> colDate;

    @FXML
    private TableColumn<Evenement, String> colLieu;

    private final AvisEvenementDAO avisDAO = new AvisEvenementDAO();
    private final EvenementDAO evenementDAO = new EvenementDAO();

    @FXML
    public void initialize() {
        // Init Comboboxes
        List<Evenement> events = evenementDAO.afficherEvenements();
        comboEvenement.setItems(FXCollections.observableArrayList(events));
        
        // Setup ListCell to display only the title in the dropdown
        comboEvenement.setCellFactory(param -> new ListCell<Evenement>() {
            @Override
            protected void updateItem(Evenement item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.getTitre());
                }
            }
        });
        comboEvenement.setButtonCell(new ListCell<Evenement>() {
            @Override
            protected void updateItem(Evenement item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.getTitre());
                }
            }
        });

        comboNote.setItems(FXCollections.observableArrayList(1, 2, 3, 4, 5));

        // Init Events Table
        colId.setCellValueFactory(new PropertyValueFactory<>("idEvenement"));
        colTitre.setCellValueFactory(new PropertyValueFactory<>("titre"));
        colType.setCellValueFactory(new PropertyValueFactory<>("typeEvenement"));
        colDate.setCellValueFactory(cellData ->
                new javafx.beans.property.SimpleStringProperty(
                        cellData.getValue().getDateDebut() != null ? cellData.getValue().getDateDebut().toLocalDate().toString() : ""
                )
        );
        colLieu.setCellValueFactory(new PropertyValueFactory<>("lieu"));

        ObservableList<Evenement> data = FXCollections.observableArrayList(events);
        tableEvenements.setItems(data);

        tableEvenements.getSelectionModel().selectedItemProperty().addListener((obs, oldV, newV) -> {
            if (newV != null) comboEvenement.setValue(newV);
        });
    }

    @FXML
    public void submitAvis() {
        Evenement selectedEvent = comboEvenement.getValue();
        String nom = txtNom.getText();
        Integer note = comboNote.getValue();
        String commentaire = txtCommentaire.getText();

        if (selectedEvent == null || nom == null || nom.isEmpty() || note == null || commentaire == null || commentaire.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Champs manquants");
            alert.setHeaderText(null);
            alert.setContentText("Veuillez remplir tous les champs.");
            alert.showAndWait();
            return;
        }

        AvisEvenement nouvelAvis = new AvisEvenement(selectedEvent.getIdEvenement(), nom, commentaire, note);
        avisDAO.ajouterAvis(nouvelAvis);

        // Reset fields
        txtNom.clear();
        txtCommentaire.clear();
        comboEvenement.setValue(null);
        comboNote.setValue(null);

        // Keep events list; nothing else to refresh here
    }
}
