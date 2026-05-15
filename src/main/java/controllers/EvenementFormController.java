package controllers;

import enums.EvenementType;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.stage.FileChooser;
import models.Evenement;
import services.EvenementService;
import util.Navigator;
import util.Session;

import java.io.File;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.file.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.UUID;

public class EvenementFormController {

    @FXML private BorderPane rootPane;
    @FXML private Label pageTitle;
    @FXML private TextField titreField;
    @FXML private TextArea descriptionArea;
    @FXML private ComboBox<EvenementType> typeCombo;
    @FXML private CheckBox organiseParSiteCheck;
    @FXML private TextField lieuField;
    @FXML private DatePicker dateDebutPicker;
    @FXML private TextField heureDebutField;
    @FXML private DatePicker dateFinPicker;
    @FXML private TextField heureFinField;
    @FXML private TextField tarifField;
    @FXML private TextField capacityField;
    @FXML private ComboBox<String> statutCombo;
    @FXML private TextField imageField;
    @FXML private Label messageLabel;

    private final EvenementService evenementService = new EvenementService();
    private Evenement evenement;

    @FXML
    private void initialize() {
        typeCombo.getItems().setAll(EvenementType.values());
        typeCombo.setValue(EvenementType.AUTRE);

        statutCombo.getItems().setAll("a_venir", "complet", "annule", "termine");
        statutCombo.setValue("a_venir");

        organiseParSiteCheck.setSelected(true);
        heureDebutField.setText("09:00");
        heureFinField.setText("12:00");
        tarifField.setText("0");
        capacityField.setText("0");
    }

    public void setEvenement(Evenement evenement) {
        this.evenement = evenement;

        if (evenement == null) {
            pageTitle.setText("Créer un événement");
            return;
        }

        pageTitle.setText("Modifier un événement");
        titreField.setText(evenement.getTitre());
        descriptionArea.setText(evenement.getDescription());
        typeCombo.setValue(evenement.getTypeEvenement() == null ? EvenementType.AUTRE : evenement.getTypeEvenement());
        organiseParSiteCheck.setSelected(evenement.isOrganiseParSite());
        lieuField.setText(evenement.getLieu());

        if (evenement.getDateDebut() != null) {
            dateDebutPicker.setValue(evenement.getDateDebut().toLocalDate());
            heureDebutField.setText(evenement.getDateDebut().toLocalTime().toString());
        }

        if (evenement.getDateFin() != null) {
            dateFinPicker.setValue(evenement.getDateFin().toLocalDate());
            heureFinField.setText(evenement.getDateFin().toLocalTime().toString());
        }

        tarifField.setText(evenement.getTarif() == null ? "0" : evenement.getTarif().toPlainString());
        capacityField.setText(String.valueOf(evenement.getCapacity()));
        statutCombo.setValue(evenement.getStatut() == null ? "a_venir" : evenement.getStatut());
        imageField.setText(evenement.getImageEvenement());
    }

    @FXML
    private void choisirImage() {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Choisir une image");
        chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Images", "*.png", "*.jpg", "*.jpeg", "*.gif"));

        File file = chooser.showOpenDialog(rootPane.getScene().getWindow());
        if (file == null) {
            return;
        }

        try {
            Path uploadDir = Paths.get(System.getProperty("user.dir"), "uploads");
            Files.createDirectories(uploadDir);

            String originalName = file.getName();
            String ext = "";
            int dot = originalName.lastIndexOf('.');
            if (dot >= 0) {
                ext = originalName.substring(dot);
            }

            Path destination = uploadDir.resolve(UUID.randomUUID() + ext);
            Files.copy(file.toPath(), destination, StandardCopyOption.REPLACE_EXISTING);

            imageField.setText(destination.toUri().toString());
        } catch (Exception e) {
            afficherErreur("Erreur image : " + e.getMessage());
        }
    }

    @FXML
    private void enregistrer() {
        try {
            String titre = text(titreField);
            String lieu = text(lieuField);

            if (titre.isBlank() || lieu.isBlank()) {
                afficherErreur("Le titre et le lieu sont obligatoires.");
                return;
            }

            LocalDate d1 = dateDebutPicker.getValue();
            LocalDate d2 = dateFinPicker.getValue();

            if (d1 == null || d2 == null) {
                afficherErreur("Les dates sont obligatoires.");
                return;
            }

            LocalTime h1 = parseTime(text(heureDebutField), "heure de debut");
            LocalTime h2 = parseTime(text(heureFinField), "heure de fin");

            LocalDateTime debut = LocalDateTime.of(d1, h1);
            LocalDateTime fin = LocalDateTime.of(d2, h2);

            if (fin.isBefore(debut)) {
                afficherErreur("La date de fin doit être après la date de début.");
                return;
            }

            BigDecimal tarif = parseTarif(text(tarifField));
            int capacity = parseCapacity(text(capacityField));

            if (tarif.compareTo(BigDecimal.ZERO) < 0 || capacity < 0) {
                afficherErreur("Le tarif et la capacité doivent être positifs.");
                return;
            }

            if (evenement == null) {
                evenement = new Evenement();
            }

            evenement.setTitre(titre);
            evenement.setDescription(text(descriptionArea));
            evenement.setTypeEvenement(typeCombo.getValue());
            evenement.setOrganiseParSite(organiseParSiteCheck.isSelected());
            evenement.setLieu(lieu);
            evenement.setDateDebut(debut);
            evenement.setDateFin(fin);
            evenement.setTarif(tarif);
            evenement.setCapacity(capacity);
            evenement.setStatut(statutCombo.getValue());
            evenement.setImageEvenement(text(imageField));
            evenement.setIdCreateur(Session.getUserId());

            if (evenement.getId() == 0) {
                evenementService.add(evenement);
            } else {
                evenementService.update(evenement);
            }

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Succès");
            alert.setHeaderText(null);
            alert.setContentText("Événement enregistré avec succès.");
            alert.showAndWait();

            retour();
        } catch (IllegalArgumentException e) {
            afficherErreur(e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            afficherErreur("Erreur enregistrement : " + e.getMessage());
        }
    }

    @FXML
    private void retour() {
        Navigator.go(rootPane, "/Evenements.fxml", "Événements - SmarTounsi");
    }

    private String text(TextInputControl control) {
        return control.getText() == null ? "" : control.getText().trim();
    }

    private BigDecimal parseTarif(String value) {
        if (value == null || value.isBlank()) {
            return BigDecimal.ZERO;
        }

        String normalized = value.toLowerCase()
                .replace(",", ".")
                .replace("tnd", "")
                .replace("dt", "")
                .replace("dinar", "")
                .replace("dinars", "")
                .replaceAll("\\s+", "")
                .trim();

        if (normalized.isBlank() || "gratuit".equals(normalized) || "free".equals(normalized)) {
            return BigDecimal.ZERO;
        }

        if (!normalized.matches("\\d+(\\.\\d{1,3})?")) {
            throw new IllegalArgumentException("Le tarif doit etre un nombre. Exemple : 15 ou 15.5 ou 15 DT.");
        }

        return new BigDecimal(normalized).setScale(3, RoundingMode.HALF_UP).stripTrailingZeros();
    }

    private int parseCapacity(String value) {
        if (value == null || value.isBlank()) {
            return 0;
        }

        String normalized = value.replaceAll("\\s+", "");
        if (!normalized.matches("\\d+")) {
            throw new IllegalArgumentException("La capacite doit etre un nombre entier. Exemple : 50.");
        }

        return Integer.parseInt(normalized);
    }

    private LocalTime parseTime(String value, String fieldName) {
        try {
            return LocalTime.parse(value);
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Format invalide pour " + fieldName + ". Exemple : 09:00.");
        }
    }

    private void afficherErreur(String message) {
        messageLabel.setText(message);
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Erreur");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
