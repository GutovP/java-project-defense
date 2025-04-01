package flower_shop.web.dto;

import flower_shop.user.model.User;
import flower_shop.user.model.UserRole;

import java.util.UUID;

public record UserResponse(UUID id, String email, UserRole role) {

    public static UserResponse fromUserEntity(User user) {

        return new UserResponse(user.getId(), user.getEmail(), user.getRole());
    }
}
