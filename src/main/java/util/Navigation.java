package util;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.Node;

public class Navigation {

    public static void navigateTo(String fxml, Node node) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    Navigation.class.getResource("/" + fxml)
            );
            Scene scene = new Scene(loader.load());
            Stage stage = (Stage) node.getScene().getWindow();
            stage.setScene(scene);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}