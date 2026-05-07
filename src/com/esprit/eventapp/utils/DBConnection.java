package com.esprit.eventapp.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {
    private static final String URL = "jdbc:mysql://localhost:3306/plateforme_education";
    private static final String USER = "root";
    private static final String PASSWORD = "";

    public static Connection getConnection() throws SQLException {
        try {
            // Forcer le chargement du driver (utile pour certaines configurations)
            Class.forName("com.mysql.cj.jdbc.Driver");
            return DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (ClassNotFoundException e) {
            System.err.println("ERREUR CRITIQUE : Le Driver MySQL est introuvable !");
            System.err.println("Veuillez ajouter 'mysql-connector-j.jar' aux bibliothèques de votre projet.");
            throw new SQLException("Driver MySQL manquant", e);
        } catch (SQLException e) {
            System.err.println("ERREUR SQL : Impossible de se connecter à la base de données.");
            System.err.println("Vérifiez que XAMPP/WAMP est lancé et que la base 'plateforme_education' existe.");
            throw e;
        }
    }
}
