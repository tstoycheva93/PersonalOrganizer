package app.task.service;

import app.category.model.Category;
import app.category.service.CategoryService;
import app.recurring_task.model.RecurringTaskType;
import app.recurring_task.service.RecurringTaskService;
import app.task.model.Task;
import app.task.model.TaskPriority;
import app.task.model.TaskReminder;
import app.task.model.TaskStatus;
import app.task.repository.TaskRepository;
import app.user.model.User;
import app.user.service.UserService;
import app.web.dto.TaskRequest;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

@Service
public class TaskService {
    private final TaskRepository taskRepository;
    private final ConversionService conversionService;
    private final CategoryService categoryService;
    private final RecurringTaskService recurringTaskService;
    private final UserService userService;

    public TaskService(TaskRepository taskRepository, ConversionService conversionService, CategoryService categoryService, RecurringTaskService recurringTaskService, UserService userService) {
        this.taskRepository = taskRepository;
        this.conversionService = conversionService;
        this.categoryService = categoryService;
        this.recurringTaskService = recurringTaskService;
        this.userService = userService;
    }

    @Transactional
    public void createTask(User user, TaskRequest task) {
        Category categoryById = categoryService.getById(task.getCategoryId());
        Task newtask = conversionService.convert(task, Task.class);
        newtask.setUser(user);
        newtask.setCategory(categoryById);
        newtask.setReminder(TaskReminder.NONE);
        Task savedTask = taskRepository.save(newtask);
        if (task.getRecurringTaskType() != null) {
            recurringTaskService.save(user, task, savedTask);
        }
        if (categoryById.getTasks() == null) {
            categoryById.setTasks(new ArrayList<>());
        }
        categoryById.getTasks().add(savedTask);
        categoryService.save(categoryById);
    }

    public List<Task> getTasks(User user) {
        User userById = userService.getById(user.getId());
        List<Task> userTasks = new ArrayList<>();
        for (Category category : user.getCategories()) {
            userTasks.addAll(category.getTasks());
        }
        return userTasks;
    }

    public int getTasksCount(User userById, TaskStatus taskStatus) {
        int count = 0;
        for (Category category : userById.getCategories()) {
            for (Task task : category.getTasks()) {
                if (task.getStatus() == taskStatus) {
                    count++;
                }
            }

        }
        return count;
    }

    public List<Task> getTasksByCategory(User userById, Category categoryById) {
        List<Task> allTasksByCategory = new ArrayList<>();
        for (Category category : userById.getCategories()) {
            if (category == categoryById) {
                allTasksByCategory.addAll(category.getTasks());
            }
        }
        return allTasksByCategory;
    }

    public int getTasksCountByCategory(User userById, TaskStatus taskStatus, Category categoryById) {
        int count = 0;
        for (Category category : userById.getCategories()) {
            if (category == categoryById) {
                for (Task task : category.getTasks()) {
                    if (task.getStatus() == taskStatus) {
                        count++;
                    }
                }
            }

        }
        return count;
    }

    public List<Task> sort(List<Task> tasks, String sort) {
        if (sort.equals("priority")) {
            tasks.sort(Comparator.comparing(Task::getPriority));
        } else if (sort.equals("due-date")) {
            tasks.sort(Comparator.comparing(Task::getDueDate));

        } else {
            // tasks.sort(Comparator.comparing(Category::Name));
            return tasks;
        }
        return tasks;
    }
@Transactional
    public void editTask(User user, TaskRequest task,Task existingTask) {
    if(task.getRecurringTaskType()!=null){
        recurringTaskService.update(task,existingTask);
    }
    Category categoryById = categoryService.getById(task.getCategoryId());
    existingTask.setTitle(task.getTitle());
    existingTask.setDescription(task.getDescription());
    existingTask.setDueDate(task.getDueDate());
    existingTask.setPriority(task.getPriority());
    existingTask.setStatus(task.getStatus());
    if(!task.getCategoryId().equals(existingTask.getCategory().getId())){
        categoryById.getTasks().add(existingTask);
        categoryService.save(categoryById);
        Category oldCategory = existingTask.getCategory();
        existingTask.setCategory(categoryById);
        oldCategory.getTasks().remove(existingTask);
        categoryService.save(oldCategory);

    }
    taskRepository.save(existingTask);

    }

    public Task getById(UUID id) {
        return taskRepository.findById(id).orElseThrow(()->new RuntimeException("No such task"));
    }
}
