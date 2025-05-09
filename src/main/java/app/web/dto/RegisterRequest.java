package app.web.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RegisterRequest {
    @Size(min = 5, max = 20)
    @NotBlank
    private String username;
    @Size(min = 8, max = 25)
    @NotBlank
    private String password;
    @Email
    private String email;
    @NotBlank
    @Size(min = 3, max = 30)
    private String name;
    @Size(min = 8, max = 25)
    private String confirmPassword;
}
