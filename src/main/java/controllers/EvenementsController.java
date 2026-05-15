package controllers;

import enums.EvenementType;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import models.AvisEvenement;
import models.Evenement;
import models.ParticipationEvenement;
import services.AvisEvenementService;
import services.EvenementService;
import services.ParticipationEvenementService;
import util.Navigator;
import util.Session;

import java.io.File;
import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class EvenementsController {

    @FXML private BorderPane rootPane;
    @FXML private TextField searchField;
    @FXML private ComboBox<String> typeFilter;
    @FXML private ComboBox<String> statutFilter;
    @FXML private ComboBox<String> organiseFilter;
    @FXML private FlowPane eventsGrid;
    @FXML private Label countLabel;

    private final EvenementService evenementService = new EvenementService();
    private final ParticipationEvenementService participationService = new ParticipationEvenementService();
    private final AvisEvenementService avisService = new AvisEvenementService();
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    @FXML
    private void initialize() {
        typeFilter.getItems().setAll("TOUS", "forum_stage", "fete", "hackathon", "conference", "autre");
        typeFilter.setValue("TOUS");

        statutFilter.getItems().setAll("TOUS", "a_venir", "complet", "annule", "termine");
        statutFilter.setValue("TOUS");

        organiseFilter.getItems().setAll("TOUS", "SITE", "EXTERNE");
        organiseFilter.setValue("TOUS");

        chargerEvenements();
    }

    @FXML
    private void chargerEvenements() {
        String keyword = searchField.getText();
        String type = typeFilter.getValue();
        String statut = statutFilter.getValue();
        String organise = organiseFilter.getValue();

        List<Evenement> evenements = evenementService.search(keyword, type, statut, organise);
        eventsGrid.getChildren().clear();

        for (Evenement event : evenements) {
            eventsGrid.getChildren().add(createEventCard(event));
        }

        countLabel.setText(evenements.size() + " événement(s)");
    }

    private VBox createEventCard(Evenement e) {
        VBox card = new VBox(12);
        card.setPrefWidth(360);
        card.setStyle("""
                -fx-background-color: white;
                -fx-background-radius: 18;
                -fx-border-color: #d7e4f2;
                -fx-border-radius: 18;
                -fx-effect: dropshadow(gaussian, rgba(2,48,71,0.12), 14, 0, 0, 4);
                """);

        StackPane imagePane = new StackPane();
        imagePane.setPrefHeight(170);
        imagePane.setStyle("-fx-background-color: #dce8f4; -fx-background-radius: 18 18 0 0;");

        ImageView imageView = new ImageView();
        imageView.setFitWidth(360);
        imageView.setFitHeight(170);
        imageView.setPreserveRatio(false);
        imageView.setSmooth(true);

        imageView.setImage(loadEventImage(e.getImageEvenement()));

        Label typeBadge = new Label(e.getTypeEvenement() == null ? "AUTRE" : e.getTypeEvenement().name());
        typeBadge.setStyle("-fx-background-color: #023047cc; -fx-text-fill: white; -fx-padding: 6 10; -fx-background-radius: 10; -fx-font-weight: bold;");
        StackPane.setAlignment(typeBadge, Pos.TOP_LEFT);
        StackPane.setMargin(typeBadge, new Insets(12));

        Label price = new Label(formatPrice(e.getTarif()));
        price.setStyle("-fx-background-color: #FEB707; -fx-text-fill: #061d33; -fx-padding: 6 10; -fx-background-radius: 10; -fx-font-weight: bold;");
        StackPane.setAlignment(price, Pos.TOP_RIGHT);
        StackPane.setMargin(price, new Insets(12));

        imagePane.getChildren().addAll(imageView, typeBadge, price);

        VBox info = new VBox(8);
        info.setPadding(new Insets(16));

        Label title = new Label(e.getTitre());
        title.setWrapText(true);
        title.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: #061d33;");

        Label details = new Label(
                "📍 " + safe(e.getLieu()) + "\n" +
                        "📅 " + (e.getDateDebut() == null ? "" : e.getDateDebut().format(formatter)) + "\n" +
                        "👥 " + participationService.compterParticipantsConfirmes(e.getId()) + "/" + e.getCapacity() + " confirmé(s)"
        );
        details.setStyle("-fx-font-size: 13px; -fx-text-fill: #475569;");
        details.setWrapText(true);

        Label statut = new Label("Statut : " + e.getStatut() + " · " + (e.isOrganiseParSite() ? "Organisé par site" : "Événement externe"));
        statut.setStyle("-fx-font-size: 12px; -fx-text-fill: #64748b;");

        HBox buttons = new HBox(8);
        buttons.setAlignment(Pos.CENTER_LEFT);

        Button participateBtn = new Button(e.isOrganiseParSite() ? "Participer" : "Intéressé");
        participateBtn.setStyle("-fx-background-color: #FEB707; -fx-text-fill: black; -fx-font-weight: bold; -fx-background-radius: 10;");
        participateBtn.setOnAction(event -> participer(e));

        Button participantsBtn = new Button("Participants");
        participantsBtn.setStyle("-fx-background-color: #168aad; -fx-text-fill: white; -fx-background-radius: 10;");
        participantsBtn.setOnAction(event -> ouvrirParticipations(e));

        Button editBtn = new Button("Modifier");
        editBtn.setStyle("-fx-background-color: #023047; -fx-text-fill: white; -fx-background-radius: 10;");
        editBtn.setOnAction(event -> ouvrirForm(e));

        Button deleteBtn = new Button("Supprimer");
        deleteBtn.setStyle("-fx-background-color: #ef4444; -fx-text-fill: white; -fx-background-radius: 10;");
        deleteBtn.setOnAction(event -> supprimer(e));

        buttons.getChildren().addAll(participateBtn, participantsBtn, editBtn, deleteBtn);
        info.getChildren().addAll(title, details, statut, buttons);

        card.getChildren().addAll(imagePane, info);
        card.setStyle(card.getStyle() + "-fx-cursor: hand;");
        card.setOnMouseClicked(event -> {
            if (!isInteractiveTarget(event.getTarget(), card)) {
                ouvrirDetailEvenement(e);
            }
        });
        return card;
    }

    private void ouvrirDetailEvenement(Evenement evenement) {
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle(safe(evenement.getTitre()));
        dialog.setHeaderText(null);
        dialog.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);

        VBox content = new VBox(18);
        content.setPadding(new Insets(18));
        content.setPrefWidth(900);
        content.setStyle("-fx-background-color: #f8fafc;");

        HBox hero = new HBox(18);
        hero.setAlignment(Pos.TOP_LEFT);

        StackPane imagePane = new StackPane();
        imagePane.setPrefSize(560, 315);
        imagePane.setMinSize(560, 315);
        imagePane.setStyle("-fx-background-color: #dce8f4; -fx-background-radius: 12;");

        Image image = loadEventImage(evenement.getImageEvenement());
        if (image == null || image.isError()) {
            Label placeholder = new Label("Image evenement");
            placeholder.setStyle("-fx-text-fill: #64748b; -fx-font-size: 18px; -fx-font-weight: bold;");
            imagePane.getChildren().add(placeholder);
        } else {
            ImageView detailImage = new ImageView(image);
            detailImage.setFitWidth(560);
            detailImage.setFitHeight(315);
            detailImage.setPreserveRatio(false);
            detailImage.setSmooth(true);
            imagePane.getChildren().add(detailImage);
        }

        VBox tarifBox = new VBox(12);
        tarifBox.setPrefWidth(270);
        tarifBox.setPadding(new Insets(18));
        tarifBox.setStyle("""
                -fx-background-color: white;
                -fx-background-radius: 12;
                -fx-border-color: #d7e4f2;
                -fx-border-radius: 12;
                """);

        Label tarifTitle = new Label("Tarif");
        tarifTitle.setStyle("-fx-font-size: 14px; -fx-text-fill: #64748b; -fx-font-weight: bold;");

        Label tarifValue = new Label(formatPrice(evenement.getTarif()));
        tarifValue.setStyle("-fx-font-size: 30px; -fx-text-fill: #023047; -fx-font-weight: bold;");

        Label meta = new Label(buildEventMeta(evenement));
        meta.setWrapText(true);
        meta.setStyle("-fx-font-size: 13px; -fx-text-fill: #475569;");

        Button payerBtn = new Button(isPaid(evenement) ? "Payer maintenant" : "Participer gratuitement");
        payerBtn.setMaxWidth(Double.MAX_VALUE);
        payerBtn.setStyle("-fx-background-color: #FEB707; -fx-text-fill: #023047; -fx-font-weight: bold; -fx-background-radius: 8; -fx-padding: 11 16;");
        payerBtn.setOnAction(event -> {
            if (isPaid(evenement)) {
                ouvrirPaiement(evenement);
            } else {
                participer(evenement);
            }
        });

        tarifBox.getChildren().addAll(tarifTitle, tarifValue, meta, payerBtn);
        hero.getChildren().addAll(imagePane, tarifBox);

        Label title = new Label(safe(evenement.getTitre()));
        title.setWrapText(true);
        title.setStyle("-fx-font-size: 28px; -fx-text-fill: #023047; -fx-font-weight: bold;");

        VBox descriptionBox = createSection("Description", safe(evenement.getDescription()).isBlank()
                ? "Aucune description pour cet evenement."
                : safe(evenement.getDescription()));

        VBox avisBox = createAvisSection(evenement);

        content.getChildren().addAll(title, hero, descriptionBox, avisBox);

        ScrollPane scrollPane = new ScrollPane(content);
        scrollPane.setFitToWidth(true);
        scrollPane.setPrefViewportHeight(720);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);

        dialog.getDialogPane().setContent(scrollPane);
        dialog.showAndWait();
    }

    private VBox createSection(String title, String body) {
        VBox box = new VBox(10);
        box.setPadding(new Insets(18));
        box.setStyle("""
                -fx-background-color: white;
                -fx-background-radius: 12;
                -fx-border-color: #d7e4f2;
                -fx-border-radius: 12;
                """);

        Label titleLabel = new Label(title);
        titleLabel.setStyle("-fx-font-size: 18px; -fx-text-fill: #023047; -fx-font-weight: bold;");

        Label bodyLabel = new Label(body);
        bodyLabel.setWrapText(true);
        bodyLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #334155; -fx-line-spacing: 4;");

        box.getChildren().addAll(titleLabel, bodyLabel);
        return box;
    }

    private VBox createAvisSection(Evenement evenement) {
        VBox box = new VBox(12);
        box.setPadding(new Insets(18));
        box.setStyle("""
                -fx-background-color: white;
                -fx-background-radius: 12;
                -fx-border-color: #d7e4f2;
                -fx-border-radius: 12;
                """);

        double moyenne = avisService.moyenneNote(evenement.getId());
        int totalAvis = avisService.countAvis(evenement.getId());
        Label summary = new Label(formatAvisSummary(moyenne, totalAvis));
        summary.setStyle("-fx-font-size: 14px; -fx-text-fill: #64748b;");

        Label title = new Label("Avis");
        title.setStyle("-fx-font-size: 18px; -fx-text-fill: #023047; -fx-font-weight: bold;");

        int initialNote = 0;
        String initialComment = "";
        if (Session.isLoggedIn()) {
            AvisEvenement existing = avisService.getByUtilisateurAndEvenement(Session.getUserId(), evenement.getId());
            if (existing != null) {
                initialNote = existing.getNote();
                initialComment = safe(existing.getCommentaire());
            }
        }

        HBox stars = new HBox(6);
        final int[] selectedNote = {initialNote};
        Button[] starButtons = new Button[5];
        for (int i = 0; i < 5; i++) {
            int note = i + 1;
            Button star = new Button(note <= selectedNote[0] ? "★" : "☆");
            star.setStyle(starStyle(note <= selectedNote[0]));
            star.setOnAction(event -> {
                selectedNote[0] = note;
                refreshStars(starButtons, selectedNote[0]);
            });
            starButtons[i] = star;
            stars.getChildren().add(star);
        }

        TextArea commentaireArea = new TextArea(initialComment);
        commentaireArea.setPromptText("Votre avis sur l'evenement");
        commentaireArea.setWrapText(true);
        commentaireArea.setPrefRowCount(3);

        Button saveAvis = new Button("Publier l'avis");
        saveAvis.setStyle("-fx-background-color: #168aad; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 8; -fx-padding: 10 16;");
        saveAvis.setOnAction(event -> enregistrerAvis(evenement, selectedNote[0], commentaireArea.getText(), summary));

        box.getChildren().addAll(title, summary, stars, commentaireArea, saveAvis);
        return box;
    }

    private void ouvrirPaiement(Evenement evenement) {
        if (!Session.isLoggedIn()) {
            afficherErreur("Veuillez vous connecter avant le paiement.");
            return;
        }

        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Paiement evenement");
        dialog.setHeaderText("Paiement de " + formatPrice(evenement.getTarif()));

        ButtonType payerType = new ButtonType("Payer", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(payerType, ButtonType.CANCEL);

        GridPane form = new GridPane();
        form.setHgap(12);
        form.setVgap(12);
        form.setPadding(new Insets(16));

        TextField nomField = new TextField();
        nomField.setPromptText("Nom sur la carte");

        TextField carteField = new TextField();
        carteField.setPromptText("Numero de carte");

        TextField expirationField = new TextField();
        expirationField.setPromptText("MM/AA");

        PasswordField cvvField = new PasswordField();
        cvvField.setPromptText("CVV");

        form.add(new Label("Nom"), 0, 0);
        form.add(nomField, 1, 0);
        form.add(new Label("Carte"), 0, 1);
        form.add(carteField, 1, 1);
        form.add(new Label("Expiration"), 0, 2);
        form.add(expirationField, 1, 2);
        form.add(new Label("CVV"), 0, 3);
        form.add(cvvField, 1, 3);

        dialog.getDialogPane().setContent(form);

        Node payerButton = dialog.getDialogPane().lookupButton(payerType);
        payerButton.addEventFilter(javafx.event.ActionEvent.ACTION, event -> {
            try {
                validerPaiement(evenement, nomField.getText(), carteField.getText(),
                        expirationField.getText(), cvvField.getText());
                chargerEvenements();
            } catch (RuntimeException ex) {
                event.consume();
                afficherErreur(ex.getMessage());
            }
        });

        dialog.showAndWait();
    }

    private void validerPaiement(Evenement evenement, String nom, String carte, String expiration, String cvv) {
        if (nom == null || nom.trim().isBlank()) {
            throw new RuntimeException("Le nom sur la carte est obligatoire.");
        }

        String cardDigits = carte == null ? "" : carte.replaceAll("\\D", "");
        if (cardDigits.length() < 12 || cardDigits.length() > 19) {
            throw new RuntimeException("Le numero de carte doit contenir entre 12 et 19 chiffres.");
        }

        String expirationValue = expiration == null ? "" : expiration.trim();
        if (!expirationValue.matches("(0[1-9]|1[0-2])/[0-9]{2,4}")) {
            throw new RuntimeException("La date d'expiration doit etre au format MM/AA.");
        }

        String cvvValue = cvv == null ? "" : cvv.trim();
        if (!cvvValue.matches("[0-9]{3,4}")) {
            throw new RuntimeException("Le CVV doit contenir 3 ou 4 chiffres.");
        }

        int userId = Session.getUserId();
        ParticipationEvenement participation = participationService.getByUtilisateurAndEvenement(userId, evenement.getId());

        if (participation == null) {
            participationService.participer(userId, evenement.getId());
            participation = participationService.getByUtilisateurAndEvenement(userId, evenement.getId());
        }

        if (participation == null) {
            throw new RuntimeException("Participation introuvable apres creation.");
        }

        String statut = safe(participation.getStatut()).toLowerCase();
        if ("confirmee".equals(statut) || "present".equals(statut)) {
            afficherInfo("Paiement", "Votre paiement est deja confirme.");
            return;
        }

        if ("waitlist".equals(statut)) {
            throw new RuntimeException("L'evenement est complet. Vous etes sur liste d'attente.");
        }

        String reference = "PAY-" + System.currentTimeMillis() + "-" + cardDigits.substring(cardDigits.length() - 4);
        participationService.confirmerPaiement(participation.getId(), "Carte bancaire", reference);
        afficherInfo("Paiement confirme", "Votre paiement a ete enregistre.");
    }

    private void enregistrerAvis(Evenement evenement, int note, String commentaire, Label summaryLabel) {
        if (!Session.isLoggedIn()) {
            afficherErreur("Veuillez vous connecter avant de publier un avis.");
            return;
        }

        if (note < 1 || note > 5) {
            afficherErreur("Veuillez choisir une note entre 1 et 5 etoiles.");
            return;
        }

        AvisEvenement avis = new AvisEvenement(note,
                commentaire == null ? "" : commentaire.trim(),
                evenement.getId(),
                Session.getUserId());
        avisService.saveOrUpdate(avis);

        summaryLabel.setText(formatAvisSummary(avisService.moyenneNote(evenement.getId()), avisService.countAvis(evenement.getId())));
        afficherInfo("Avis", "Votre avis a ete enregistre.");
    }

    private boolean isInteractiveTarget(Object target, Node card) {
        if (!(target instanceof Node node)) {
            return false;
        }

        while (node != null && node != card) {
            if (node instanceof ButtonBase || node instanceof TextInputControl || node instanceof ComboBoxBase<?>) {
                return true;
            }
            node = node.getParent();
        }

        return false;
    }

    private String buildEventMeta(Evenement evenement) {
        String date = evenement.getDateDebut() == null ? "Date non precisee" : evenement.getDateDebut().format(formatter);
        int confirmes = participationService.compterParticipantsConfirmes(evenement.getId());
        return safe(evenement.getLieu()) + "\n"
                + date + "\n"
                + confirmes + "/" + evenement.getCapacity() + " participant(s) confirme(s)";
    }

    private Image loadEventImage(String source) {
        if (source == null || source.isBlank()) {
            return null;
        }

        String value = source.trim();
        try {
            if (value.startsWith("http://") || value.startsWith("https://") || value.startsWith("file:")) {
                return new Image(value, true);
            }

            File file = new File(value);
            if (file.isFile()) {
                return new Image(file.toURI().toString(), true);
            }

            String resourcePath = value.startsWith("/") ? value : "/" + value;
            var resource = getClass().getResource(resourcePath);
            if (resource != null) {
                return new Image(resource.toExternalForm(), true);
            }
        } catch (RuntimeException ignored) {
        }

        return null;
    }

    private boolean isPaid(Evenement evenement) {
        return evenement.getTarif() != null && evenement.getTarif().compareTo(BigDecimal.ZERO) > 0;
    }

    private String formatAvisSummary(double moyenne, int totalAvis) {
        if (totalAvis <= 0) {
            return "Aucun avis pour le moment.";
        }
        return String.format("%.1f/5 sur %d avis", moyenne, totalAvis);
    }

    private String starStyle(boolean selected) {
        String color = selected ? "#FEB707" : "#94a3b8";
        return "-fx-background-color: transparent; -fx-text-fill: " + color + "; -fx-font-size: 25px; -fx-padding: 0 2; -fx-cursor: hand;";
    }

    private void refreshStars(Button[] starButtons, int selectedNote) {
        for (int i = 0; i < starButtons.length; i++) {
            boolean selected = i < selectedNote;
            starButtons[i].setText(selected ? "★" : "☆");
            starButtons[i].setStyle(starStyle(selected));
        }
    }

    private void participer(Evenement e) {
        try {
            int userId = Session.getUserId();

            if (e.isOrganiseParSite()) {
                participationService.participer(userId, e.getId());
                afficherInfo("Participation", "Votre demande de participation est enregistrée.");
            } else {
                participationService.interesser(userId, e.getId());
                afficherInfo("Intérêt", "Vous êtes marqué comme intéressé par cet événement externe.");
            }

            chargerEvenements();
        } catch (Exception ex) {
            afficherErreur(ex.getMessage());
        }
    }

    @FXML
    private void ouvrirAjout() {
        ouvrirForm(null);
    }

    private void ouvrirForm(Evenement evenement) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/EvenementForm.fxml"));
            Parent root = loader.load();

            EvenementFormController controller = loader.getController();
            controller.setEvenement(evenement);

            Stage stage = (Stage) rootPane.getScene().getWindow();
            stage.setScene(new Scene(root, 1400, 850));
            stage.setTitle(evenement == null ? "Créer événement" : "Modifier événement");
            stage.show();
        } catch (Exception ex) {
            ex.printStackTrace();
            afficherErreur("Impossible d'ouvrir le formulaire : " + ex.getMessage());
        }
    }

    private void ouvrirParticipations(Evenement evenement) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ParticipationEvenement.fxml"));
            Parent root = loader.load();

            ParticipationEvenementController controller = loader.getController();
            controller.setEvenement(evenement);

            Stage stage = (Stage) rootPane.getScene().getWindow();
            stage.setScene(new Scene(root, 1400, 850));
            stage.setTitle("Participants - " + evenement.getTitre());
            stage.show();
        } catch (Exception ex) {
            ex.printStackTrace();
            afficherErreur("Impossible d'ouvrir les participations : " + ex.getMessage());
        }
    }

    private void supprimer(Evenement e) {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Suppression");
        confirm.setHeaderText(null);
        confirm.setContentText("Supprimer l'événement : " + e.getTitre() + " ?");

        if (confirm.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
            evenementService.delete(e);
            chargerEvenements();
        }
    }

    private String formatPrice(BigDecimal price) {
        if (price == null || price.compareTo(BigDecimal.ZERO) == 0) {
            return "Gratuit";
        }
        return price + " TND";
    }

    private String safe(String value) {
        return value == null ? "" : value;
    }

    private void afficherInfo(String titre, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titre);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void afficherErreur(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Erreur");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML private void goDashboard() { Navigator.goDashboard(rootPane); }
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
