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
import javafx.stage.FileChooser;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

public class AddEvenementController {

    @FXML
    private TextField titreField;
    @FXML
    private TextArea descField;
    @FXML
    private TextField typeField;
    @FXML
    private TextField lieuField;
    @FXML
    private DatePicker dateDebutPicker;
    @FXML
    private TextField heureDebutField;
    @FXML
    private DatePicker dateFinPicker;
    @FXML
    private TextField heureFinField;
    @FXML
    private TextField prixField;
    @FXML
    private TextField placesField;
    @FXML
    private Label messageLabel;
    @FXML
    private Label imagePathLabel;

    private String selectedImagePath = null;

    private EvenementDAO evenementDAO = new EvenementDAO();
    private DashboardController dashboardController;

    public void setDashboardController(DashboardController dashboardController) {
        this.dashboardController = dashboardController;
    }

    @FXML
    void handleChooseImage(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choisir une image de l'événement");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Images", "*.png", "*.jpg", "*.jpeg", "*.gif"));
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        File selectedFile = fileChooser.showOpenDialog(stage);

        if (selectedFile != null) {
            try {
                // Créer un dossier uploads à la racine du projet pour ne pas dépendre du
                // classpath
                String projectPath = System.getProperty("user.dir");
                Path uploadsPath = Paths.get(projectPath, "uploads");
                if (!Files.exists(uploadsPath)) {
                    Files.createDirectories(uploadsPath);
                }

                // Copy file with a unique name
                String originalName = selectedFile.getName();
                String extension = "";
                int i = originalName.lastIndexOf('.');
                if (i > 0) {
                    extension = originalName.substring(i);
                }
                String uniqueName = UUID.randomUUID().toString() + extension;
                Path destination = uploadsPath.resolve(uniqueName);

                Files.copy(selectedFile.toPath(), destination, StandardCopyOption.REPLACE_EXISTING);

                // Sauvegarder l'URI sous forme de chaîne (ex: file:/C:/...)
                selectedImagePath = destination.toUri().toString();
                imagePathLabel.setText(originalName);

            } catch (IOException e) {
                e.printStackTrace();
                messageLabel.setText("Erreur lors de la copie de l'image.");
            }
        }
    }

    @FXML
    void handleSave(ActionEvent event) {
        try {
            String titre = titreField.getText();
            String desc = descField.getText();
            String type = typeField.getText();
            String lieu = lieuField.getText();
            
            LocalDate dateDeb = dateDebutPicker.getValue();
            String hDebStr = heureDebutField.getText();
            LocalDate dateFin = dateFinPicker.getValue();
            String hFinStr = heureFinField.getText();
            
            String prixStr = prixField.getText();
            String placesStr = placesField.getText();

            if (titre.isEmpty() || type.isEmpty() || lieu.isEmpty() || 
                dateDeb == null || hDebStr.isEmpty() || 
                dateFin == null || hFinStr.isEmpty() ||
                prixStr == null || prixStr.trim().isEmpty() || 
                placesStr == null || placesStr.trim().isEmpty()) {
                messageLabel.setText("Veuillez remplir tous les champs obligatoires.");
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
                messageLabel.setText("La date de fin doit être après la date de début.");
                return;
            }

            double prix = Double.parseDouble(prixStr.replace(',', '.').trim());
            int places = Integer.parseInt(placesStr.trim());
            
            if (places < 0 || prix < 0) {
                messageLabel.setText("Prix et places doivent être positifs.");
                return;
            }

            Evenement e = new Evenement(titre, desc, type, start, end, lieu, "A_VENIR", selectedImagePath);
            e.setPrix(prix);
            e.setCapacity(places);
            
            System.out.println("[FRONTEND] Envoi des données au service de création...");
            boolean success = evenementDAO.ajouterEvenement(e);

            if (success) {
                System.out.println("[FRONTEND] Création réussie !");
                // Alerte de succès (Style Moderne)
                javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.INFORMATION);
                alert.setTitle("Succès");
                alert.setHeaderText("Événement Créé");
                alert.setContentText("L'événement '" + titre + "' a été enregistré avec succès.");
                alert.showAndWait();

                goBack(event);
            } else {
                System.out.println("[FRONTEND] Échec de la création en base.");
                messageLabel.setText("Erreur : Impossible de sauvegarder l'événement dans la base de données.");
            }

        } catch (java.time.format.DateTimeParseException ex) {
            messageLabel.setText("Heure invalide. Format HH:MM (ex: 14:30).");
            System.err.println("Erreur format date/heure : " + ex.getMessage());
        } catch (NumberFormatException ex) {
            messageLabel.setText("Prix ou nombre de places invalide.");
            System.err.println("Erreur format numérique : " + ex.getMessage());
        } catch (Exception ex) {
            messageLabel.setText("Erreur lors de la sauvegarde.");
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
