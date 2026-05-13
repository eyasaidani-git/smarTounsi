package com.esprit.eventapp.controllers;

import com.esprit.eventapp.models.Participation;
import com.esprit.eventapp.models.User;
import com.esprit.eventapp.services.ParticipationDAO;
import com.esprit.eventapp.utils.SessionManager;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.util.Callback;

import java.util.List;

public class ParticipationController {

    @FXML private TextField searchField;
    @FXML private ComboBox<String> statusFilter;
    @FXML private TableView<Participation> participationTable;
    @FXML private TableColumn<Participation, String> colUser;
    @FXML private TableColumn<Participation, String> colEvent;
    @FXML private TableColumn<Participation, java.sql.Timestamp> colDate;
    @FXML private TableColumn<Participation, String> colStatus;
    @FXML private TableColumn<Participation, Void> colActions;

    @FXML private HBox statsContainer;
    @FXML private Label lblTotalStats;
    @FXML private Label lblConfirmedStats;
    @FXML private Label lblWaitlistStats;
    @FXML private Label lblPresentStats;

    private final ParticipationDAO participationDAO = new ParticipationDAO();
    private final ObservableList<Participation> masterData = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        setupTable();
        setupFiltersAndBinding();
        loadData();
        checkAccess();
    }

    private void checkAccess() {
        User currentUser = SessionManager.getInstance().getCurrentUser();
        if (currentUser == null) return;

        boolean isAdmin = "ADMIN".equalsIgnoreCase(currentUser.getRole());
        
        // On vérifie si dans la liste chargée, il y a des événements que l'utilisateur a créés
        // Si oui, il est considéré comme organisateur pour cette vue.
        boolean isCreator = masterData.stream()
                .anyMatch(p -> {
                    // Pour savoir si c'est son événement, on peut regarder si on a accès à l'ID créateur
                    // Mais le plus simple ici est de voir si le DAO a retourné des lignes où il est propriétaire
                    // (La requête SQL filtre déjà cela)
                    return true; // Par défaut, si on arrive ici, le filtrage DAO a déjà fait le job
                });

        // Logique simplifiée : Si c'est un ADMIN, on montre. 
        // Si c'est un USER, on montre les stats UNIQUEMENT s'il y a des participations à SES événements.
        // On va vérifier si parmi les participations, il y en a où il n'est pas lui-même le participant (donc il est l'organisateur)
        boolean hasOrganizedEvents = masterData.stream()
                .anyMatch(p -> p.getIdUser() != currentUser.getId());

        if (!isAdmin && !hasOrganizedEvents) {
            statsContainer.setVisible(false);
            statsContainer.setManaged(false);
        } else {
            statsContainer.setVisible(true);
            statsContainer.setManaged(true);
        }
    }

    private void setupTable() {
        colUser.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getUserName()));
        colEvent.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getEventTitle()));
        colDate.setCellValueFactory(new PropertyValueFactory<>("dateParticipation"));
        
        colDate.setCellFactory(column -> new TableCell<Participation, java.sql.Timestamp>() {
            @Override
            protected void updateItem(java.sql.Timestamp item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) setText(null);
                else setText(item.toLocalDateTime().format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")));
            }
        });

        // Badge styling for Status
        colStatus.setCellValueFactory(new PropertyValueFactory<>("statut"));
        colStatus.setCellFactory(column -> new TableCell<Participation, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    Label badge = new Label(item.toUpperCase());
                    badge.setStyle("-fx-padding: 5 10; -fx-background-radius: 10; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 10px;");
                    switch (item.toLowerCase()) {
                        case "confirmée": badge.setStyle(badge.getStyle() + "-fx-background-color: #27ae60;"); break;
                        case "waitlist": badge.setStyle(badge.getStyle() + "-fx-background-color: #f39c12;"); break;
                        case "présent": badge.setStyle(badge.getStyle() + "-fx-background-color: #3498db;"); break;
                        case "annulée": badge.setStyle(badge.getStyle() + "-fx-background-color: #95a5a6;"); break;
                        case "absence": badge.setStyle(badge.getStyle() + "-fx-background-color: #e74c3c;"); break;
                    }
                    setGraphic(badge);
                    setText(null);
                }
            }
        });

        setupActionButtons();
    }

    private void setupActionButtons() {
        colActions.setCellFactory(param -> new TableCell<Participation, Void>() {
            private final Button btnCheckIn = new Button("✔");
            private final Button btnCancel = new Button("✖");
            private final HBox pane = new HBox(5, btnCheckIn, btnCancel);

            {
                btnCheckIn.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-cursor: hand;");
                btnCancel.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-cursor: hand;");
                
                btnCheckIn.setOnAction(event -> {
                    Participation p = getTableView().getItems().get(getIndex());
                    participationDAO.checkIn(p.getIdParticipation());
                    loadData();
                });

                btnCancel.setOnAction(event -> {
                    Participation p = getTableView().getItems().get(getIndex());
                    participationDAO.annulerParticipation(p.getIdParticipation(), p.getIdEvenement());
                    loadData();
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) setGraphic(null);
                else {
                    Participation p = getTableView().getItems().get(getIndex());
                    
                    // Seul l'admin ou le créateur de cet événement spécifique peut contrôler
                    User cur = SessionManager.getInstance().getCurrentUser();
                    if (cur == null) {
                        setGraphic(null);
                        return;
                    }

                    boolean isAdmin = "ADMIN".equalsIgnoreCase(cur.getRole());
                    
                    // On peut vérifier si cur.getId() est le créateur de p.getIdEvenement()
                    // Mais comme la liste est déjà filtrée pour ne montrer que ses événements (s'il n'est pas admin),
                    // on vérifie simplement s'il n'est pas lui-même le participant de cette ligne.
                    boolean isHisEvent = p.getIdUser() != cur.getId();

                    if (isAdmin || isHisEvent) {
                        btnCheckIn.setDisable(p.getStatut().equals("présent") || p.getStatut().equals("annulée"));
                        btnCancel.setDisable(p.getStatut().equals("annulée"));
                        setGraphic(pane);
                    } else {
                        setGraphic(null);
                    }
                }
            }
        });
    }

    private void setupFiltersAndBinding() {
        statusFilter.setItems(FXCollections.observableArrayList("Tous", "Confirmée", "Waitlist", "Présent", "Absence", "Annulée"));
        statusFilter.setValue("Tous");

        FilteredList<Participation> filteredData = new FilteredList<>(masterData, p -> true);

        searchField.textProperty().addListener((o, old, newValue) -> applyFilters(filteredData));
        statusFilter.valueProperty().addListener((o, old, newValue) -> applyFilters(filteredData));

        SortedList<Participation> sortedData = new SortedList<>(filteredData);
        sortedData.comparatorProperty().bind(participationTable.comparatorProperty());
        participationTable.setItems(sortedData);
    }

    private void applyFilters(FilteredList<Participation> filteredData) {
        filteredData.setPredicate(p -> {
            String search = searchField.getText().toLowerCase();
            String status = statusFilter.getValue();

            boolean matchesSearch = search.isEmpty() || 
                    p.getUserName().toLowerCase().contains(search) || 
                    p.getEventTitle().toLowerCase().contains(search);

            boolean matchesStatus = status.equals("Tous") || p.getStatut().equalsIgnoreCase(status);

            return matchesSearch && matchesStatus;
        });
    }

    private void loadData() {
        User cur = SessionManager.getInstance().getCurrentUser();
        if (cur == null) return;

        List<Participation> list = participationDAO.afficherParticipationsParFiltre(cur.getId(), cur.getRole());
        masterData.setAll(list);
        updateStats();
    }

    private void updateStats() {
        // Stats calculées localement sur les données affichées
        long total = masterData.size();
        long confirmed = masterData.stream().filter(p -> p.getStatut().equals("confirmée")).count();
        long waitlist = masterData.stream().filter(p -> p.getStatut().equals("waitlist")).count();
        long present = masterData.stream().filter(p -> p.getStatut().equals("présent")).count();
        
        lblTotalStats.setText(String.valueOf(total));
        lblConfirmedStats.setText(String.valueOf(confirmed));
        lblWaitlistStats.setText(String.valueOf(waitlist));
        lblPresentStats.setText(String.valueOf(present));
    }

    @FXML
    private void handleExport() {
        javafx.stage.FileChooser fileChooser = new javafx.stage.FileChooser();
        fileChooser.setTitle("Exporter les participations");
        fileChooser.getExtensionFilters().add(new javafx.stage.FileChooser.ExtensionFilter("Fichier CSV", "*.csv"));
        fileChooser.setInitialFileName("participations_export.csv");
        
        java.io.File file = fileChooser.showSaveDialog(participationTable.getScene().getWindow());
        
        if (file != null) {
            try (java.io.PrintWriter writer = new java.io.PrintWriter(file)) {
                // Header
                writer.println("Utilisateur;Evenement;Date;Statut");
                
                for (Participation p : participationTable.getItems()) {
                    writer.println(String.format("%s;%s;%s;%s",
                            p.getUserName(),
                            p.getEventTitle(),
                            p.getDateParticipation(),
                            p.getStatut()
                    ));
                }
                
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Exportation réussie");
                alert.setHeaderText(null);
                alert.setContentText("Les données ont été exportées avec succès vers :\n" + file.getAbsolutePath());
                alert.showAndWait();
                
            } catch (java.io.IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    @FXML
    private void refreshTable() {
        loadData();
    }
}
