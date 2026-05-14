package util;

import org.mindrot.jbcrypt.BCrypt;

public class PasswordUtil {

    public static boolean isStrongPassword(String password) {
        if (password == null) {
            return false;
        }

        String regex = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).{8,}$";
        return password.matches(regex);
    }

    public static String getPasswordRulesMessage() {
        return "Le mot de passe doit contenir au minimum 8 caractères, une lettre majuscule, une lettre minuscule et un chiffre.";
    }

    public static String hashPassword(String plainPassword) {
        return BCrypt.hashpw(plainPassword, BCrypt.gensalt(12));
    }

    public static boolean checkPassword(String plainPassword, String hashedPassword) {
        if (plainPassword == null || hashedPassword == null) {
            return false;
        }

        return BCrypt.checkpw(plainPassword, hashedPassword);
    }

    public static boolean isBCryptHash(String value) {
        if (value == null) {
            return false;
        }

        return value.startsWith("$2a$") || value.startsWith("$2b$") || value.startsWith("$2y$");
    }
}