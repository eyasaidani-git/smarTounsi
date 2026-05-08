package controleurs;

import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import models.Planning;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class PlanningController {

    @FXML
    private Label monthLabel;

    @FXML
    private Label selectedDateBadge;

    @FXML
    private Label todoCounterLabel;

    @FXML
    private Label fileNameLabel;

    @FXML
    private GridPane calendarGrid;

    @FXML
    private VBox todoListBox;

    @FXML
    private VBox emptyBox;

    @FXML
    private VBox dropZone;

    @FXML
    private TextField titleField;

    @FXML
    private TextField timeField;

    @FXML
    private TextField moduleField;

    @FXML
    private ComboBox<String> typeComboBox;

    private YearMonth currentMonth;
    private LocalDate selectedDate;
    private File selectedFile;

    private final List<Planning> planningList = new ArrayList<>();

    @FXML
    public void initialize() {
        currentMonth = YearMonth.now();
        selectedDate = LocalDate.now();

        typeComboBox.getItems().addAll(
                "Révision",
                "Examen",
                "Projet",
                "Document",
                "Cours",
                "Autre"
        );
        typeComboBox.setValue("Révision");

        selectedDateBadge.setText(formatDate(selectedDate));
        fileNameLabel.setText("Aucun fichier sélectionné");

        configureDragAndDrop();
        generateCalendar();
        refreshTodoList();
    }

    @FXML
    private void goPreviousMonth() {
        currentMonth = currentMonth.minusMonths(1);
        generateCalendar();
    }

    @FXML
    private void goNextMonth() {
        currentMonth = currentMonth.plusMonths(1);
        generateCalendar();
    }

    @FXML
    private void chooseFile() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choisir un document");

        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Documents", "*.pdf", "*.doc", "*.docx", "*.ppt", "*.pptx", "*.xls", "*.xlsx"),
                new FileChooser.ExtensionFilter("Images", "*.png", "*.jpg", "*.jpeg"),
                new FileChooser.ExtensionFilter("Vidéos", "*.mp4", "*.avi", "*.mkv"),
                new FileChooser.ExtensionFilter("Tous les fichiers", "*.*")
        );

        File file = fileChooser.showOpenDialog(null);

        if (file != null) {
            selectedFile = file;
            fileNameLabel.setText(file.getName());
        }
    }

    @FXML
    private void clearFile() {
        selectedFile = null;
        fileNameLabel.setText("Aucun fichier sélectionné");
    }

    @FXML
    private void addPlanning() {
        String titre = titleField.getText().trim();
        String type = typeComboBox.getValue();
        String heure = timeField.getText().trim();
        String module = moduleField.getText().trim();

        if (titre.isEmpty()) {
            showAlert("Champ obligatoire", "Veuillez saisir un titre ou un document.");
            return;
        }

        if (heure.isEmpty()) {
            heure = "Non défini";
        }

        if (module.isEmpty()) {
            module = "Non défini";
        }

        String nomFichier = null;
        String cheminFichier = null;

        if (selectedFile != null) {
            nomFichier = selectedFile.getName();
            cheminFichier = selectedFile.getAbsolutePath();
        }

        Planning planning = new Planning(
                1,
                selectedDate,
                titre,
                type,
                heure,
                module,
                nomFichier,
                cheminFichier
        );

        planningList.add(planning);

        clearForm();
        generateCalendar();
        refreshTodoList();
    }

    private void configureDragAndDrop() {
        dropZone.setOnDragOver(event -> {
            if (event.getGestureSource() != dropZone && event.getDragboard().hasFiles()) {
                event.acceptTransferModes(TransferMode.COPY);
            }
            event.consume();
        });

        dropZone.setOnDragEntered(event -> {
            if (event.getDragboard().hasFiles()) {
                dropZone.getStyleClass().add("drop-zone-active");
            }
            event.consume();
        });

        dropZone.setOnDragExited(event -> {
            dropZone.getStyleClass().remove("drop-zone-active");
            event.consume();
        });

        dropZone.setOnDragDropped(event -> {
            Dragboard dragboard = event.getDragboard();
            boolean success = false;

            if (dragboard.hasFiles()) {
                selectedFile = dragboard.getFiles().get(0);
                fileNameLabel.setText(selectedFile.getName());
                success = true;
            }

            event.setDropCompleted(success);
            event.consume();
        });
    }

    private void generateCalendar() {
        calendarGrid.getChildren().clear();

        String[] days = {"Lun", "Mar", "Mer", "Jeu", "Ven", "Sam", "Dim"};

        for (int i = 0; i < days.length; i++) {
            Label dayLabel = new Label(days[i]);
            dayLabel.getStyleClass().add("calendar-day-name");
            dayLabel.setMaxWidth(Double.MAX_VALUE);
            dayLabel.setAlignment(Pos.CENTER);
            calendarGrid.add(dayLabel, i, 0);
        }

        String monthName = currentMonth.getMonth()
                .getDisplayName(TextStyle.FULL, Locale.FRENCH);

        monthLabel.setText(capitalize(monthName) + " " + currentMonth.getYear());

        LocalDate firstDay = currentMonth.atDay(1);
        int firstDayColumn = firstDay.getDayOfWeek().getValue() - 1;
        int daysInMonth = currentMonth.lengthOfMonth();

        int row = 1;
        int column = firstDayColumn;

        for (int day = 1; day <= daysInMonth; day++) {
            LocalDate date = currentMonth.atDay(day);

            VBox dayCell = createDayCell(date);
            calendarGrid.add(dayCell, column, row);

            column++;

            if (column == 7) {
                column = 0;
                row++;
            }
        }
    }

    private VBox createDayCell(LocalDate date) {
        VBox cell = new VBox(5);
        cell.setPadding(new Insets(8));
        cell.setMinHeight(90);
        cell.setAlignment(Pos.TOP_LEFT);
        cell.getStyleClass().add("calendar-cell");

        if (date.equals(LocalDate.now())) {
            cell.getStyleClass().add("today-cell");
        }

        if (date.equals(selectedDate)) {
            cell.getStyleClass().add("selected-cell");
        }

        Label dayNumber = new Label(String.valueOf(date.getDayOfMonth()));
        dayNumber.getStyleClass().add("day-number");

        long count = planningList.stream()
                .filter(p -> p.getDateRevision().equals(date))
                .count();

        Label countLabel = new Label();

        if (count > 0) {
            countLabel.setText(count + " tâche(s)");
            countLabel.getStyleClass().add("task-count");
        }

        cell.getChildren().add(dayNumber);

        if (count > 0) {
            cell.getChildren().add(countLabel);
        }

        cell.setOnMouseClicked(event -> {
            selectedDate = date;
            selectedDateBadge.setText(formatDate(selectedDate));
            generateCalendar();
            refreshTodoList();
        });

        return cell;
    }

    private void refreshTodoList() {
        todoListBox.getChildren().clear();

        List<Planning> tasksForSelectedDay = planningList.stream()
                .filter(p -> p.getDateRevision().equals(selectedDate))
                .toList();

        todoCounterLabel.setText(tasksForSelectedDay.size() + " élément(s)");

        if (tasksForSelectedDay.isEmpty()) {
            emptyBox.setVisible(true);
            emptyBox.setManaged(true);
            return;
        }

        emptyBox.setVisible(false);
        emptyBox.setManaged(false);

        for (Planning planning : tasksForSelectedDay) {
            VBox card = createTodoCard(planning);
            todoListBox.getChildren().add(card);
        }
    }

    private VBox createTodoCard(Planning planning) {
        VBox card = new VBox(6);
        card.setPadding(new Insets(12));
        card.getStyleClass().add("todo-card");

        HBox topLine = new HBox(8);
        topLine.setAlignment(Pos.CENTER_LEFT);

        Label typeLabel = new Label(planning.getType());
        typeLabel.getStyleClass().add("type-badge");

        Label timeLabel = new Label(planning.getHeure());
        timeLabel.getStyleClass().add("time-label");

        topLine.getChildren().addAll(typeLabel, timeLabel);

        Label titleLabel = new Label(planning.getTitre());
        titleLabel.getStyleClass().add("todo-title");

        Label moduleLabel = new Label("Module : " + planning.getModule());
        moduleLabel.getStyleClass().add("todo-module");

        card.getChildren().addAll(topLine, titleLabel, moduleLabel);

        if (planning.getNomFichier() != null && planning.getCheminFichier() != null) {
            HBox fileLine = new HBox(8);
            fileLine.setAlignment(Pos.CENTER_LEFT);

            Label fileLabel = new Label("📎 " + planning.getNomFichier());
            fileLabel.getStyleClass().add("file-label");

            Button openButton = new Button("Ouvrir");
            openButton.getStyleClass().add("small-button");
            openButton.setOnAction(event -> openFile(planning.getCheminFichier()));

            fileLine.getChildren().addAll(fileLabel, openButton);
            card.getChildren().add(fileLine);
        }

        return card;
    }

    private void openFile(String path) {
        try {
            File file = new File(path);

            if (file.exists()) {
                Desktop.getDesktop().open(file);
            } else {
                showAlert("Fichier introuvable", "Le fichier attaché n'existe plus dans cet emplacement.");
            }

        } catch (IOException e) {
            showAlert("Erreur", "Impossible d'ouvrir le fichier.");
        }
    }

    private void clearForm() {
        titleField.clear();
        timeField.clear();
        moduleField.clear();
        typeComboBox.setValue("Révision");
        selectedFile = null;
        fileNameLabel.setText("Aucun fichier sélectionné");
    }

    private String formatDate(LocalDate date) {
        String day = date.getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.FRENCH);
        String month = date.getMonth().getDisplayName(TextStyle.FULL, Locale.FRENCH);

        return capitalize(day) + " " + date.getDayOfMonth() + " " + capitalize(month) + " " + date.getYear();
    }

    private String capitalize(String text) {
        if (text == null || text.isEmpty()) {
            return text;
        }

        return text.substring(0, 1).toUpperCase() + text.substring(1);
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}