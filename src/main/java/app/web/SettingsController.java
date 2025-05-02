package app.web;

import app.settings.SettingsProperties;
import app.settings.SettingsService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/settings")
public class SettingsController {
    private final SettingsService settingsService;
    private final SettingsProperties settingsProperties;

    public SettingsController(SettingsService settingsService, SettingsProperties settingsProperties) {
        this.settingsService = settingsService;
        this.settingsProperties = settingsProperties;
    }

    @GetMapping
    public ModelAndView getAdminSettingsPage(@RequestParam(value = "enabled", defaultValue = "false") boolean enabled) {
        ModelAndView model = new ModelAndView();
        model.setViewName("admin/settings");
        model.addObject("enabled", settingsProperties.isMaintenance());
        model.addObject("register", settingsProperties.isEnabledRegister());
        return model;
    }

    @PutMapping("/maintenance")
    public String setMaintenance(@RequestParam(value = "enabled", defaultValue = "false") boolean enabled) {
        settingsService.setMaintenance(enabled);
        return "redirect:/settings?enabled=" + enabled + "&register=" + settingsProperties.isEnabledRegister();
    }

    @PutMapping("/register")
    public String setRegisterNoMore(@RequestParam(name = "r", required = false) boolean enabled) {
        settingsService.setEnabledRegister(enabled);
        return "redirect:/settings?enabled=" + settingsProperties.isMaintenance() + "&register=" + enabled;

    }
}
