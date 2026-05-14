package Controllers;

import javafx.animation.KeyFrame;
import javafx.animation.PauseTransition;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.util.Duration;
import models.Question;
import models.Quiz;
import models.Reponse;
import services.QuestionService;
import services.ReponseService;
import services.ResultatQuizService;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;



public class PlayQuizController implements Initializable {

    // ===== FXML =====
    @FXML private VBox  questionsBox;     // zone principale (quiz play FXML original)
    @FXML private Label timerLabel;

    // === Ajoutés pour la navigation (si vous mettez à jour le FXML) ===
    @FXML private Label       questionNumeroLabel;
    @FXML private ProgressBar progressBar;
    @FXML private VBox        questionCardZone;   // zone où on injecte la question courante
    @FXML private Label       feedbackLabel;
    @FXML private Button      btnPrecedent;
    @FXML private Button      btnSuivant;

    // ===== State =====
    private Quiz                   quiz;
    private List<Question>         questions;
    private List<Reponse>          reponsesParQuestion;
    private int                    indexCourant   = 0;
    private final Map<Integer, String> reponsesUser = new HashMap<>();  // qId → réponse texte
    private Timeline               timer;
    private int                    tempsRestant;
    private boolean                modeNavigation = false; // true si FXML mis à jour

    private final QuestionService  questionService = new QuestionService();
    private final ReponseService   reponseService  = new ReponseService();
    private final ResultatQuizService resultatService = new ResultatQuizService();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Détecter si le FXML supporte la navigation question-par-question
        modeNavigation = (questionCardZone != null);
    }

    public void initialiserQuiz(Quiz quiz) {
        int minutes = quiz.getTempsLimite();

        if(minutes <= 0){
            minutes = 30;
        }

        tempsRestant = minutes * 60;
        this.quiz = quiz;

        // Charger les questions depuis la BDD
        this.questions = questionService.getByQuiz(quiz.getId());

        if (questions.isEmpty()) {
            afficherErreur("Ce quiz ne contient pas encore de questions.");
            return;
        }

        // Préparer le timer
        tempsRestant = quiz.getTempsLimite() > 0 ? quiz.getTempsLimite() * 60 : 1800;
        demarrerTimer();

        // Afficher selon le mode FXML
        if (modeNavigation) {
            afficherQuestionNavigation(0);
        } else {
            // Mode original : toutes les questions visibles
            afficherToutesLesQuestions();
        }
    }


    private void afficherToutesLesQuestions() {
        questionsBox.getChildren().clear();

        for (Question q : questions) {
            VBox card = creerCarteQuestion(q);
            questionsBox.getChildren().add(card);
        }
    }

    private void afficherQuestionNavigation(int index) {
        if (questions == null || index < 0 || index >= questions.size()) return;

        Question q = questions.get(index);

        // Progress
        if (progressBar != null) {
            progressBar.setProgress((double)(index + 1) / questions.size());
        }
        if (questionNumeroLabel != null) {
            questionNumeroLabel.setText("Question " + (index + 1) + " / " + questions.size());
        }
        if (btnPrecedent != null) {
            btnPrecedent.setVisible(index > 0);
        }
        if (btnSuivant != null) {
            btnSuivant.setText(index == questions.size() - 1 ? "Terminer ✓" : "Suivant →");
        }
        if (feedbackLabel != null) {
            feedbackLabel.setVisible(false);
            feedbackLabel.setManaged(false);
        }

        questionCardZone.getChildren().clear();
        questionCardZone.getChildren().add(creerCarteQuestion(q));
    }

    private VBox creerCarteQuestion(Question q) {

        VBox card = new VBox(15);
        card.getStyleClass().add("quiz-card");
        card.setStyle("-fx-padding:20; -fx-background-color:white; -fx-background-radius:12;");

        // Enoncé
        Label enonce = new Label(q.getEnonce());
        enonce.getStyleClass().add("quiz-title");
        enonce.setWrapText(true);
        enonce.setStyle("-fx-font-size:15px; -fx-font-weight:bold; -fx-text-fill:#023047;");

        card.getChildren().add(enonce);

        // Charger les réponses depuis la BDD
        List<Reponse> reponses = reponseService.getByQuestion(q.getId());

        switch (q.getTypeQuestion()) {

            case QCM -> {
                card.getChildren().add(new Label("Choisissez la bonne réponse :"));
                ToggleGroup tg = new ToggleGroup();

                for (Reponse r : reponses) {
                    RadioButton rb = new RadioButton(r.getTexteReponse());
                    rb.setToggleGroup(tg);
                    rb.setStyle("-fx-font-size:14px; -fx-padding:8;");
                    rb.setUserData(r.getTexteReponse());
                    rb.setOnAction(e -> reponsesUser.put(q.getId(), r.getTexteReponse()));

                    // Restaurer sélection précédente
                    if (r.getTexteReponse().equals(reponsesUser.get(q.getId()))) {
                        rb.setSelected(true);
                    }
                    card.getChildren().add(rb);
                }
            }

            case VRAI_FAUX -> {
                Label lbl = new Label("Votre réponse :");
                lbl.setStyle("-fx-font-size:12px; -fx-text-fill:#555870;");

                ToggleGroup tg   = new ToggleGroup();
                RadioButton vrai = new RadioButton("✓  Vrai");
                RadioButton faux = new RadioButton("✗  Faux");
                vrai.setToggleGroup(tg);
                faux.setToggleGroup(tg);

                vrai.setStyle("-fx-font-size:14px; -fx-font-weight:bold; -fx-text-fill:#1A5E35;");
                faux.setStyle("-fx-font-size:14px; -fx-font-weight:bold; -fx-text-fill:#922B21;");

                String repPrecedente = reponsesUser.get(q.getId());
                if ("Vrai".equals(repPrecedente)) vrai.setSelected(true);
                if ("Faux".equals(repPrecedente)) faux.setSelected(true);

                vrai.setOnAction(e -> reponsesUser.put(q.getId(), "Vrai"));
                faux.setOnAction(e -> reponsesUser.put(q.getId(), "Faux"));

                HBox ligne = new HBox(30, vrai, faux);
                card.getChildren().addAll(lbl, ligne);
            }

            case REPONSE_LIBRE -> {
                Label lbl = new Label("Votre réponse libre :");
                lbl.setStyle("-fx-font-size:12px; -fx-text-fill:#555870;");

                TextArea ta = new TextArea();
                ta.setPromptText("Écrivez votre réponse ici...");
                ta.setPrefHeight(100);
                ta.setWrapText(true);
                ta.setStyle("-fx-background-radius:8; -fx-border-color:#D5D8E0; -fx-border-radius:8;");

                // Restaurer texte précédent
                String prevText = reponsesUser.get(q.getId());
                if (prevText != null) ta.setText(prevText);

                ta.setOnKeyReleased(e -> {
                    if (!ta.getText().trim().isEmpty()) {
                        reponsesUser.put(q.getId(), ta.getText().trim());
                    }
                });

                card.getChildren().addAll(lbl, ta);
            }
        }

        return card;
    }


    private void demarrerTimer() {

        if (timer != null) {
            timer.stop();
        }

        // AFFICHAGE INITIAL IMMÉDIAT

        int minInitial = tempsRestant / 60;
        int secInitial = tempsRestant % 60;

        timerLabel.setText(
                String.format("%02d:%02d",
                        minInitial,
                        secInitial)
        );

        timer = new Timeline(

                new KeyFrame(
                        Duration.seconds(1),

                        e -> {

                            // décrémenter APRÈS 1 seconde
                            tempsRestant--;

                            int min =
                                    tempsRestant / 60;

                            int sec =
                                    tempsRestant % 60;

                            if (timerLabel != null) {

                                timerLabel.setText(
                                        String.format(
                                                "%02d:%02d",
                                                min,
                                                sec
                                        )
                                );

                                // Rouge si moins de 1 min

                                if (tempsRestant <= 60) {

                                    timerLabel.setStyle(
                                            "-fx-text-fill:#E74C3C;" +
                                                    "-fx-font-weight:bold;" +
                                                    "-fx-font-size:16px;"
                                    );
                                }
                            }

                            // FIN TIMER

                            if (tempsRestant <= 0) {

                                timer.stop();

                                terminerQuiz(null);
                            }
                        }
                )
        );

        timer.setCycleCount(
                Timeline.INDEFINITE
        );

        timer.play();
    }


    @FXML
    void suivant(ActionEvent event) {
        if (modeNavigation) {
            if (indexCourant < questions.size() - 1) {
                indexCourant++;
                afficherQuestionNavigation(indexCourant);
            } else {
                terminerQuiz(event);
            }
        }
    }

    @FXML
    void precedent(ActionEvent event) {
        if (modeNavigation && indexCourant > 0) {
            indexCourant--;
            afficherQuestionNavigation(indexCourant);
        }
    }

    private int calculerScore() {
        int score = 0;

        for (Question q : questions) {
            String repUser = reponsesUser.get(q.getId());
            if (repUser == null || repUser.isEmpty()) continue;

            if (q.getTypeQuestion().name().equals("REPONSE_LIBRE")) {
                // Les libres comptent si répondues
                score += q.getPoints();
                continue;
            }

            // Comparer avec les bonnes réponses en BDD
            List<Reponse> reponsesBDD = reponseService.getByQuestion(q.getId());
            for (Reponse r : reponsesBDD) {
                if (r.isEstCorrecte() &&
                        r.getTexteReponse().trim().equalsIgnoreCase(repUser.trim())) {
                    score += q.getPoints();
                    break;
                }
            }
        }
        return score;
    }

    // ================================================
    // TERMINER LE QUIZ
    // ================================================
    @FXML
    void terminerQuiz(ActionEvent event) {
        if (timer != null) timer.stop();

        int score     = calculerScore();
        int total     = quiz.getScoreTotal();
        int nbQ       = questions.size();
        int seuil     = (int) Math.ceil(nbQ * 0.6); // 60% pour valider
        boolean valide = score >= seuil;

        // Message selon résultat
        String statut = valide ? "✅ QUIZ VALIDÉ !" : "❌ Quiz non validé";
        String msg = statut + "\n\n"
                + "Score : " + score + " / " + total + "\n"
                + "Questions : " + nbQ + "\n"
                + "Seuil : " + seuil + "/" + nbQ + " bonnes réponses\n"
                + (valide ? "🎉 Félicitations !" : "💪 Réessayez, vous pouvez y arriver !");

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Résultat du quiz");
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();

        // Retour à la liste des quiz
        fermerQuiz(event);
    }


    private void fermerQuiz(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/view/quiz.fxml"));
            Parent root = loader.load();

            Scene scene = new Scene(root);
            scene.getStylesheets().add(
                    getClass().getResource("/style/quiz.css").toExternalForm());

            Stage stage;
            if (event != null) {
                stage = (Stage) ((javafx.scene.Node) event.getSource())
                        .getScene().getWindow();
            } else {
                stage = (Stage) (questionsBox != null
                        ? questionsBox.getScene().getWindow()
                        : questionCardZone.getScene().getWindow());
            }

            stage.setScene(scene);
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void afficherErreur(String message) {
        Alert a = new Alert(Alert.AlertType.WARNING, message, ButtonType.OK);
        a.setHeaderText(null);
        a.showAndWait();
    }
}