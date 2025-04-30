package app.web;

import app.category.service.CategoryService;
import app.security.AuthUser;
import app.task.service.TaskService;
import app.user.model.User;
import app.user.service.UserService;
import app.utils.Deadline;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.List;

@Controller
public class StatisticsController {
    private final TaskService taskService;
    private final UserService userService;
    private final CategoryService categoryService;

    public StatisticsController(TaskService taskService, UserService userService, UserService userService1, CategoryService categoryService) {
        this.taskService = taskService;
        this.userService = userService1;
        this.categoryService = categoryService;
    }

    @GetMapping("/statistics")
    public ModelAndView getStatisticsPage(@AuthenticationPrincipal AuthUser authUser) {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("client/my-stats");
        Map<String, Object> taskData = new HashMap<>();
        User user = userService.getById(authUser.getUserId());
        Map<String, Integer> completion = taskService.getCountOfTasksByStatus(user);
        Map<String, Integer> categories = categoryService.getCountOfCategoriesAndTheirTasks(user);
        List<String> categoryNames = categoryService.getCategoryNames(user);
        taskData.put("completion", completion);
        taskData.put("categories", categories);
        taskData.put("categoryNames", categoryNames);
        List<Deadline> upcomingDeadlines = taskService.getTasksIntoDeadlineObject(user);
        taskData.put("upcomingDeadlines", upcomingDeadlines);
        modelAndView.addObject("taskData", taskData);

        return modelAndView;
    }

    @GetMapping("/analytics")

    public ModelAndView getAnalyticsPage() {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("admin/analytics");
        return modelAndView;
    }
}
