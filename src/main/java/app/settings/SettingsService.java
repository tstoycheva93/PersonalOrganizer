package app.settings;

import app.security.AuthUser;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.session.SessionInformation;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class SettingsService {
    private final SettingsProperties settings;
    private final SessionRegistry sessionRegistry;

    public SettingsService(SettingsProperties settings, SessionRegistry sessionRegistry) {
        this.settings = settings;
        this.sessionRegistry = sessionRegistry;
    }

    public void setMaintenance(boolean enabled) {
        settings.setMaintenance(enabled);
        if (enabled) {
            expireAllSessions();
        }
    }

    public void expireAllSessions() {
        for (Object principal : sessionRegistry.getAllPrincipals()) {
            if (principal instanceof AuthUser userDetails) {
                boolean isAdmin = userDetails.getAuthorities().stream()
                        .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN"));

                if (isAdmin) {
                    continue;
                }
                List<SessionInformation> sessions = sessionRegistry.getAllSessions(principal, false);
                for (SessionInformation session : sessions) {
                    session.expireNow();
                }
            }
        }
    }

    public void setEnabledRegister(boolean enabled) {
        settings.setEnabledRegister(enabled);
    }
}
