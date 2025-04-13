package app.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class StatisticsController {
    @GetMapping("/statistics")
    public ModelAndView getStatisticsPage(){
        ModelAndView modelAndView=new ModelAndView();
        modelAndView.setViewName("client/my-stats");
        return modelAndView;
    }
    @GetMapping("/analytics")

    public ModelAndView getAnalyticsPage(){
        ModelAndView modelAndView=new ModelAndView();
        modelAndView.setViewName("admin/analytics");
        return modelAndView;
    }
}
