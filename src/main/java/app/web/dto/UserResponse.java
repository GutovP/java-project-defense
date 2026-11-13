package app.web.dto;

import app.user.model.User;
import app.user.model.UserRole;
import java.time.LocalDateTime;
import java.util.UUID;

public record UserResponse(UUID id, String email, LocalDateTime registeredOn, UserRole role) {

    public static UserResponse fromUserEntity(User user) {

        return new UserResponse(user.getId(), user.getEmail(), user.getRegisteredOn(), user.getRole());
    }
}
