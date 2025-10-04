package com.andy.tempoapp.service.internal;

import com.andy.tempoapp.entity.Alert;
import com.andy.tempoapp.entity.Subscription;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    public void sendEmail(String to, String subject, String text) throws MessagingException {
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);

        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(text, true);

        mailSender.send(mimeMessage);
    }

    public void sendAlertNotification(Alert alert, Double currentValue) {
        if (alert == null) {
            log.warn("Attempted to send email for null alert");
            return;
        }

        String to = null;
        Subscription subscription = alert.getSubscription();
        if (subscription != null) {
            try {
                if (subscription.getUser() != null) {
                    to = subscription.getUser().getEmail();
                }
            } catch (Exception ignored) { }
        }

        if (to == null || to.isBlank()) {
            log.warn("No recipient email found for alert id={}", alert.getId());
            return;
        }

        String subject = "Sensor alert triggered";
        String location = subscription != null ? subscription.getLocationId() : "unknown";
        String body = String.format("<div style=\"font-family:Arial,sans-serif;background:#f4f4f4;padding:24px;border-radius:6px;border:1px solid #e0e0e0;\">" +
                        "<h2 style=\"color:#333;margin-bottom:16px;\">Alert Notification</h2>" +
                        "<p style=\"font-size:15px;color:#222;\"><b>Alert ID:</b> %s<br/>" +
                        "<b>Sensor ID:</b> %s<br/>" +
                        "<b>Location:</b> %s<br/>" +
                        "<b>Current Value:</b> %s<br/>" +
                        "<b>Threshold:</b> %s</p></div>",
                alert.getId(), alert.getSensorId(), location,
                currentValue, alert.getThreshold()
        );

        try {
            sendEmail(to, subject, body);
            log.info("Sent alert email to {} for alert id={}", to, alert.getId());
        } catch (MessagingException me) {
            log.error("Failed to send alert email to {} for alert id={}: {}", to, alert.getId(), me.getMessage(), me);
        } catch (Exception e) {
            log.error("Unexpected error sending alert email to {} for alert id={}: {}", to, alert.getId(), e.getMessage(), e);
        }
    }

}
