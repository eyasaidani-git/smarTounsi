package controllers;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import models.Evenement;
import models.ParticipationEvenement;
import services.ParticipationEvenementService;
import util.Navigator;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class ParticipationEvenementController {

    @FXML private BorderPane rootPane;
    @FXML private Label titleLabel;
    @FXML private TableView<ParticipationEvenement> participationTable;

    @FXML private TableColumn<ParticipationEvenement, Integer> idColumn;
    @FXML private TableColumn<ParticipationEvenement, String> utilisateurColumn;
    @FXML private TableColumn<ParticipationEvenement, String> evenementColumn;
    @FXML private TableColumn<ParticipationEvenement, String> typeColumn;
    @FXML private TableColumn<ParticipationEvenement, String> statutColumn;
    @FXML private TableColumn<ParticipationEvenement, BigDecimal> montantColumn;
    @FXML private TableColumn<ParticipationEvenement, LocalDateTime> dateColumn;

    private final ParticipationEvenementService service = new ParticipationEvenementService();
    private Evenement evenement;

    @FXML
    private void initialize() {
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        utilisateurColumn.setCellValueFactory(new PropertyValueFactory<>("nomUtilisateur"));
        evenementColumn.setCellValueFactory(new PropertyValueFactory<>("titreEvenement"));
        typeColumn.setCellValueFactory(new PropertyValueFactory<>("typeParticipation"));
        statutColumn.setCellValueFactory(new PropertyValueFactory<>("statut"));
        montantColumn.setCellValueFactory(new PropertyValueFactory<>("montantPaye"));
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("dateParticipation"));

        charger();
    }

    public void setEvenement(Evenement evenement) {
        this.evenement = evenement;
        titleLabel.setText("Participants : " + evenement.getTitre());
        charger();
    }

    @FXML
    private void charger() {
        List<ParticipationEvenement> data;

        if (evenement == null) {
            data = service.getAll();
        } else {
            data = service.getByEvenement(evenement.getId());
        }

        participationTable.setItems(FXCollections.observableArrayList(data));
    }

    @FXML
    private void confirmerPaiement() {
        ParticipationEvenement p = selected();
        if (p == null) return;

        TextInputDialog dialog = new TextInputDialog("paiement-manuel");
        dialog.setTitle("Paiement");
        dialog.setHeaderText(null);
        dialog.setContentText("Référence paiement :");

        dialog.showAndWait().ifPresent(ref -> {
            service.confirmerPaiement(p.getId(), "manuel", ref);
            charger();
        });
    }

    @FXML
    private void marquerPresent() {
        ParticipationEvenement p = selected();
        if (p == null) return;

        service.marquerPresent(p.getId());
        charger();
    }

    @FXML
    private void marquerAbsent() {
        ParticipationEvenement p = selected();
        if (p == null) return;

        service.marquerAbsent(p.getId());
        charger();
    }

    @FXML
    private void annuler() {
        ParticipationEvenement p = selected();
        if (p == null) return;

        service.annuler(p.getId());
        charger();
    }

    @FXML
    private void supprimer() {
        ParticipationEvenement p = selected();
        if (p == null) return;

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Suppression");
        confirm.setHeaderText(null);
        confirm.setContentText("Supprimer cette participation ?");

        if (confirm.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
            service.delete(p);
            charger();
        }
    }

    private ParticipationEvenement selected() {
        ParticipationEvenement p = participationTable.getSelectionModel().getSelectedItem();

        if (p == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Sélection");
            alert.setHeaderText(null);
            alert.setContentText("Sélectionnez une ligne.");
            alert.showAndWait();
        }

        return p;
    }

    @FXML private void retour() { Navigator.go(rootPane, "/Evenements.fxml", "Événements - SmarTounsi"); }
    @FXML private void goDashboard() { Navigator.go(rootPane, "/Profil.fxml", "Dashboard - SmarTounsi"); }
    @FXML private void goModules() { Navigator.go(rootPane, "/Modules.fxml", "Modules - SmarTounsi"); }
    @FXML private void goBibliotheque() { Navigator.go(rootPane, "/Document.fxml", "Bibliothèque - SmarTounsi"); }
    @FXML private void goUpload() { Navigator.go(rootPane, "/UploadDocument.fxml", "Upload - SmarTounsi"); }
    @FXML private void goCalendrier() { Navigator.go(rootPane, "/Planning.fxml", "Calendrier - SmarTounsi"); }
    @FXML private void goFavoris() { Navigator.go(rootPane, "/Favoris.fxml", "Favoris - SmarTounsi"); }
    @FXML private void goQuiz() { Navigator.go(rootPane, "/quiz.fxml", "Quiz - SmarTounsi"); }
    @FXML private void goProjets() { Navigator.go(rootPane, "/ProjectView.fxml", "Projets - SmarTounsi"); }
    @FXML private void goEvenements() { Navigator.go(rootPane, "/Evenements.fxml", "Événements - SmarTounsi"); }
    @FXML private void goProfil() { Navigator.go(rootPane, "/Profil.fxml", "Profil - SmarTounsi"); }
    @FXML private void goNotifications() { Navigator.go(rootPane, "/Notification.fxml", "Notifications - SmarTounsi"); }
    @FXML private void deconnexion() { Navigator.logout(rootPane); }
}
