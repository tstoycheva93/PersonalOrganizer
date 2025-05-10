package app.scheduler;

import app.notification.model.Notification;
import app.notification.model.NotificationType;
import app.notification.service.NotificationService;
import app.task.model.Task;
import app.task.model.TaskStatus;
import app.task.service.TaskService;
import app.user.model.User;
import app.user.service.UserService;
import jakarta.transaction.Transactional;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Component
public class MyTaskScheduler {

    private final TaskService taskService;
    private final NotificationService notificationService;
    private final UserService userService;

    public MyTaskScheduler(TaskService taskService, NotificationService notificationService, UserService userService) {
        this.taskService = taskService;
        this.notificationService = notificationService;
        this.userService = userService;
    }

    @Scheduled(cron = "0 * * * * *")
    @Transactional
    void makeReminders() {
        List<Task> allTask = taskService.getAllTask();
        for (Task task : allTask) {
            LocalDateTime now = LocalDateTime.now();
            LocalDateTime due = task.getDueDate();
            if (due.isAfter(now) && task.getStatus() != TaskStatus.COMPLETED) {
                Notification notification = buildNotificationIfTaskIsNotFinishedOnTime(task);
                Notification byTask = notificationService.getByTask(task);
                if (byTask == null) {
                    saveNotification(task, notification);
                }
            }
            Duration duration = Duration.between(now, due);
            if (!duration.isNegative() && duration.toMinutes() <= 30) {
                Notification notification = buildNotificationForTaskThatOnlyLeft30Minutes(task, duration);
                Notification byTask = notificationService.getByTask(task);
                if (byTask == null || byTask.getBody().equals("For task %s left only %s minutes".formatted(task.getTitle(), duration.toMinutes()))) {
                    saveNotification(task, notification);
                }
            }
        }
    }

    private static Notification buildNotificationForTaskThatOnlyLeft30Minutes(Task task, Duration duration) {
        return Notification.builder()
                .task(task)
                .user(task.getUser())
                .isRead(false)
                .isDeleted(false)
                .body("For task %s left only %s minutes".formatted(task.getTitle(), duration.toMinutes()))
                .type(NotificationType.WEB)
                .subject("Not completed task")
                .build();
    }

    private static Notification buildNotificationIfTaskIsNotFinishedOnTime(Task task) {
        return Notification.builder()
                .task(task)
                .user(task.getUser())
                .isRead(false)
                .isDeleted(false)
                .body("The task %s has not been completed on time".formatted(task.getTitle()))
                .type(NotificationType.WEB)
                .subject("Not completed task")
                .build();
    }

    private void saveNotification(Task task, Notification notification) {
        Notification save = notificationService.save(notification);
        User user = task.getUser();
        if (user.getNotifications() == null) {
            user.setNotifications(new ArrayList<>());
        }
        user.getNotifications().add(save);
        userService.save(user);
    }
}
