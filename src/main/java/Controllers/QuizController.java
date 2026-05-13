package Controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;

public class QuizController {

    @FXML
    private ComboBox<String> comboMatiere;

    @FXML
    private TextField searchField;

    // ==========================================
    // INITIALIZE
    // ==========================================

    @FXML
    public void initialize() {

        comboMatiere.getItems().addAll(

                "Mathématiques",
                "Java",
                "Réseaux",
                "Web",
                "Electronique",
                "Base de données"
        );
    }

    // ==========================================
    // COMMENCER QUIZ
    // ==========================================

    @FXML
    void commencerQuiz(ActionEvent event) {

        try {

            Parent root = FXMLLoader.load(
                    getClass().getResource(
                            "/view/quiz.play.fxml"
                    )
            );

            Scene scene = new Scene(root);

            scene.getStylesheets().add(
                    getClass()
                            .getResource("/style/quiz.css")
                            .toExternalForm()
            );

            Stage stage = (Stage)

                    ((javafx.scene.Node) event.getSource())
                            .getScene()
                            .getWindow();

            stage.setScene(scene);

            stage.show();

        } catch (IOException e) {

            e.printStackTrace();
        }
    }

    // ==========================================
    // CREER QUIZ
    // ==========================================

    @FXML
    void creerQuiz(ActionEvent event) {

        try {

            Parent root = FXMLLoader.load(
                    getClass().getResource(
                            "/view/quiz_create.fxml"
                    )
            );

            Scene scene = new Scene(root);

            scene.getStylesheets().add(
                    getClass()
                            .getResource("/style/quiz.css")
                            .toExternalForm()
            );

            Stage stage = (Stage)

                    ((javafx.scene.Node) event.getSource())
                            .getScene()
                            .getWindow();

            stage.setScene(scene);

            stage.show();

        } catch (IOException e) {

            e.printStackTrace();
        }
    }

    // ==========================================
    // RECHERCHE QUIZ
    // ==========================================

    @FXML
    void rechercherQuiz(ActionEvent event) {

        String matiere = comboMatiere.getValue();

        String texte = searchField.getText();

        Alert alert =
                new Alert(Alert.AlertType.INFORMATION);

        alert.setHeaderText(null);

        // =========================
        // RECHERCHE MATIERE
        // =========================

        if (matiere != null) {

            alert.setContentText(
                    "✅ La matière \"" +
                            matiere +
                            "\" existe."
            );
        }

        // =========================
        // RECHERCHE TEXTE
        // =========================

        else if (texte != null &&
                !texte.isEmpty()) {

            if (
                    texte.toLowerCase().contains("java")
                            || texte.toLowerCase().contains("math")
                            || texte.toLowerCase().contains("réseaux")
            ) {

                alert.setContentText(
                        "✅ Quiz trouvé : " + texte
                );

            } else {

                alert.setContentText(
                        "❌ Aucun quiz trouvé."
                );
            }
        }

        // =========================
        // VIDE
        // =========================

        else {

            alert.setContentText(
                    "⚠ Veuillez saisir une recherche."
            );
        }

        alert.showAndWait();
    }
}