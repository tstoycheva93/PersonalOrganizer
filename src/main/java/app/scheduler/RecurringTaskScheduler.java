package app.scheduler;

import app.recurring_task.model.RecurringTask;
import app.recurring_task.repository.RecurringTaskRepository;
import app.task.service.TaskService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.nio.file.AccessDeniedException;
import java.time.LocalDateTime;
import java.util.List;

@Component
public class RecurringTaskScheduler {
    private final TaskService taskService;
    private final RecurringTaskRepository recurringTaskRepository;

    public RecurringTaskScheduler(TaskService taskService, RecurringTaskRepository recurringTaskRepository) {
        this.taskService = taskService;
        this.recurringTaskRepository = recurringTaskRepository;
    }
    @Scheduled(cron = "0 * * * * *")
    public void generateRecurringTasks() throws AccessDeniedException {
        List<RecurringTask> recurringTasks = recurringTaskRepository.findAll();
        for (RecurringTask recurringTask : recurringTasks) {
            if(shouldGenerateTask(recurringTask)){
           taskService.generateRecurringTask(recurringTask);

            }
        }
    }

    private boolean shouldGenerateTask(RecurringTask recurringTask) {
        LocalDateTime now = LocalDateTime.now();
        return(now.isBefore(recurringTask.getEndDate()) && recurringTask.getTask().getDueDate().isBefore(now));

    }
}
