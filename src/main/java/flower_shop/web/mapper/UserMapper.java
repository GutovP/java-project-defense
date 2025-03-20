package flower_shop.web.mapper;

import flower_shop.user.model.User;
import flower_shop.user.model.UserRole;
import lombok.Data;

import java.util.UUID;

@Data
public class UserMapper {

    private UUID userId;
    private String firstName;
    private String lastName;
    private String email;
    private UserRole role;

    public UserMapper(User user) {
        this.userId = user.getId();
        this.firstName = user.getFirstName();
        this.lastName = user.getLastName();
        this.email = user.getEmail();
        this.role = user.getRole();
    }
}
