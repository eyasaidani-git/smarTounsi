package util;
import models.Utilisateur;
public class Session {
    private static Utilisateur utilisateurConnecte;
    public static Utilisateur getUtilisateurConnecte() {
        return utilisateurConnecte;
    }

    public static void setUtilisateurConnecte(Utilisateur utilisateur) {
        utilisateurConnecte = utilisateur;
    }

    public static int getIdUtilisateurConnecte() {
        if (utilisateurConnecte != null) {
            return utilisateurConnecte.getId();
        }
        return -1;
    }

    public static String getRoleUtilisateurConnecte() {
        if (utilisateurConnecte != null) {
            return utilisateurConnecte.getRole();
        }
        return null;
    }

    public static boolean estEtudiant() {
        return utilisateurConnecte != null &&
                utilisateurConnecte.getRole().equalsIgnoreCase("etudiant");
    }

    public static boolean estAdmin() {
        return utilisateurConnecte != null &&
                utilisateurConnecte.getRole().equalsIgnoreCase("admin");
    }

    public static boolean estEnseignant() {
        return utilisateurConnecte != null &&
                utilisateurConnecte.getRole().equalsIgnoreCase("enseignant");
    }
}
