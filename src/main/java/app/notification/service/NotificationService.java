package app.notification.service;

import app.notification.model.Notification;
import app.subscription.model.Subscription;
import app.subscription.model.SubscriptionType;
import app.user.model.User;
import app.user.service.UserService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.ocpsoft.prettytime.PrettyTime;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;

import java.util.ArrayList;
import java.util.List;

@Service
public class NotificationService {
    private final JavaMailSender mailSender;
    private final UserService userService;
    @Value("${spring.mail.username}")
    private String adminEmail;

    public NotificationService(JavaMailSender mailSender, UserService userService) {
        this.mailSender = mailSender;
        this.userService = userService;
    }

    public List<String> getDatesPrettyPrinting(List<Notification> notifications) {
        List<String> prettyDateFormat = new ArrayList<>();
        PrettyTime pt = new PrettyTime();
        notifications.forEach(notification -> {
            prettyDateFormat.add(pt.format(notification.getCreatedAt()));
        });
        return prettyDateFormat;
    }

    public void sendEmail(String recipientOption, String subject, String content, String userSearch) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
        if (userSearch != null && !userSearch.isEmpty()) {
            User user = userService.getByEmail(userSearch);
            String emailBody = buildEmailBody(user.getUsername(), content);
            helper.setTo(userSearch);
            helper.setSubject(subject);
            helper.setText(emailBody, true);
            mailSender.send(message);
            return;
        }
        switch (recipientOption) {
            case "all" -> {
                List<User> users = userService.getAll();
                for (User user : users) {
                    String emailBody = buildEmailBody(user.getUsername(), content);
                    helper.setTo(user.getEmail());
                    helper.setSubject(subject);
                    helper.setText(emailBody, true);
                    mailSender.send(message);
                }
            }
            case "active" -> {
                List<User> users = userService.getAll();
                for (User user : users) {
                    if (!user.isActive()) {
                        continue;
                    }
                    String emailBody = buildEmailBody(user.getUsername(), content);
                    helper.setTo(user.getEmail());
                    helper.setSubject(subject);
                    helper.setText(emailBody, true);
                    mailSender.send(message);
                }
            }
            case "premium" -> {
                List<User> users = userService.getAll();
                for (User user : users) {
                    if (user.getSubscription().getType() == SubscriptionType.FREE) {
                        continue;
                    }
                    String emailBody = buildEmailBody(user.getUsername(), content);
                    helper.setTo(user.getEmail());
                    helper.setSubject(subject);
                    helper.setText(emailBody, true);
                    mailSender.send(message);
                }
            }
        }
    }

    private String buildEmailBody(String username, String messageContent) {
        return String.format("""
                    <html>
                    <head>
                        <style>
                            body {
                                font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
                                background-color: #e6f0fa;
                                margin: 0;
                                padding: 20px;
                            }
                            .container {
                                background-color: #ffffff;
                                max-width: 600px;
                                margin: auto;
                                border-radius: 10px;
                                overflow: hidden;
                                box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
                            }
                            .header {
                                background-color: #0077cc;
                                color: white;
                                text-align: center;
                                padding: 20px 0;
                                font-size: 24px;
                                font-weight: 600;
                                letter-spacing: 1px;
                            }
                            .content {
                                padding: 30px;
                                color: #333333;
                                font-size: 16px;
                                line-height: 1.6;
                            }
                            .content p {
                                margin: 0 0 15px;
                            }
                            .footer {
                                background-color: #f0f4f8;
                                text-align: center;
                                padding: 15px;
                                font-size: 13px;
                                color: #666;
                                border-top: 1px solid #ddd;
                            }
                        </style>
                    </head>
                    <body>
                        <div class="container">
                            <div class="header">Hello, %s!</div>
                            <div class="content">
                                <p>%s</p>
                                <p>Thank you for being part of the Taskly community!</p>
                            </div>
                            <div class="footer">
                                &copy; 2025 Taskly. All rights reserved.
                            </div>
                        </div>
                    </body>
                    </html>
                """, username, messageContent);
    }
}
