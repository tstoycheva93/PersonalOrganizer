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
public class ForgotPasswordRequest {
    @NotBlank(message = "Email can not be blank")
    @NotBlank
    @Email(message = "Not valid email")
    private String email;
    @NotBlank(message = "Username can not be blank")
    @Size(min=5,max=20)
    private String username;
}
