package app.web.dto;

import app.user.model.User;
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
