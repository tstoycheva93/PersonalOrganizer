package app.web.dto;

import app.task.model.TaskPriority;
import lombok.*;

import java.util.UUID;

@Getter
@Setter
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class CategoryCombinedWithTask {
    private String color;
    private String titleTask;
    private String date;
    private TaskPriority priority;
}
