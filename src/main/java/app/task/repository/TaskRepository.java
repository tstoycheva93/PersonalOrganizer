package app.task.repository;

import app.task.model.Task;
import app.user.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
@Repository
public interface TaskRepository extends JpaRepository<Task, UUID> {
    List<Task> findAllByUserAndDueDateBetween(User user, LocalDateTime startDay, LocalDateTime endDay);
}
