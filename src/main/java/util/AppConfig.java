package util;

import java.io.BufferedReader;
import java.io.IOException;
import java.text.Normalizer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public final class AppConfig {

    private AppConfig() {
    }

    public static String get(String key) {
        String value = System.getenv(key);
        if (value != null && !value.isBlank()) {
            return value;
        }

        value = System.getProperty(key);
        if (value != null && !value.isBlank()) {
            return value;
        }

        return readFromDotEnv(key);
    }

    public static String getRequired(String key, String helpMessage) {
        String value = get(key);
        if (value == null || value.isBlank()) {
            throw new RuntimeException(key + " n'est pas configure.\n" + helpMessage);
        }
        return value;
    }

    public static String cleanGmailAppPassword(String rawPassword) {
        if (rawPassword == null) {
            return null;
        }

        String cleaned = Normalizer.normalize(rawPassword, Normalizer.Form.NFKC).trim();

        if ((cleaned.startsWith("\"") && cleaned.endsWith("\""))
                || (cleaned.startsWith("'") && cleaned.endsWith("'"))) {
            cleaned = cleaned.substring(1, cleaned.length() - 1);
        }

        return cleaned
                .replace("\uFEFF", "")
                .replace("\u200B", "")
                .replace("\u00A0", "")
                .replaceAll("[\\p{C}\\p{Z}\\s-]+", "")
                .replaceAll("[^A-Za-z0-9]", "")
                .trim();
    }

    public static String getCleanGmailAppPassword(String key) {
        ConfigValue[] values = {
                new ConfigValue("variable d'environnement", System.getenv(key)),
                new ConfigValue("propriété Java", System.getProperty(key)),
                new ConfigValue("fichier .env", readFromDotEnv(key))
        };

        ConfigValue firstInvalid = null;
        String firstInvalidCleaned = null;

        for (ConfigValue value : values) {
            if (value.value == null || value.value.isBlank()) {
                continue;
            }

            String cleaned = cleanGmailAppPassword(value.value);
            if (cleaned != null && cleaned.length() == 16) {
                System.out.println("[AppConfig] " + key + " lu depuis " + value.source
                        + " avec longueur nettoyee = 16");
                return cleaned;
            }

            if (firstInvalid == null) {
                firstInvalid = value;
                firstInvalidCleaned = cleaned;
            }
        }

        if (firstInvalid != null) {
            throw new RuntimeException(
                    "Le mot de passe d'application Gmail doit contenir exactement 16 caracteres apres nettoyage.\n" +
                            "Source lue : " + firstInvalid.source + ".\n" +
                            "Longueur brute : " + firstInvalid.value.length() + ".\n" +
                            "Longueur apres nettoyage : " + (firstInvalidCleaned == null ? 0 : firstInvalidCleaned.length()) + ".\n" +
                            "Si votre fichier .env contient la bonne valeur, supprimez ou corrigez aussi la variable " +
                            key + " dans IntelliJ Run > Edit Configurations > Environment variables."
            );
        }

        throw new RuntimeException(
                key + " n'est pas configure.\n" +
                        "Ajoutez dans .env : " + key + "=motdepasseapplicationgmail16caracteres"
        );
    }

    private static class ConfigValue {
        private final String source;
        private final String value;

        private ConfigValue(String source, String value) {
            this.source = source;
            this.value = value;
        }
    }

    private static String readFromDotEnv(String key) {
        Path envPath = Path.of(System.getProperty("user.dir"), ".env");
        if (!Files.isRegularFile(envPath)) {
            return null;
        }

        try (BufferedReader reader = Files.newBufferedReader(envPath, StandardCharsets.UTF_8)) {
            String line;
            while ((line = reader.readLine()) != null) {
                String trimmed = line.trim();

                if (trimmed.isEmpty() || trimmed.startsWith("#")) {
                    continue;
                }

                int equalsIndex = trimmed.indexOf('=');
                if (equalsIndex <= 0) {
                    continue;
                }

                String lineKey = trimmed.substring(0, equalsIndex).trim();
                if (!key.equals(lineKey)) {
                    continue;
                }

                return trimmed.substring(equalsIndex + 1).trim();
            }
        } catch (IOException e) {
            throw new RuntimeException("Impossible de lire le fichier .env : " + e.getMessage(), e);
        }

        return null;
    }
}
