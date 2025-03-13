package app.recurring_task.model;

import app.notification.model.NotificationType;
import app.task.model.Task;
import app.user.model.User;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

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
    @JoinColumn(unique = true)
    private Task task;
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private RecurringTaskType type;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    @Column(nullable = true)
    private Integer maxOccurrences=1000;

}