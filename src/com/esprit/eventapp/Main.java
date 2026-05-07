package com.esprit.eventapp;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        // Charger l'interface de connexion au démarrage avec le nouveau chemin
        Parent root = FXMLLoader.load(getClass().getResource("/com/esprit/eventapp/views/SignIn.fxml"));
        primaryStage.setTitle("Plateforme Éducation - Connexion");
        primaryStage.setMaximized(true);
        primaryStage.setFullScreen(true);
        primaryStage.setScene(new Scene(root));
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
