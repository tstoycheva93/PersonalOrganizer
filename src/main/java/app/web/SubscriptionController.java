package app.web;

import app.security.AuthUser;
import app.subscription.service.SubscriptionService;
import app.user.model.User;
import app.user.service.UserService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import java.util.UUID;

@Controller
@RequestMapping("/subscriptions")
public class SubscriptionController {
    private final UserService userService;
    private final SubscriptionService subscriptionService;

    public SubscriptionController(UserService userService, SubscriptionService subscriptionService) {
        this.userService = userService;
        this.subscriptionService = subscriptionService;
    }

    @GetMapping
    public ModelAndView getSubscriptionPage(@AuthenticationPrincipal AuthUser authUser) {
        ModelAndView model = new ModelAndView();
        User user = userService.getById(authUser.getUserId());
        model.setViewName("client/subscriptions");
        model.addObject("user", user);
        return model;
    }

    @PutMapping("/user/{id}")
    public ModelAndView updateSubscriptionPage(@PathVariable UUID id, @AuthenticationPrincipal AuthUser authUser) {
        ModelAndView modelAndView = new ModelAndView("redirect:/subscriptions");
        User user = userService.getById(id);
        subscriptionService.createPremiumSubscriptionForUser(user);
        userService.updateSubscriptionCountIfPremium(user);
        return modelAndView;
    }
}
