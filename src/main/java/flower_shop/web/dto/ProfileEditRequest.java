package flower_shop.web.dto;

import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ProfileEditRequest {

    @Size(min = 3, max = 20, message = "First name length must be between 3 and 20 characters!")
    private String firstName;

    @Size(min = 3, max = 20, message = "Last name length must be between 3 and 20 characters!")
    private String lastName;

    @Size(min = 6, max = 20, message = "Password length must be between 6 and 20 characters!")
    private String password;
}
