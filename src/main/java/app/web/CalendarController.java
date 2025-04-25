package app.web;

import app.notification.service.NotificationService;
import app.security.AuthUser;
import app.user.model.User;
import app.user.service.UserService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

@Controller
@RequestMapping("/calendar")
public class CalendarController {
    private final UserService userService;
    private final NotificationService notificationService;

    public CalendarController(UserService userService, NotificationService notificationService) {
        this.userService = userService;
        this.notificationService = notificationService;
    }

    @GetMapping("/")
    public ModelAndView getHomePage(@AuthenticationPrincipal AuthUser authUser) {
        ModelAndView model=new ModelAndView();
        User user = userService.getById(authUser.getUserId());
        model.setViewName("client/calendar");
        model.addObject("user", user);
        List<String> dates = notificationService.getDatesPrettyPrinting(user.getNotifications());
        model.addObject("dates", dates);
        return model;
    }
}
