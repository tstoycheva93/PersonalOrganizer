package app.web.converters;

import app.task.model.Task;
import app.web.dto.TaskRequest;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class FromTaskRequestToTask implements Converter<TaskRequest, Task> {
    @Override
    public Task convert(TaskRequest task) {

        return Task.builder()
                .id(task.getId())
                .title(task.getTitle())
                .description(task.getDescription())
                .priority(task.getPriority())
                .status(task.getStatus())
                .dueDate(task.getDueDate())
                .startDate(task.getStartDate().atStartOfDay())
                .build();  }

}
