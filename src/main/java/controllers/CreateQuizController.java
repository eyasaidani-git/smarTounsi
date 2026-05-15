package controllers;

import enums.QuestionType;
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
import models.Question;
import models.Quiz;
import models.Reponse;
import models.Utilisateur;
import services.QuestionService;
import services.QuizService;
import services.ReponseService;
import util.DBConnection;
import util.Session;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ResourceBundle;


public class CreateQuizController implements Initializable {

    @FXML private VBox               questionsContainer;
    @FXML private ComboBox<String>   matiereQuiz;
    @FXML private TextField          titreQuiz;
    @FXML private TextArea           descriptionQuiz;
    @FXML private Spinner<Integer>   tempsLimiteSpinner;  // ← valeur lue pour le timer
    @FXML private Label              compteurLabel;

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
                "Mathématiques", "Java", "Réseaux",
                "Electronique", "Base de données", "Web");

        // Initialiser le Spinner temps limite
        if (tempsLimiteSpinner != null) {
            SpinnerValueFactory<Integer> factory =
                    new SpinnerValueFactory.IntegerSpinnerValueFactory(5, 180, 30);
            tempsLimiteSpinner.setValueFactory(factory);
            tempsLimiteSpinner.setEditable(true);
        }

        ajouterBlocQuestion();
    }


    @FXML
    void ajouterQuestion(ActionEvent event) {
        if (questionNumber <= 20) {
            ajouterBlocQuestion();
        } else {
            afficherErreur("Maximum 20 questions par quiz.");
        }
    }

    private void ajouterBlocQuestion() {

        VBox card = new VBox(10);
        card.getStyleClass().add("quiz-card");
        card.setStyle(
                "-fx-padding:16; -fx-background-color:white; " +
                        "-fx-background-radius:12; -fx-border-color:#E0E3E8; " +
                        "-fx-border-width:1; -fx-border-radius:12;");

        // ---- Header : numéro + supprimer ----
        HBox header = new HBox(10);
        header.setAlignment(Pos.CENTER_LEFT);

        Label title = new Label("Question " + questionNumber);
        title.setStyle("-fx-font-size:14px; -fx-font-weight:bold; -fx-text-fill:#023047;");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button btnSup = new Button("🗑 Supprimer");
        btnSup.setStyle(
                "-fx-background-color:#E74C3C; -fx-text-fill:white; " +
                        "-fx-background-radius:8; -fx-font-size:11px; -fx-cursor:hand; -fx-padding:6 12;");
        btnSup.setOnAction(e -> {
            questionsContainer.getChildren().remove(card);
            renumeroterQuestions();
        });

        header.getChildren().addAll(title, spacer, btnSup);

        // ---- Enoncé ----
        TextField questionField = new TextField();
        questionField.setPromptText("Écrire la question ici...");
        questionField.setStyle(
                "-fx-background-radius:8; -fx-border-color:#D5D8E0; " +
                        "-fx-border-radius:8; -fx-padding:9; -fx-font-size:13px;");

        // ---- Type de réponse ----
        Label typeLabel = new Label("Type de réponse *");
        typeLabel.setStyle("-fx-font-size:12px; -fx-font-weight:bold; -fx-text-fill:#023047;");

        ComboBox<String> typeReponse = new ComboBox<>();
        typeReponse.getItems().addAll("QCM", "Vrai / Faux", "Réponse Libre");
        typeReponse.setPromptText("Choisir le type...");
        typeReponse.setMaxWidth(Double.MAX_VALUE);
        typeReponse.setStyle("-fx-background-radius:8; -fx-border-color:#D5D8E0; -fx-border-radius:8;");

        // ---- Zone réponses ----
        VBox reponsesBox = new VBox(10);

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
    // ZONE RÉPONSES SELON LE TYPE
    // ================================================
    private void buildReponsesZone(String type, VBox reponsesBox) {
        if (type == null) return;

        switch (type) {

            case "QCM" -> {
                Label lbl = new Label("Cochez ✔ la (les) bonne(s) réponse(s) :");
                lbl.setStyle("-fx-font-size:12px; -fx-text-fill:#555870;");
                reponsesBox.getChildren().add(lbl);

                String[] lettres = {"A", "B", "C", "D"};
                for (int i = 0; i < 4; i++) {
                    reponsesBox.getChildren().add(buildOptionRow(lettres[i]));
                }

                Button btnPlus = new Button("+ Ajouter une option");
                btnPlus.setStyle(
                        "-fx-background-color:transparent; -fx-text-fill:#229DBC; " +
                                "-fx-font-size:12px; -fx-cursor:hand;");
                btnPlus.setOnAction(e -> {
                    // nb de HBox déjà là (index 1..n, index 0 = label, dernier = bouton)
                    int n = (int) reponsesBox.getChildren().stream()
                            .filter(c -> c instanceof HBox).count();
                    String l = n < 26 ? String.valueOf((char)('A' + n)) : String.valueOf(n + 1);
                    reponsesBox.getChildren().add(
                            reponsesBox.getChildren().size() - 1,  // avant le bouton
                            buildOptionRow(l));
                });
                reponsesBox.getChildren().add(btnPlus);
            }

            case "Vrai / Faux" -> {
                Label lbl = new Label("Sélectionnez la bonne réponse :");
                lbl.setStyle("-fx-font-size:12px; -fx-text-fill:#555870;");

                ToggleGroup tg   = new ToggleGroup();
                RadioButton vrai = new RadioButton("✓  Vrai");
                RadioButton faux = new RadioButton("✗  Faux");
                vrai.setToggleGroup(tg);
                faux.setToggleGroup(tg);
                vrai.setStyle("-fx-font-size:14px; -fx-text-fill:#1A5E35; -fx-font-weight:bold;");
                faux.setStyle("-fx-font-size:14px; -fx-text-fill:#922B21; -fx-font-weight:bold;");

                HBox ligne = new HBox(30, vrai, faux);
                ligne.setAlignment(Pos.CENTER_LEFT);
                reponsesBox.getChildren().addAll(lbl, ligne);
            }

            case "Réponse Libre" -> {
                Label lbl = new Label("Réponse correcte de référence (optionnel) :");
                lbl.setStyle("-fx-font-size:12px; -fx-text-fill:#555870;");

                TextArea libre = new TextArea();
                libre.setPromptText("Entrez la réponse attendue...");
                libre.setPrefHeight(80);
                libre.setWrapText(true);
                libre.setStyle(
                        "-fx-background-radius:8; -fx-border-color:#D5D8E0; " +
                                "-fx-border-radius:8; -fx-font-size:13px;");

                Label info = new Label("ℹ️ Les réponses libres sont évaluées par le professeur.");
                info.setStyle("-fx-font-size:11px; -fx-text-fill:#229DBC;");

                reponsesBox.getChildren().addAll(lbl, libre, info);
            }
        }
    }

    private HBox buildOptionRow(String lettre) {
        HBox ligne = new HBox(10);
        ligne.setAlignment(Pos.CENTER_LEFT);

        CheckBox correct = new CheckBox();
        correct.setTooltip(new Tooltip("Cocher = bonne réponse"));

        Label lLet = new Label(lettre);
        lLet.setStyle("-fx-font-weight:bold; -fx-text-fill:#023047; -fx-min-width:18px;");

        TextField rep = new TextField();
        rep.setPromptText("Réponse " + lettre + "...");
        rep.setStyle(
                "-fx-background-radius:8; -fx-border-color:#D5D8E0; " +
                        "-fx-border-radius:8; -fx-padding:8; -fx-font-size:13px;");
        HBox.setHgrow(rep, Priority.ALWAYS);

        ligne.getChildren().addAll(correct, lLet, rep);
        return ligne;
    }

    // ================================================
    // RENUMÉROTER APRÈS SUPPRESSION
    // ================================================
    private void renumeroterQuestions() {
        int num = 1;
        for (javafx.scene.Node node : questionsContainer.getChildren()) {
            if (!(node instanceof VBox card)) continue;
            if (card.getChildren().isEmpty()) continue;
            if (!(card.getChildren().get(0) instanceof HBox header)) continue;
            if (header.getChildren().isEmpty()) continue;
            if (header.getChildren().get(0) instanceof Label lbl) {
                lbl.setText("Question " + num++);
            }
        }
        questionNumber = num;
        majCompteur();
    }

    private void majCompteur() {
        if (compteurLabel != null)
            compteurLabel.setText((questionNumber - 1) + " question(s)");
    }

    // ================================================
    // ENREGISTRER LE QUIZ  ← CORRECTION TIMER ICI
    // ================================================
    @FXML
    void enregistrerQuiz(ActionEvent event) {
        try {
            // Validation
            String titre = titreQuiz.getText() == null ? "" : titreQuiz.getText().trim();
            if (titre.isEmpty()) { afficherErreur("Veuillez saisir le titre."); return; }

            String matiere = matiereQuiz.getValue();
            if (matiere == null) { afficherErreur("Veuillez choisir une matière."); return; }

            Utilisateur currentUser = Session.getCurrentUser();
            if (currentUser == null || currentUser.getId() <= 0) {
                afficherErreur("Utilisateur non connecté. Veuillez vous reconnecter.");
                return;
            }

            if (questionsContainer.getChildren().isEmpty()) {
                afficherErreur("Ajoutez au moins une question."); return;
            }

            // ✅ CORRECTION : lire la vraie valeur du Spinner
            int tempsLimite = 30; // valeur par défaut de sécurité
            if (tempsLimiteSpinner != null && tempsLimiteSpinner.getValue() != null) {
                tempsLimite = tempsLimiteSpinner.getValue();
            }

            int idModule = getIdModule(matiere, currentUser.getId());
            int nbQTotal = questionsContainer.getChildren().size();

            // Créer le Quiz
            Quiz quiz = new Quiz();
            quiz.setTitre(titre);
            quiz.setDescription(
                    descriptionQuiz.getText() == null ? "" : descriptionQuiz.getText().trim());
            quiz.setIdModule(idModule);
            quiz.setIdCreateur(currentUser.getId());
            quiz.setTempsLimite(tempsLimite);  // ← ici la vraie valeur
            quiz.setScoreTotal(nbQTotal);
            quiz.setEstActif(true);

            quizService.add(quiz);

            if (quiz.getId() == 0) {
                afficherErreur("❌ Échec de la sauvegarde du quiz."); return;
            }

            int idQuiz = quiz.getId();
            int ordre  = 1;
            int nbSauvees = 0;

            // Parcourir les questions
            for (javafx.scene.Node node : questionsContainer.getChildren()) {
                if (!(node instanceof VBox card)) continue;
                // structure : [0]=HBox(header), [1]=TextField, [2]=Label, [3]=ComboBox, [4]=VBox
                if (card.getChildren().size() < 5) continue;

                TextField questionField = (TextField)  card.getChildren().get(1);
                ComboBox<String> typeBox = (ComboBox<String>) card.getChildren().get(3);
                VBox reponsesBox        = (VBox)       card.getChildren().get(4);

                String enonce    = questionField.getText();
                String typeChoisi = typeBox.getValue();

                if (enonce == null || enonce.trim().isEmpty()) continue;
                if (typeChoisi == null) continue;

                QuestionType qt = switch (typeChoisi) {
                    case "QCM"          -> QuestionType.QCM;
                    case "Vrai / Faux"  -> QuestionType.VRAI_FAUX;
                    default             -> QuestionType.REPONSE_LIBRE;
                };

                Question question = new Question();
                question.setIdQuiz(idQuiz);
                question.setEnonce(enonce.trim());
                question.setTypeQuestion(qt);
                question.setPoints(1);
                question.setOrdre(ordre);
                questionService.add(question);

                if (question.getId() == 0) continue;

                sauvegarderReponses(qt, reponsesBox, question.getId());
                ordre++;
                nbSauvees++;
            }

            // ✅ Mettre à jour scoreTotal avec le vrai nombre de questions sauvées
            quiz.setScoreTotal(nbSauvees);
            quizService.update(quiz);

            Alert ok = new Alert(Alert.AlertType.INFORMATION);
            ok.setHeaderText(null);
            ok.setContentText(
                    "✅ Quiz \"" + titre + "\" enregistré !\n"
                            + nbSauvees + " question(s) | Temps : " + tempsLimite + " min");
            ok.showAndWait();

            retourQuiz(event);

        } catch (Exception e) {
            e.printStackTrace();
            afficherErreur("Erreur inattendue : " + e.getMessage());
        }
    }

    // ================================================
    // SAUVEGARDER LES RÉPONSES
    // ================================================
    private void sauvegarderReponses(QuestionType type, VBox reponsesBox, int idQuestion) {
        switch (type) {

            case QCM -> {
                for (javafx.scene.Node n : reponsesBox.getChildren()) {
                    if (!(n instanceof HBox ligne)) continue;
                    if (ligne.getChildren().size() < 3) continue;
                    CheckBox  cb    = (CheckBox)  ligne.getChildren().get(0);
                    TextField tf    = (TextField) ligne.getChildren().get(2);
                    String    texte = tf.getText();
                    if (texte != null && !texte.trim().isEmpty()) {
                        Reponse r = new Reponse();
                        r.setIdQuestion(idQuestion);
                        r.setTexteReponse(texte.trim());
                        r.setEstCorrecte(cb.isSelected());
                        reponseService.add(r);
                    }
                }
            }

            case VRAI_FAUX -> {
                // reponsesBox: [0]=Label, [1]=HBox(RadioButtons)
                if (reponsesBox.getChildren().size() < 2) return;
                HBox ligne = (HBox) reponsesBox.getChildren().get(1);
                if (ligne.getChildren().size() < 2) return;
                RadioButton vraiBtn = (RadioButton) ligne.getChildren().get(0);
                boolean vraiOk = vraiBtn.isSelected();

                Reponse r1 = new Reponse();
                r1.setIdQuestion(idQuestion);
                r1.setTexteReponse("Vrai");
                r1.setEstCorrecte(vraiOk);
                reponseService.add(r1);

                Reponse r2 = new Reponse();
                r2.setIdQuestion(idQuestion);
                r2.setTexteReponse("Faux");
                r2.setEstCorrecte(!vraiOk);
                reponseService.add(r2);
            }

            case REPONSE_LIBRE -> {
                // reponsesBox: [0]=Label, [1]=TextArea, [2]=Label info
                if (reponsesBox.getChildren().size() < 2) return;
                TextArea ta = (TextArea) reponsesBox.getChildren().get(1);
                String texte = ta.getText();
                if (texte != null && !texte.trim().isEmpty()) {
                    Reponse r = new Reponse();
                    r.setIdQuestion(idQuestion);
                    r.setTexteReponse(texte.trim());
                    r.setEstCorrecte(true);
                    reponseService.add(r);
                }
            }
        }
    }

    // ================================================
    // HELPERS
    // ================================================
    private int getIdModule(String matiere, int idCreateur) {
        if (matiere == null || matiere.isBlank()) {
            throw new RuntimeException("Matière invalide.");
        }

        String selectSql = "SELECT id_module FROM modules WHERE LOWER(nom_module) = LOWER(?) LIMIT 1";

        Connection conn = DBConnection.getInstance().getConn();

        try (PreparedStatement ps = conn.prepareStatement(selectSql)) {

            ps.setString(1, matiere.trim());

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("id_module");
                }
            }

        } catch (Exception e) {
            throw new RuntimeException("Erreur recherche module : " + e.getMessage());
        }

        String insertSql = "INSERT INTO modules (nom_module, description, id_createur) VALUES (?, ?, ?)";

        try (PreparedStatement ps = conn.prepareStatement(insertSql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, matiere.trim());
            ps.setString(2, "Module créé automatiquement depuis la création du quiz.");
            ps.setInt(3, idCreateur);
            ps.executeUpdate();

            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    return keys.getInt(1);
                }
            }

        } catch (Exception e) {
            throw new RuntimeException("Erreur création module : " + e.getMessage());
        }

        throw new RuntimeException("Impossible de créer ou récupérer le module.");
    }

    private void afficherErreur(String msg) {
        Alert a = new Alert(Alert.AlertType.ERROR);
        a.setHeaderText(null);
        a.setContentText(msg);
        a.showAndWait();
    }

    @FXML
    void retourQuiz(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("/quiz.fxml"));
        Scene scene = new Scene(root);
        scene.getStylesheets().add(
                getClass().getResource("/styles/quiz.css").toExternalForm());
        Stage stage = (Stage) questionsContainer.getScene().getWindow();
        stage.setScene(scene);
        stage.show();
    }
}
