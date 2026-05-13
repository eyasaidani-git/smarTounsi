package Controllers;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;

public class PlayQuizController {

    @FXML
    private VBox questionsBox;

    @FXML
    private Label timerLabel;

    // =========================
    // TIMER
    // =========================

    private Timeline timeline;

    // 5 minutes = 300 secondes

    private int tempsRestant = 300;

    // =========================
    // INITIALIZE
    // =========================

    @FXML
    public void initialize() {

        // LANCER TIMER

        commencerTimer();

        // QUESTIONS

        ajouterQuestion(
                "1. Java est un langage ?"
        );

        ajouterQuestion(
                "2. Que signifie POO ?"
        );

        ajouterQuestion(
                "3. Quelle classe est utilisée pour afficher ?"
        );
    }

    // =========================
    // AJOUT QUESTION
    // =========================

    private void ajouterQuestion(String texte){

        Label q = new Label(texte);

        q.getStyleClass().add("quiz-title");

        VBox card = new VBox(15);

        card.getStyleClass().add("quiz-card");

        card.getChildren().add(q);

        questionsBox.getChildren().add(card);
    }

    // =========================
    // DEMARRER TIMER
    // =========================

    private void commencerTimer(){

        timeline = new Timeline(

                new KeyFrame(
                        Duration.seconds(1),

                        e -> {

                            tempsRestant--;

                            int minutes =
                                    tempsRestant / 60;

                            int secondes =
                                    tempsRestant % 60;

                            timerLabel.setText(

                                    String.format(
                                            "%02d:%02d",
                                            minutes,
                                            secondes
                                    )
                            );

                            // =========================
                            // TEMPS TERMINE
                            // =========================

                            if(tempsRestant <= 0){

                                timeline.stop();

                                terminerQuiz(null);
                            }
                        }
                )
        );

        timeline.setCycleCount(
                Timeline.INDEFINITE
        );

        timeline.play();
    }

    // =========================
    // CALCUL SCORE
    // =========================

    private float calculerScore(){

        // PLUS TARD :
        // calcul réel des réponses

        return 15;
    }

    // =========================
    // TERMINER QUIZ
    // =========================

    @FXML
    void terminerQuiz(ActionEvent event){

        // STOP TIMER

        if(timeline != null){

            timeline.stop();
        }

        // SCORE

        float score =
                calculerScore();

        Alert alert = new Alert(
                Alert.AlertType.INFORMATION
        );

        alert.setTitle("Quiz terminé");

        alert.setHeaderText(null);

        alert.setContentText(
                "✅ Votre score est : "
                        + score
                        + "/20"
        );

        alert.showAndWait();

        fermerQuiz(event);
    }

    // =========================
    // FERMER QUIZ
    // =========================

    private void fermerQuiz(ActionEvent event){

        try {

            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource(
                            "/view/quiz.fxml"
                    )
            );

            Parent root = loader.load();

            Scene scene = new Scene(root);

            // CSS

            scene.getStylesheets().add(
                    getClass()
                            .getResource("/style/quiz.css")
                            .toExternalForm()
            );

            Stage stage;

            // =========================
            // SI BOUTON CLIQUE
            // =========================

            if(event != null){

                stage = (Stage)

                        ((javafx.scene.Node)
                                event.getSource())
                                .getScene()
                                .getWindow();
            }

            // =========================
            // SI TIMER TERMINE
            // =========================

            else{

                stage = (Stage)
                        questionsBox
                                .getScene()
                                .getWindow();
            }

            stage.setScene(scene);

            stage.show();

        } catch (IOException e) {

            e.printStackTrace();
        }
    }
}