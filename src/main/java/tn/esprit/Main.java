package tn.esprit;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage stage) throws Exception {

        FXMLLoader loader =
                new FXMLLoader(
                        getClass().getResource(
                                "/UploadDocument.fxml"
                        )
                );

        Scene scene =
                new Scene(loader.load());

        stage.setTitle("smarTounsi");

        stage.setScene(scene);

        stage.setWidth(1400);
        stage.setHeight(900);

        stage.show();
    }


    public static void main(String[] args) {
        launch();
    }

}