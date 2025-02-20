package flower_shop.web.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RegisterRequest {

    @NotEmpty(message = "First name cannot be empty!")
    @Size(min = 3, max = 20, message = "First name length must be between 3 and 20 characters!")
    private String firstName;

    @NotEmpty(message = "Last name cannot be empty!")
    @Size(min = 3, max = 20, message = "Last name length must be between 3 and 20 characters!")
    private String lastName;

    @NotEmpty(message = "Email cannot be empty!")
    @Email
    private String email;

    @NotEmpty(message = "Password cannot be empty!")
    @Size(min = 6, max = 20, message = "Password length must be between 3 and 20 characters!")
    private String password;
}
