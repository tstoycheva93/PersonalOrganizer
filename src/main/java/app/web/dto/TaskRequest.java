package app.web.dto;

import app.category.model.Category;
import app.recurring_task.model.RecurringTaskType;
import app.task.model.TaskPriority;
import app.task.model.TaskStatus;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TaskRequest {
    private UUID id;
    private String title;
    private String description;
    private UUID categoryId;
    private LocalDateTime dueDate;
    private TaskPriority  priority;
    private TaskStatus status;
    private RecurringTaskType recurringTaskType;
    private LocalDate endOccurrence;
    private LocalDate startDate;
}
