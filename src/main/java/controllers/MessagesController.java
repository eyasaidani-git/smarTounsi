package controllers;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import models.MessagePrive;
import models.Utilisateur;
import services.MessagePriveService;
import services.UtilisateurService;
import util.Navigator;
import util.Session;

import java.time.LocalDateTime;
import java.util.List;

public class MessagesController {
    @FXML private BorderPane rootPane;
    @FXML private Label userLabel;
    @FXML private ComboBox<String> destinataireCombo;
    @FXML private TableView<MessagePrive> conversationTable;
    @FXML private TextArea messageArea;

    private Utilisateur currentUser;
    private UtilisateurService utilisateurService;
    private MessagePriveService messagePriveService;

    @FXML
    private void initialize() {
        currentUser = Session.getCurrentUser();
        if (currentUser == null) {
            Navigator.go(rootPane, "/Connexion.fxml", "Connexion - SmarTounsi");
            return;
        }

        utilisateurService = new UtilisateurService();
        messagePriveService = new MessagePriveService();
        userLabel.setText(labelUtilisateur(currentUser));

        setupTable();
        loadDestinataires();
    }

    private void setupTable() {
        TableColumn<MessagePrive, Number> idColumn = new TableColumn<>("ID");
        idColumn.setCellValueFactory(data -> new SimpleIntegerProperty(data.getValue().getId()));
        idColumn.setPrefWidth(70);

        TableColumn<MessagePrive, String> directionColumn = new TableColumn<>("Sens");
        directionColumn.setCellValueFactory(data -> new SimpleStringProperty(
                data.getValue().getIdExpediteur() == currentUser.getId() ? "Envoyé" : "Reçu"));
        directionColumn.setPrefWidth(90);

        TableColumn<MessagePrive, String> messageColumn = new TableColumn<>("Message");
        messageColumn.setCellValueFactory(data -> new SimpleStringProperty(safe(data.getValue().getContenu())));
        messageColumn.setPrefWidth(520);

        TableColumn<MessagePrive, String> dateColumn = new TableColumn<>("Date");
        dateColumn.setCellValueFactory(data -> new SimpleStringProperty(formatDate(data.getValue().getDateEnvoi())));
        dateColumn.setPrefWidth(160);

        conversationTable.getColumns().setAll(idColumn, directionColumn, messageColumn, dateColumn);
    }

    private void loadDestinataires() {
        destinataireCombo.getItems().clear();
        List<Utilisateur> users = utilisateurService.getAll();

        for (Utilisateur user : users) {
            if (user.getId() != currentUser.getId()) {
                destinataireCombo.getItems().add(user.getId() + " - " + labelUtilisateur(user));
            }
        }

        if (!destinataireCombo.getItems().isEmpty()) {
            destinataireCombo.getSelectionModel().selectFirst();
            loadConversation();
        }
    }

    @FXML
    private void loadConversation() {
        int destinataireId = parseLeadingId(destinataireCombo.getValue());
        if (destinataireId <= 0) {
            conversationTable.getItems().clear();
            return;
        }

        List<MessagePrive> messages = messagePriveService.getConversation(currentUser.getId(), destinataireId);
        conversationTable.setItems(FXCollections.observableArrayList(messages));

        for (MessagePrive message : messages) {
            if (message.getIdDestinataire() == currentUser.getId() && !message.isEstLu()) {
                messagePriveService.marquerCommeLu(message.getId());
            }
        }
    }

    @FXML
    private void sendMessage() {
        int destinataireId = parseLeadingId(destinataireCombo.getValue());
        String contenu = messageArea.getText() == null ? "" : messageArea.getText().trim();

        if (destinataireId <= 0 || contenu.isBlank()) {
            showAlert(Alert.AlertType.WARNING, "Message", "Choisissez un destinataire et saisissez un message.");
            return;
        }

        messagePriveService.add(new MessagePrive(currentUser.getId(), destinataireId, contenu));
        messageArea.clear();
        loadConversation();
    }

    @FXML
    private void goProfil() {
        Navigator.go(rootPane, isAdmin() ? "/AdminDashboard.fxml" : "/Profil.fxml", isAdmin() ? "Admin - SmarTounsi" : "Profil - SmarTounsi");
    }

    @FXML
    private void logout() {
        Navigator.logout(rootPane);
    }

    private int parseLeadingId(String value) {
        if (value == null || value.isBlank()) {
            return 0;
        }

        try {
            return Integer.parseInt(value.split("\\s+", 2)[0]);
        } catch (NumberFormatException e) {
            return 0;
        }
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

    private String safe(String value) {
        return value == null ? "" : value;
    }

    private String formatDate(LocalDateTime value) {
        return value == null ? "" : value.toString().replace('T', ' ');
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
