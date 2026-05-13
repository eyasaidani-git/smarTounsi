package com.esprit.eventapp;

import com.esprit.eventapp.utils.DatabaseMigration;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        // Vérifier et migrer la base de données automatiquement
        DatabaseMigration.runMigrations();

        // Charger l'interface de connexion au démarrage
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
