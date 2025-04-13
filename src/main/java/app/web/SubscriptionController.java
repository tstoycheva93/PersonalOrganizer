package app.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/subscriptions")
public class SubscriptionController {
    @GetMapping
    public ModelAndView getSubscriptionPage(){
        ModelAndView model=new ModelAndView();
        model.setViewName("client/subscriptions");
        return model;
    }
}
