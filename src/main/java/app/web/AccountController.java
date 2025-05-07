package app.web;

import app.security.AuthUser;
import app.user.model.User;
import app.user.service.UserService;
import app.web.dto.UserRequest;
import org.springframework.core.convert.ConversionService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.UUID;

@Controller
@RequestMapping("/accounts")
public class AccountController {
    private final UserService userService;
    private final ConversionService conversionService;

    public AccountController(UserService userService, ConversionService conversionService) {
        this.userService = userService;
        this.conversionService = conversionService;
    }

    @GetMapping
    public ModelAndView getAccountsPage(@AuthenticationPrincipal AuthUser authUser) {
        ModelAndView model = new ModelAndView();
        model.setViewName("client/account");
        User user = userService.getById(authUser.getUserId());
        model.addObject("user", user);
        UserRequest convert = conversionService.convert(user, UserRequest.class);
        model.addObject("request", convert);
        model.addObject("page", "Account");
        return model;
    }

    @PutMapping("/{id}")
    public ModelAndView updateAccountPage(@PathVariable("id") UUID id, UserRequest userRequest) {
        ModelAndView model = new ModelAndView();
        User user = userService.getById(id);
        userService.editUser(user, userRequest);
        model.setViewName("redirect:/accounts");
        model.addObject("page", "Account");
        return model;
    }

    @PutMapping("/password/{id}")
    public ModelAndView passwordAccountPage(@PathVariable("id") UUID id, UserRequest userRequest) {
        ModelAndView model = new ModelAndView();
        User user = userService.getById(id);
        userService.editPassword(user, userRequest);
        model.setViewName("redirect:/accounts");
        model.addObject("page", "Account");
        return model;
    }

    @DeleteMapping("/{id}")
    public ModelAndView deleteAccountPage(@PathVariable("id") UUID id) {
        ModelAndView model = new ModelAndView();
        User user = userService.getById(id);
        userService.deleteUser(user);
        model.setViewName("redirect:/login");
        return model;
    }
}
