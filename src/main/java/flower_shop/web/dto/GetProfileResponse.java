package flower_shop.web.dto;

import flower_shop.user.model.User;
import flower_shop.web.mapper.UserMapper;
import lombok.Data;

@Data
public class GetProfileResponse {

    private UserMapper user;

    public GetProfileResponse(User user) {
        this.user = new UserMapper(user);
    }
}
