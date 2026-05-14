package tn.esprit;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
<<<<<<< HEAD
=======
import javafx.scene.Parent;
>>>>>>> origin/gestionikram
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage stage) throws Exception {
<<<<<<< HEAD
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/Acceuil.fxml"));

        Scene scene = new Scene(loader.load(), 1400, 850);

        stage.setTitle("SmarTounsi");
        stage.setScene(scene);
        stage.setMinWidth(1200);
        stage.setMinHeight(750);
=======

        Parent root = FXMLLoader.load(
                getClass().getResource("/view/quiz.fxml")
        );

        Scene scene = new Scene(root);

        scene.getStylesheets().add(
                getClass()
                        .getResource("/style/quiz.css")
                        .toExternalForm()
        );

        stage.setTitle("SmarTounsi - Quiz");

        stage.setScene(scene);

        stage.setMaximized(true);

>>>>>>> origin/gestionikram
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}