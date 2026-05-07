package com.esprit.eventapp.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class TestDB {
    public static void main(String[] args) {
        System.out.println("Test de connexion à la base de données...");
        try {
            Connection conn = DBConnection.getConnection();
            if (conn != null) {
                System.out.println("Connexion réussie !");
            }
        } catch (SQLException e) {
            System.out.println("Erreur de connexion SQL : " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            System.out.println("Autre erreur : " + e.getMessage());
            e.printStackTrace();
        }
    }
}
