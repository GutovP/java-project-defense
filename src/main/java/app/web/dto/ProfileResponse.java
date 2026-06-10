package app.web.dto;

import app.user.model.User;
import app.user.model.UserRole;
import lombok.Data;

import java.util.UUID;

@Data
public class ProfileResponse {

    private UUID userId;
    private String firstName;
    private String lastName;
    private String email;
    private UserRole role;
    private String token;

    public ProfileResponse(User user) {
        this.userId = user.getId();
        this.firstName = user.getFirstName();
        this.lastName = user.getLastName();
        this.email = user.getEmail();
        this.role = user.getRole();
    }

    public ProfileResponse(User user, String token) {
        this(user);
        this.token = token;
    }
}
