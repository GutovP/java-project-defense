package flower_shop.web.dto;

import flower_shop.user.model.User;
import lombok.Data;

@Data
public class LoginResponse {

    private String token;
    private ProfileResponse user;

    public LoginResponse(String token, User user) {
        this.token = token;
        this.user = new ProfileResponse(user);
    }
}
