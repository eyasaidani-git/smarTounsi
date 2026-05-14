package util;

import models.Utilisateur;

public class Session {

    private static Utilisateur currentUser;
    private static int moduleId;
    private static String moduleNom;

    public static Utilisateur getCurrentUser() {
        return currentUser;
    }

    public static void setCurrentUser(Utilisateur user) {
        currentUser = user;
    }

    public static void clear() {
        currentUser = null;
        moduleId = 0;
        moduleNom = null;
    }

    public static boolean isLoggedIn() {
        return currentUser != null;
    }

    public static int getUserId() {
        return currentUser == null ? 1 : currentUser.getId();
    }

    public static int getModuleId() {
        return moduleId;
    }

    public static void setModuleId(int id) {
        moduleId = id;
    }

    public static String getModuleNom() {
        return moduleNom;
    }

    public static void setModuleNom(String nom) {
        moduleNom = nom;
    }
}
