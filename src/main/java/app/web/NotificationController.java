package app.web;

import app.notification.service.NotificationService;
import jakarta.mail.MessagingException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class NotificationController {
    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @GetMapping("/email")
    public ModelAndView getSendEmailPage() {
        ModelAndView model = new ModelAndView();
        model.addObject("recipientOption", "all");
        model.addObject("subject", "");
        model.addObject("content", "");
        model.setViewName("admin/send-email");
        return model;
    }

    @PostMapping("/email")
    public ModelAndView sendEmail(@RequestParam(name = "recipientOption") String recipientOption,
                                  @RequestParam(name = "subject") String subject,
                                  @RequestParam(name = "content") String content,
                                  @RequestParam(name = "userSearch") String userSearch) throws MessagingException {
        notificationService.sendEmail(recipientOption, subject, content, userSearch);
        return new ModelAndView("redirect:/email");
    }
}
