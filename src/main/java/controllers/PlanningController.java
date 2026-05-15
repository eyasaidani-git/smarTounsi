package controllers;

import javafx.animation.FadeTransition;
import javafx.animation.PauseTransition;
import javafx.animation.TranslateTransition;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.util.Duration;
import services.MailService;
import util.Navigator;
import util.Session;

import java.io.File;
import java.net.URL;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

public class PlanningController implements Initializable {

    @FXML private GridPane calendarGrid;
    @FXML private Label lblMonthYear;
    @FXML private Label lblSelectedDate;
    @FXML private Label lblCount;
    @FXML private VBox todoPanel;
    @FXML private VBox dropZone;
    @FXML private VBox vboxTodos;

    @FXML private TextField fieldTitre;
    @FXML private TextField fieldHeure;
    @FXML private TextArea fieldDescription;
    @FXML private ComboBox<String> comboType;
    @FXML private ListView<String> listFichiers;

    private YearMonth currentMonth;
    private LocalDate selectedDate;
    private String userEmail;

    private final Map<LocalDate, ObservableList<TodoItem>> todoMap = new HashMap<>();
    private final ObservableList<String> fichiersEnCours = FXCollections.observableArrayList();

    private static final DateTimeFormatter FR_DATE =
            DateTimeFormatter.ofPattern("d MMMM yyyy", Locale.FRENCH);
    private static final String[] JOURS_EN_TETE = {"Lun", "Mar", "Mer", "Jeu", "Ven", "Sam", "Dim"};

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        currentMonth = YearMonth.now();
        selectedDate = LocalDate.now();
        userEmail = resolveCurrentUserEmail();

        if (comboType.getItems().isEmpty()) {
            comboType.setItems(FXCollections.observableArrayList("Cours", "TD", "Examen", "DS"));
            comboType.setValue("Cours");
        }

        listFichiers.setItems(fichiersEnCours);
        listFichiers.setCellFactory(lv -> new FichierCell());

        buildCalendar();
        updateTodoPanel();
        refreshMailReminderScheduler();
    }

    private void buildCalendar() {
        calendarGrid.getChildren().clear();
        calendarGrid.getRowConstraints().clear();

        String moisFr = currentMonth.getMonth()
                .getDisplayName(java.time.format.TextStyle.FULL, Locale.FRENCH);
        moisFr = moisFr.substring(0, 1).toUpperCase() + moisFr.substring(1);
        lblMonthYear.setText(moisFr + " " + currentMonth.getYear());

        RowConstraints headerRow = new RowConstraints(30);
        calendarGrid.getRowConstraints().add(headerRow);
        for (int c = 0; c < 7; c++) {
            Label hdr = new Label(JOURS_EN_TETE[c]);
            hdr.getStyleClass().add("day-header");
            hdr.setMaxWidth(Double.MAX_VALUE);
            hdr.setAlignment(Pos.CENTER);
            calendarGrid.add(hdr, c, 0);
        }

        LocalDate premierJour = currentMonth.atDay(1);
        int depart = premierJour.getDayOfWeek().getValue() - 1;

        LocalDate today = LocalDate.now();
        int row = 1;
        int col = depart;

        LocalDate precedent = premierJour.minusDays(depart);
        for (int i = 0; i < depart; i++) {
            LocalDate d = precedent.plusDays(i);
            calendarGrid.add(createDayButton(d, true), i, row);
        }

        int nbJours = currentMonth.lengthOfMonth();
        for (int jour = 1; jour <= nbJours; jour++) {
            if (col > 6) {
                col = 0;
                row++;
            }
            LocalDate d = currentMonth.atDay(jour);
            Button btn = createDayButton(d, false);
            if (d.equals(today)) {
                btn.getStyleClass().add("day-btn-today");
            }
            if (d.equals(selectedDate)) {
                btn.getStyleClass().add("day-btn-selected");
            }
            calendarGrid.add(btn, col, row);
            col++;
        }

        LocalDate suivant = currentMonth.atEndOfMonth().plusDays(1);
        int extra = 0;
        while (col <= 6) {
            calendarGrid.add(createDayButton(suivant.plusDays(extra), true), col, row);
            col++;
            extra++;
        }

        int totalRows = row + 1;
        for (int r = 0; r < totalRows; r++) {
            if (calendarGrid.getRowConstraints().size() <= r) {
                RowConstraints rc = new RowConstraints();
                rc.setVgrow(Priority.ALWAYS);
                rc.setMinHeight(60);
                calendarGrid.getRowConstraints().add(rc);
            }
        }
    }

    private Button createDayButton(LocalDate date, boolean outsideMonth) {
        VBox content = new VBox(2);
        content.setPadding(new Insets(4, 6, 4, 6));

        Label numLabel = new Label(String.valueOf(date.getDayOfMonth()));
        numLabel.setStyle("-fx-font-size:13px; -fx-font-weight:bold;");
        content.getChildren().add(numLabel);

        ObservableList<TodoItem> items = todoMap.get(date);
        if (items != null && !items.isEmpty()) {
            for (int i = 0; i < Math.min(items.size(), 2); i++) {
                Label badge = new Label(items.get(i).getTitre());
                badge.getStyleClass().add("todo-badge");
                badge.getStyleClass().add(badgeClassForType(items.get(i).getType()));
                badge.setMaxWidth(Double.MAX_VALUE);
                content.getChildren().add(badge);
            }
            if (items.size() > 2) {
                Label more = new Label("+" + (items.size() - 2));
                more.getStyleClass().add("todo-badge");
                content.getChildren().add(more);
            }
        }

        Button btn = new Button();
        btn.setGraphic(content);
        btn.getStyleClass().add("day-btn");
        if (outsideMonth) {
            btn.getStyleClass().add("day-btn-outside");
        }
        btn.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        btn.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
        btn.setOnAction(e -> selectDate(date));
        return btn;
    }

    private String badgeClassForType(String type) {
        return switch (type.toLowerCase()) {
            case "examen" -> "todo-badge-exam";
            case "ds" -> "todo-badge-ds";
            case "td" -> "todo-badge-revision";
            default -> "";
        };
    }

    @FXML
    private void prevMonth() {
        currentMonth = currentMonth.minusMonths(1);
        buildCalendar();
    }

    @FXML
    private void nextMonth() {
        currentMonth = currentMonth.plusMonths(1);
        buildCalendar();
    }

    private void selectDate(LocalDate date) {
        selectedDate = date;
        lblSelectedDate.setText(date.format(FR_DATE));
        fichiersEnCours.clear();
        fieldTitre.clear();
        fieldHeure.setText("09:00 AM");
        fieldDescription.clear();
        comboType.setValue("Cours");
        buildCalendar();
        updateTodoPanel();

        FadeTransition ft = new FadeTransition(Duration.millis(200), todoPanel);
        ft.setFromValue(0.7);
        ft.setToValue(1.0);
        ft.play();
    }

    private void updateTodoPanel() {
        vboxTodos.getChildren().clear();
        ObservableList<TodoItem> items = todoMap.get(selectedDate);

        if (items == null || items.isEmpty()) {
            VBox empty = new VBox(4);
            empty.setAlignment(Pos.CENTER);
            Label star = new Label("Rien de planifie");
            star.getStyleClass().add("empty-label");
            Label hint = new Label("Ajoutez des elements ci-dessus");
            hint.setStyle("-fx-text-fill:#a0b8c8; -fx-font-size:11px;");
            empty.getChildren().addAll(star, hint);
            vboxTodos.getChildren().add(empty);
            lblCount.setText("0 elements");
            return;
        }

        lblCount.setText(items.size() + " element" + (items.size() > 1 ? "s" : ""));
        for (TodoItem item : items) {
            vboxTodos.getChildren().add(buildTodoCard(item));
        }
    }

    private VBox buildTodoCard(TodoItem item) {
        VBox card = new VBox(3);
        card.getStyleClass().addAll("todo-card", "todo-card-type-" + item.getType().toLowerCase());
        card.setPadding(new Insets(6, 10, 6, 10));

        HBox header = new HBox(6);
        header.setAlignment(Pos.CENTER_LEFT);

        Label typeBadge = new Label(item.getType());
        typeBadge.getStyleClass().add("todo-badge");
        typeBadge.getStyleClass().add(badgeClassForType(item.getType()));

        Label title = new Label(item.getTitre());
        title.getStyleClass().add("todo-card-title");
        title.setMaxWidth(180);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button btnDel = new Button("x");
        btnDel.setStyle("-fx-background-color:transparent; -fx-text-fill:#FB8402; "
                + "-fx-font-size:11px; -fx-cursor:hand; -fx-padding:0 2;");
        btnDel.setOnAction(e -> {
            todoMap.get(selectedDate).remove(item);
            buildCalendar();
            updateTodoPanel();
            refreshMailReminderScheduler();
        });

        header.getChildren().addAll(typeBadge, title, spacer, btnDel);

        Label meta = new Label(item.getHeure()
                + (item.getFichiers().isEmpty() ? "" : "  " + item.getFichiers().size() + " fichier(s)"));
        meta.getStyleClass().add("todo-card-meta");

        card.getChildren().addAll(header, meta);

        if (!item.getDescription().isBlank()) {
            Label desc = new Label(item.getDescription());
            desc.setStyle("-fx-text-fill:#556070; -fx-font-size:10px;");
            desc.setWrapText(true);
            card.getChildren().add(desc);
        }

        return card;
    }

    @FXML
    private void ajouterTodo() {
        String titre = fieldTitre.getText().trim();
        if (titre.isBlank()) {
            fieldTitre.setStyle("-fx-border-color: #FB8402;");
            shakeNode(fieldTitre);
            return;
        }
        fieldTitre.setStyle("");

        TodoItem item = new TodoItem(
                titre,
                comboType.getValue(),
                fieldHeure.getText().trim().isBlank() ? "09:00 AM" : fieldHeure.getText().trim(),
                fieldDescription.getText().trim(),
                new ArrayList<>(fichiersEnCours)
        );

        todoMap.computeIfAbsent(selectedDate, k -> FXCollections.observableArrayList()).add(item);

        fieldTitre.clear();
        fieldHeure.setText("09:00 AM");
        fieldDescription.clear();
        comboType.setValue("Cours");
        fichiersEnCours.clear();

        buildCalendar();
        updateTodoPanel();
        showToast("Ajoute pour le " + selectedDate.format(FR_DATE));
        refreshMailReminderScheduler();
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
        refreshMailReminderScheduler();
    }

    private String resolveCurrentUserEmail() {
        if (Session.getCurrentUser() == null) {
            return "";
        }

        String email = Session.getCurrentUser().getEmail();
        return email == null ? "" : email.trim();
    }

    private void refreshMailReminderScheduler() {
        Map<LocalDate, List<TodoItem>> reminderMap = new HashMap<>();
        for (Map.Entry<LocalDate, ObservableList<TodoItem>> entry : todoMap.entrySet()) {
            reminderMap.put(entry.getKey(), new ArrayList<>(entry.getValue()));
        }
        MailService.getInstance().startDailyCheck(reminderMap, userEmail);
    }

    @FXML
    private void parcourirFichier() {
        FileChooser fc = new FileChooser();
        fc.setTitle("Choisir un fichier");
        fc.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Tous les fichiers", "*.*"),
                new FileChooser.ExtensionFilter("PDF", "*.pdf"),
                new FileChooser.ExtensionFilter("Word", "*.docx", "*.doc"),
                new FileChooser.ExtensionFilter("PowerPoint", "*.pptx", "*.ppt"),
                new FileChooser.ExtensionFilter("Images", "*.png", "*.jpg", "*.jpeg"),
                new FileChooser.ExtensionFilter("Videos", "*.mp4", "*.avi", "*.mkv")
        );

        List<File> files = fc.showOpenMultipleDialog(dropZone.getScene().getWindow());
        if (files != null) {
            for (File f : files) {
                if (!fichiersEnCours.contains(f.getAbsolutePath())) {
                    fichiersEnCours.add(f.getAbsolutePath());
                }
            }
        }
    }

    @FXML
    private void handleDragOver(DragEvent event) {
        if (event.getDragboard().hasFiles() || event.getDragboard().hasUrl()) {
            event.acceptTransferModes(TransferMode.COPY);
            dropZone.getStyleClass().add("drop-zone-active");
        }
        event.consume();
    }

    @FXML
    private void handleDragDropped(DragEvent event) {
        Dragboard db = event.getDragboard();
        boolean ok = false;
        if (db.hasFiles()) {
            for (File f : db.getFiles()) {
                if (!fichiersEnCours.contains(f.getAbsolutePath())) {
                    fichiersEnCours.add(f.getAbsolutePath());
                }
            }
            ok = true;
        } else if (db.hasUrl()) {
            String url = db.getUrl();
            if (!fichiersEnCours.contains(url)) {
                fichiersEnCours.add(url);
            }
            ok = true;
        }
        dropZone.getStyleClass().remove("drop-zone-active");
        event.setDropCompleted(ok);
        event.consume();
    }

    private void showToast(String message) {
        Label toast = new Label(message);
        toast.getStyleClass().add("toast");

        if (todoPanel.getScene() != null) {
            StackPane root = (StackPane) todoPanel.getScene().lookup("#toastLayer");
            if (root != null) {
                root.getChildren().add(toast);
                StackPane.setAlignment(toast, Pos.BOTTOM_CENTER);
                StackPane.setMargin(toast, new Insets(0, 0, 20, 0));
                PauseTransition pause = new PauseTransition(Duration.seconds(2));
                pause.setOnFinished(e -> root.getChildren().remove(toast));
                pause.play();
                return;
            }
        }

        todoPanel.getChildren().add(toast);
        PauseTransition pause = new PauseTransition(Duration.seconds(2));
        pause.setOnFinished(e -> todoPanel.getChildren().remove(toast));
        pause.play();
    }

    private void shakeNode(javafx.scene.Node node) {
        TranslateTransition tt = new TranslateTransition(Duration.millis(60), node);
        tt.setByX(6);
        tt.setCycleCount(6);
        tt.setAutoReverse(true);
        tt.setOnFinished(e -> node.setTranslateX(0));
        tt.play();
    }

    @FXML private void handleDashboard(ActionEvent event) {
        Navigator.go((Node) event.getSource(), "/Acceuil.fxml", "Dashboard - SmarTounsi");
    }

    @FXML private void handleModules(ActionEvent event) {
        Navigator.go((Node) event.getSource(), "/Modules.fxml", "Modules - SmarTounsi");
    }

    @FXML private void handleDocuments(ActionEvent event) {
        Navigator.go((Node) event.getSource(), "/Document.fxml", "Documents - SmarTounsi");
    }

    @FXML private void handleUpload(ActionEvent event) {
        Navigator.go((Node) event.getSource(), "/UploadDocument.fxml", "Upload - SmarTounsi");
    }

    @FXML private void handlePlanning(ActionEvent event) {
        Navigator.go((Node) event.getSource(), "/Planning.fxml", "Planning - SmarTounsi");
    }

    @FXML private void handleFavoris(ActionEvent event) {
        Navigator.go((Node) event.getSource(), "/Favoris.fxml", "Favoris - SmarTounsi");
    }

    @FXML private void handleQuiz(ActionEvent event) {
        Navigator.go((Node) event.getSource(), "/quiz.fxml", "Quiz - SmarTounsi");
    }

    @FXML private void handleProjets(ActionEvent event) {
        Navigator.go((Node) event.getSource(), "/ProjectView.fxml", "Projets - SmarTounsi");
    }

    @FXML private void handleEvenements(ActionEvent event) {
        Navigator.go((Node) event.getSource(), "/Evenements.fxml", "Evenements - SmarTounsi");
    }

    @FXML private void handleProfil(ActionEvent event) {
        Navigator.go((Node) event.getSource(), "/Profil.fxml", "Profil - SmarTounsi");
    }

    @FXML private void handleNotifications(ActionEvent event) {
        Navigator.go((Node) event.getSource(), "/Notification.fxml", "Notifications - SmarTounsi");
    }

    @FXML private void handleDeconnexion(ActionEvent event) {
        Navigator.logout((Node) event.getSource());
    }

    public static class TodoItem {
        private final String titre;
        private final String type;
        private final String heure;
        private final String description;
        private final List<String> fichiers;

        public TodoItem(String titre, String type, String heure, String description, List<String> fichiers) {
            this.titre = titre;
            this.type = type;
            this.heure = heure;
            this.description = description;
            this.fichiers = fichiers;
        }

        public String getTitre() {
            return titre;
        }

        public String getType() {
            return type;
        }

        public String getHeure() {
            return heure;
        }

        public String getDescription() {
            return description;
        }

        public List<String> getFichiers() {
            return fichiers;
        }

        @Override
        public String toString() {
            return "[" + type + "] " + titre + " - " + heure;
        }
    }

    private class FichierCell extends ListCell<String> {
        @Override
        protected void updateItem(String path, boolean empty) {
            super.updateItem(path, empty);
            if (empty || path == null) {
                setText(null);
                setGraphic(null);
                return;
            }

            String icon = iconForPath(path);
            String label = path.length() > 38 ? "..." + path.substring(path.length() - 36) : path;

            HBox box = new HBox(6);
            box.setAlignment(Pos.CENTER_LEFT);
            Label ico = new Label(icon);
            Label lbl = new Label(label);
            lbl.setStyle("-fx-font-size:10px; -fx-text-fill:#023047;");

            Button del = new Button("x");
            del.setStyle("-fx-background-color:transparent; -fx-text-fill:#FB8402;"
                    + "-fx-font-size:9px; -fx-cursor:hand; -fx-padding:0;");
            del.setOnAction(e -> fichiersEnCours.remove(path));

            Region sp = new Region();
            HBox.setHgrow(sp, Priority.ALWAYS);
            box.getChildren().addAll(ico, lbl, sp, del);
            setGraphic(box);
            setText(null);
        }

        private String iconForPath(String p) {
            String low = p.toLowerCase();
            if (low.endsWith(".pdf")) {
                return "PDF";
            }
            if (low.endsWith(".docx") || low.endsWith(".doc")) {
                return "DOC";
            }
            if (low.endsWith(".pptx") || low.endsWith(".ppt")) {
                return "PPT";
            }
            if (low.endsWith(".mp4") || low.endsWith(".avi") || low.endsWith(".mkv")) {
                return "VID";
            }
            if (low.endsWith(".png") || low.endsWith(".jpg") || low.endsWith(".jpeg")) {
                return "IMG";
            }
            if (low.startsWith("http")) {
                return "URL";
            }
            return "FILE";
        }
    }
}
