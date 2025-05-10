package app.task.service;

import app.category.model.Category;
import app.category.service.CategoryService;
import app.exception.*;
import app.recurring_task.service.RecurringTaskService;
import app.subscription.model.SubscriptionType;
import app.task.model.Task;
import app.task.model.TaskReminder;
import app.task.model.TaskStatus;
import app.task.repository.TaskRepository;
import app.user.model.User;
import app.user.service.UserService;
import app.utils.Deadline;
import app.web.dto.TaskRequest;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.file.AccessDeniedException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.chrono.ChronoLocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

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
    public void createTask(User user, TaskRequest task)  {
        if(task.getTitle().isEmpty() || (task.getTitle().length() <4 || task.getTitle().length() > 100)) {
            throw new TitleException("Task title length must be between 4 and 100 characters");
        }
        if(task.getDescription().isEmpty() || (task.getDescription().length() <20 || task.getDescription().length() > 1000)) {
            throw new DescriptionException("Task description length must be between 20 and 1000 characters");
        }
        if(task.getStartDate().isAfter(ChronoLocalDate.from(task.getDueDate()))){
            throw new DateValidation("The start date must be before due date!");
        }
        if(task.getStartDate().isBefore(LocalDate.now())){
            throw new StartDateInPast("Start date can not be in the past!");
        }
        if(task.getPriority()==null){
            throw new PriorityException("You must set priority to the task!");
        }
        if(task.getStatus()==null){
            throw new StatusException("You must set status to the task!");
        }
        if(task.getCategoryId()==null){
            throw new CategoryException("You must set category for the task!");
        }
        int tasksNumber = 0;
        for (Category category : user.getCategories()) {
           tasksNumber+= category.getTasks().size();
           if(tasksNumber>10 && user.getSubscription().getType().equals(SubscriptionType.FREE)){
               throw new NotPremiumUser("You are out of free tasks,upgrade now!");
           }
        }


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
    public void editTask(User user, TaskRequest task, Task existingTask) {
        if (task.getRecurringTaskType() != null) {
            recurringTaskService.update(task, existingTask);
        } else if (existingTask.getRecurringTask() != null) {
            existingTask.setRecurring(false);
            existingTask.setRecurringTask(null);
            taskRepository.save(existingTask);

        }

        Category categoryById = categoryService.getById(task.getCategoryId());
        existingTask.setTitle(task.getTitle());
        existingTask.setDescription(task.getDescription());
        existingTask.setDueDate(task.getDueDate());
        existingTask.setPriority(task.getPriority());
        existingTask.setStatus(task.getStatus());
        if (!task.getCategoryId().equals(existingTask.getCategory().getId())) {
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
        return taskRepository.findById(id).orElseThrow(() -> new RuntimeException("No such task"));
    }

    public Map<LocalDate, Integer> getTaskCountsBetweenDatesAndUser(LocalDate start, LocalDate end, User user) {
        List<Object[]> results = taskRepository.countTasksByDateBetweenAndUser(start.atStartOfDay(), end.atTime(23, 59, 59), user);
        Map<LocalDate, Integer> taskCounts = new HashMap<>();
        for (Object[] result : results) {
            LocalDateTime date = (LocalDateTime) result[0];
            Long count = (Long) result[1];
            taskCounts.put(date.toLocalDate(), count.intValue());
        }
        return taskCounts;
    }


    @Transactional
    public void deleteTask(User user, UUID id) throws AccessDeniedException {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Task not found"));
        if (!task.getUser().getId().equals(user.getId())) {
            throw new AccessDeniedException("You don't have permission to delete this task.");
        }
        UUID categoryId = task.getCategory().getId();
        Category category = user.getCategories()
                .stream()
                .filter(c -> c.getId().equals(categoryId))
                .findFirst()
                .orElseThrow(() -> new EntityNotFoundException("Category not found for user"));
        category.getTasks().remove(task);
        categoryService.save(category);
        taskRepository.delete(task);
    }


    public Map<String, Integer> getCountOfTasksByStatus(User user) {
        Map<String, Integer> countOfTasksByStatus = new HashMap<>();
        countOfTasksByStatus.put("completed", 0);
        countOfTasksByStatus.put("inProgress", 0);
        countOfTasksByStatus.put("notStarted", 0);
        for (Category category : user.getCategories()) {
            for (Task task : category.getTasks()) {
                if (task.getStatus() == TaskStatus.COMPLETED) {
                    countOfTasksByStatus.put("completed", countOfTasksByStatus.get("completed") + 1);
                } else if (task.getStatus() == TaskStatus.IN_PROGRESS) {
                    countOfTasksByStatus.put("inProgress", countOfTasksByStatus.get("inProgress") + 1);
                } else if (task.getStatus() == TaskStatus.NOT_STARTED) {
                    countOfTasksByStatus.put("notStarted", countOfTasksByStatus.get("notStarted") + 1);
                }
            }
        }
        return countOfTasksByStatus;
    }

    public List<Deadline> getTasksIntoDeadlineObject(User user) {
        List<Deadline> deadlines = new ArrayList<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEEE, MMMM d HH:mm", Locale.ENGLISH);

        for (Category category : user.getCategories()) {
            for (Task task : category.getTasks()) {
                if (task.getDueDate().isAfter(LocalDateTime.now())) {
                    deadlines.add(Deadline.builder()
                            .id(task.getId())
                            .title(task.getTitle())
                            .category(category.getName())
                            .dueDate(task.getDueDate().format(formatter))
                            .priority(task.getPriority().toString())
                            .status(task.getStatus().toString())
                            .build());
                }
            }
        }
        return deadlines;
    }

    public int getAllCreatedTasks() {
        return taskRepository.countAllByStatus(TaskStatus.NOT_STARTED);
    }

    public List<Integer> getAllCreatedTasksByDay() {
        List<Integer> createdTasks = new ArrayList<>();
        LocalDate today = LocalDate.now();
        LocalDate thisWeekMonday = today.with(DayOfWeek.MONDAY);
        LocalDate lastWeekStart = thisWeekMonday.minusWeeks(1);
        for (int i = 0; i < 7; i++) {
            int count = taskRepository.countAllByCreatedAtBetween(lastWeekStart.atStartOfDay(), lastWeekStart.atTime(23, 59, 59));
            createdTasks.add(count);
            lastWeekStart = lastWeekStart.plusDays(1);
        }
        return createdTasks;
    }

    public List<Integer> getAllCompletedTasksByDay() {
        List<Integer> completedTasks = new ArrayList<>();
        LocalDate today = LocalDate.now();
        LocalDate thisWeekMonday = today.with(DayOfWeek.MONDAY);
        LocalDate lastWeekStart = thisWeekMonday.minusWeeks(1);
        for (int i = 0; i < 7; i++) {
            int count = taskRepository.countAllByStatusAndUpdatedAtBetween(TaskStatus.COMPLETED, lastWeekStart.atStartOfDay(), lastWeekStart.atTime(23, 59, 59));
            completedTasks.add(count);
            lastWeekStart = lastWeekStart.plusDays(1);
        }
        return completedTasks;
    }

    public List<Task> getRecentActivityForDay(LocalDateTime now) {
        return taskRepository.findAllByUpdatedAtBetween(now, now.toLocalDate().atTime(23, 59, 59));
    }

    public int getAllTasksCount() {
        return taskRepository.findAll().size();
    }

    public List<Task> getAllTask() {
        return taskRepository.findAll();
    }
}
