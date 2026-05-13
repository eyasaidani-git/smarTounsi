package com.esprit.eventapp.utils;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Gère la migration automatique de la base de données au démarrage.
 * Ajoute les colonnes manquantes sans détruire les données existantes.
 */
public class DatabaseMigration {

    public static void runMigrations() {
        System.out.println("[Migration] Vérification de la base de données...");

        try (Connection conn = DBConnection.getConnection()) {

            // Migration 1 : ajouter createur_id à la table evenement si elle n'existe pas
            if (!columnExists(conn, "evenement", "createur_id")) {
                System.out.println("[Migration] Ajout de la colonne createur_id...");
                try (Statement st = conn.createStatement()) {
                    st.execute("ALTER TABLE evenement ADD COLUMN createur_id INT NULL");
                    System.out.println("[Migration] Colonne createur_id ajoutée avec succès.");
                }
            } else {
                System.out.println("[Migration] Colonne createur_id déjà présente. OK.");
            }

            if (!columnExists(conn, "evenement", "image")) {
                System.out.println("[Migration] Ajout de la colonne image...");
                try (Statement st = conn.createStatement()) {
                    st.execute("ALTER TABLE evenement ADD COLUMN image VARCHAR(500) NULL");
                    System.out.println("[Migration] Colonne image ajoutée avec succès.");
                }
            }

            if (!columnExists(conn, "evenement", "prix")) {
                System.out.println("[Migration] Ajout de la colonne prix...");
                try (Statement st = conn.createStatement()) {
                    st.execute("ALTER TABLE evenement ADD COLUMN prix DOUBLE DEFAULT 0");
                    System.out.println("[Migration] Colonne prix ajoutée avec succès.");
                }
            }

            if (!columnExists(conn, "evenement", "places_disponibles")) {
                System.out.println("[Migration] Ajout de la colonne places_disponibles...");
                try (Statement st = conn.createStatement()) {
                    st.execute("ALTER TABLE evenement ADD COLUMN places_disponibles INT DEFAULT 0");
                    System.out.println("[Migration] Colonne places_disponibles ajoutée avec succès.");
                }
            }

            // Migration 2 : assigner les événements sans propriétaire au premier utilisateur ADMIN
            // (ou au premier utilisateur si pas d'admin)
            try (Statement st = conn.createStatement()) {
                // Compter les événements orphelins
                ResultSet check = st.executeQuery("SELECT COUNT(*) FROM evenement WHERE createur_id IS NULL");
                check.next();
                int orphelins = check.getInt(1);

                if (orphelins > 0) {
                    System.out.println("[Migration] " + orphelins + " événement(s) sans propriétaire détecté(s). Attribution au premier utilisateur...");

                    // Trouver le premier admin, sinon le premier utilisateur
                    ResultSet adminRs = st.executeQuery(
                        "SELECT id FROM user WHERE UPPER(role) = 'ADMIN' ORDER BY id LIMIT 1"
                    );
                    int ownerId = 0;
                    if (adminRs.next()) {
                        ownerId = adminRs.getInt("id");
                    } else {
                        ResultSet firstUser = st.executeQuery("SELECT id FROM user ORDER BY id LIMIT 1");
                        if (firstUser.next()) {
                            ownerId = firstUser.getInt("id");
                        }
                    }

                    if (ownerId > 0) {
                        st.executeUpdate("UPDATE evenement SET createur_id = " + ownerId + " WHERE createur_id IS NULL");
                        System.out.println("[Migration] Événements assignés à l'utilisateur ID=" + ownerId + ".");
                    }
                } else {
                    System.out.println("[Migration] Tous les événements ont un propriétaire. OK.");
                }
            } catch (Exception e) {
                System.err.println("[Migration] Erreur migration 2 : " + e.getMessage());
            }

        } catch (SQLException e) {
            System.err.println("[Migration] Erreur lors de la migration : " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static boolean columnExists(Connection conn, String tableName, String columnName) throws SQLException {
        DatabaseMetaData meta = conn.getMetaData();
        try (ResultSet rs = meta.getColumns(null, null, tableName, columnName)) {
            return rs.next();
        }
    }
}
