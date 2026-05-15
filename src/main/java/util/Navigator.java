package util;

import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;

public class Navigator {

    public static void go(Node source, String fxmlPath, String title) {
        try {
            if (source == null || source.getScene() == null) {
                showError("Erreur navigation", "Source JavaFX invalide.");
                return;
            }

            FXMLLoader loader = new FXMLLoader(Navigator.class.getResource(fxmlPath));

            if (loader.getLocation() == null) {
                showError("FXML introuvable", "Impossible de trouver : " + fxmlPath);
                return;
            }

            Parent root = loader.load();
            UiRoleUtil.applySessionRoleLabels(root);

            Stage stage = (Stage) source.getScene().getWindow();
            stage.setScene(new Scene(root, 1400, 850));
            stage.setTitle(title);
            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
            showError("Erreur navigation", "Impossible d'ouvrir : " + fxmlPath + "\n" + e.getMessage());
        }
    }

    public static void logout(Node source) {
        Session.clear();
        go(source, "/Connexion.fxml", "Connexion - SmarTounsi");
    }

    public static void goDashboard(Node source) {
        if (Session.getCurrentUser() != null
                && "admin".equalsIgnoreCase(Session.getCurrentUser().getRole())) {
            go(source, "/AdminDashboard.fxml", "Admin - SmarTounsi");
        } else {
            go(source, "/Profil.fxml", "Dashboard - SmarTounsi");
        }
    }

    public static void applySessionRoleLabels(Parent root) {
        UiRoleUtil.applySessionRoleLabels(root);
    }

    private static void showError(String titre, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(titre);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
