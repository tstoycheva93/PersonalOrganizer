package app.web.exception_handler;

import app.exception.*;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@ControllerAdvice
public class ExceptionHandlerAdvice {

    @ExceptionHandler(UsernameAlreadyExistException.class)
    public String usernameAlreadyExist(UsernameAlreadyExistException ex, RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute("message", ex.getMessage());
        return "redirect:/register";
    }

    @ExceptionHandler(EmailAlreadyExistException.class)
    public String emailAlreadyExist(EmailAlreadyExistException ex, RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute("message", ex.getMessage());
        return "redirect:/register";
    }

    @ExceptionHandler(PasswordDoesNotMatch.class)
    public String passwordDoeNotMatch(PasswordDoesNotMatch ex, RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute("message", ex.getMessage());
        return "redirect:/login";
    }

    @ExceptionHandler(EmailNotFoundException.class)
    public ModelAndView handleEmailNotFoundException(EmailNotFoundException e) {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("index/error");
        modelAndView.addObject("message", e.getMessage());
        modelAndView.addObject("type", "email");
        modelAndView.addObject("title", "Email Not Found");
        return modelAndView;
    }
}
