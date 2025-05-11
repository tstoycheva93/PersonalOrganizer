package app.task.model;

import app.category.model.Category;
import app.notification.model.Notification;
import app.recurring_task.model.RecurringTask;
import app.user.model.User;
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

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String description;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;

    @Column(nullable = false)
    private LocalDateTime startDate;

    @Column(nullable = false)
    private LocalDateTime dueDate;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private TaskPriority priority;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private TaskStatus status;

    private boolean isRecurring;

    private boolean hasReminder;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private TaskReminder reminder;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    private boolean isDeleted;

    @OneToOne(mappedBy = "task", cascade = CascadeType.ALL, orphanRemoval = true)
    private RecurringTask recurringTask;
}