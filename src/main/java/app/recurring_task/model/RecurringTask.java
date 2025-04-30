// src/main/java/app/recurring_task/model/RecurringTask.java
package app.recurring_task.model;

import app.task.model.Task;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter
@Setter
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
public class RecurringTask {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @OneToOne
    @JoinColumn(name = "task_id", unique = true)
    private Task task;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private RecurringTaskType type;

    private LocalDateTime startDate;
    private LocalDateTime endDate;

    @Column(nullable = true)
    private Integer maxOccurrences = 1000;
}