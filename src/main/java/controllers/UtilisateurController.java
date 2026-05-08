package controllers;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import models.Utilisateur;
import services.UtilisateurService;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class UtilisateurController {
    @FXML private TextField searchField;
    @FXML private TextField nomField;
    @FXML private TextField prenomField;
    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;
    @FXML private ComboBox<String> roleCombo;
    @FXML private TextField photoField;
    @FXML private CheckBox actifCheck;
    @FXML private Label statusLabel;

    @FXML private TableView<Utilisateur> userTable;
    @FXML private TableColumn<Utilisateur, Number> idCol;
    @FXML private TableColumn<Utilisateur, String> nomCol;
    @FXML private TableColumn<Utilisateur, String> prenomCol;
    @FXML private TableColumn<Utilisateur, String> emailCol;
    @FXML private TableColumn<Utilisateur, String> roleCol;
    @FXML private TableColumn<Utilisateur, Boolean> actifCol;

    private final UtilisateurService utilisateurService = new UtilisateurService();
    private final ObservableList<Utilisateur> utilisateurs = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        roleCombo.setItems(FXCollections.observableArrayList("ADMIN", "TEACHER", "STUDENT"));
        roleCombo.getSelectionModel().select("STUDENT");

        idCol.setCellValueFactory(data -> new SimpleIntegerProperty(data.getValue().getId()));
        nomCol.setCellValueFactory(data -> new SimpleStringProperty(nullToEmpty(data.getValue().getNom())));
        prenomCol.setCellValueFactory(data -> new SimpleStringProperty(nullToEmpty(data.getValue().getPrenom())));
        emailCol.setCellValueFactory(data -> new SimpleStringProperty(nullToEmpty(data.getValue().getEmail())));
        roleCol.setCellValueFactory(data -> new SimpleStringProperty(nullToEmpty(data.getValue().getRole())));
        actifCol.setCellValueFactory(data -> new SimpleBooleanProperty(data.getValue().isActif()));
        actifCol.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(Boolean actif, boolean empty) {
                super.updateItem(actif, empty);
                setText(empty ? null : Boolean.TRUE.equals(actif) ? "Oui" : "Non");
            }
        });

        userTable.setItems(utilisateurs);
        userTable.getSelectionModel().selectedItemProperty().addListener((obs, oldUser, newUser) -> remplirFormulaire(newUser));
        chargerUtilisateurs();
    }

    @FXML
    public void chargerUtilisateurs() {
        utilisateurs.setAll(utilisateurService.getAll());
        statusLabel.setText(utilisateurs.size() + " utilisateur(s) chargé(s).");
    }

    @FXML
    public void rechercherUtilisateur() {
        String q = searchField.getText() == null ? "" : searchField.getText().trim().toLowerCase(Locale.ROOT);
        if (q.isEmpty()) {
            chargerUtilisateurs();
            return;
        }
        List<Utilisateur> resultat = utilisateurService.getAll().stream()
                .filter(u -> contains(u.getNom(), q) || contains(u.getPrenom(), q) || contains(u.getEmail(), q) || contains(u.getRole(), q))
                .toList();
        utilisateurs.setAll(resultat);
        statusLabel.setText(resultat.size() + " résultat(s) trouvé(s).");
    }

    @FXML
    public void ajouterUtilisateur() {
        if (!formulaireValide(true)) return;

        Utilisateur u = new Utilisateur(
                nomField.getText().trim(),
                prenomField.getText().trim(),
                emailField.getText().trim(),
                passwordField.getText().trim(),
                roleCombo.getValue()
        );
        u.setPhotoProfil(photoField.getText() == null ? null : photoField.getText().trim());
        u.setActif(actifCheck.isSelected());

        utilisateurService.add(u);
        chargerUtilisateurs();
        viderFormulaire();
        statusLabel.setText("Utilisateur ajouté avec succès.");
    }

    @FXML
    public void modifierUtilisateur() {
        Utilisateur selected = userTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            alert(Alert.AlertType.WARNING, "Sélection obligatoire", "Sélectionnez un utilisateur à modifier.");
            return;
        }
        if (!formulaireValide(false)) return;

        selected.setNom(nomField.getText().trim());
        selected.setPrenom(prenomField.getText().trim());
        selected.setEmail(emailField.getText().trim());
        selected.setRole(roleCombo.getValue());
        selected.setPhotoProfil(photoField.getText() == null ? null : photoField.getText().trim());
        selected.setActif(actifCheck.isSelected());

        utilisateurService.update(selected);
        chargerUtilisateurs();
        statusLabel.setText("Utilisateur modifié.");
    }

    @FXML
    public void supprimerUtilisateur() {
        Utilisateur selected = userTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            alert(Alert.AlertType.WARNING, "Sélection obligatoire", "Sélectionnez un utilisateur à supprimer.");
            return;
        }
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirmation");
        confirm.setHeaderText("Supprimer l'utilisateur ?");
        confirm.setContentText(selected.getPrenom() + " " + selected.getNom() + " sera supprimé de la base.");
        confirm.showAndWait().ifPresent(btn -> {
            if (btn == ButtonType.OK) {
                utilisateurService.delete(selected);
                chargerUtilisateurs();
                viderFormulaire();
                statusLabel.setText("Utilisateur supprimé.");
            }
        });
    }

    @FXML
    public void viderFormulaire() {
        userTable.getSelectionModel().clearSelection();
        nomField.clear();
        prenomField.clear();
        emailField.clear();
        passwordField.clear();
        photoField.clear();
        roleCombo.getSelectionModel().select("STUDENT");
        actifCheck.setSelected(true);
    }

    @FXML
    public void ouvrirMessages(ActionEvent event) throws IOException {
        changerScene(event, "/fxml/messages_prives.fxml", "SmarTounsi - Messages privés");
    }

    private void remplirFormulaire(Utilisateur u) {
        if (u == null) return;
        nomField.setText(nullToEmpty(u.getNom()));
        prenomField.setText(nullToEmpty(u.getPrenom()));
        emailField.setText(nullToEmpty(u.getEmail()));
        passwordField.clear();
        roleCombo.setValue(u.getRole());
        photoField.setText(nullToEmpty(u.getPhotoProfil()));
        actifCheck.setSelected(u.isActif());
    }

    private boolean formulaireValide(boolean checkPassword) {
        if (nomField.getText().isBlank() || prenomField.getText().isBlank() || emailField.getText().isBlank() || roleCombo.getValue() == null) {
            alert(Alert.AlertType.ERROR, "Formulaire incomplet", "Nom, prénom, email et rôle sont obligatoires.");
            return false;
        }
        if (checkPassword && passwordField.getText().isBlank()) {
            alert(Alert.AlertType.ERROR, "Mot de passe obligatoire", "Le mot de passe est obligatoire pour ajouter un utilisateur.");
            return false;
        }
        if (!emailField.getText().contains("@")) {
            alert(Alert.AlertType.ERROR, "Email invalide", "Saisissez un email valide.");
            return false;
        }
        return true;
    }

    private void changerScene(ActionEvent event, String fxml, String title) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource(fxml));
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.setTitle(title);
    }

    private static boolean contains(String value, String q) {
        return value != null && value.toLowerCase(Locale.ROOT).contains(q);
    }

    private static String nullToEmpty(String value) {
        return value == null ? "" : value;
    }

    private void alert(Alert.AlertType type, String title, String message) {
        Alert a = new Alert(type);
        a.setTitle(title);
        a.setHeaderText(null);
        a.setContentText(message);
        a.showAndWait();
    }
}
