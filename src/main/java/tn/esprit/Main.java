package tn.esprit;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/Acceuil.fxml"));

        Scene scene = new Scene(loader.load(), 1400, 850);

        stage.setTitle("smartounsi");
        stage.setScene(scene);
        stage.setMinWidth(1200);
        stage.setMinHeight(750);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}