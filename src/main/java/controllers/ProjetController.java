package controllers;

import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Window;
import models.Projet;
import services.ProjetService;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

public class ProjetController {

    @FXML private TextField searchField;
    @FXML private FlowPane cardsContainer;
    @FXML private VBox addFormPane;
    @FXML private VBox listPane;

    @FXML private TextField nomField;
    @FXML private TextArea descriptionArea;
    @FXML private TextField collaborateursField;
    @FXML private CheckBox codeCheckBox;
    @FXML private CheckBox presentationCheckBox;
    @FXML private CheckBox rapportCheckBox;

    private final ProjetService projetService = new ProjetService();
    private List<Projet> allProjects = new ArrayList<>();

    @FXML
    public void initialize() {
        refreshProjects();

        searchField.textProperty().addListener((obs, oldValue, newValue) -> filterProjects(newValue));
    }

    @FXML
    private void showAddForm() {
        clearForm();
        addFormPane.setVisible(true);
        addFormPane.setManaged(true);
        listPane.setVisible(false);
        listPane.setManaged(false);
        nomField.requestFocus();
    }

    @FXML
    private void hideAddForm() {
        addFormPane.setVisible(false);
        addFormPane.setManaged(false);
        listPane.setVisible(true);
        listPane.setManaged(true);
    }

    @FXML
    private void createProject() {
        String nom = nomField.getText() == null ? "" : nomField.getText().trim();
        String description = descriptionArea.getText() == null ? "" : descriptionArea.getText().trim();

        boolean contientCode = codeCheckBox.isSelected();
        boolean contientPresentation = presentationCheckBox.isSelected();
        boolean contientRapport = rapportCheckBox.isSelected();

        if (nom.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Le nom du projet est obligatoire.");
            return;
        }

        if (!contientCode && !contientPresentation && !contientRapport) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Choisis au minimum : Code, Présentation ou Rapport.");
            return;
        }

        int idCreateur = 1; // TODO: remplacer par l'id de l'utilisateur connecté
        Projet projet = new Projet(nom, description, idCreateur, contientCode, contientPresentation, contientRapport);
        projet.setStatut("en_cours");

        int id = projetService.addAndReturnId(projet);

        if (id > 0) {
            projet.setId(id);
            showAlert(Alert.AlertType.INFORMATION, "Succès", "Projet créé avec succès. Tu peux maintenant charger les fichiers choisis depuis la carte du projet.");
            clearForm();
            hideAddForm();
            refreshProjects();
        } else {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Le projet n'a pas été ajouté. Vérifie la base de données.");
        }
    }

    private void refreshProjects() {
        allProjects = projetService.getAll();
        afficherProjets(allProjects);
    }

    private void filterProjects(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            afficherProjets(allProjects);
            return;
        }

        String search = keyword.toLowerCase(Locale.ROOT).trim();
        List<Projet> filtered = allProjects.stream()
                .filter(p -> containsIgnoreCase(p.getNomProjet(), search) || containsIgnoreCase(p.getDescription(), search))
                .collect(Collectors.toList());

        afficherProjets(filtered);
    }

    private boolean containsIgnoreCase(String value, String search) {
        return value != null && value.toLowerCase(Locale.ROOT).contains(search);
    }

    private void afficherProjets(List<Projet> projets) {
        cardsContainer.getChildren().clear();

        if (projets == null || projets.isEmpty()) {
            Label empty = new Label("Aucun projet trouvé.");
            empty.getStyleClass().add("empty-text");
            cardsContainer.getChildren().add(empty);
            return;
        }

        for (Projet p : projets) {
            VBox card = new VBox(12);
            card.getStyleClass().add("project-card");

            Label title = new Label(getProjectIcon(p) + " " + p.getNomProjet());
            title.getStyleClass().add("card-title");

            Label desc = new Label(p.getDescription() == null || p.getDescription().isEmpty() ? "Aucune description." : p.getDescription());
            desc.getStyleClass().add("card-desc");
            desc.setWrapText(true);

            HBox badges = new HBox(8);
            if (p.isContientCode()) {
                badges.getChildren().add(createBadge("💻 Code", "badge-blue"));
            }
            if (p.isContientPresentation()) {
                badges.getChildren().add(createBadge("📊 Présentation", "badge-yellow"));
            }
            if (p.isContientRapport()) {
                badges.getChildren().add(createBadge("📄 Rapport", "badge-green"));
            }

            Label collab = new Label("Collaborateurs · " + getShortContentText(p));
            collab.getStyleClass().add("collab-text");

            HBox avatars = new HBox(-5);
            avatars.getChildren().addAll(
                    createAvatar("A", "avatar-blue"),
                    createAvatar("S", "avatar-purple"),
                    createAvatar("K", "avatar-orange")
            );

            HBox uploadActions = new HBox(8);
            if (p.isContientCode()) {
                uploadActions.getChildren().add(createUploadButton(p, "code"));
            }
            if (p.isContientPresentation()) {
                uploadActions.getChildren().add(createUploadButton(p, "presentation"));
            }
            if (p.isContientRapport()) {
                uploadActions.getChildren().add(createUploadButton(p, "rapport"));
            }

            card.getChildren().addAll(title, desc, badges, collab, avatars, uploadActions);
            cardsContainer.getChildren().add(card);
        }
    }

    private Label createBadge(String text, String styleClass) {
        Label badge = new Label(text);
        badge.getStyleClass().addAll("badge", styleClass);
        return badge;
    }

    private Label createAvatar(String text, String styleClass) {
        Label avatar = new Label(text);
        avatar.getStyleClass().addAll("avatar", styleClass);
        return avatar;
    }

    private Button createUploadButton(Projet projet, String type) {
        String path = getFilePath(projet, type);
        boolean uploaded = path != null && !path.trim().isEmpty();

        Button button = new Button(getButtonText(type, uploaded));
        button.getStyleClass().add("upload-button");
        if (uploaded) {
            button.getStyleClass().add("uploaded-button");
        }

        button.setOnAction(event -> chooseFileForProject(projet, type, button));
        return button;
    }

    private void chooseFileForProject(Projet projet, String type, Node ownerNode) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Charger " + type + " du projet");

        if ("code".equals(type)) {
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter(
                    "Code / archive", "*.java", "*.zip", "*.rar", "*.py", "*.js", "*.html", "*.css", "*.sql", "*.xml"));
        } else if ("presentation".equals(type)) {
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter(
                    "Présentation", "*.ppt", "*.pptx", "*.pdf"));
        } else {
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter(
                    "Rapport", "*.pdf", "*.doc", "*.docx"));
        }

        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Tous les fichiers", "*.*"));

        Window window = ownerNode.getScene().getWindow();
        File selectedFile = fileChooser.showOpenDialog(window);

        if (selectedFile != null) {
            setFilePath(projet, type, selectedFile.getAbsolutePath());
            projetService.update(projet);
            showAlert(Alert.AlertType.INFORMATION, "Fichier ajouté", "Fichier chargé avec succès :\n" + selectedFile.getName());
            refreshProjects();
        }
    }

    private String getButtonText(String type, boolean uploaded) {
        if ("code".equals(type)) {
            return uploaded ? "💻 Code chargé ✓" : "💻 Charger code";
        }
        if ("presentation".equals(type)) {
            return uploaded ? "📊 Présentation chargée ✓" : "📊 Charger présentation";
        }
        return uploaded ? "📄 Rapport chargé ✓" : "📄 Charger rapport";
    }

    private String getFilePath(Projet projet, String type) {
        if ("code".equals(type)) {
            return projet.getFichierCode();
        }
        if ("presentation".equals(type)) {
            return projet.getFichierPresentation();
        }
        return projet.getFichierRapport();
    }

    private void setFilePath(Projet projet, String type, String path) {
        if ("code".equals(type)) {
            projet.setFichierCode(path);
        } else if ("presentation".equals(type)) {
            projet.setFichierPresentation(path);
        } else {
            projet.setFichierRapport(path);
        }
    }

    private String getProjectIcon(Projet p) {
        if (p.isContientCode()) {
            return "🚀";
        }
        if (p.isContientPresentation()) {
            return "📊";
        }
        return "📄";
    }

    private String getShortContentText(Projet p) {
        List<String> parts = new ArrayList<>();
        if (p.isContientCode()) {
            parts.add("Code");
        }
        if (p.isContientPresentation()) {
            parts.add("Présentation");
        }
        if (p.isContientRapport()) {
            parts.add("Rapport");
        }
        return String.join(" · ", parts);
    }

    private void clearForm() {
        nomField.clear();
        descriptionArea.clear();
        collaborateursField.clear();
        codeCheckBox.setSelected(true);
        presentationCheckBox.setSelected(true);
        rapportCheckBox.setSelected(true);
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
