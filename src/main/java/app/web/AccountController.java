package app.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/accounts")
public class AccountController {
    @GetMapping
    public ModelAndView getAccountsPage(){
        ModelAndView model=new ModelAndView();
        model.setViewName("client/account");
        return model;
    }
}
