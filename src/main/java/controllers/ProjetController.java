package controllers;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import models.Projet;
import services.ProjetService;

import java.util.List;

public class ProjetController {

    @FXML private TextField searchField;
    @FXML private FlowPane cardsContainer;

    private final ProjetService projetService = new ProjetService();

    @FXML
    public void initialize() {
        afficherProjets(projetService.getAll());

        searchField.textProperty().addListener((obs, oldValue, newValue) -> {
            if (newValue == null || newValue.trim().isEmpty()) {
                afficherProjets(projetService.getAll());
            } else {
                afficherProjets(projetService.search(newValue));
            }
        });
    }

    private void afficherProjets(List<Projet> projets) {
        cardsContainer.getChildren().clear();

        for (Projet p : projets) {
            VBox card = new VBox(12);
            card.getStyleClass().add("project-card");

            Label title = new Label("🚀 " + p.getNom());
            title.getStyleClass().add("card-title");

            Label desc = new Label(p.getDescription());
            desc.getStyleClass().add("card-desc");
            desc.setWrapText(true);

            HBox badges = new HBox(8);

            if (p.isaCode()) {
                badges.getChildren().add(createBadge("💻 Code", "badge-blue"));
            }
            if (p.isaPresentation()) {
                badges.getChildren().add(createBadge("📊 Présentation", "badge-yellow"));
            }
            if (p.isaRapport()) {
                badges.getChildren().add(createBadge("📄 Rapport", "badge-green"));
            }

            Label collab = new Label("3 collaborateurs · Informatique");
            collab.getStyleClass().add("collab-text");

            HBox avatars = new HBox(-5);
            avatars.getChildren().addAll(
                    createAvatar("A", "avatar-blue"),
                    createAvatar("S", "avatar-purple"),
                    createAvatar("K", "avatar-orange")
            );

            card.getChildren().addAll(title, desc, badges, collab, avatars);
            cardsContainer.getChildren().add(card);
        }
    }

    private Label createBadge(String text, String styleClass) {
        Label badge = new Label(text);
        badge.getStyleClass().addAll("badge", styleClass);
        return badge;
    }

    private Label createAvatar(String text, String styleClass) {
        Label avatar = new Label(text);
        avatar.getStyleClass().addAll("avatar", styleClass);
        return avatar;
    }

    @FXML
    private void ajouterProjet() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Nouveau projet");
        alert.setHeaderText(null);
        alert.setContentText("Ici on ouvrira la fenêtre Ajouter Projet.");
        alert.showAndWait();
    }
}