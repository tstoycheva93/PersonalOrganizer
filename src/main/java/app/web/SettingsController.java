package app.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/settings")
public class SettingsController {
    @GetMapping
    public ModelAndView getAdminSettingsPage(){
        ModelAndView model=new ModelAndView();
        model.setViewName("admin/settings");
        return model;
    }
}
