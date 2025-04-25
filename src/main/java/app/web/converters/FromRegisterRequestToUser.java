package app.web.converters;

import app.user.model.User;
import app.user.model.UserRole;
import app.web.dto.RegisterRequest;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class FromRegisterRequestToUser implements Converter<RegisterRequest, User> {
    @Override
    public User convert(RegisterRequest source) {
        return User.builder()
                .username(source.getUsername())
                .active(true)
                .name(source.getName())
                .role(UserRole.USER)
                .email(source.getEmail())
                .build();
    }
}
