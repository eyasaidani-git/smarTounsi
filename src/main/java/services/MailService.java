package services;

import controllers.PlanningController.TodoItem;

import jakarta.mail.Authenticator;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import models.Quiz;
import models.Utilisateur;
import util.AppConfig;

import java.io.UnsupportedEncodingException;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class MailService {

    private static final String FROM_EMAIL = "smartounsi6@gmail.com";

    private static final String MAIL_PASSWORD_KEY = "SMARTOUNSI_MAIL_PASSWORD";

    private static final String SENDER_NAME = "smarTounsi";
    private static final String SMTP_HOST = "smtp.gmail.com";
    private static final int SMTP_PORT = 587;

    private static MailService instance;

    private ScheduledExecutorService scheduler;
    private final Set<String> sentReminderKeys = new HashSet<>();

    private MailService() {
    }

    public static MailService getInstance() {
        if (instance == null) {
            instance = new MailService();
        }
        return instance;
    }

    public void startDailyCheck(Map<LocalDate, List<TodoItem>> todoMap, String userEmail) {
        stopScheduler();

        Map<LocalDate, List<TodoItem>> snapshot = copyTodoMap(todoMap);

        scheduler = Executors.newSingleThreadScheduledExecutor(runnable -> {
            Thread thread = new Thread(runnable, "mail-reminder-scheduler");
            thread.setDaemon(true);
            return thread;
        });

        scheduler.execute(() -> checkAndNotify(snapshot, userEmail));

        scheduler.scheduleAtFixedRate(
                () -> checkAndNotify(snapshot, userEmail),
                calculateInitialDelay(),
                TimeUnit.DAYS.toSeconds(1),
                TimeUnit.SECONDS
        );

        System.out.println("[MailService] Vérification quotidienne activée.");
    }

    public void stopScheduler() {
        if (scheduler != null && !scheduler.isShutdown()) {
            scheduler.shutdownNow();
        }
    }

    public void checkAndNotify(Map<LocalDate, List<TodoItem>> todoMap, String userEmail) {
        if (userEmail == null || userEmail.isBlank()) {
            System.err.println("[MailService] Email destinataire manquant.");
            return;
        }

        if (todoMap == null || todoMap.isEmpty()) {
            return;
        }

        LocalDate revisionDate = LocalDate.now().plusDays(3);
        List<TodoItem> items = todoMap.get(revisionDate);

        if (items == null || items.isEmpty()) {
            return;
        }

        String reminderKey = userEmail + "|" + revisionDate;

        if (sentReminderKeys.contains(reminderKey)) {
            return;
        }

        try {
            sendReminderEmail(userEmail, revisionDate, items);
            sentReminderKeys.add(reminderKey);

        } catch (MessagingException | UnsupportedEncodingException | RuntimeException e) {
            System.err.println("[MailService] Erreur envoi email : " + e.getMessage());
        }
    }

    public void sendReminderEmail(String toEmail, LocalDate revisionDate, List<TodoItem> items)
            throws MessagingException, UnsupportedEncodingException {

        Session session = createMailSession();

        MimeMessage message = new MimeMessage(session);
        message.setFrom(new InternetAddress(FROM_EMAIL, SENDER_NAME, "UTF-8"));
        message.setRecipient(Message.RecipientType.TO, new InternetAddress(toEmail));

        message.setSubject(
                "Rappel smarTounsi - révision dans 3 jours (" + revisionDate.format(dateFormat()) + ")",
                "UTF-8"
        );

        message.setSentDate(new Date());
        message.setContent(buildHtmlBody(revisionDate, items), "text/html; charset=UTF-8");

        Transport.send(message);

        System.out.println("[MailService] Email envoyé à : " + toEmail);
    }

    public void sendTestEmail(String toEmail) throws MessagingException, UnsupportedEncodingException {
        Session session = createMailSession();

        MimeMessage message = new MimeMessage(session);
        message.setFrom(new InternetAddress(FROM_EMAIL, SENDER_NAME, "UTF-8"));
        message.setRecipient(Message.RecipientType.TO, new InternetAddress(toEmail));
        message.setSubject("Test smarTounsi - service mail", "UTF-8");
        message.setSentDate(new Date());

        message.setContent(
                "<h2>smarTounsi</h2><p>Le service mail fonctionne correctement.</p>",
                "text/html; charset=UTF-8"
        );

        Transport.send(message);

        System.out.println("[MailService] Email de test envoyé à : " + toEmail);
    }

    public void sendQuizScoreEmail(String teacherEmail, Utilisateur student, Quiz quiz, int score,
                                   int total, int questionCount, boolean validated)
            throws MessagingException, UnsupportedEncodingException {
        if (teacherEmail == null || teacherEmail.isBlank()) {
            throw new MessagingException("Email enseignant manquant.");
        }

        Session session = createMailSession();

        MimeMessage message = new MimeMessage(session);
        message.setFrom(new InternetAddress(FROM_EMAIL, SENDER_NAME, "UTF-8"));
        message.setRecipient(Message.RecipientType.TO, new InternetAddress(teacherEmail));
        message.setSubject("Score quiz smarTounsi - " + safeText(quiz == null ? "" : quiz.getTitre()), "UTF-8");
        message.setSentDate(new Date());
        message.setContent(buildQuizScoreHtmlBody(student, quiz, score, total, questionCount, validated),
                "text/html; charset=UTF-8");

        Transport.send(message);
        System.out.println("[MailService] Score quiz envoye a : " + teacherEmail);
    }

    private Session createMailSession() {
        String cleanPassword = AppConfig.getCleanGmailAppPassword(MAIL_PASSWORD_KEY);

        System.out.println("[MailService] FROM_EMAIL = " + FROM_EMAIL);
        System.out.println("[MailService] APP_PASSWORD configuré = oui");
        System.out.println("[MailService] Longueur APP_PASSWORD après nettoyage = " + cleanPassword.length());

        if (cleanPassword.length() != 16) {
            throw new RuntimeException(
                    "Le mot de passe d'application Gmail doit contenir exactement 16 caractères sans espaces. Longueur actuelle : "
                            + cleanPassword.length()
            );
        }

        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", SMTP_HOST);
        props.put("mail.smtp.port", String.valueOf(SMTP_PORT));
        props.put("mail.smtp.ssl.trust", SMTP_HOST);

        return Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(FROM_EMAIL, cleanPassword);
            }
        });
    }

    private String buildHtmlBody(LocalDate revisionDate, List<TodoItem> items) {
        StringBuilder rows = new StringBuilder();

        for (TodoItem item : items) {
            rows.append("""
                    <tr>
                        <td style="padding:12px;border-bottom:1px solid #e5edf3;">
                            <strong>%s</strong>
                            <div style="color:#229DBC;margin-top:4px;">%s - %s</div>
                            %s
                            %s
                        </td>
                    </tr>
                    """.formatted(
                    escapeHtml(item.getTitre()),
                    escapeHtml(item.getType()),
                    escapeHtml(item.getHeure()),
                    formatDescription(item),
                    formatFiles(item)
            ));
        }

        return """
                <!DOCTYPE html>
                <html lang="fr">
                <head>
                    <meta charset="UTF-8">
                </head>
                <body style="margin:0;padding:0;background:#f4f7fb;font-family:Arial,sans-serif;color:#023047;">
                    <table width="100%%" cellpadding="0" cellspacing="0" style="padding:28px 0;background:#f4f7fb;">
                        <tr>
                            <td align="center">
                                <table width="600" cellpadding="0" cellspacing="0" style="background:#ffffff;border-radius:10px;overflow:hidden;">
                                    <tr>
                                        <td style="background:#023047;color:#ffffff;padding:22px 28px;text-align:center;">
                                            <div style="font-size:22px;font-weight:bold;">smarTounsi</div>
                                            <div style="color:#FEB707;margin-top:6px;">Rappel de révision</div>
                                        </td>
                                    </tr>
                                    <tr>
                                        <td style="padding:24px 28px;">
                                            <p style="margin:0 0 12px 0;">Bonjour,</p>
                                            <p style="margin:0 0 16px 0;">
                                                Vous avez <strong>%d révision(s)</strong> planifiée(s)
                                                dans 3 jours, le <strong>%s</strong>.
                                            </p>
                                            <table width="100%%" cellpadding="0" cellspacing="0" style="border:1px solid #e5edf3;border-radius:8px;">
                                                %s
                                            </table>
                                            <p style="margin:18px 0 0 0;color:#556070;font-size:13px;">
                                                Cet email a été envoyé automatiquement par smarTounsi.
                                            </p>
                                        </td>
                                    </tr>
                                </table>
                            </td>
                        </tr>
                    </table>
                </body>
                </html>
                """.formatted(items.size(), revisionDate.format(dateFormat()), rows);
    }

    private String formatDescription(TodoItem item) {
        if (item.getDescription() == null || item.getDescription().isBlank()) {
            return "";
        }

        return "<div style=\"color:#556070;margin-top:6px;\">"
                + escapeHtml(item.getDescription())
                + "</div>";
    }

    private String buildQuizScoreHtmlBody(Utilisateur student, Quiz quiz, int score, int total,
                                          int questionCount, boolean validated) {
        String studentName = student == null ? "Utilisateur" : student.getNomComplet();
        String studentEmail = student == null ? "" : student.getEmail();
        String quizTitle = quiz == null ? "Quiz" : quiz.getTitre();
        String moduleName = quiz == null ? "" : quiz.getNomModule();
        String status = validated ? "Valide" : "Non valide";
        String statusColor = validated ? "#1A7F45" : "#C2410C";

        return """
                <!DOCTYPE html>
                <html lang="fr">
                <head>
                    <meta charset="UTF-8">
                </head>
                <body style="margin:0;padding:0;background:#f4f7fb;font-family:Arial,sans-serif;color:#023047;">
                    <table width="100%%" cellpadding="0" cellspacing="0" style="padding:28px 0;background:#f4f7fb;">
                        <tr>
                            <td align="center">
                                <table width="600" cellpadding="0" cellspacing="0" style="background:#ffffff;border-radius:10px;overflow:hidden;">
                                    <tr>
                                        <td style="background:#023047;color:#ffffff;padding:22px 28px;text-align:center;">
                                            <div style="font-size:22px;font-weight:bold;">smarTounsi</div>
                                            <div style="color:#FEB707;margin-top:6px;">Resultat de quiz</div>
                                        </td>
                                    </tr>
                                    <tr>
                                        <td style="padding:24px 28px;">
                                            <p style="margin:0 0 14px 0;">Bonjour,</p>
                                            <p style="margin:0 0 18px 0;">Un etudiant vient de terminer votre quiz.</p>
                                            <table width="100%%" cellpadding="0" cellspacing="0" style="border:1px solid #e5edf3;border-radius:8px;">
                                                <tr><td style="padding:10px 12px;"><strong>Etudiant</strong></td><td style="padding:10px 12px;">%s</td></tr>
                                                <tr><td style="padding:10px 12px;"><strong>Email</strong></td><td style="padding:10px 12px;">%s</td></tr>
                                                <tr><td style="padding:10px 12px;"><strong>Quiz</strong></td><td style="padding:10px 12px;">%s</td></tr>
                                                <tr><td style="padding:10px 12px;"><strong>Module</strong></td><td style="padding:10px 12px;">%s</td></tr>
                                                <tr><td style="padding:10px 12px;"><strong>Score</strong></td><td style="padding:10px 12px;">%d / %d</td></tr>
                                                <tr><td style="padding:10px 12px;"><strong>Questions</strong></td><td style="padding:10px 12px;">%d</td></tr>
                                                <tr><td style="padding:10px 12px;"><strong>Statut</strong></td><td style="padding:10px 12px;color:%s;font-weight:bold;">%s</td></tr>
                                            </table>
                                            <p style="margin:18px 0 0 0;color:#556070;font-size:13px;">
                                                Cet email a ete envoye automatiquement par smarTounsi.
                                            </p>
                                        </td>
                                    </tr>
                                </table>
                            </td>
                        </tr>
                    </table>
                </body>
                </html>
                """.formatted(
                escapeHtml(safeText(studentName)),
                escapeHtml(safeText(studentEmail)),
                escapeHtml(safeText(quizTitle)),
                escapeHtml(safeText(moduleName)),
                score,
                total,
                questionCount,
                statusColor,
                escapeHtml(status)
        );
    }

    private String formatFiles(TodoItem item) {
        if (item.getFichiers() == null || item.getFichiers().isEmpty()) {
            return "";
        }

        StringBuilder html = new StringBuilder(
                "<ul style=\"margin:8px 0 0 18px;padding:0;color:#556070;font-size:12px;\">"
        );

        for (String file : item.getFichiers()) {
            String fileName = fileName(file);
            html.append("<li>").append(escapeHtml(fileName)).append("</li>");
        }

        html.append("</ul>");

        return html.toString();
    }

    private String fileName(String path) {
        if (path == null || path.isBlank()) {
            return "";
        }

        int slash = Math.max(path.lastIndexOf('/'), path.lastIndexOf('\\'));

        if (slash >= 0) {
            return path.substring(slash + 1);
        }

        return path;
    }

    private String escapeHtml(String value) {
        return Objects.toString(value, "")
                .replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&#39;");
    }

    private String safeText(String value) {
        return value == null ? "" : value;
    }

    private DateTimeFormatter dateFormat() {
        return DateTimeFormatter.ofPattern("dd/MM/yyyy", Locale.FRENCH);
    }

    private long calculateInitialDelay() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime nextMidnight = now.toLocalDate().plusDays(1).atStartOfDay();

        return Duration.between(now, nextMidnight).getSeconds();
    }

    private Map<LocalDate, List<TodoItem>> copyTodoMap(Map<LocalDate, List<TodoItem>> todoMap) {
        Map<LocalDate, List<TodoItem>> copy = new java.util.HashMap<>();

        if (todoMap == null) {
            return copy;
        }

        for (Map.Entry<LocalDate, List<TodoItem>> entry : todoMap.entrySet()) {
            copy.put(entry.getKey(), new ArrayList<>(entry.getValue()));
        }

        return copy;
    }
}
