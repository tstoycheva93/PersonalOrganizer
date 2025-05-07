package app.web;

import app.category.service.CategoryService;
import app.exports.ExportService;
import app.security.AuthUser;
import app.task.service.TaskService;
import app.user.model.User;
import app.user.service.UserService;
import app.utils.Deadline;
import app.utils.RecentActivities;
import app.utils.RecentActivity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class StatisticsController {
    private final TaskService taskService;
    private final UserService userService;
    private final CategoryService categoryService;
    private final RecentActivities recentActivities;
    private final ExportService exportService;

    public StatisticsController(TaskService taskService, UserService userService, UserService userService1, CategoryService categoryService, RecentActivities recentActivities, ExportService exportService) {
        this.taskService = taskService;
        this.userService = userService1;
        this.categoryService = categoryService;
        this.recentActivities = recentActivities;
        this.exportService = exportService;
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
        modelAndView.addObject("page", "Statistics");
        return modelAndView;
    }

    @GetMapping("/analytics")
    @PreAuthorize("hasRole('ADMIN')")
    public ModelAndView getAnalyticsPage(@RequestParam(name = "growth", defaultValue = "week", required = false) String growth) {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("admin/analytics");
        int allCreatedTask = taskService.getAllCreatedTasks();
        int allUsers = userService.getAll().size();
        int activeUsers = userService.getActiveUsers();
        Map<String, Integer> chartInfo = userService.getChartInfoForUsers(growth);
        modelAndView.addObject("allCreatedTasks", allCreatedTask);
        modelAndView.addObject("allUsers", allUsers);
        modelAndView.addObject("activeUsers", activeUsers);
        modelAndView.addObject("growth", growth);
        if (growth.equals("week")) {
            modelAndView.addObject("chartInfoLabels", new ArrayList<>(chartInfo.keySet()).reversed());
            modelAndView.addObject("chartInfoValues", new ArrayList<>(chartInfo.values()).reversed());
        } else {
            modelAndView.addObject("chartInfoLabels", new ArrayList<>(chartInfo.keySet()));
            modelAndView.addObject("chartInfoValues", new ArrayList<>(chartInfo.values()));
        }
        List<Integer> taskCreated = taskService.getAllCreatedTasksByDay();
        List<Integer> taskCompleted = taskService.getAllCompletedTasksByDay();
        modelAndView.addObject("taskCreated", taskCreated);
        modelAndView.addObject("taskCompleted", taskCompleted);
        List<RecentActivity> recentActivityList = recentActivities.getRecentActivities();
        modelAndView.addObject("recentActivity", recentActivityList);
        modelAndView.addObject("page", "Analytics");

        return modelAndView;
    }

}
