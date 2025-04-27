package app.web;

import app.security.AuthUser;
import app.user.model.User;
import app.user.service.UserService;
import app.web.dto.LoginRequest;
import app.web.dto.RegisterRequest;
import org.springframework.boot.Banner;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class HomeController {
    private final UserService userService;

    public HomeController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/")
    public ModelAndView getIndexPage(){
        ModelAndView model=new ModelAndView();
        model.setViewName("index/index");
        return model;
    }
    @GetMapping("/home")
    public ModelAndView getHomePage(){
        ModelAndView model=new ModelAndView();
        model.setViewName("redirect:/calendar");
        return model;
    }
    @GetMapping("/dashboard")
    @PreAuthorize("hasRole('ADMIN')")
    public ModelAndView getDashboardPage(){
        ModelAndView model=new ModelAndView();
        model.setViewName("admin/admin");
        return model;
    }
    @GetMapping("/login")
    public ModelAndView getLoginPage(){
        ModelAndView model=new ModelAndView();
        model.setViewName("index/login");
        model.addObject("request", new LoginRequest());
        return model;
    }
    @GetMapping("/register")
    public ModelAndView getRegisterPage() {
        ModelAndView model = new ModelAndView();
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
}
