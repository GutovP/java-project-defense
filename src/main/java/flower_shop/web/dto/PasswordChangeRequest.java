package flower_shop.web.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class PasswordChangeRequest {

    @NotBlank
    private String currentPassword;

    @NotBlank
    private String newPassword;
}
