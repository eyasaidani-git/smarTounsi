package util;

import models.Utilisateur;

public class Session {
    private static int    moduleId;
    private static String moduleNom;
    private static int    documentId;
    private static String documentNom;
    private static int    userId = 1; // par défaut jusqu'à l'ajout du login

    public static int getModuleId() { return moduleId; }
    public static void setModuleId(int id) { moduleId = id; }

    public static String getModuleNom() { return moduleNom; }
    public static void setModuleNom(String nom) { moduleNom = nom; }

    public static int getDocumentId() { return documentId; }
    public static void setDocumentId(int id) { documentId = id; }

    public static String getDocumentNom() { return documentNom; }
    public static void setDocumentNom(String nom) { documentNom = nom; }

    public static int getUserId() {
        return userId;
    }

    public static void clear() {

    }

    public static Utilisateur getUser() {
        return null;
    }
}