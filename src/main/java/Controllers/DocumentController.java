package tn.smartounsi.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import models.Document;
import models.DocumentType;
import models.Module;
import services.DocumentService;
import util.Session;

import java.util.List;
import java.util.stream.Collectors;

public class BibliothequeController {

    @FXML
    private FlowPane documentsContainer;

    @FXML
    private TextField searchField;

    @FXML
    private ComboBox<String> moduleComboBox;

    private final DocumentService documentService = new DocumentService();

    @FXML
    public void initialize() {
        moduleComboBox.getItems().add("Tous les modules");

        for (Module module : documentService.getAllModules()) {
            moduleComboBox.getItems().add(module.getName());
        }

        moduleComboBox.setValue("Tous les modules");

        showAll();
    }

    @FXML
    private void showAll() {
        List<Document> documents;

        if (Session.isAdmin()) {
            documents = documentService.getAllDocumentsForAdmin();
        } else {
            documents = documentService.getApprovedDocuments();
        }

        displayDocuments(documents);
    }

    @FXML
    private void showCours() {
        displayDocuments(documentService.getDocumentsByType(DocumentType.COURS));
    }

    @FXML
    private void showTD() {
        displayDocuments(documentService.getDocumentsByType(DocumentType.TD));
    }

    @FXML
    private void showDS() {
        displayDocuments(documentService.getDocumentsByType(DocumentType.DS));
    }

    @FXML
    private void showExamens() {
        displayDocuments(documentService.getDocumentsByType(DocumentType.EXAMEN));
    }

    @FXML
    private void showVideos() {
        displayDocuments(documentService.getDocumentsByType(DocumentType.VIDEO));
    }

    @FXML
    private void showImages() {
        displayDocuments(documentService.getDocumentsByType(DocumentType.IMAGE));
    }

    @FXML
    private void showLiens() {
        displayDocuments(documentService.getDocumentsByType(DocumentType.LIEN));
    }

    @FXML
    private void handleSearch() {
        String keyword = searchField.getText().toLowerCase().trim();
        String selectedModule = moduleComboBox.getValue();

        List<Document> documents;

        if (Session.isAdmin()) {
            documents = documentService.getAllDocumentsForAdmin();
        } else {
            documents = documentService.getApprovedDocuments();
        }

        List<Document> filteredDocuments = documents.stream()
                .filter(document ->
                        document.getTitle().toLowerCase().contains(keyword)
                                || document.getDescription().toLowerCase().contains(keyword)
                                || document.getType().name().toLowerCase().contains(keyword)
                )
                .filter(document ->
                        selectedModule.equals("Tous les modules")
                                || document.getModuleName().equalsIgnoreCase(selectedModule)
                )
                .collect(Collectors.toList());

        displayDocuments(filteredDocuments);
    }

    private void displayDocuments(List<Document> documents) {
        documentsContainer.getChildren().clear();

        if (documents.isEmpty()) {
            Label emptyLabel = new Label("Aucun document trouvé.");
            emptyLabel.getStyleClass().add("empty-label");
            documentsContainer.getChildren().add(emptyLabel);
            return;
        }

        for (Document document : documents) {
            VBox card = createDocumentCard(document);
            documentsContainer.getChildren().add(card);
        }
    }

    private VBox createDocumentCard(Document document) {
        VBox card = new VBox(10);
        card.getStyleClass().add("document-card");
        card.setPrefWidth(270);

        Label statusLabel = new Label(document.isApproved() ? "Approuvé" : "En attente");
        statusLabel.getStyleClass().add(document.isApproved() ? "approved-badge" : "pending-badge");

        Label typeLabel = new Label(document.getType().name());
        typeLabel.getStyleClass().add("type-badge");

        Label titleLabel = new Label(document.getTitle());
        titleLabel.getStyleClass().add("card-title");
        titleLabel.setWrapText(true);

        Label moduleLabel = new Label(document.getModuleName() + " • " + document.getUploadDate());
        moduleLabel.getStyleClass().add("card-subtitle");

        Label descriptionLabel = new Label(document.getDescription());
        descriptionLabel.getStyleClass().add("card-description");
        descriptionLabel.setWrapText(true);

        HBox actions = new HBox(8);

        Button consultButton = new Button("Consulter");
        consultButton.getStyleClass().add("primary-button");
        consultButton.setOnAction(event -> consultDocument(document));

        Button downloadButton = new Button("Télécharger");
        downloadButton.getStyleClass().add("secondary-button");
        downloadButton.setOnAction(event -> downloadDocument(document));

        actions.getChildren().addAll(consultButton, downloadButton);

        if (Session.isAdmin()) {
            if (!document.isApproved()) {
                Button approveButton = new Button("Approuver");
                approveButton.getStyleClass().add("success-button");
                approveButton.setOnAction(event -> {
                    documentService.approveDocument(document);
                    showAll();
                });

                actions.getChildren().add(approveButton);
            }

            Button deleteButton = new Button("Supprimer");
            deleteButton.getStyleClass().add("danger-button");
            deleteButton.setOnAction(event -> {
                documentService.deleteDocument(document);
                showAll();
            });

            actions.getChildren().add(deleteButton);
        }

        card.getChildren().addAll(
                statusLabel,
                typeLabel,
                titleLabel,
                moduleLabel,
                descriptionLabel,
                actions
        );

        return card;
    }

    private void consultDocument(Document document) {
        System.out.println("Consulter : " + document.getFilePath());
    }

    private void downloadDocument(Document document) {
        System.out.println("Télécharger : " + document.getFilePath());
    }
}