package util;

public class EmailValidator {

    public static boolean isValidEmailFormat(String email) {
        if (email == null) {
            return false;
        }

        String regex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";
        return email.trim().matches(regex);
    }

    public static String getEmailRulesMessage() {
        return "Veuillez saisir une adresse email valide, par exemple : nom@example.com";
    }
}