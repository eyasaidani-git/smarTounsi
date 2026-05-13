package com.esprit.eventapp.controllers;

import com.esprit.eventapp.models.User;
import com.esprit.eventapp.services.UserDAO;
import com.esprit.eventapp.utils.SessionManager;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;

public class SignInController {

    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private Label messageLabel;

    private UserDAO userDAO = new UserDAO();

    @FXML
    void handleSignIn(ActionEvent event) {
        String username = usernameField.getText();
        String password = passwordField.getText();

        if (username.isEmpty() || password.isEmpty()) {
            messageLabel.setText("Veuillez remplir tous les champs.");
            return;
        }

        User user = userDAO.login(username, password);
        if (user != null) {
            SessionManager.getInstance().setCurrentUser(user);
            messageLabel.setText("Connexion réussie ! Bienvenue " + user.getPrenom());
            
            // Redirection vers le Dashboard
            try {
                Parent root = FXMLLoader.load(getClass().getResource("/com/esprit/eventapp/views/Dashboard.fxml"));
                Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                Scene scene = new Scene(root);
                stage.setScene(scene);
                stage.setMaximized(true);
                stage.setFullScreen(true);
                stage.show();
            } catch (IOException e) {
                e.printStackTrace();
                messageLabel.setText("Erreur lors du chargement du Dashboard.");
            }
        } else {
            messageLabel.setText("Nom d'utilisateur ou mot de passe incorrect.");
        }
    }

    @FXML
    void switchToSignUp(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("/com/esprit/eventapp/views/SignUp.fxml"));
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setMaximized(true);
        stage.setFullScreen(true);
        stage.show();
    }
}
