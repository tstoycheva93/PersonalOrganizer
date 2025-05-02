package app.web.dto;

import app.user.model.User;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserRequest {
    private String username;
    private String password;
    private String email;
    private String confirmPassword;
    private String photo;
    private String name;

}
