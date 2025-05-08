package app.web.dto;

import app.user.model.User;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserRequest {
    @NotBlank(message = "Username can not be blank")
    @Size(min=5,max=20)
    private String username;
    private String password;
    @NotBlank(message = "Email can not be blank")
    @Email(message = "Not valid email")
    private String email;
    private String confirmPassword;
    private String photo;
    @NotBlank(message = "Name can not be blank")
    @Size(min=3,max=30)
    private String name;

}
