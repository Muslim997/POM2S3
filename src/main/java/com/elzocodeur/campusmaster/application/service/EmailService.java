package com.elzocodeur.campusmaster.application.service;

import com.elzocodeur.campusmaster.domain.entity.NotificationPush;
import com.elzocodeur.campusmaster.domain.entity.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Service
@Slf4j
public class EmailService {

    private final JavaMailSender mailSender;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    @Value("${spring.mail.from:noreply@campusmaster.com}")
    private String fromEmail;

    @Value("${app.name:CampusMaster}")
    private String appName;

    @Async
    public void sendSimpleEmail(String to, String subject, String text) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(to);
            message.setSubject(subject);
            message.setText(text);

            mailSender.send(message);
            log.info("Email envoyé avec succès à {}", to);
        } catch (Exception e) {
            log.error("Erreur lors de l'envoi de l'email à {}: {}", to, e.getMessage());
            throw new RuntimeException("Échec de l'envoi de l'email", e);
        }
    }

    @Async
    public void sendHtmlEmail(String to, String subject, String htmlContent) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(fromEmail);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(htmlContent, true);

            mailSender.send(message);
            log.info("Email HTML envoyé avec succès à {}", to);
        } catch (MessagingException e) {
            log.error("Erreur lors de l'envoi de l'email HTML à {}: {}", to, e.getMessage());
            throw new RuntimeException("Échec de l'envoi de l'email HTML", e);
        }
    }

    @Async
    public void sendNotificationEmail(User user, NotificationPush notification) {
        String htmlContent = buildNotificationEmailTemplate(user, notification);
        sendHtmlEmail(user.getEmail(), notification.getTitle(), htmlContent);
    }

    private String buildNotificationEmailTemplate(User user, NotificationPush notification) {
        StringBuilder html = new StringBuilder();
        html.append("<!DOCTYPE html>");
        html.append("<html>");
        html.append("<head>");
        html.append("<meta charset='UTF-8'>");
        html.append("<style>");
        html.append("body { font-family: Arial, sans-serif; line-height: 1.6; color: #333; }");
        html.append(".container { max-width: 600px; margin: 0 auto; padding: 20px; }");
        html.append(".header { background-color: #4CAF50; color: white; padding: 20px; text-align: center; }");
        html.append(".content { background-color: #f9f9f9; padding: 20px; margin-top: 20px; }");
        html.append(".footer { text-align: center; margin-top: 20px; color: #666; font-size: 12px; }");
        html.append(".priority-urgent { border-left: 4px solid #f44336; }");
        html.append(".priority-high { border-left: 4px solid #ff9800; }");
        html.append(".priority-normal { border-left: 4px solid #2196F3; }");
        html.append(".priority-low { border-left: 4px solid #9E9E9E; }");
        html.append(".action-button { display: inline-block; padding: 10px 20px; background-color: #4CAF50; ");
        html.append("color: white; text-decoration: none; border-radius: 5px; margin-top: 15px; }");
        html.append("</style>");
        html.append("</head>");
        html.append("<body>");
        html.append("<div class='container'>");

        // Header
        html.append("<div class='header'>");
        html.append("<h1>").append(appName).append("</h1>");
        html.append("</div>");

        // Content
        String priorityClass = "priority-" + notification.getPriority().name().toLowerCase();
        html.append("<div class='content ").append(priorityClass).append("'>");
        html.append("<h2>").append(notification.getTitle()).append("</h2>");
        html.append("<p>Bonjour ").append(user.getFirstName()).append(" ").append(user.getLastName()).append(",</p>");
        html.append("<p>").append(notification.getMessage()).append("</p>");

        // Action button if URL is provided
        if (notification.getActionUrl() != null && !notification.getActionUrl().isEmpty()) {
            html.append("<a href='").append(notification.getActionUrl()).append("' class='action-button'>");
            html.append("Voir les détails");
            html.append("</a>");
        }

        html.append("</div>");

        // Footer
        html.append("<div class='footer'>");
        html.append("<p>Vous recevez cet email car vous êtes inscrit sur ").append(appName).append(".</p>");
        html.append("<p>Pour gérer vos préférences de notification, connectez-vous à votre compte.</p>");
        html.append("</div>");

        html.append("</div>");
        html.append("</body>");
        html.append("</html>");

        return html.toString();
    }

    public void sendDevoirPublieEmail(User user, String devoirTitre, String coursNom, String deadline) {
        String subject = "Nouveau devoir : " + devoirTitre;
        String htmlContent = buildDevoirEmailTemplate(user, devoirTitre, coursNom, deadline, "publié");
        sendHtmlEmail(user.getEmail(), subject, htmlContent);
    }

    public void sendDevoirCorrigeEmail(User user, String devoirTitre, Double note) {
        String subject = "Devoir corrigé : " + devoirTitre;
        String message = String.format(
            "Bonjour %s %s,\n\nVotre devoir '%s' a été corrigé.\nNote obtenue : %.2f/20\n\n" +
            "Connectez-vous pour voir les détails et les commentaires de votre enseignant.",
            user.getFirstName(), user.getLastName(), devoirTitre, note
        );
        sendSimpleEmail(user.getEmail(), subject, message);
    }

    public void sendDeadlineProche(User user, String devoirTitre, String deadline) {
        String subject = "Rappel : Deadline proche - " + devoirTitre;
        String htmlContent = buildDevoirEmailTemplate(user, devoirTitre, null, deadline, "deadline");
        sendHtmlEmail(user.getEmail(), subject, htmlContent);
    }

    private String buildDevoirEmailTemplate(User user, String devoirTitre, String coursNom,
                                           String deadline, String type) {
        StringBuilder html = new StringBuilder();
        html.append("<!DOCTYPE html>");
        html.append("<html><head><meta charset='UTF-8'></head><body>");
        html.append("<div style='font-family: Arial, sans-serif; max-width: 600px; margin: 0 auto;'>");
        html.append("<h2>").append(appName).append("</h2>");
        html.append("<p>Bonjour ").append(user.getFirstName()).append(" ").append(user.getLastName()).append(",</p>");

        if ("publié".equals(type)) {
            html.append("<p>Un nouveau devoir a été publié :</p>");
            html.append("<ul>");
            html.append("<li><strong>Devoir :</strong> ").append(devoirTitre).append("</li>");
            if (coursNom != null) {
                html.append("<li><strong>Cours :</strong> ").append(coursNom).append("</li>");
            }
            html.append("<li><strong>Date limite :</strong> ").append(deadline).append("</li>");
            html.append("</ul>");
        } else if ("deadline".equals(type)) {
            html.append("<p style='color: #f44336;'><strong>Rappel : La deadline approche !</strong></p>");
            html.append("<p>Le devoir '<strong>").append(devoirTitre).append("</strong>' ");
            html.append("doit être rendu avant le <strong>").append(deadline).append("</strong>.</p>");
        }

        html.append("<p>Connectez-vous pour plus de détails.</p>");
        html.append("</div></body></html>");

        return html.toString();
    }
}
