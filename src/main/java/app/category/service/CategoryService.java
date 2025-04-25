package app.category.service;

import app.category.model.Category;
import app.task.model.Task;
import app.web.dto.CategoryCombinedWithTask;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
public class CategoryService {
    public List<CategoryCombinedWithTask> makeCombinedObject(List<Category> categories, LocalDate parse) {
        List<CategoryCombinedWithTask> categoryCombinedWithTasks = new ArrayList<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
        for (Category category : categories) {
            for (Task task : category.getTasks()) {
                if (parse.equals(task.getDueDate().toLocalDate())) {
                    CategoryCombinedWithTask c = new CategoryCombinedWithTask();
                    c.setColor(category.getColor());
                    c.setPriority(task.getPriority());
                    c.setTitleTask(task.getTitle());
                    c.setDate(task.getDueDate().toLocalTime().format(formatter));
                    categoryCombinedWithTasks.add(c);
                }
            }
        }
        return categoryCombinedWithTasks;
    }
}
