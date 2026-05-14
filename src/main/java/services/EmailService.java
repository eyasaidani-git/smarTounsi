package services;

import jakarta.mail.Authenticator;
import jakarta.mail.Message;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;

import java.util.Properties;

public class EmailService {

    private static final String FROM_EMAIL = "smartounsi6@gmail.com";


    private static final String APP_PASSWORD =
            System.getenv("SMARTOUNSI_MAIL_PASSWORD");


    private static String nettoyerMotDePasse(String raw) {
        if (raw == null) return null;
        return raw.replaceAll("\\s+", "").trim();
    }


    public void sendVerificationCode(String toEmail, String code) {
        String subject = "Code de vérification SmarTounsi";

        String body = """
                Bonjour,

                Voici votre code de vérification SmarTounsi :

                %s

                Ce code est valable pendant 10 minutes.

                Si vous n'avez pas demandé ce code, ignorez cet email.

                — SmarTounsi
                """.formatted(code);

        sendEmail(toEmail, subject, body);
    }

    public void sendResetCode(String toEmail, String code) {
        String subject = "Code de réinitialisation SmarTounsi";

        String body = """
                Bonjour,

                Voici votre code de réinitialisation de mot de passe SmarTounsi :

                %s

                Ce code est valable pendant 10 minutes.

                Si vous n'avez pas demandé cette réinitialisation, ignorez cet email.

                — SmarTounsi
                """.formatted(code);

        sendEmail(toEmail, subject, body);
    }

    private void sendEmail(String toEmail, String subject, String body) {
        try {
            if (APP_PASSWORD == null || APP_PASSWORD.isBlank()) {
                throw new RuntimeException(
                        "SMARTOUNSI_MAIL_PASSWORD n'est pas configuré.\n" +
                                "Dans IntelliJ : Run → Edit Configurations → Environment variables\n" +
                                "Générez un mot de passe sur : myaccount.google.com → Sécurité → Mots de passe des applications"
                );
            }

            String cleanPassword = nettoyerMotDePasse(APP_PASSWORD);

            System.out.println("=== Envoi email ===");
            System.out.println("Expéditeur  : " + FROM_EMAIL);
            System.out.println("Destinataire: " + toEmail);
            System.out.println("Longueur mot de passe (après nettoyage) : " + cleanPassword.length());

            if (cleanPassword.length() != 16) {
                throw new RuntimeException(
                        "Le mot de passe d'application Gmail doit être exactement 16 caractères.\n" +
                                "Longueur actuelle après nettoyage : " + cleanPassword.length() + " caractères.\n\n" +
                                "Solution :\n" +
                                "1. Allez sur myaccount.google.com\n" +
                                "2. Sécurité → Mots de passe des applications\n" +
                                "3. Créez un nouveau mot de passe pour 'SmarTounsi'\n" +
                                "4. Copiez les 16 caractères (avec ou sans espaces) dans la variable SMARTOUNSI_MAIL_PASSWORD"
                );
            }

            Properties props = new Properties();
            props.put("mail.smtp.auth", "true");
            props.put("mail.smtp.starttls.enable", "true");
            props.put("mail.smtp.host", "smtp.gmail.com");
            props.put("mail.smtp.port", "587");
            props.put("mail.smtp.ssl.trust", "smtp.gmail.com");
            props.put("mail.smtp.connectiontimeout", "10000");
            props.put("mail.smtp.timeout", "10000");

            final String finalPassword = cleanPassword;

            Session session = Session.getInstance(props, new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(FROM_EMAIL, finalPassword);
                }
            });

            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(FROM_EMAIL));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail));
            message.setSubject(subject);
            message.setText(body);

            Transport.send(message);

            System.out.println("✓ Email envoyé avec succès à : " + toEmail);

        } catch (Exception e) {
            throw new RuntimeException("Erreur envoi email : " + e.getMessage());
        }
    }
}