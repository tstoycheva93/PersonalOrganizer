package app.utils;

import app.task.model.Task;
import app.task.model.TaskStatus;
import app.task.service.TaskService;
import app.user.model.User;
import app.user.service.UserService;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Component
public class RecentActivities {

    private final TaskService taskService;
    private final UserService userService;

    public RecentActivities(TaskService taskService, UserService userService) {
        this.taskService = taskService;
        this.userService = userService;
    }

    public List<RecentActivity> getRecentActivities() {
        List<Task> tasks = taskService.getRecentActivityForDay(LocalDate.now().atStartOfDay());
        List<User> users = userService.getRecentActivityForDay(LocalDate.now().atStartOfDay());
        List<RecentActivity> recentActivities = new ArrayList<>();
        LocalDate now = LocalDate.now();
        getTasksAsRecentActivity(tasks, now, recentActivities);
        getUserAsRecentActivity(users, now, recentActivities);
        return recentActivities;
    }

    private void getUserAsRecentActivity(List<User> users, LocalDate now, List<RecentActivity> recentActivities) {
        for (User user : users) {
            RecentActivity build = RecentActivity.builder()
                    .date(now)
                    .activity("Account Created")
                    .description("New User Registered")
                    .username(user.getUsername())
                    .build();
            recentActivities.add(build);
        }
    }

    private void getTasksAsRecentActivity(List<Task> tasks, LocalDate now, List<RecentActivity> recentActivities) {
        for (Task task : tasks) {
            String activity;
            String description;
            if (task.getStatus() == TaskStatus.NOT_STARTED) {
                activity = "Task Created";
                description = "Created new task %s".formatted(task.getTitle());
            } else if (task.getStatus() == TaskStatus.COMPLETED) {
                activity = "Task Completed";
                description = "Completed task %s".formatted(task.getTitle());
            } else if (task.getStatus() == TaskStatus.IN_PROGRESS) {
                activity = "Task In Progress";
                description = "In progress task %s".formatted(task.getTitle());
            } else {
                activity = "Task Not Started";
                description = "Task %s Not Started".formatted(task.getTitle());
            }

            RecentActivity build = RecentActivity.builder()
                    .date(now)
                    .activity(activity)
                    .description(description)
                    .username(task.getUser().getUsername())
                    .build();
            recentActivities.add(build);

        }
    }
}
