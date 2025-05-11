package app.notification.service;

import app.notification.model.Notification;
import app.notification.repository.NotificationRepository;
import app.subscription.model.Subscription;
import app.subscription.model.SubscriptionType;
import app.task.model.Task;
import app.user.model.User;
import app.user.service.UserService;
import app.web.dto.ForgotPasswordRequest;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.transaction.Transactional;
import org.ocpsoft.prettytime.PrettyTime;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


@Service
public class NotificationService {
    private final JavaMailSender mailSender;
    private final UserService userService;
    private final NotificationRepository notificationRepository;
    @Value("${spring.mail.username}")
    private String adminEmail;

    public NotificationService(JavaMailSender mailSender, UserService userService, NotificationRepository notificationRepository) {
        this.mailSender = mailSender;
        this.userService = userService;
        this.notificationRepository = notificationRepository;
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

    private String buildEmailBodySendNewPassword(String username, String newPassword) {
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
                            .password-box {
                                background-color: #f2f2f2;
                                border: 1px dashed #0077cc;
                                padding: 10px 15px;
                                font-size: 18px;
                                font-weight: bold;
                                text-align: center;
                                margin: 20px 0;
                                color: #0077cc;
                                border-radius: 6px;
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
                            <div class="header">Password Reset</div>
                            <div class="content">
                                <p>Hello, %s!</p>
                                <p>Your password has been reset. Here is your new password:</p>
                                <div class="password-box">%s</div>
                                <p>We recommend logging in and changing this password as soon as possible.</p>
                                <p>Thank you for using Taskly!</p>
                            </div>
                            <div class="footer">
                                &copy; 2025 Taskly. All rights reserved.
                            </div>
                        </div>
                    </body>
                    </html>
                """, username, newPassword);
    }

    @Transactional
    public void sendNewPassword(ForgotPasswordRequest forgotPasswordRequest) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
        String password = generatePassword();
        User user = userService.getByEmail(forgotPasswordRequest.getEmail());
        String emailBody = buildEmailBodySendNewPassword(user.getUsername(), password);
        helper.setTo(user.getEmail());
        helper.setSubject("New Password");
        helper.setText(emailBody, true);
        mailSender.send(message);
        userService.setNewPassword(user,password);

    }

    private String generatePassword() {
        SecureRandom random = new SecureRandom();
        StringBuilder password = new StringBuilder();
        String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%^&*()-_=+";
        int PASSWORD_LENGTH = 12;
        for (int i = 0; i < PASSWORD_LENGTH; i++) {
            int index = random.nextInt(CHARACTERS.length());
            password.append(CHARACTERS.charAt(index));
        }

        return password.toString();
    }

    public Notification save(Notification notification) {
        return notificationRepository.save(notification);
    }

    public Notification getByTask(Task task) {
        Optional<Notification> byTask = notificationRepository.findByTaskId(task.getId());
        return byTask.orElse(null);
    }
}
