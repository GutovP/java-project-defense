package flower_shop.web.mapper;

import flower_shop.user.model.User;
import lombok.Data;

@Data
public class UserMapper {

    private String firstName;
    private String lastName;
    private String email;

    public UserMapper(User user) {
        this.firstName = user.getFirstName();
        this.lastName = user.getLastName();
        this.email = user.getEmail();
    }
}
