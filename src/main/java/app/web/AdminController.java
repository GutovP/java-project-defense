package app.web;

import app.admin.service.AdminService;
import app.security.AuthenticationMetadata;
import app.user.model.User;
import app.user.model.UserRole;
import app.web.dto.UserResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

import static app.web.Paths.API_V1_BASE_PATH;

@RestController
@RequestMapping(API_V1_BASE_PATH + "/admin")
@Tag(name = "Admin Endpoints", description = "Admin related endpoints")
public class AdminController {

    private final AdminService adminService;

    @Autowired
    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    @GetMapping("/users")
    public ResponseEntity<List<UserResponse>> getAllUsers(@AuthenticationPrincipal AuthenticationMetadata authenticationMetadata) {

        UserRole userRole = authenticationMetadata.getUserRole();

        List<User> users = adminService.getAllUsers(userRole);
        List<UserResponse> usersResponse = users
                .stream()
                .map(UserResponse::fromUserEntity)
                .toList();

        return ResponseEntity.status(HttpStatus.OK).body(usersResponse);
    }

    @PutMapping("/{userId}/role")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> updateUserRole(@PathVariable UUID userId, @RequestParam UserRole newRole) {

        adminService.changeUserRole(userId, newRole);

        return ResponseEntity.status(HttpStatus.OK).build();
    }
}
