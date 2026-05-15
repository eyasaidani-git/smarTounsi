package services;

import jakarta.mail.Authenticator;
import jakarta.mail.Message;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import util.AppConfig;

import java.util.Properties;

public class EmailService {

    private static final String FROM_EMAIL = "smartounsi6@gmail.com";
    private static final String SMTP_HOST = "smtp.gmail.com";
    private static final String SMTP_PORT = "587";
    private static final String MAIL_PASSWORD_KEY = "SMARTOUNSI_MAIL_PASSWORD";

    public void sendVerificationCode(String toEmail, String code) {
        String subject = "Code de verification SmarTounsi";
        String body = """
                Bonjour,

                Voici votre code de verification SmarTounsi :

                %s

                Ce code est valable pendant 10 minutes.

                Si vous n'avez pas demande ce code, ignorez cet email.

                - SmarTounsi
                """.formatted(code);

        sendEmail(toEmail, subject, body);
    }

    public void sendResetCode(String toEmail, String code) {
        String subject = "Code de reinitialisation SmarTounsi";
        String body = """
                Bonjour,

                Voici votre code de reinitialisation de mot de passe SmarTounsi :

                %s

                Ce code est valable pendant 10 minutes.

                Si vous n'avez pas demande cette reinitialisation, ignorez cet email.

                - SmarTounsi
                """.formatted(code);

        sendEmail(toEmail, subject, body);
    }

    private void sendEmail(String toEmail, String subject, String body) {
        try {
            String cleanPassword = readCleanAppPassword();

            System.out.println("=== Envoi email ===");
            System.out.println("Expediteur  : " + FROM_EMAIL);
            System.out.println("Destinataire: " + toEmail);
            System.out.println("Mot de passe d'application configure = oui");
            System.out.println("Longueur mot de passe apres nettoyage = " + cleanPassword.length());

            Properties props = new Properties();
            props.put("mail.smtp.auth", "true");
            props.put("mail.smtp.starttls.enable", "true");
            props.put("mail.smtp.host", SMTP_HOST);
            props.put("mail.smtp.port", SMTP_PORT);
            props.put("mail.smtp.ssl.trust", SMTP_HOST);
            props.put("mail.smtp.connectiontimeout", "10000");
            props.put("mail.smtp.timeout", "10000");

            Session session = Session.getInstance(props, new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(FROM_EMAIL, cleanPassword);
                }
            });

            MimeMessage message = new MimeMessage(session);
            message.setFrom(new InternetAddress(FROM_EMAIL));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail));
            message.setSubject(subject, "UTF-8");
            message.setText(body, "UTF-8");

            Transport.send(message);
            System.out.println("Email envoye avec succes a : " + toEmail);

        } catch (Exception e) {
            throw new RuntimeException("Erreur envoi email : " + e.getMessage(), e);
        }
    }

    private String readCleanAppPassword() {
        String rawPassword = AppConfig.getRequired(
                MAIL_PASSWORD_KEY,
                "Ajoutez la variable dans IntelliJ : Run > Edit Configurations > Environment variables.\n" +
                        "Ou creez un fichier .env a la racine du projet avec :\n" +
                        MAIL_PASSWORD_KEY + "=votre_mot_de_passe_application_gmail"
        );

        String cleanPassword = AppConfig.cleanGmailAppPassword(rawPassword);
        if (cleanPassword == null || cleanPassword.isBlank()) {
            throw new RuntimeException(MAIL_PASSWORD_KEY + " est vide apres nettoyage.");
        }

        if (cleanPassword.length() != 16) {
            throw new RuntimeException(
                    "Le mot de passe d'application Gmail doit contenir exactement 16 caracteres apres nettoyage. " +
                            "Longueur actuelle : " + cleanPassword.length() + ".\n" +
                            "Utilisez uniquement le mot de passe d'application Gmail de 16 caracteres, " +
                            "pas le mot de passe normal du compte Gmail."
            );
        }

        return cleanPassword;
    }
}
