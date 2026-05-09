package Controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import models.Quiz;
import services.QuizService;

import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.stream.Collectors;

public class QuizController {

    @FXML
    private ComboBox<String> filterMatiereComboBox;

    @FXML
    private TextField searchField;

    @FXML
    private GridPane quizGrid;

    @FXML
    private Label totalQuizLabel;

    private final QuizService quizService = new QuizService();

    private final ObservableList<Quiz> quizList = FXCollections.observableArrayList();
    private final ObservableList<String> matieres = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        loadQuizzesFromDatabase();
        refreshMatiereFilter();
        renderQuizzes();
    }

    @FXML
    private void filterQuizzes() {
        renderQuizzes();
    }

    @FXML
    private void handleSearch() {
        renderQuizzes();
    }

    @FXML
    private void openCreateQuizDialog() {
        Dialog<Quiz> dialog = new Dialog<>();
        dialog.setTitle("Créer un quiz");
        dialog.setHeaderText("Ajouter un quiz");

        ButtonType addButtonType = new ButtonType("Ajouter", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(addButtonType, ButtonType.CANCEL);

        TextField titreField = new TextField();
        titreField.setPromptText("Ex : Quiz Java — POO Fondamentaux");

        TextArea descriptionArea = new TextArea();
        descriptionArea.setPromptText("Description du quiz");
        descriptionArea.setPrefRowCount(3);

        ComboBox<String> moduleComboBox = new ComboBox<>();
        moduleComboBox.getItems().addAll(
                "1 - Mathématiques",
                "2 - Réseaux",
                "3 - Java",
                "4 - Electronique",
                "5 - Base de données",
                "6 - Web",
                "7 - Français",
                "8 - Anglais"
        );
        moduleComboBox.setValue("1 - Mathématiques");

        Spinner<Integer> createurSpinner = new Spinner<>(1, 9999, 1);
        createurSpinner.setEditable(true);

        GridPane form = new GridPane();
        form.setHgap(12);
        form.setVgap(12);
        form.setPadding(new Insets(16));

        form.add(new Label("Titre"), 0, 0);
        form.add(titreField, 1, 0);

        form.add(new Label("Description"), 0, 1);
        form.add(descriptionArea, 1, 1);

        form.add(new Label("Module"), 0, 2);
        form.add(moduleComboBox, 1, 2);

        form.add(new Label("ID Créateur"), 0, 3);
        form.add(createurSpinner, 1, 3);

        dialog.getDialogPane().setContent(form);

        Node addButton = dialog.getDialogPane().lookupButton(addButtonType);
        addButton.setDisable(true);

        titreField.textProperty().addListener((obs, oldValue, newValue) -> {
            addButton.setDisable(newValue.trim().isEmpty());
        });

        dialog.setResultConverter(button -> {
            if (button == addButtonType) {
                String titre = titreField.getText().trim();
                String description = descriptionArea.getText().trim();
                int idModule = extractModuleId(moduleComboBox.getValue());
                int idCreateur = createurSpinner.getValue();

                if (description.isEmpty()) {
                    description = "Quiz de " + getMatiereByModuleId(idModule);
                }

                return new Quiz(titre, description, idModule, idCreateur);
            }

            return null;
        });

        Optional<Quiz> result = dialog.showAndWait();

        result.ifPresent(quiz -> {
            quizService.add(quiz);
            loadQuizzesFromDatabase();
            refreshMatiereFilter();
            renderQuizzes();
        });
    }

    private void loadQuizzesFromDatabase() {
        quizList.clear();
        quizList.addAll(quizService.getAll());
    }

    private void refreshMatiereFilter() {
        String currentValue = filterMatiereComboBox.getValue();

        matieres.clear();
        matieres.add("Toutes les matières");

        quizList.stream()
                .map(Quiz::getMatiere)
                .distinct()
                .sorted()
                .forEach(matieres::add);

        filterMatiereComboBox.setItems(matieres);

        if (currentValue != null && matieres.contains(currentValue)) {
            filterMatiereComboBox.setValue(currentValue);
        } else {
            filterMatiereComboBox.setValue("Toutes les matières");
        }
    }

    private void renderQuizzes() {
        quizGrid.getChildren().clear();

        List<Quiz> filtered = getFilteredQuizzes();

        totalQuizLabel.setText(filtered.size() + " quiz");

        int column = 0;
        int row = 0;

        for (Quiz quiz : filtered) {
            VBox card = createQuizCard(quiz);
            quizGrid.add(card, column, row);

            column++;

            if (column == 2) {
                column = 0;
                row++;
            }
        }

        if (filtered.isEmpty()) {
            Label emptyLabel = new Label("Aucun quiz trouvé.");
            emptyLabel.getStyleClass().add("empty-label");
            quizGrid.add(emptyLabel, 0, 0, 2, 1);
        }
    }

    private List<Quiz> getFilteredQuizzes() {
        String selectedMatiere = filterMatiereComboBox.getValue();

        String search = searchField.getText() == null
                ? ""
                : searchField.getText().trim().toLowerCase(Locale.ROOT);

        return quizList.stream()
                .filter(quiz ->
                        selectedMatiere == null
                                || selectedMatiere.equals("Toutes les matières")
                                || quiz.getMatiere().equals(selectedMatiere)
                )
                .filter(quiz ->
                        search.isEmpty()
                                || quiz.getTitre().toLowerCase(Locale.ROOT).contains(search)
                                || quiz.getDescription().toLowerCase(Locale.ROOT).contains(search)
                                || quiz.getMatiere().toLowerCase(Locale.ROOT).contains(search)
                )
                .collect(Collectors.toList());
    }

    private VBox createQuizCard(Quiz quiz) {
        VBox card = new VBox(14);
        card.setPadding(new Insets(22, 24, 22, 24));
        card.getStyleClass().add("quiz-card");
        card.setMaxWidth(Double.MAX_VALUE);

        Label title = new Label(quiz.getTitre());
        title.getStyleClass().add("quiz-title");
        title.setWrapText(true);

        HBox badges = new HBox(10);
        badges.setAlignment(Pos.CENTER_LEFT);

        Label matiereBadge = createBadge(quiz.getIcone() + "  " + quiz.getMatiere());
        Label questionsBadge = createBadge(quiz.getNombreQuestions() + " questions");
        Label typeBadge = createBadge(quiz.getType());

        badges.getChildren().addAll(matiereBadge, questionsBadge, typeBadge);

        Label descriptionLabel = new Label(quiz.getDescription());
        descriptionLabel.getStyleClass().add("quiz-description");
        descriptionLabel.setWrapText(true);

        HBox actions = new HBox(10);
        actions.setAlignment(Pos.CENTER_LEFT);

        Button startButton = new Button("▶ Commencer");
        startButton.getStyleClass().add("start-button");
        startButton.setOnAction(event -> startQuiz(quiz));

        Button deleteButton = new Button("Supprimer");
        deleteButton.getStyleClass().add("delete-button");
        deleteButton.setOnAction(event -> deleteQuiz(quiz));

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        actions.getChildren().addAll(startButton, spacer, deleteButton);

        card.getChildren().addAll(title, badges, descriptionLabel, actions);

        return card;
    }

    private Label createBadge(String text) {
        Label badge = new Label(text);
        badge.getStyleClass().add("quiz-badge");
        return badge;
    }

    private void startQuiz(Quiz quiz) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Commencer le quiz");
        alert.setHeaderText(quiz.getTitre());
        alert.setContentText(
                "ID Quiz : " + quiz.getId()
                        + "\nMatière : " + quiz.getMatiere()
                        + "\nQuestions : " + quiz.getNombreQuestions()
                        + "\nType : " + quiz.getType()
                        + "\n\nIci tu peux ouvrir la page des questions de ce quiz."
        );
        alert.showAndWait();
    }

    private void deleteQuiz(Quiz quiz) {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Supprimer le quiz");
        confirm.setHeaderText("Voulez-vous supprimer ce quiz ?");
        confirm.setContentText(quiz.getTitre());

        Optional<ButtonType> result = confirm.showAndWait();

        if (result.isPresent() && result.get() == ButtonType.OK) {
            quizService.delete(quiz);
            loadQuizzesFromDatabase();
            refreshMatiereFilter();
            renderQuizzes();
        }
    }

    private int extractModuleId(String value) {
        if (value == null || value.isEmpty()) {
            return 1;
        }

        try {
            return Integer.parseInt(value.split("-")[0].trim());
        } catch (Exception e) {
            return 1;
        }
    }

    private String getMatiereByModuleId(int idModule) {
        switch (idModule) {
            case 1:
                return "Mathématiques";
            case 2:
                return "Réseaux";
            case 3:
                return "Java";
            case 4:
                return "Electronique";
            case 5:
                return "Base de données";
            case 6:
                return "Web";
            case 7:
                return "Français";
            case 8:
                return "Anglais";
            default:
                return "Module " + idModule;
        }
    }
}