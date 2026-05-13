package com.esprit.eventapp.controllers;

import com.esprit.eventapp.models.AvisEvenement;
import com.esprit.eventapp.models.Evenement;
import com.esprit.eventapp.services.AvisEvenementDAO;
import com.esprit.eventapp.services.EvenementDAO;
import com.esprit.eventapp.utils.SessionManager;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.geometry.Pos;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import com.esprit.eventapp.services.ParticipationDAO;
import com.esprit.eventapp.models.User;
import java.io.File;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Timestamp;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.List;
import java.util.Optional;

public class EventDetailsController {

    @FXML private ImageView heroImage;
    @FXML private Label lblTitreHero;
    @FXML private Label lblLieuHero;
    @FXML private Label lblPrixHero;

    @FXML private Label lblDateDebutBillet;
    @FXML private Label lblDateFinBillet;
    @FXML private Label lblPrixBillet;
    @FXML private Button btnReserver;
    @FXML private Label lblSoldOut;
    @FXML private Label lblPlacesStatus;
    @FXML private VBox userStatusBox;
    @FXML private Label lblUserStatusInfo;

    private final ParticipationDAO participationDAO = new ParticipationDAO();

    @FXML private Label lblTypeDesc;
    @FXML private Label lblDateDebutDesc;
    @FXML private Label lblDateFinDesc;
    @FXML private ImageView contentImage;
    @FXML private Label lblDescription;

    @FXML private VBox avisContainer;
    @FXML private TextField txtNom;
    @FXML private ComboBox<Integer> comboNote;
    @FXML private TextArea txtCommentaire;

    /** VBox contenant les boutons d'administration */
    @FXML private VBox actionsBox;

    private final AvisEvenementDAO avisDAO = new AvisEvenementDAO();
    private final EvenementDAO evenementDAO = new EvenementDAO();
    private Evenement event;
    private DashboardController dashboardController;

    private static final java.time.format.DateTimeFormatter DT_FORMAT = java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    @FXML
    public void initialize() {
        comboNote.setItems(FXCollections.observableArrayList(1, 2, 3, 4, 5));
    }

    public void setDashboardController(DashboardController dashboardController) {
        this.dashboardController = dashboardController;
    }

    public void setEvent(Evenement event) {
        this.event = event;
        renderEvent();
        loadAvis();
        checkCreator();
    }

    /** Affiche les boutons Modifier/Supprimer si l'utilisateur est créateur ou ADMIN, et vérifie le statut d'inscription */
    private void checkCreator() {
        User currentUser = SessionManager.getInstance().getCurrentUser();
        if (currentUser == null || event == null) return;

        String role = currentUser.getRole();
        int userId = currentUser.getId();

        boolean isAdmin = "ADMIN".equalsIgnoreCase(role);
        boolean isCreator = event.getCreateurId() != 0 && event.getCreateurId() == userId;

        if (isAdmin || isCreator) {
            if (actionsBox != null) {
                actionsBox.setVisible(true);
                actionsBox.setManaged(true);
            }
        }

        // Vérification du statut d'inscription
        String status = participationDAO.getUserParticipationStatus(event.getIdEvenement(), userId);
        if (status != null) {
            userStatusBox.setVisible(true);
            userStatusBox.setManaged(true);
            btnReserver.setDisable(true);
            btnReserver.setText("DÉJÀ INSCRIT");
            
            if (status.equalsIgnoreCase("waitlist")) {
                int pos = participationDAO.getWaitlistPosition(event.getIdEvenement(), userId);
                lblUserStatusInfo.setText("LISTE D'ATTENTE (Position: " + pos + ")");
                lblUserStatusInfo.setStyle("-fx-text-fill: #f39c12;");
            } else {
                lblUserStatusInfo.setText(status.toUpperCase());
                lblUserStatusInfo.setStyle("-fx-text-fill: #27ae60;");
            }
        }
    }

    private void renderEvent() {
        if (event == null) return;

        lblTitreHero.setText(nullSafe(event.getTitre()));
        lblLieuHero.setText("📍 " + nullSafe(event.getLieu()));
        lblPrixHero.setText(formatPrix(event.getPrix()));

        String startStr = event.getDateDebut().format(DT_FORMAT);
        String endStr = event.getDateFin().format(DT_FORMAT);

        lblDateDebutBillet.setText(startStr);
        lblDateFinBillet.setText(endStr);
        lblPrixBillet.setText(formatPrix(event.getPrix()));

        // Calcul des places restantes
        java.util.Map<String, Integer> stats = participationDAO.getStats();
        int confirmedCount = stats.getOrDefault("confirmée", 0) + stats.getOrDefault("présent", 0);
        int remaining = event.getCapacity() - confirmedCount;

        if (remaining <= 0) {
            lblPlacesStatus.setText("Événement COMPLET - Liste d'attente active");
            lblPlacesStatus.setStyle("-fx-text-fill: #e67e22;");
        } else {
            lblPlacesStatus.setText(remaining + " places disponibles sur " + event.getCapacity());
            lblPlacesStatus.setStyle("-fx-text-fill: #27ae60;");
        }

        lblTypeDesc.setText(nullSafe(event.getTypeEvenement()));
        lblDateDebutDesc.setText(startStr);
        lblDateFinDesc.setText(endStr);
        lblDescription.setText(nullSafe(event.getDescription()));

        loadEventImages();
    }

    private void loadEventImages() {
        heroImage.setImage(null);
        contentImage.setImage(null);
        if (event.getImage() == null || event.getImage().trim().isEmpty()) {
            return;
        }
        String spec = event.getImage().trim();
        if (!spec.startsWith("http://") && !spec.startsWith("https://")
                && !spec.startsWith("file:") && !spec.startsWith("jar:")) {
            Path p = Paths.get(spec);
            if (Files.isRegularFile(p)) {
                spec = p.toUri().toString();
            }
        }
        try {
            Image img = new Image(spec);
            if (!img.isError()) {
                heroImage.setImage(img);
                contentImage.setImage(img);
                heroImage.setSmooth(true);
                contentImage.setSmooth(true);
            }
        } catch (Exception e) {
            System.err.println("Erreur chargement image: " + e.getMessage());
        }
    }

    private static String formatPrix(double prix) {
        if (Double.isNaN(prix) || Double.isInfinite(prix)) {
            return "0";
        }
        if (Math.abs(prix - Math.rint(prix)) < 1e-9) {
            return String.format(Locale.FRENCH, "%.0f", prix);
        }
        return String.format(Locale.FRENCH, "%.2f", prix);
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
        VBox card = new VBox(5);
        card.getStyleClass().add("comment-card");

        Label header = new Label(nullSafe(avis.getNomAuteur()));
        header.getStyleClass().add("comment-author");

        Label date = new Label(formatTimestamp(avis.getDateAvis()));
        date.getStyleClass().add("comment-date");

        Label body = new Label(nullSafe(avis.getCommentaire()));
        body.setWrapText(true);
        body.getStyleClass().add("comment-text");
        
        // Note Badge
        Label noteBadge = new Label("⭐ " + avis.getNote() + "/5");
        noteBadge.setStyle("-fx-background-color: #FFB800; -fx-text-fill: white; -fx-padding: 2 8; -fx-background-radius: 10; -fx-font-size: 10px; -fx-font-weight: bold;");

        HBox topRow = new HBox(10, header, noteBadge);
        topRow.setAlignment(Pos.CENTER_LEFT);

        card.getChildren().addAll(topRow, body, date);
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
        nouvelAvis.setDateAvis(new Timestamp(System.currentTimeMillis()));
        avisDAO.ajouterAvis(nouvelAvis);

        txtNom.clear();
        txtCommentaire.clear();
        comboNote.setValue(null);
        loadAvis();
    }

    @FXML
    public void handleReserver() {
        if (event == null || event.getCapacity() <= 0) {
            return;
        }
        
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/esprit/eventapp/views/BuyTicket.fxml"));
            Parent root = loader.load();
            
            BuyTicketController controller = loader.getController();
            controller.setEvent(event, this);
            
            javafx.stage.Stage stage = new javafx.stage.Stage();
            stage.initModality(javafx.stage.Modality.APPLICATION_MODAL);
            stage.setTitle("Achat de Billet - " + event.getTitre());
            stage.setScene(new javafx.scene.Scene(root));
            stage.showAndWait();
            
        } catch (IOException e) {
            System.err.println("Erreur chargement BuyTicket.fxml: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    public void goBack(ActionEvent event) {
        if (dashboardController != null) {
            dashboardController.showEventsView();
        }
    }

    @FXML
    public void handleEdit() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/esprit/eventapp/views/EditEvenement.fxml"));
            Parent root = loader.load();

            EditEvenementController controller = loader.getController();
            controller.setEvenement(event);
            controller.setDashboardController(dashboardController);

            if (dashboardController != null) {
                dashboardController.loadNode(root);
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    @FXML
    public void handleDelete() {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirmer la suppression");
        confirm.setHeaderText("Supprimer « " + event.getTitre() + " » ?");
        confirm.setContentText("Cette action est irréversible. Tous les avis associés seront également supprimés.");

        Optional<ButtonType> result = confirm.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            evenementDAO.supprimerEvenement(event.getIdEvenement());
            if (dashboardController != null) {
                dashboardController.showEventsView();
            }
        }
    }

    private static String nullSafe(String s) {
        return s == null ? "" : s;
    }

    private static String formatTimestamp(Timestamp ts) {
        if (ts == null) return "";
        return ts.toLocalDateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
    }
}
