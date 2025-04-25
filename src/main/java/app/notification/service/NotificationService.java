package app.notification.service;

import app.notification.model.Notification;
import org.ocpsoft.prettytime.PrettyTime;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class NotificationService {
    public List<String> getDatesPrettyPrinting(List<Notification> notifications) {
        List<String> prettyDateFormat = new ArrayList<>();
        PrettyTime pt = new PrettyTime();
        notifications.forEach(notification -> {
            prettyDateFormat.add(pt.format(notification.getCreatedAt()));
        });
        return prettyDateFormat;
    }
}
