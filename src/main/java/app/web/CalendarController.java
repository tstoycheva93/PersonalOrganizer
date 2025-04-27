package app.web;

import app.category.service.CategoryService;
import app.notification.service.NotificationService;
import app.security.AuthUser;
import app.user.model.User;
import app.user.service.UserService;
import app.utils.CalendarDay;
import app.utils.CalendarUtils;
import app.web.dto.CategoryCombinedWithTask;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

@Controller
@RequestMapping("/calendar")
public class CalendarController {
    private final UserService userService;
    private final NotificationService notificationService;
    private final CategoryService categoryService;
    private final CalendarUtils calendarUtils;

    public CalendarController(UserService userService, NotificationService notificationService, CategoryService categoryService, CalendarUtils calendarUtils) {
        this.userService = userService;
        this.notificationService = notificationService;
        this.categoryService = categoryService;
        this.calendarUtils = calendarUtils;
    }

    @GetMapping
    public ModelAndView getHomePage(@AuthenticationPrincipal AuthUser authUser,
                                    @RequestParam(value = "date", required = false) String date) {
        ModelAndView model = new ModelAndView();
        User user = userService.getById(authUser.getUserId());
        model.setViewName("client/calendar");
        model.addObject("user", user);
        List<String> dates = notificationService.getDatesPrettyPrinting(user.getNotifications());
        model.addObject("dates", dates);
        if (date == null || date.isEmpty()) {
            date = LocalDate.now().toString();
        }
        List<CalendarDay> calendarDays = calendarUtils.generateCalendarDays(LocalDate.parse(date).getYear(), LocalDate.parse(date).getMonth().getValue(), LocalDate.parse(date),user);
        model.addObject("calendarDays", calendarDays);
        List<CategoryCombinedWithTask> categoryCombinedWithTasks = categoryService.makeCombinedObject(user.getCategories(), LocalDate.parse(date));
        model.addObject("categoryCombinedWithTasks", categoryCombinedWithTasks);
        if (LocalDate.now().equals(LocalDate.parse(date))) {
            date = "Today";
        }
        model.addObject("date", date);
        return model;
    }
}
