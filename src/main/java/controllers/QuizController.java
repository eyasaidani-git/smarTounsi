package controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import models.Quiz;
import services.QuizService;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;


public class QuizController implements Initializable {

    @FXML private ComboBox<String> comboMatiere;
    @FXML private TextField        searchField;
    @FXML private GridPane         quizGrid;       // ← ajouté dans le FXML
    @FXML private VBox emptyLabel;    // ← message "aucun quiz"

    private final QuizService quizService = new QuizService();


    @Override
    public void initialize(URL url, ResourceBundle rb) {

        comboMatiere.getItems().addAll(
                "Toutes les matières",
                "Mathématiques",
                "Java",
                "Réseaux",
                "Web",
                "Electronique",
                "Base de données"
        );
        comboMatiere.setValue("Toutes les matières");

        chargerQuiz(null, null);
    }

    private void chargerQuiz(String terme, String matiere) {

        quizGrid.getChildren().clear();

        List<Quiz> liste;

        // Recherche filtrée ou liste complète
        if ((terme != null && !terme.isEmpty())
                || (matiere != null && !matiere.equals("Toutes les matières"))) {
            liste = quizService.rechercher(terme, matiere);
        } else {
            liste = quizService.getAll();
        }

        if (liste.isEmpty()) {
            emptyLabel.setVisible(true);
            emptyLabel.setManaged(true);
            return;
        }

        emptyLabel.setVisible(false);
        emptyLabel.setManaged(false);

        int col = 0, row = 0;
        for (Quiz quiz : liste) {
            VBox card = creerCarteQuiz(quiz);
            quizGrid.add(card, col, row);
            col++;
            if (col == 2) { col = 0; row++; }
        }
    }

    private VBox creerCarteQuiz(Quiz quiz) {

        VBox card = new VBox(12);
        card.getStyleClass().add("quiz-card");
        card.setPadding(new Insets(20));

        // Titre
        Label titre = new Label(quiz.getTitre());
        titre.getStyleClass().add("quiz-title");
        titre.setWrapText(true);

        // Infos : matière + nb questions + type
        int nbQ = quizService.compterQuestions(quiz.getId());
        String nomMod = quiz.getNomModule() != null ? quiz.getNomModule() : "—";
        String emoji  = emojiModule(nomMod);

        Label infos = new Label(emoji + " " + nomMod + " | " + nbQ + " questions | "
                + (quiz.getTempsLimite() > 0 ? quiz.getTempsLimite() + " min" : "Sans limite"));
        infos.setStyle("-fx-text-fill:#555870; -fx-font-size:12px;");
        infos.setWrapText(true);

        // Description (si présente)
        if (quiz.getDescription() != null && !quiz.getDescription().isEmpty()) {
            Label desc = new Label(quiz.getDescription());
            desc.setStyle("-fx-text-fill:#888; -fx-font-size:11px;");
            desc.setWrapText(true);
            card.getChildren().add(desc);
        }

        // Bouton Commencer
        Button btnCommencer = new Button("▶ Commencer");
        btnCommencer.getStyleClass().add("yellow-button");
        btnCommencer.setOnAction(e -> commencerQuizAvec(quiz, e));

        card.getChildren().addAll(titre, infos, btnCommencer);
        return card;
    }

    private String emojiModule(String nom) {
        if (nom == null) return "📘";
        return switch (nom.toLowerCase()) {
            case "mathématiques" -> "📐";
            case "réseaux"       -> "🌐";
            case "java"          -> "☕";
            case "electronique"  -> "⚡";
            case "web"           -> "🌍";
            case "base de données" -> "🗄";
            default              -> "📘";
        };
    }

    private void commencerQuizAvec(Quiz quiz, ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/view/quiz.play.fxml"));
            Parent root = loader.load();

            PlayQuizController ctrl = loader.getController();
            ctrl.initialiserQuiz(quiz);    // ← passer le quiz sélectionné

            Scene scene = new Scene(root);
            scene.getStylesheets().add(
                    getClass().getResource("/style/quiz.css").toExternalForm());

            Stage stage = (Stage) ((javafx.scene.Node) event.getSource())
                    .getScene().getWindow();
            stage.setScene(scene);
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Impossible de charger le quiz.", ButtonType.OK).showAndWait();
        }
    }

    @FXML
    void rechercherQuiz(ActionEvent event) {
        String terme   = searchField.getText() == null ? "" : searchField.getText().trim();
        String matiere = comboMatiere.getValue();
        chargerQuiz(terme, matiere);
    }

    @FXML
    void creerQuiz(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(
                    getClass().getResource("/view/quiz_create.fxml"));
            Scene scene = new Scene(root);
            scene.getStylesheets().add(
                    getClass().getResource("/style/quiz.css").toExternalForm());
            Stage stage = (Stage) ((javafx.scene.Node) event.getSource())
                    .getScene().getWindow();
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
