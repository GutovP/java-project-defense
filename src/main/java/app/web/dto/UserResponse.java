package app.web.dto;

import app.user.model.User;
import app.user.model.UserRole;

import java.util.UUID;

public record UserResponse(UUID id, String email, UserRole role) {

    public static UserResponse fromUserEntity(User user) {

        return new UserResponse(user.getId(), user.getEmail(), user.getRole());
    }
}
