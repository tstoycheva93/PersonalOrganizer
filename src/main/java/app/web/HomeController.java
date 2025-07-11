package app.web;

import app.notification.service.NotificationService;
import app.security.AuthUser;
import app.task.service.TaskService;
import app.settings.SettingsProperties;
import app.user.model.User;
import app.user.service.UserService;
import app.web.dto.ForgotPasswordRequest;
import app.web.dto.LoginRequest;
import app.web.dto.RegisterRequest;
import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import org.springframework.boot.Banner;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class HomeController {
    private final UserService userService;
    private final TaskService taskService;
    private final SettingsProperties settingsProperties;
    private final NotificationService notificationService;

    public HomeController(UserService userService, TaskService taskService, SettingsProperties settingsProperties, NotificationService notificationService) {
        this.userService = userService;
        this.taskService = taskService;
        this.settingsProperties = settingsProperties;
        this.notificationService = notificationService;
    }

    @GetMapping("/")
    public ModelAndView getIndexPage() {
        ModelAndView model = new ModelAndView();
        model.setViewName("index/index");
        return model;
    }

    @GetMapping("/home")
    public ModelAndView getHomePage() {
        ModelAndView model = new ModelAndView();
        model.setViewName("redirect:/calendar");
        model.addObject("page", "Calendar");
        return model;
    }

    @GetMapping("/dashboard")
    @PreAuthorize("hasRole('ADMIN')")
    public ModelAndView getDashboardPage() {
        ModelAndView model = new ModelAndView();
        model.setViewName("admin/admin");
        model.addObject("page", "Dashboard");
        model.addObject("totalUserCount", userService.countTotalUsers());
        model.addObject("totalNumberOfTasks", taskService.getAllTasksCount());
        model.addObject("totalActiveUsers", userService.getActiveUsersCount());
        model.addObject("totalPremiumUsers", userService.getPremiumUsers());
        model.addObject("topUsers", userService.topUsers());

        return model;
    }

    @GetMapping("/login")
    public ModelAndView getLoginPage(@RequestParam(name = "error", required = false) String error) {
        ModelAndView model = new ModelAndView();
        model.setViewName("index/login");
        model.addObject("request", new LoginRequest());
        if (error != null) {
            model.addObject("message", "Incorrect username or password!");
        }
        return model;
    }

    @GetMapping("/register")
    public ModelAndView getRegisterPage() {
        ModelAndView model = new ModelAndView();
        if (!settingsProperties.isEnabledRegister()) {
            model.setViewName("redirect:/errors/register");
            return model;
        }
        model.setViewName("index/register");
        model.addObject("request", new RegisterRequest());
        return model;
    }

    @PostMapping("/register")
    public ModelAndView registerNewUser(RegisterRequest registerRequest, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return new ModelAndView("index/register");
        }
        userService.registerUser(registerRequest);
        return new ModelAndView("redirect:/login");
    }

    @GetMapping("/errors/maintenance")
    public ModelAndView getErrorPage() {
        ModelAndView model = new ModelAndView();
        model.setViewName("index/error");
        model.addObject("title", "We'll Be Right Back");
        model.addObject("message", "Our website is currently undergoing scheduled maintenance.\n" +
                "We are working hard to improve your experience and will be back online shortly.\n" +
                "Thank you for your patience and understanding.");
        model.addObject("type", "loggedOut");
        return model;
    }

    @GetMapping("/errors/register")
    public ModelAndView getRegisterErrorPage() {
        ModelAndView model = new ModelAndView();
        model.setViewName("index/error");
        model.addObject("title", "Temporarily Unavailable");
        model.addObject("message", "Registration is currently disabled due to ongoing maintenance. "
                + "We’re updating our system to serve you better. "
                + "Please check back later. Thank you for your patience!");
        model.addObject("type", "loggedOut");
        return model;
    }

    @GetMapping("/faq")
    public ModelAndView getFaqPage(@AuthenticationPrincipal AuthUser authUser) {
        ModelAndView model = new ModelAndView();
        model.setViewName("client/faq");
        User user = userService.getById(authUser.getUserId());
        model.addObject("user", user);
        model.addObject("page", "FAQ");
        return model;
    }

    @GetMapping("/help")
    public ModelAndView getHelpPage(@AuthenticationPrincipal AuthUser authUser) {
        ModelAndView model = new ModelAndView();
        model.setViewName("client/help");
        User user = userService.getById(authUser.getUserId());
        model.addObject("user", user);
        model.addObject("page", "FAQ");
        return model;
    }

    @GetMapping("/forgot-password")
    public ModelAndView getForgotPasswordPage() {
        ModelAndView model = new ModelAndView();
        model.setViewName("index/forgot-password");
        model.addObject("forgotPassword", new ForgotPasswordRequest());
        return model;
    }

    @PutMapping("/forgot-password")
    public ModelAndView forgotPasswordPage( @ModelAttribute ForgotPasswordRequest forgotPasswordRequest,BindingResult bindingResult) throws MessagingException {
        ModelAndView model = new ModelAndView();
        if (bindingResult.hasErrors()) {
            model.setViewName("index/forgot-password");
            return model;
        }
        model.setViewName("redirect:/login");
        notificationService.sendNewPassword(forgotPasswordRequest);
        return model;
    }

}
