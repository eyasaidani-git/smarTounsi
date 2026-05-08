package tn.esprit;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import models.Projet;
import services.ProjetService;

public class ProjetController {

    @FXML
    private TextField nomField;

    @FXML
    private TextArea descriptionArea;

    @FXML
    private TextField collaborateursField;

    @FXML
    private CheckBox codeCheckBox;

    @FXML
    private CheckBox presentationCheckBox;

    @FXML
    private CheckBox rapportCheckBox;

    private final ProjetService projetService = new ProjetService();

    @FXML
    public void ajouterProjet() {
        String nom = nomField.getText();
        String description = descriptionArea.getText();

        if (nom == null || nom.trim().isEmpty()) {
            showAlert("Erreur", "Le nom du projet est obligatoire.");
            return;
        }

        Projet projet = new Projet(
                nom,
                description,
                codeCheckBox.isSelected(),
                presentationCheckBox.isSelected(),
                rapportCheckBox.isSelected(),
                1
        );

        projetService.add(projet);

        showAlert("Succès", "Projet ajouté avec succès.");

        nomField.clear();
        descriptionArea.clear();
        collaborateursField.clear();
        codeCheckBox.setSelected(true);
        presentationCheckBox.setSelected(true);
        rapportCheckBox.setSelected(true);
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}