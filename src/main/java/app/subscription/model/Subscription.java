package app.subscription.model;

import app.notification.model.NotificationType;
import app.user.model.User;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import lombok.Getter;
import lombok.Setter;
import lombok.Builder;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;
@Entity
@Getter
@Setter
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
public class Subscription {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    @OneToOne
    private User user;
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private SubscriptionPeriod period;
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private SubscriptionType type;
    @Column(nullable = false)
    private BigDecimal price;
    private boolean renewable;
    @CreationTimestamp
    private LocalDateTime createdAt;
    @UpdateTimestamp
    private LocalDateTime completedAt;
}
