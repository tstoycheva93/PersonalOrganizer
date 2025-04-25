package app.web;

import app.category.service.CategoryService;
import app.notification.service.NotificationService;
import app.security.AuthUser;
import app.user.model.User;
import app.user.service.UserService;
import app.web.dto.CategoryCombinedWithTask;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping("/calendar")
public class CalendarController {
    private final UserService userService;
    private final NotificationService notificationService;
    private final CategoryService categoryService;

    public CalendarController(UserService userService, NotificationService notificationService, CategoryService categoryService) {
        this.userService = userService;
        this.notificationService = notificationService;
        this.categoryService = categoryService;
    }

    @GetMapping
    public ModelAndView getHomePage(@AuthenticationPrincipal AuthUser authUser,
                                    @RequestParam("date") String date) {
        ModelAndView model=new ModelAndView();
        User user = userService.getById(authUser.getUserId());
        model.setViewName("client/calendar");
        model.addObject("user", user);
        List<String> dates = notificationService.getDatesPrettyPrinting(user.getNotifications());
        model.addObject("dates", dates);
        List<CategoryCombinedWithTask> categoryCombinedWithTasks = categoryService.makeCombinedObject(user.getCategories(), LocalDate.parse(date));
        model.addObject("categoryCombinedWithTasks", categoryCombinedWithTasks);
        return model;
    }
}
