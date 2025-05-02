package app.settings;

import lombok.*;
import org.springframework.stereotype.Component;

@Component
@Getter
@Setter
@Builder
@AllArgsConstructor
public class SettingsProperties {
    private boolean maintenance;
    private boolean enabledRegister;

    public SettingsProperties() {
        this.maintenance = false;
        this.enabledRegister = true;
    }
}
