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
    @ExceptionHandler(TitleException.class)
    public ModelAndView handleTitleException(TitleException e) {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("index/error");
        modelAndView.addObject("message", e.getMessage());
        modelAndView.addObject("type", "email");
        modelAndView.addObject("title", "Unsuccessful task creation, invalid title! ");
        return modelAndView;
    }
    @ExceptionHandler(DescriptionException.class)
    public ModelAndView handleDescriptionException(DescriptionException e) {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("index/error");
        modelAndView.addObject("message", e.getMessage());
        modelAndView.addObject("type", "email");
        modelAndView.addObject("title", "Unsuccessful task creation, invalid description! ");
        return modelAndView;
    }
    @ExceptionHandler(DateValidation.class)
    public ModelAndView handleDueDateException(DateValidation e) {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("index/error");
        modelAndView.addObject("message", e.getMessage());
        modelAndView.addObject("type", "email");
        modelAndView.addObject("title", "Unsuccessful task creation, date doesn't match! ");
        return modelAndView;
    }
    @ExceptionHandler(StartDateInPast.class)
    public ModelAndView handleStartDateException(StartDateInPast e) {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("index/error");
        modelAndView.addObject("message", e.getMessage());
        modelAndView.addObject("type", "email");
        modelAndView.addObject("title", "Unsuccessful task creation, date doesn't match! ");
        return modelAndView;
    }
    @ExceptionHandler(PriorityException.class)
    public ModelAndView handlePriorityException(PriorityException e) {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("index/error");
        modelAndView.addObject("message", e.getMessage());
        modelAndView.addObject("type", "email");
        modelAndView.addObject("title", "Unsuccessful task creation, must have priority! ");
        return modelAndView;
    }
    @ExceptionHandler(StatusException.class)
    public ModelAndView handleStatusException(StatusException e) {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("index/error");
        modelAndView.addObject("message", e.getMessage());
        modelAndView.addObject("type", "email");
        modelAndView.addObject("title", "Unsuccessful task creation, must have status! ");
        return modelAndView;
    }
    @ExceptionHandler(NotPremiumUser.class)
    public ModelAndView handleNotPremiumUserException(NotPremiumUser e) {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("index/error");
        modelAndView.addObject("message", e.getMessage());
        modelAndView.addObject("type", "email");
        modelAndView.addObject("title", "Unsuccessful task creation, upgrade for more tasks! ");
        return modelAndView;
    }
    @ExceptionHandler(CategoryException.class)
    public ModelAndView handleCategoryException(CategoryException e) {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("index/error");
        modelAndView.addObject("message", e.getMessage());
        modelAndView.addObject("type", "email");
        modelAndView.addObject("title", "Unsuccessful task creation, must set a category! ");
        return modelAndView;
    }
    @ExceptionHandler(UserStatusException.class)
    public ModelAndView handleUserStatusException(UserStatusException e) {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("index/error");
        modelAndView.addObject("message", e.getMessage());
        modelAndView.addObject("type", "email");
        modelAndView.addObject("title", "Unsuccessful editing, must set a status! ");
        return modelAndView;
    }
    @ExceptionHandler(UserSubsException.class)
    public ModelAndView handleUserSubsException(UserSubsException e) {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("index/error");
        modelAndView.addObject("message", e.getMessage());
        modelAndView.addObject("type", "email");
        modelAndView.addObject("title", "Unsuccessful editing, must set a SUBSCRIPTION TYPE! ");
        return modelAndView;
    }
}
