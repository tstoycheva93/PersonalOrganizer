package app.security;

import app.settings.SettingsProperties;
import app.settings.SettingsService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Collection;

@Component
public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {
    private final SettingsService settingsService;
    private final SettingsProperties settings;

    public CustomAuthenticationSuccessHandler(SettingsService settingsService, SettingsProperties settingsProperties) {
        this.settingsService = settingsService;
        this.settings = settingsProperties;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {

        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        if (settings.isMaintenance()) {
            settingsService.setMaintenance(true);
            response.sendRedirect("/errors/maintenance");
            return;
        }
        for (GrantedAuthority authority : authorities) {
            String role = authority.getAuthority();
            if (role.equals("ROLE_ADMIN")) {
                response.sendRedirect("/dashboard");
                return;
            } else if (role.equals("ROLE_USER")) {
                response.sendRedirect("/home");
                return;
            }
        }

        response.sendRedirect("/");
    }
}


