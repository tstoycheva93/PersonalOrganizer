package app.web;

import org.springframework.boot.Banner;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class HomeController {
    @GetMapping("/")
    public ModelAndView getIndexPage(){
        ModelAndView model=new ModelAndView();

        model.setViewName("index/index");
        return model;
    }
    @GetMapping("/home")
    public ModelAndView getHomePage(){
        ModelAndView model=new ModelAndView();
        model.setViewName("client/calendar");
        return model;
    }
    @GetMapping("/dashboard")
    public ModelAndView getDashboardPage(){
        ModelAndView model=new ModelAndView();
        model.setViewName("admin/admin");
        return model;
    }
    @GetMapping("/login")
    public ModelAndView getLoginPage(){
        ModelAndView model=new ModelAndView();
        model.setViewName("index/login");
        return model;
    }
    @GetMapping("/register")
    public ModelAndView getRegisterPage() {
        ModelAndView model = new ModelAndView();
        model.setViewName("index/register");
        return model;
    }
}
