package app.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class NotificationController {
    @GetMapping("/email")
    public ModelAndView getSendEmailPage(){
        ModelAndView model=new ModelAndView();
        model.setViewName("admin/send-email");
        return model;
    }
}
