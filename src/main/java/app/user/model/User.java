// src/main/java/app/user/model/User.java
package app.user.model;

import app.category.model.Category;
import app.notification.model.Notification;
import app.subscription.model.Subscription;
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
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String firstName;

    @Column(nullable = false)
    private String lastName;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String photoPath;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private UserRole role;

    private boolean isActive;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    @OneToOne(mappedBy = "user")
    private Subscription subscription;

    @OneToMany(mappedBy = "user")
    private List<Notification> notifications;

    @OneToMany(mappedBy = "user")
    private List<Category> categories;
}