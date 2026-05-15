package controllers;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import models.Reclamation;
import models.Utilisateur;
import services.ReclamationService;
import util.Navigator;
import util.Session;

public class ReclamationsController {
    @FXML private BorderPane rootPane;
    @FXML private Label userLabel;
    @FXML private Label statusLabel;
    @FXML private TableView<Reclamation> reclamationsTable;
    @FXML private TextField sujetField;
    @FXML private TextArea contenuArea;

    private Utilisateur currentUser;
    private ReclamationService reclamationService;

    @FXML
    private void initialize() {
        currentUser = Session.getCurrentUser();
        if (currentUser == null) {
            Navigator.go(rootPane, "/Connexion.fxml", "Connexion - SmarTounsi");
            return;
        }

        reclamationService = new ReclamationService();
        userLabel.setText(labelUtilisateur(currentUser));
        setupTable();
        refreshReclamations();
    }

    private void setupTable() {
        TableColumn<Reclamation, Number> idColumn = new TableColumn<>("ID");
        idColumn.setCellValueFactory(data -> new SimpleIntegerProperty(data.getValue().getId()));
        idColumn.setPrefWidth(70);

        TableColumn<Reclamation, String> sujetColumn = new TableColumn<>("Sujet");
        sujetColumn.setCellValueFactory(data -> new SimpleStringProperty(safe(data.getValue().getSujet())));
        sujetColumn.setPrefWidth(220);

        TableColumn<Reclamation, String> contenuColumn = new TableColumn<>("Contenu");
        contenuColumn.setCellValueFactory(data -> new SimpleStringProperty(safe(data.getValue().getContenu())));
        contenuColumn.setPrefWidth(320);

        TableColumn<Reclamation, String> statutColumn = new TableColumn<>("Statut");
        statutColumn.setCellValueFactory(data -> new SimpleStringProperty(safe(data.getValue().getStatut())));
        statutColumn.setPrefWidth(120);

        TableColumn<Reclamation, String> reponseColumn = new TableColumn<>("Réponse / motif");
        reponseColumn.setCellValueFactory(data -> new SimpleStringProperty(firstNotBlank(
                data.getValue().getReponseAdmin(),
                data.getValue().getMotifRejet())));
        reponseColumn.setPrefWidth(300);

        reclamationsTable.getColumns().setAll(idColumn, sujetColumn, contenuColumn, statutColumn, reponseColumn);
    }

    @FXML
    private void envoyerReclamation() {
        String sujet = sujetField.getText() == null ? "" : sujetField.getText().trim();
        String contenu = contenuArea.getText() == null ? "" : contenuArea.getText().trim();

        if (sujet.isBlank() || contenu.isBlank()) {
            showAlert(Alert.AlertType.WARNING, "Réclamation", "Le sujet et le contenu sont obligatoires.");
            return;
        }

        Reclamation reclamation = new Reclamation(currentUser.getId(), sujet, contenu);
        reclamationService.add(reclamation);
        sujetField.clear();
        contenuArea.clear();
        refreshReclamations();

        if (ReclamationService.STATUT_REJETEE.equals(reclamation.getStatut())) {
            statusLabel.setText("Réclamation rejetée automatiquement à cause de mots interdits.");
        } else {
            statusLabel.setText("Réclamation envoyée et en attente de traitement.");
        }
    }

    @FXML
    private void refreshReclamations() {
        if (isAdmin()) {
            reclamationsTable.setItems(FXCollections.observableArrayList(reclamationService.getAll()));
        } else {
            reclamationsTable.setItems(FXCollections.observableArrayList(reclamationService.getByUtilisateur(currentUser.getId())));
        }
    }

    @FXML
    private void goProfil() {
        Navigator.go(rootPane, isAdmin() ? "/AdminDashboard.fxml" : "/Profil.fxml", isAdmin() ? "Admin - SmarTounsi" : "Profil - SmarTounsi");
    }

    @FXML
    private void logout() {
        Navigator.logout(rootPane);
    }

    private boolean isAdmin() {
        return currentUser != null && "admin".equalsIgnoreCase(safe(currentUser.getRole()));
    }

    private String labelUtilisateur(Utilisateur user) {
        String name = user.getNomComplet();
        if (name == null || name.isBlank()) {
            name = "Utilisateur " + user.getId();
        }
        return name + " (" + safe(user.getEmail()) + ")";
    }

    private String firstNotBlank(String first, String second) {
        return first != null && !first.isBlank() ? first : safe(second);
    }

    private String safe(String value) {
        return value == null ? "" : value;
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
