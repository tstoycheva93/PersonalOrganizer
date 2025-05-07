package app.user.repository;

import app.subscription.model.SubscriptionType;
import app.user.model.UserRole;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import app.user.model.User;

import java.time.LocalDateTime;
import java.util.List;
import javax.management.relation.Role;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {
    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);

    List<User> findTop3BySubscriptionTypeOrderBySubscriptionCountDesc(SubscriptionType subscriptionType);
List<User> findUserByRole(UserRole role);

    List<User> findAllByCreatedAtBetween(LocalDateTime createdAtAfter, LocalDateTime createdAtBefore);

}
