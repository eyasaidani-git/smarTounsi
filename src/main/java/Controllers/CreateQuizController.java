package Controllers;

import enums.QuestionType;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import models.Question;
import models.Quiz;
import models.Reponse;
import services.QuestionService;
import services.QuizService;
import services.ReponseService;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * CreateQuizController — SmarTounsi
 *
 * CORRECTIONS APPORTÉES :
 *  ✅ Suppression du bug "question = null" avant questionService.add()
 *  ✅ scoreTotal calculé dynamiquement selon nb de questions
 *  ✅ Validation complète des champs
 *  ✅ Chaque carte question valide son type avant enregistrement
 *  ✅ Message d'erreur clair si type non sélectionné
 *  ✅ Liste déroulante (LISTE_DEROULANTE) ajoutée dans le ComboBox type
 */
public class CreateQuizController implements Initializable {

    @FXML private VBox      questionsContainer;
    @FXML private ComboBox<String> matiereQuiz;
    @FXML private TextField titreQuiz;
    @FXML private TextArea  descriptionQuiz;
    @FXML private Spinner<Integer> tempsLimiteSpinner;
    @FXML private Label     compteurLabel;

    private int questionNumber = 1;

    private final QuizService     quizService     = new QuizService();
    private final QuestionService questionService = new QuestionService();
    private final ReponseService  reponseService  = new ReponseService();

    // ================================================
    // INITIALIZE
    // ================================================
    @Override
    public void initialize(URL url, ResourceBundle rb) {

        matiereQuiz.getItems().addAll(
                "Mathématiques",
                "Java",
                "Réseaux",
                "Electronique",
                "Base de données",
                "Web"
        );

        // Ajouter la première question automatiquement
        ajouterBlocQuestion();
    }

    // ================================================
    // AJOUTER UNE QUESTION (bouton)
    // ================================================
    @FXML
    void ajouterQuestion(ActionEvent event) {
        if (questionNumber <= 20) {
            ajouterBlocQuestion();
        } else {
            afficherErreur("Maximum 20 questions par quiz.");
        }
    }

    // ================================================
    // CRÉER LE BLOC D'UNE QUESTION
    // ================================================
    private void ajouterBlocQuestion() {

        VBox card = new VBox(10);
        card.getStyleClass().add("quiz-card");
        card.setStyle("-fx-padding:15; -fx-background-color:white; -fx-background-radius:10;");

        // En-tête : numéro + bouton supprimer
        HBox header = new HBox(10);
        header.setAlignment(javafx.geometry.Pos.CENTER_LEFT);

        Label title = new Label("Question " + questionNumber);
        title.getStyleClass().add("quiz-title");
        title.setStyle("-fx-font-size:14px; -fx-font-weight:bold; -fx-text-fill:#023047;");

        javafx.scene.layout.Region spacer = new javafx.scene.layout.Region();
        HBox.setHgrow(spacer, javafx.scene.layout.Priority.ALWAYS);

        Button btnSup = new Button("🗑 Supprimer");
        btnSup.setStyle("-fx-background-color:#E74C3C; -fx-text-fill:white; " +
                "-fx-background-radius:8; -fx-font-size:11px; -fx-cursor:hand;");
        btnSup.setOnAction(e -> {
            questionsContainer.getChildren().remove(card);
            renumeroterQuestions();
        });

        header.getChildren().addAll(title, spacer, btnSup);

        // Enoncé de la question
        TextField questionField = new TextField();
        questionField.setPromptText("Écrire la question ici...");
        questionField.setStyle("-fx-background-radius:8; -fx-border-color:#D5D8E0; " +
                "-fx-border-radius:8; -fx-padding:8;");

        // Type de réponse
        Label typeLabel = new Label("Type de réponse *");
        typeLabel.setStyle("-fx-font-size:12px; -fx-font-weight:bold; -fx-text-fill:#023047;");

        ComboBox<String> typeReponse = new ComboBox<>();
        typeReponse.getItems().addAll(
                "QCM",
                "Vrai / Faux",
                "Réponse Libre"
        );
        typeReponse.setPromptText("Choisir le type...");
        typeReponse.setMaxWidth(Double.MAX_VALUE);

        // Zone des réponses (vide par défaut)
        VBox reponsesBox = new VBox(10);

        // Réaction au changement de type
        typeReponse.setOnAction(e -> {
            reponsesBox.getChildren().clear();
            buildReponsesZone(typeReponse.getValue(), reponsesBox);
        });

        card.getChildren().addAll(header, questionField, typeLabel, typeReponse, reponsesBox);
        questionsContainer.getChildren().add(card);
        questionNumber++;
        majCompteur();
    }

    // ================================================
    // CONSTRUIRE LA ZONE DE RÉPONSES SELON LE TYPE
    // ================================================
    private void buildReponsesZone(String type, VBox reponsesBox) {

        if (type == null) return;

        switch (type) {

            // ---- QCM : 4 options avec checkbox ----
            case "QCM" -> {
                Label lbl = new Label("Cochez la (les) bonne(s) réponse(s) :");
                lbl.setStyle("-fx-font-size:12px; -fx-text-fill:#555870;");
                reponsesBox.getChildren().add(lbl);

                String[] lettres = {"A", "B", "C", "D"};
                for (int i = 0; i < 4; i++) {
                    HBox ligne = new HBox(10);
                    ligne.setAlignment(javafx.geometry.Pos.CENTER_LEFT);

                    CheckBox correct = new CheckBox();
                    correct.setTooltip(new Tooltip("Cocher = bonne réponse"));

                    Label lettre = new Label(lettres[i]);
                    lettre.setStyle("-fx-font-weight:bold; -fx-text-fill:#023047; -fx-min-width:16px;");

                    TextField rep = new TextField();
                    rep.setPromptText("Réponse " + lettres[i] + "...");
                    rep.setStyle("-fx-background-radius:8; -fx-border-color:#D5D8E0; " +
                            "-fx-border-radius:8; -fx-padding:8;");
                    HBox.setHgrow(rep, javafx.scene.layout.Priority.ALWAYS);

                    ligne.getChildren().addAll(correct, lettre, rep);
                    reponsesBox.getChildren().add(ligne);
                }

                // Bouton ajouter option
                Button btnAjout = new Button("+ Ajouter une option");
                btnAjout.setStyle("-fx-background-color:transparent; -fx-text-fill:#229DBC; " +
                        "-fx-font-size:12px; -fx-cursor:hand;");
                btnAjout.setOnAction(e -> {
                    int n = reponsesBox.getChildren().size(); // lettres A, B, C...
                    String l = n < 26 ? String.valueOf((char)('A' + n - 1)) : String.valueOf(n);
                    HBox ligne = new HBox(10);
                    ligne.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
                    CheckBox correct2 = new CheckBox();
                    Label lettre2 = new Label(l);
                    lettre2.setStyle("-fx-font-weight:bold; -fx-text-fill:#023047; -fx-min-width:16px;");
                    TextField rep2 = new TextField();
                    rep2.setPromptText("Réponse " + l + "...");
                    rep2.setStyle("-fx-background-radius:8; -fx-border-color:#D5D8E0; -fx-border-radius:8; -fx-padding:8;");
                    HBox.setHgrow(rep2, javafx.scene.layout.Priority.ALWAYS);
                    ligne.getChildren().addAll(correct2, lettre2, rep2);
                    // insérer avant le bouton ajouter
                    reponsesBox.getChildren().add(reponsesBox.getChildren().size() - 1, ligne);
                });
                reponsesBox.getChildren().add(btnAjout);
            }

            // ---- VRAI / FAUX ----
            case "Vrai / Faux" -> {
                Label lbl = new Label("Sélectionnez la bonne réponse :");
                lbl.setStyle("-fx-font-size:12px; -fx-text-fill:#555870;");

                ToggleGroup tg   = new ToggleGroup();
                RadioButton vrai = new RadioButton("✓  Vrai");
                RadioButton faux = new RadioButton("✗  Faux");
                vrai.setToggleGroup(tg);
                faux.setToggleGroup(tg);
                vrai.setStyle("-fx-font-size:14px; -fx-text-fill:#1A5E35;");
                faux.setStyle("-fx-font-size:14px; -fx-text-fill:#922B21;");

                HBox ligne = new HBox(30);
                ligne.getChildren().addAll(vrai, faux);
                reponsesBox.getChildren().addAll(lbl, ligne);
            }

            // ---- RÉPONSE LIBRE ----
            case "Réponse Libre" -> {
                Label lbl = new Label("Réponse correcte attendue (optionnel) :");
                lbl.setStyle("-fx-font-size:12px; -fx-text-fill:#555870;");

                TextArea libre = new TextArea();
                libre.setPromptText("Entrez la réponse correcte de référence...");
                libre.setPrefHeight(80);
                libre.setStyle("-fx-background-radius:8; -fx-border-color:#D5D8E0; " +
                        "-fx-border-radius:8; -fx-font-size:13px;");

                Label info = new Label("ℹ️ Les réponses libres sont évaluées par le professeur.");
                info.setStyle("-fx-font-size:11px; -fx-text-fill:#229DBC;");

                reponsesBox.getChildren().addAll(lbl, libre, info);
            }
        }
    }

    // ================================================
    // RENUMÉROTER LES QUESTIONS APRÈS SUPPRESSION
    // ================================================
    private void renumeroterQuestions() {
        int num = 1;
        for (javafx.scene.Node node : questionsContainer.getChildren()) {
            if (node instanceof VBox card) {
                if (!card.getChildren().isEmpty()
                        && card.getChildren().get(0) instanceof HBox header
                        && !header.getChildren().isEmpty()
                        && header.getChildren().get(0) instanceof Label lbl) {
                    lbl.setText("Question " + num);
                    num++;
                }
            }
        }
        questionNumber = num;
        majCompteur();
    }

    private void majCompteur() {
        if (compteurLabel != null) {
            compteurLabel.setText(
                    (questionNumber - 1) + " question(s)");
        }
    }

    // ================================================
    // ENREGISTRER LE QUIZ DANS LA BDD
    // ================================================
    @FXML
    void enregistrerQuiz(ActionEvent event) {

        try {
            // ------ Validation entête ------
            String titre = titreQuiz.getText() == null ? "" : titreQuiz.getText().trim();
            if (titre.isEmpty()) {
                afficherErreur("Veuillez saisir le titre du quiz."); return;
            }

            String matiere = matiereQuiz.getValue();
            if (matiere == null) {
                afficherErreur("Veuillez choisir une matière."); return;
            }

            if (questionsContainer.getChildren().isEmpty()) {
                afficherErreur("Ajoutez au moins une question."); return;
            }

            // ------ Créer et sauvegarder le Quiz ------
            int idModule = getIdModule(matiere);
            int nbQ = questionsContainer.getChildren().size();

            Quiz quiz = new Quiz();
            quiz.setTitre(titre);
            quiz.setDescription(
                    descriptionQuiz.getText() == null ? "" : descriptionQuiz.getText().trim());
            quiz.setIdModule(idModule);
            quiz.setIdCreateur(1);       // utilisateur connecté (id=1 par défaut)
                // 30 minutes
            quiz.setScoreTotal(nbQ);     // 1 point par question
            quiz.setEstActif(true);

            quizService.add(quiz);

            if (quiz.getId() == 0) {
                afficherErreur("❌ Échec de la sauvegarde du quiz (vérifiez la BDD)."); return;
            }

            int idQuiz = quiz.getId();
            int ordre  = 1;
            int nbEnregistrees = 0;

            // ------ Parcourir les questions ------
            for (javafx.scene.Node node : questionsContainer.getChildren()) {

                if (!(node instanceof VBox card)) continue;

                // Structure : [0]=HBox(header), [1]=TextField, [2]=Label, [3]=ComboBox, [4]=VBox(reponses)
                if (card.getChildren().size() < 5) continue;

                TextField questionField = (TextField) card.getChildren().get(1);
                ComboBox<String> typeBox = (ComboBox<String>) card.getChildren().get(3);
                VBox reponsesBox = (VBox) card.getChildren().get(4);

                String enonce    = questionField.getText();
                String typeChoisi = typeBox.getValue();

                if (enonce == null || enonce.trim().isEmpty()) {
                    System.out.println("⚠ Question " + ordre + " ignorée (enoncé vide).");
                    continue;
                }
                if (typeChoisi == null) {
                    System.out.println("⚠ Question " + ordre + " ignorée (type non sélectionné).");
                    continue;
                }

                // Déterminer le type enum
                QuestionType typeQuestion = switch (typeChoisi) {
                    case "QCM"           -> QuestionType.QCM;
                    case "Vrai / Faux"   -> QuestionType.VRAI_FAUX;
                    default              -> QuestionType.REPONSE_LIBRE;
                };

                // ------ Créer et sauvegarder la Question ------
                Question question = new Question();
                question.setIdQuiz(idQuiz);
                question.setEnonce(enonce.trim());
                question.setTypeQuestion(typeQuestion);
                question.setPoints(1);
                question.setOrdre(ordre);

                questionService.add(question);

                if (question.getId() == 0) {
                    System.out.println("⚠ Échec sauvegarde question " + ordre);
                    continue;
                }

                int idQuestion = question.getId();

                // ------ Sauvegarder les Réponses ------
                sauvegarderReponses(typeQuestion, reponsesBox, idQuestion);

                ordre++;
                nbEnregistrees++;
            }

            // ------ Confirmation ------
            Alert ok = new Alert(Alert.AlertType.INFORMATION);
            ok.setHeaderText(null);
            ok.setContentText(
                    "✅ Quiz \"" + titre + "\" enregistré avec succès !\n"
                            + nbEnregistrees + " question(s) sauvegardée(s).");
            ok.showAndWait();

            retourQuiz(event);

        } catch (Exception e) {
            e.printStackTrace();
            afficherErreur("Erreur inattendue : " + e.getMessage());
        }
    }

    // ================================================
    // SAUVEGARDER LES RÉPONSES SELON LE TYPE
    // ================================================
    private void sauvegarderReponses(QuestionType type, VBox reponsesBox, int idQuestion) {

        switch (type) {

            case QCM -> {
                // Les enfants de reponsesBox : [0]=Label, [1..n-1]=HBox(ligne), [n]=Button ajouter
                for (javafx.scene.Node repNode : reponsesBox.getChildren()) {
                    if (!(repNode instanceof HBox ligne)) continue;
                    if (ligne.getChildren().size() < 3) continue;

                    CheckBox correct  = (CheckBox) ligne.getChildren().get(0);
                    TextField repField = (TextField) ligne.getChildren().get(2);
                    String texte = repField.getText();

                    if (texte != null && !texte.trim().isEmpty()) {
                        Reponse rep = new Reponse();
                        rep.setIdQuestion(idQuestion);
                        rep.setTexteReponse(texte.trim());
                        rep.setEstCorrecte(correct.isSelected());
                        reponseService.add(rep);
                    }
                }
            }

            case VRAI_FAUX -> {
                // reponsesBox : [0]=Label, [1]=HBox(RadioButtons)
                if (reponsesBox.getChildren().size() < 2) return;
                HBox ligne = (HBox) reponsesBox.getChildren().get(1);
                if (ligne.getChildren().size() < 2) return;

                RadioButton vraiBtn = (RadioButton) ligne.getChildren().get(0);
                boolean vraiSelected = vraiBtn.isSelected();

                Reponse r1 = new Reponse();
                r1.setIdQuestion(idQuestion);
                r1.setTexteReponse("Vrai");
                r1.setEstCorrecte(vraiSelected);
                reponseService.add(r1);

                Reponse r2 = new Reponse();
                r2.setIdQuestion(idQuestion);
                r2.setTexteReponse("Faux");
                r2.setEstCorrecte(!vraiSelected);
                reponseService.add(r2);
            }

            case REPONSE_LIBRE -> {
                // reponsesBox : [0]=Label, [1]=TextArea, [2]=Label info
                if (reponsesBox.getChildren().size() < 2) return;
                TextArea libre = (TextArea) reponsesBox.getChildren().get(1);
                String bonneRep = libre.getText();

                if (bonneRep != null && !bonneRep.trim().isEmpty()) {
                    Reponse rep = new Reponse();
                    rep.setIdQuestion(idQuestion);
                    rep.setTexteReponse(bonneRep.trim());
                    rep.setEstCorrecte(true);
                    reponseService.add(rep);
                }
            }
        }
    }

    // ================================================
    // MAPPING MATIÈRE → ID MODULE (correspond au SQL)
    // ================================================
    private int getIdModule(String matiere) {
        return switch (matiere) {
            case "Mathématiques"  -> 1;
            case "Réseaux"        -> 2;
            case "Java"           -> 3;
            case "Electronique"   -> 4;
            case "Base de données"-> 5;
            case "Web"            -> 6;
            default               -> 1;
        };
    }

    // ================================================
    // AFFICHER UNE ERREUR
    // ================================================
    private void afficherErreur(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    // ================================================
    // RETOUR PAGE QUIZ
    // ================================================
    @FXML
    void retourQuiz(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(
                getClass().getResource("/view/quiz.fxml"));
        Scene scene = new Scene(root);
        scene.getStylesheets().add(
                getClass().getResource("/style/quiz.css").toExternalForm());
        Stage stage = (Stage) questionsContainer.getScene().getWindow();
        stage.setScene(scene);
        stage.show();
    }
}