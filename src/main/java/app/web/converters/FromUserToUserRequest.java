package app.web.converters;

import app.user.model.User;
import app.web.dto.UserRequest;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class FromUserToUserRequest implements Converter<User, UserRequest> {
    @Override
    public UserRequest convert(User source) {
        return UserRequest.builder()
                .email(source.getEmail())
                .password(source.getPassword())
                .name(source.getName())
                .username(source.getUsername())
                .build();
    }
}
