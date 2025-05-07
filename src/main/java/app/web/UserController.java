package app.web;

import app.security.AuthUser;
import app.subscription.model.SubscriptionType;
import app.user.model.User;
import app.user.service.UserService;
import app.web.dto.CategoryRequest;
import app.web.dto.EditUserRequestByAdmin;
import app.web.dto.TaskRequest;
import app.web.dto.UserRequest;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/users")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ModelAndView getUsersPage() {
        ModelAndView model=new ModelAndView();
        model.addObject("allUsers",userService.getAllUsers());
        model.addObject("accTypes", SubscriptionType.values());
        model.addObject("statusType", SubscriptionType.values());
        model.addObject("userRequest",new EditUserRequestByAdmin());
        model.setViewName("admin/users");
        return model;
    }
    @PutMapping("/save")
    public ModelAndView editUser( @ModelAttribute EditUserRequestByAdmin user) {
        ModelAndView model = new ModelAndView();
        model.setViewName("redirect:/users");
        userService.editUserByAdmin(user);
        return model;

    }



}
