package app.web;

import app.category.model.Category;
import app.category.service.CategoryService;
import app.exception.NotPremiumUser;
import app.recurring_task.model.RecurringTaskType;
import app.security.AuthUser;
import app.task.model.Task;
import app.task.model.TaskPriority;
import app.task.model.TaskStatus;
import app.task.service.TaskService;
import app.user.model.User;
import app.user.service.UserService;
import app.web.dto.CategoryRequest;
import app.web.dto.TaskRequest;
import jakarta.annotation.Priority;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.nio.file.AccessDeniedException;
import java.util.List;
import java.util.UUID;

@Controller
@RequestMapping("/tasks")
public class TaskController {
    private final TaskService taskService;
    private final UserService userService;
    private final CategoryService categoryService;

    public TaskController(TaskService taskService, UserService userService, CategoryService categoryService) {
        this.taskService = taskService;
        this.userService = userService;
        this.categoryService = categoryService;
    }

    @GetMapping()
    public ModelAndView getTasksPage(@AuthenticationPrincipal AuthUser authUser,
                                     @RequestParam(name = "sort", required = false) String sort) {
        ModelAndView model = new ModelAndView();

        model.setViewName("client/my-tasks");
        User userById = userService.getById(authUser.getUserId());
        List<Task> tasks = taskService.getTasks(userById);
        if (sort != null) {
            tasks = taskService.sort(tasks, sort);
        }
        model.addObject("user", userById);
        model.addObject("categoryRequest", new CategoryRequest());
        model.addObject("taskRequest", new TaskRequest());
        model.addObject("listOfCategories", userById.getCategories());
        model.addObject("priorities", TaskPriority.values());
        model.addObject("recurringTypes", RecurringTaskType.values());
        model.addObject("status", TaskStatus.values());
        model.addObject("userTasks", tasks);
        model.addObject("taskCountToDo", taskService.getTasksCount(userById, TaskStatus.NOT_STARTED));
        model.addObject("taskCountProgress", taskService.getTasksCount(userById, TaskStatus.IN_PROGRESS));
        model.addObject("taskCountComplete", taskService.getTasksCount(userById, TaskStatus.COMPLETED));
        model.addObject("page", "Tasks");
        return model;
    }

    @GetMapping("/category/{id}")
    public ModelAndView specificCategory(@AuthenticationPrincipal AuthUser authUser, @PathVariable UUID id) {
        Category categoryById = categoryService.getById(id);
        ModelAndView model = new ModelAndView();
        model.setViewName("client/my-tasks");
        User userById = userService.getById(authUser.getUserId());
        model.addObject("categoryRequest", new CategoryRequest());
        model.addObject("taskRequest", new TaskRequest());
        model.addObject("listOfCategories", userById.getCategories());
        model.addObject("priorities", TaskPriority.values());
        model.addObject("status", TaskStatus.values());
        model.addObject("userTasks", taskService.getTasksByCategory(userById, categoryById));
        model.addObject("taskCountToDo", taskService.getTasksCountByCategory(userById, TaskStatus.NOT_STARTED, categoryById));
        model.addObject("taskCountProgress", taskService.getTasksCountByCategory(userById, TaskStatus.IN_PROGRESS, categoryById));
        model.addObject("taskCountComplete", taskService.getTasksCountByCategory(userById, TaskStatus.COMPLETED, categoryById));
        return model;
    }

    @PostMapping()
    public ModelAndView addTask(@AuthenticationPrincipal AuthUser authUser, TaskRequest taskRequest)  {
        ModelAndView model = new ModelAndView();
        model.setViewName("redirect:/tasks");
        User user = userService.getById(authUser.getUserId());
        taskService.createTask(user, taskRequest);
        return model;
    }

    @PostMapping("/category")
    public ModelAndView addCategory(@AuthenticationPrincipal AuthUser authUser, CategoryRequest categoryRequest) {
        ModelAndView model = new ModelAndView();
        model.setViewName("redirect:/tasks");
        User user = userService.getById(authUser.getUserId());
        categoryService.createCategory(user, categoryRequest);
        return model;
    }


    @PutMapping("/save")
    public ModelAndView editTask(@AuthenticationPrincipal AuthUser authUser, @ModelAttribute TaskRequest task) {
        ModelAndView model = new ModelAndView();
        model.setViewName("redirect:/tasks");
        User user = userService.getById(authUser.getUserId());
        taskService.editTask(user, task, taskService.getById(task.getId()));
        return model;

    }

    @DeleteMapping("delete/{id}")
    public String deleteTask(@AuthenticationPrincipal AuthUser authUser, @PathVariable UUID id) throws AccessDeniedException {
        User user = userService.getById(authUser.getUserId());
        taskService.deleteTask(user, id);
        return "redirect:/tasks";
    }



}
