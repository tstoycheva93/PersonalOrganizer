package app.task.model;

import app.category.model.Category;
import app.notification.model.Notification;
import app.recurring_task.model.RecurringTask;
import app.subscription.model.Subscription;
import app.user.model.User;
import app.user.model.UserRole;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@Getter
@Setter
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
public class Task {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    @OneToOne
    private User createdBy;
    @OneToOne
    private User changedBy;
    @Column(nullable = false)
    private String title;
    @Column(nullable = false)
    private String description;
    @ManyToOne
    private Category category;
    @Column(nullable = false)
    private LocalDateTime dueDate;
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private TaskPriority priority;
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private TaskStatus status;
    private boolean isRecurring;
    @CreationTimestamp
    private LocalDateTime createdAt;
    @UpdateTimestamp
    private LocalDateTime updatedAt;
    private boolean isDeleted;
    private LocalDateTime completedAt;


}
