package controllers;

import enums.DocumentStatut;
import enums.DocumentType;
import enums.EvenementType;
import enums.PlanningType;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.stage.FileChooser;
import models.Document;
import models.Evenement;
import models.MessagePrive;
import models.Module;
import models.ParticipationEvenement;
import models.Planning;
import models.Reclamation;
import models.Utilisateur;
import services.*;
import util.Navigator;
import util.Session;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.List;

public class AdminDashboardController {
    @FXML private BorderPane rootPane;
    @FXML private Label adminNameLabel;
    @FXML private Label statusLabel;

    @FXML private TableView<Module> modulesTable;
    @FXML private TextField moduleNomField;
    @FXML private TextArea moduleDescriptionArea;
    @FXML private TextField moduleCreateurIdField;

    @FXML private TableView<Document> documentsTable;
    @FXML private TextField docTitreField;
    @FXML private TextArea docDescriptionArea;
    @FXML private ComboBox<DocumentType> docTypeCombo;
    @FXML private TextField docFichierField;
    @FXML private TextField docModuleIdField;
    @FXML private TextField docUploaderIdField;
    @FXML private ComboBox<DocumentStatut> docStatutCombo;

    @FXML private TableView<Evenement> evenementsTable;
    @FXML private TextField eventTitreField;
    @FXML private TextArea eventDescriptionArea;
    @FXML private ComboBox<EvenementType> eventTypeCombo;
    @FXML private CheckBox eventOrganiseCheck;
    @FXML private TextField eventLieuField;
    @FXML private DatePicker eventDateDebutPicker;
    @FXML private TextField eventHeureDebutField;
    @FXML private DatePicker eventDateFinPicker;
    @FXML private TextField eventHeureFinField;
    @FXML private TextField eventTarifField;
    @FXML private TextField eventCapacityField;
    @FXML private ComboBox<String> eventStatutCombo;
    @FXML private TextField eventImageField;

    @FXML private TableView<Planning> planningTable;
    @FXML private TextField planningUserIdField;
    @FXML private TextField planningTitreField;
    @FXML private DatePicker planningDatePicker;
    @FXML private TextField planningHeureField;
    @FXML private ComboBox<PlanningType> planningTypeCombo;

    @FXML private TableView<Utilisateur> usersTable;

    @FXML private TableView<ParticipationEvenement> participantsTable;
    @FXML private ComboBox<String> participantEventCombo;

    @FXML private TableView<Reclamation> reclamationsTable;
    @FXML private TextArea reclamationResponseArea;

    @FXML private TableView<MessagePrive> messagesTable;
    @FXML private ComboBox<String> messageDestinataireCombo;
    @FXML private TextArea messageContentArea;

    private Utilisateur currentUser;
    private ModuleService moduleService;
    private DocumentService documentService;
    private EvenementService evenementService;
    private PlanningService planningService;
    private UtilisateurService utilisateurService;
    private ParticipationEvenementService participationService;
    private ReclamationService reclamationService;
    private MessagePriveService messagePriveService;

    @FXML
    private void initialize() {
        currentUser = Session.getCurrentUser();

        if (currentUser == null) {
            Navigator.go(rootPane, "/Connexion.fxml", "Connexion - SmarTounsi");
            return;
        }

        if (!isAdmin(currentUser)) {
            showAlert(Alert.AlertType.WARNING, "Accès refusé", "Cette interface est réservée à l'administrateur.");
            Navigator.go(rootPane, "/Profil.fxml", "Profil - SmarTounsi");
            return;
        }

        adminNameLabel.setText(labelUtilisateur(currentUser));
        initServices();
        initCombos();
        initTables();
        refreshAll();
    }

    private void initServices() {
        moduleService = new ModuleService();
        documentService = new DocumentService();
        evenementService = new EvenementService();
        planningService = new PlanningService();
        utilisateurService = new UtilisateurService();
        participationService = new ParticipationEvenementService();
        reclamationService = new ReclamationService();
        messagePriveService = new MessagePriveService();
    }

    private void initCombos() {
        docTypeCombo.setItems(FXCollections.observableArrayList(DocumentType.values()));
        docTypeCombo.setValue(DocumentType.COURS);
        docStatutCombo.setItems(FXCollections.observableArrayList(DocumentStatut.values()));
        docStatutCombo.setValue(DocumentStatut.EN_ATTENTE);

        eventTypeCombo.setItems(FXCollections.observableArrayList(EvenementType.values()));
        eventTypeCombo.setValue(EvenementType.AUTRE);
        eventStatutCombo.setItems(FXCollections.observableArrayList("a_venir", "complet", "annule", "termine"));
        eventStatutCombo.setValue("a_venir");

        planningTypeCombo.setItems(FXCollections.observableArrayList(PlanningType.values()));
        planningTypeCombo.setValue(PlanningType.REVISION);
    }

    private void initTables() {
        setupModulesTable();
        setupDocumentsTable();
        setupEvenementsTable();
        setupPlanningTable();
        setupUsersTable();
        setupParticipantsTable();
        setupReclamationsTable();
        setupMessagesTable();
    }

    private void setupModulesTable() {
        modulesTable.getColumns().setAll(
                intColumn("ID", Module::getId),
                textColumn("Nom", Module::getNomModule),
                textColumn("Description", Module::getDescription),
                textColumn("Créateur", m -> m.getIdCreateur() == null ? "" : String.valueOf(m.getIdCreateur()))
        );
        modulesTable.getSelectionModel().selectedItemProperty().addListener((obs, old, selected) -> fillModuleForm(selected));
    }

    private void setupDocumentsTable() {
        documentsTable.getColumns().setAll(
                intColumn("ID", Document::getId),
                textColumn("Titre", Document::getTitre),
                textColumn("Type", d -> d.getTypeDocument() == null ? "" : d.getTypeDocument().name()),
                intColumn("Module", Document::getIdModule),
                textColumn("Statut", d -> d.getStatut() == null ? "" : d.getStatut().name()),
                textColumn("Fichier", Document::getFichierUrl)
        );
        documentsTable.getSelectionModel().selectedItemProperty().addListener((obs, old, selected) -> fillDocumentForm(selected));
    }

    private void setupEvenementsTable() {
        evenementsTable.getColumns().setAll(
                intColumn("ID", Evenement::getId),
                textColumn("Titre", Evenement::getTitre),
                textColumn("Type", e -> e.getTypeEvenement() == null ? "" : e.getTypeEvenement().name()),
                textColumn("Lieu", Evenement::getLieu),
                textColumn("Début", e -> formatDate(e.getDateDebut())),
                textColumn("Statut", Evenement::getStatut),
                intColumn("Capacité", Evenement::getCapacity)
        );
        evenementsTable.getSelectionModel().selectedItemProperty().addListener((obs, old, selected) -> fillEvenementForm(selected));
    }

    private void setupPlanningTable() {
        planningTable.getColumns().setAll(
                intColumn("ID", Planning::getId),
                intColumn("Utilisateur", Planning::getIdUtilisateur),
                textColumn("Titre", Planning::getTitre),
                textColumn("Date", p -> formatDate(p.getDateRevision())),
                textColumn("Type", p -> p.getTypeActivite() == null ? "" : p.getTypeActivite().name())
        );
        planningTable.getSelectionModel().selectedItemProperty().addListener((obs, old, selected) -> fillPlanningForm(selected));
    }

    private void setupUsersTable() {
        usersTable.getColumns().setAll(
                intColumn("ID", Utilisateur::getId),
                textColumn("Nom", Utilisateur::getNomComplet),
                textColumn("Email", Utilisateur::getEmail),
                textColumn("Rôle", Utilisateur::getRole),
                textColumn("Filière", Utilisateur::getFiliere),
                textColumn("Actif", u -> u.isEstActif() ? "oui" : "non")
        );
    }

    private void setupParticipantsTable() {
        participantsTable.getColumns().setAll(
                intColumn("ID", ParticipationEvenement::getId),
                textColumn("Utilisateur", ParticipationEvenement::getNomUtilisateur),
                textColumn("Événement", ParticipationEvenement::getTitreEvenement),
                textColumn("Type", ParticipationEvenement::getTypeParticipation),
                textColumn("Statut", ParticipationEvenement::getStatut),
                textColumn("Paiement", p -> p.getMontantPaye() == null ? "0" : p.getMontantPaye().toPlainString())
        );
    }

    private void setupReclamationsTable() {
        reclamationsTable.getColumns().setAll(
                intColumn("ID", Reclamation::getId),
                textColumn("Utilisateur", Reclamation::getNomUtilisateur),
                textColumn("Sujet", Reclamation::getSujet),
                textColumn("Statut", Reclamation::getStatut),
                textColumn("Motif/Reponse", r -> firstNotBlank(r.getMotifRejet(), r.getReponseAdmin()))
        );
        reclamationsTable.getSelectionModel().selectedItemProperty().addListener((obs, old, selected) -> {
            reclamationResponseArea.setText(selected == null ? "" : safe(selected.getReponseAdmin()));
        });
    }

    private void setupMessagesTable() {
        messagesTable.getColumns().setAll(
                intColumn("ID", MessagePrive::getId),
                intColumn("De", MessagePrive::getIdExpediteur),
                intColumn("À", MessagePrive::getIdDestinataire),
                textColumn("Message", MessagePrive::getContenu),
                textColumn("Date", m -> formatDate(m.getDateEnvoi())),
                textColumn("Lu", m -> m.isEstLu() ? "oui" : "non")
        );
    }

    @FXML
    private void refreshAll() {
        refreshUsers();
        refreshModules();
        refreshDocuments();
        refreshEvenements();
        refreshPlanning();
        refreshParticipants();
        refreshReclamations();
        refreshMessages();
        setStatus("Données admin actualisées.");
    }

    private void refreshModules() {
        modulesTable.setItems(FXCollections.observableArrayList(moduleService.getAll()));
    }

    private void refreshDocuments() {
        documentsTable.setItems(FXCollections.observableArrayList(documentService.getAll()));
    }

    private void refreshEvenements() {
        List<Evenement> evenements = evenementService.getAll();
        evenementsTable.setItems(FXCollections.observableArrayList(evenements));

        participantEventCombo.getItems().clear();
        participantEventCombo.getItems().add("Tous les événements");
        for (Evenement e : evenements) {
            participantEventCombo.getItems().add(e.getId() + " - " + safe(e.getTitre()));
        }
        participantEventCombo.setValue("Tous les événements");
    }

    private void refreshPlanning() {
        planningTable.setItems(FXCollections.observableArrayList(planningService.getAll()));
    }

    private void refreshUsers() {
        List<Utilisateur> users = utilisateurService.getAll();
        usersTable.setItems(FXCollections.observableArrayList(users));

        messageDestinataireCombo.getItems().clear();
        for (Utilisateur u : users) {
            if (u.getId() != currentUser.getId()) {
                messageDestinataireCombo.getItems().add(u.getId() + " - " + labelUtilisateur(u));
            }
        }
    }

    private void refreshParticipants() {
        participantsTable.setItems(FXCollections.observableArrayList(participationService.getAll()));
    }

    private void refreshReclamations() {
        reclamationsTable.setItems(FXCollections.observableArrayList(reclamationService.getAll()));
    }

    private void refreshMessages() {
        messagesTable.setItems(FXCollections.observableArrayList(messagePriveService.getMessagesRecus(currentUser.getId())));
    }

    @FXML
    private void saveModule() {
        if (text(moduleNomField).isBlank()) {
            showAlert(Alert.AlertType.WARNING, "Module", "Le nom du module est obligatoire.");
            return;
        }

        Module selected = modulesTable.getSelectionModel().getSelectedItem();
        Module module = selected == null ? new Module() : selected;
        module.setNomModule(text(moduleNomField));
        module.setDescription(text(moduleDescriptionArea));
        module.setIdCreateur(parseNullableInt(text(moduleCreateurIdField), currentUser.getId()));

        if (selected == null) {
            moduleService.add(module);
        } else {
            moduleService.update(module);
        }

        clearModuleForm();
        refreshModules();
        setStatus("Module enregistré.");
    }

    @FXML
    private void deleteModule() {
        Module selected = modulesTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            return;
        }

        moduleService.delete(selected);
        clearModuleForm();
        refreshModules();
        setStatus("Module supprimé.");
    }

    @FXML
    private void newModule() {
        modulesTable.getSelectionModel().clearSelection();
        clearModuleForm();
    }

    @FXML
    private void saveDocument() {
        if (text(docTitreField).isBlank() || text(docModuleIdField).isBlank()) {
            showAlert(Alert.AlertType.WARNING, "Document", "Le titre et l'ID module sont obligatoires.");
            return;
        }

        Document selected = documentsTable.getSelectionModel().getSelectedItem();
        Document document = selected == null ? new Document() : selected;
        document.setTitre(text(docTitreField));
        document.setDescription(text(docDescriptionArea));
        document.setTypeDocument(docTypeCombo.getValue() == null ? DocumentType.COURS : docTypeCombo.getValue());
        document.setFichierUrl(text(docFichierField));
        document.setIdModule(parseRequiredInt(text(docModuleIdField), "ID module"));
        document.setIdUploadeur(parseNullableInt(text(docUploaderIdField), currentUser.getId()));
        document.setStatut(docStatutCombo.getValue() == null ? DocumentStatut.EN_ATTENTE : docStatutCombo.getValue());

        if (selected == null) {
            documentService.add(document);
        } else {
            documentService.update(document);
        }

        clearDocumentForm();
        refreshDocuments();
        setStatus("Document enregistré.");
    }

    @FXML
    private void deleteDocument() {
        Document selected = documentsTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            return;
        }

        documentService.delete(selected);
        clearDocumentForm();
        refreshDocuments();
        setStatus("Document supprimé.");
    }

    @FXML
    private void approveDocument() {
        Document selected = documentsTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            return;
        }

        documentService.approuver(selected.getId(), currentUser.getId());
        refreshDocuments();
        setStatus("Document approuvé.");
    }

    @FXML
    private void rejectDocument() {
        Document selected = documentsTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            return;
        }

        documentService.rejeter(selected.getId(), currentUser.getId());
        refreshDocuments();
        setStatus("Document rejeté.");
    }

    @FXML
    private void newDocument() {
        documentsTable.getSelectionModel().clearSelection();
        clearDocumentForm();
    }

    @FXML
    private void saveEvenement() {
        if (text(eventTitreField).isBlank()) {
            showAlert(Alert.AlertType.WARNING, "Événement", "Le titre est obligatoire.");
            return;
        }

        Evenement selected = evenementsTable.getSelectionModel().getSelectedItem();
        Evenement evenement = selected == null ? new Evenement() : selected;
        evenement.setTitre(text(eventTitreField));
        evenement.setDescription(text(eventDescriptionArea));
        evenement.setTypeEvenement(eventTypeCombo.getValue() == null ? EvenementType.AUTRE : eventTypeCombo.getValue());
        evenement.setOrganiseParSite(eventOrganiseCheck.isSelected());
        evenement.setLieu(text(eventLieuField));
        evenement.setDateDebut(parseDateTime(eventDateDebutPicker, eventHeureDebutField));
        evenement.setDateFin(parseDateTime(eventDateFinPicker, eventHeureFinField));
        evenement.setTarif(parseBigDecimal(text(eventTarifField)));
        evenement.setCapacity(parseNullableInt(text(eventCapacityField), 0));
        evenement.setStatut(eventStatutCombo.getValue());
        evenement.setIdCreateur(currentUser.getId());
        evenement.setImageEvenement(text(eventImageField));

        if (selected == null) {
            evenementService.add(evenement);
        } else {
            evenementService.update(evenement);
        }

        clearEvenementForm();
        refreshEvenements();
        setStatus("Événement enregistré.");
    }

    @FXML
    private void deleteEvenement() {
        Evenement selected = evenementsTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            return;
        }

        evenementService.delete(selected);
        clearEvenementForm();
        refreshEvenements();
        setStatus("Événement supprimé.");
    }

    @FXML
    private void newEvenement() {
        evenementsTable.getSelectionModel().clearSelection();
        clearEvenementForm();
    }

    @FXML
    private void savePlanning() {
        if (text(planningUserIdField).isBlank() || text(planningTitreField).isBlank()) {
            showAlert(Alert.AlertType.WARNING, "Planning", "L'utilisateur et le titre sont obligatoires.");
            return;
        }

        Planning selected = planningTable.getSelectionModel().getSelectedItem();
        Planning planning = selected == null ? new Planning() : selected;
        planning.setIdUtilisateur(parseRequiredInt(text(planningUserIdField), "ID utilisateur"));
        planning.setTitre(text(planningTitreField));
        planning.setDateRevision(parseDateTime(planningDatePicker, planningHeureField));
        planning.setTypeActivite(planningTypeCombo.getValue() == null ? PlanningType.REVISION : planningTypeCombo.getValue());

        if (selected == null) {
            planningService.add(planning);
        } else {
            planningService.update(planning);
        }

        clearPlanningForm();
        refreshPlanning();
        setStatus("Planning enregistré.");
    }

    @FXML
    private void deletePlanning() {
        Planning selected = planningTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            return;
        }

        planningService.delete(selected);
        clearPlanningForm();
        refreshPlanning();
        setStatus("Planning supprimé.");
    }

    @FXML
    private void newPlanning() {
        planningTable.getSelectionModel().clearSelection();
        clearPlanningForm();
    }

    @FXML
    private void downloadUsersCsv() {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Télécharger la liste des utilisateurs");
        chooser.setInitialFileName("utilisateurs.csv");
        chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV", "*.csv"));

        File file = chooser.showSaveDialog(rootPane.getScene().getWindow());
        if (file == null) {
            return;
        }

        try (PrintWriter writer = new PrintWriter(new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8))) {
            writer.println("id;nom_complet;email;role;filiere;annee;universite;actif");
            for (Utilisateur u : utilisateurService.getAll()) {
                writer.println(csv(u.getId()) + ";" +
                        csv(u.getNomComplet()) + ";" +
                        csv(u.getEmail()) + ";" +
                        csv(u.getRole()) + ";" +
                        csv(u.getFiliere()) + ";" +
                        csv(u.getAnnee()) + ";" +
                        csv(u.getUniversite()) + ";" +
                        csv(u.isEstActif() ? "oui" : "non"));
            }
            setStatus("Liste utilisateurs exportée.");
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Export utilisateurs", e.getMessage());
        }
    }

    @FXML
    private void activateUser() {
        Utilisateur selected = usersTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            return;
        }

        utilisateurService.activerCompte(selected.getId());
        refreshUsers();
        setStatus("Utilisateur activé.");
    }

    @FXML
    private void deactivateUser() {
        Utilisateur selected = usersTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            return;
        }

        utilisateurService.desactiverCompte(selected.getId());
        refreshUsers();
        setStatus("Utilisateur désactivé.");
    }

    @FXML
    private void filterParticipants() {
        String selected = participantEventCombo.getValue();
        int eventId = parseLeadingId(selected);

        if (eventId <= 0) {
            refreshParticipants();
        } else {
            participantsTable.setItems(FXCollections.observableArrayList(participationService.getByEvenement(eventId)));
        }
        setStatus("Participants actualisés.");
    }

    @FXML
    private void markParticipantPresent() {
        ParticipationEvenement selected = participantsTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            return;
        }
        participationService.marquerPresent(selected.getId());
        filterParticipants();
    }

    @FXML
    private void markParticipantAbsent() {
        ParticipationEvenement selected = participantsTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            return;
        }
        participationService.marquerAbsent(selected.getId());
        filterParticipants();
    }

    @FXML
    private void cancelParticipation() {
        ParticipationEvenement selected = participantsTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            return;
        }
        participationService.annuler(selected.getId());
        filterParticipants();
    }

    @FXML
    private void traiterReclamation() {
        Reclamation selected = reclamationsTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            return;
        }

        reclamationService.traiter(selected.getId(), currentUser.getId(), text(reclamationResponseArea));
        refreshReclamations();
        setStatus("Réclamation traitée.");
    }

    @FXML
    private void mettreReclamationEnAttente() {
        Reclamation selected = reclamationsTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            return;
        }

        reclamationService.remettreEnAttente(selected.getId(), currentUser.getId());
        refreshReclamations();
        setStatus("Réclamation remise en attente.");
    }

    @FXML
    private void rejeterReclamation() {
        Reclamation selected = reclamationsTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            return;
        }

        reclamationService.rejeter(selected.getId(), currentUser.getId(), text(reclamationResponseArea));
        refreshReclamations();
        setStatus("Réclamation rejetée.");
    }

    @FXML
    private void deleteReclamation() {
        Reclamation selected = reclamationsTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            return;
        }

        reclamationService.delete(selected);
        refreshReclamations();
        setStatus("Réclamation supprimée.");
    }

    @FXML
    private void sendAdminMessage() {
        int destinataireId = parseLeadingId(messageDestinataireCombo.getValue());
        String contenu = text(messageContentArea);

        if (destinataireId <= 0 || contenu.isBlank()) {
            showAlert(Alert.AlertType.WARNING, "Message", "Choisissez un destinataire et saisissez un message.");
            return;
        }

        messagePriveService.add(new MessagePrive(currentUser.getId(), destinataireId, contenu));
        messageContentArea.clear();
        refreshMessages();
        setStatus("Message envoyé.");
    }

    @FXML
    private void goProfil() {
        Navigator.go(rootPane, "/Profil.fxml", "Profil - SmarTounsi");
    }

    @FXML
    private void logout() {
        Navigator.logout(rootPane);
    }

    private void fillModuleForm(Module module) {
        moduleNomField.setText(module == null ? "" : safe(module.getNomModule()));
        moduleDescriptionArea.setText(module == null ? "" : safe(module.getDescription()));
        moduleCreateurIdField.setText(module == null || module.getIdCreateur() == null ? "" : String.valueOf(module.getIdCreateur()));
    }

    private void fillDocumentForm(Document document) {
        docTitreField.setText(document == null ? "" : safe(document.getTitre()));
        docDescriptionArea.setText(document == null ? "" : safe(document.getDescription()));
        docTypeCombo.setValue(document == null || document.getTypeDocument() == null ? DocumentType.COURS : document.getTypeDocument());
        docFichierField.setText(document == null ? "" : safe(document.getFichierUrl()));
        docModuleIdField.setText(document == null ? "" : String.valueOf(document.getIdModule()));
        docUploaderIdField.setText(document == null ? "" : String.valueOf(document.getIdUploadeur()));
        docStatutCombo.setValue(document == null || document.getStatut() == null ? DocumentStatut.EN_ATTENTE : document.getStatut());
    }

    private void fillEvenementForm(Evenement evenement) {
        eventTitreField.setText(evenement == null ? "" : safe(evenement.getTitre()));
        eventDescriptionArea.setText(evenement == null ? "" : safe(evenement.getDescription()));
        eventTypeCombo.setValue(evenement == null || evenement.getTypeEvenement() == null ? EvenementType.AUTRE : evenement.getTypeEvenement());
        eventOrganiseCheck.setSelected(evenement == null || evenement.isOrganiseParSite());
        eventLieuField.setText(evenement == null ? "" : safe(evenement.getLieu()));
        setDateTimeFields(evenement == null ? null : evenement.getDateDebut(), eventDateDebutPicker, eventHeureDebutField);
        setDateTimeFields(evenement == null ? null : evenement.getDateFin(), eventDateFinPicker, eventHeureFinField);
        eventTarifField.setText(evenement == null || evenement.getTarif() == null ? "0" : evenement.getTarif().toPlainString());
        eventCapacityField.setText(evenement == null ? "0" : String.valueOf(evenement.getCapacity()));
        eventStatutCombo.setValue(evenement == null ? "a_venir" : safe(evenement.getStatut()));
        eventImageField.setText(evenement == null ? "" : safe(evenement.getImageEvenement()));
    }

    private void fillPlanningForm(Planning planning) {
        planningUserIdField.setText(planning == null ? "" : String.valueOf(planning.getIdUtilisateur()));
        planningTitreField.setText(planning == null ? "" : safe(planning.getTitre()));
        setDateTimeFields(planning == null ? null : planning.getDateRevision(), planningDatePicker, planningHeureField);
        planningTypeCombo.setValue(planning == null || planning.getTypeActivite() == null ? PlanningType.REVISION : planning.getTypeActivite());
    }

    private void clearModuleForm() {
        fillModuleForm(null);
    }

    private void clearDocumentForm() {
        fillDocumentForm(null);
    }

    private void clearEvenementForm() {
        fillEvenementForm(null);
    }

    private void clearPlanningForm() {
        fillPlanningForm(null);
    }

    private <T> TableColumn<T, Number> intColumn(String title, IntGetter<T> getter) {
        TableColumn<T, Number> column = new TableColumn<>(title);
        column.setCellValueFactory(data -> new SimpleIntegerProperty(getter.get(data.getValue())));
        column.setPrefWidth(90);
        return column;
    }

    private <T> TableColumn<T, String> textColumn(String title, TextGetter<T> getter) {
        TableColumn<T, String> column = new TableColumn<>(title);
        column.setCellValueFactory(data -> new SimpleStringProperty(safe(getter.get(data.getValue()))));
        column.setPrefWidth(150);
        return column;
    }

    private LocalDateTime parseDateTime(DatePicker picker, TextField timeField) {
        if (picker.getValue() == null) {
            return null;
        }

        String time = text(timeField);
        if (time.isBlank()) {
            return picker.getValue().atStartOfDay();
        }

        try {
            return picker.getValue().atTime(LocalTime.parse(time));
        } catch (DateTimeParseException e) {
            showAlert(Alert.AlertType.WARNING, "Heure invalide", "Utilisez le format HH:mm.");
            throw e;
        }
    }

    private void setDateTimeFields(LocalDateTime dateTime, DatePicker picker, TextField timeField) {
        if (dateTime == null) {
            picker.setValue(null);
            timeField.clear();
            return;
        }

        picker.setValue(dateTime.toLocalDate());
        timeField.setText(dateTime.toLocalTime().withSecond(0).withNano(0).toString());
    }

    private int parseRequiredInt(String value, String fieldName) {
        try {
            return Integer.parseInt(value.trim());
        } catch (Exception e) {
            throw new IllegalArgumentException(fieldName + " doit être un nombre.");
        }
    }

    private int parseNullableInt(String value, int defaultValue) {
        if (value == null || value.isBlank()) {
            return defaultValue;
        }

        return parseRequiredInt(value, "Valeur");
    }

    private int parseLeadingId(String value) {
        if (value == null || value.isBlank() || value.startsWith("Tous")) {
            return 0;
        }

        String first = value.split("\\s+", 2)[0];
        try {
            return Integer.parseInt(first);
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    private BigDecimal parseBigDecimal(String value) {
        if (value == null || value.isBlank()) {
            return BigDecimal.ZERO;
        }

        return new BigDecimal(value.replace(',', '.').trim());
    }

    private String labelUtilisateur(Utilisateur utilisateur) {
        if (utilisateur == null) {
            return "";
        }

        String name = utilisateur.getNomComplet();
        if (name == null || name.isBlank()) {
            name = "Utilisateur " + utilisateur.getId();
        }

        return name + " (" + safe(utilisateur.getEmail()) + ")";
    }

    private boolean isAdmin(Utilisateur utilisateur) {
        return utilisateur != null && "admin".equalsIgnoreCase(safe(utilisateur.getRole()));
    }

    private String text(TextInputControl control) {
        return control == null || control.getText() == null ? "" : control.getText().trim();
    }

    private String safe(String value) {
        return value == null ? "" : value;
    }

    private String firstNotBlank(String first, String second) {
        return first != null && !first.isBlank() ? first : safe(second);
    }

    private String formatDate(LocalDateTime value) {
        return value == null ? "" : value.toString().replace('T', ' ');
    }

    private String csv(Object value) {
        String text = value == null ? "" : String.valueOf(value);
        return "\"" + text.replace("\"", "\"\"") + "\"";
    }

    private void setStatus(String message) {
        statusLabel.setText(message);
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private interface IntGetter<T> {
        int get(T value);
    }

    private interface TextGetter<T> {
        String get(T value);
    }
}
