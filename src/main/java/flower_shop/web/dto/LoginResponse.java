package flower_shop.web.dto;

import flower_shop.user.model.User;
import flower_shop.web.mapper.UserMapper;
import lombok.Data;

@Data
public class LoginResponse {

    private String token;
    private UserMapper user;

    public LoginResponse(String token, User user) {
        this.token = token;
        this.user = new UserMapper(user);
    }
}
