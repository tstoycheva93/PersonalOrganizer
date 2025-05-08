package app.task.repository;

import app.task.model.Task;
import app.task.model.TaskStatus;
import app.user.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
@Repository
public interface TaskRepository extends JpaRepository<Task, UUID> {
    List<Task> findAllByUserAndDueDateBetween(User user, LocalDateTime startDay, LocalDateTime endDay);

    int countAllByStatus(TaskStatus status);

    int countAllByCreatedAtBetween(LocalDateTime createdAtAfter, LocalDateTime createdAtBefore);

    int countAllByStatusAndUpdatedAtBetween(TaskStatus status, LocalDateTime updatedAtAfter, LocalDateTime updatedAtBefore);

    List<Task> findAllByUpdatedAtBetween(LocalDateTime updatedAtAfter, LocalDateTime updatedAtBefore);
    @Query("SELECT t.dueDate, COUNT(t) FROM Task t WHERE t.user = :user AND t.dueDate BETWEEN :start AND :end GROUP BY t.dueDate")
    List<Object[]> countTasksByDateBetweenAndUser(@Param("start") LocalDateTime start,
                                                  @Param("end") LocalDateTime end,
                                                  @Param("user") User user);

}
