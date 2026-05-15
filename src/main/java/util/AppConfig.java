package util;

import java.io.BufferedReader;
import java.io.IOException;
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

        String cleaned = rawPassword.trim();

        if ((cleaned.startsWith("\"") && cleaned.endsWith("\""))
                || (cleaned.startsWith("'") && cleaned.endsWith("'"))) {
            cleaned = cleaned.substring(1, cleaned.length() - 1);
        }

        return cleaned
                .replace("\uFEFF", "")
                .replace("\u200B", "")
                .replace("\u00A0", "")
                .replaceAll("[\\s-]+", "")
                .trim();
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
