package app.recurring_task.repository;

import app.recurring_task.model.RecurringTask;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface RecurringTaskRepository extends JpaRepository<RecurringTask, UUID> {
}
