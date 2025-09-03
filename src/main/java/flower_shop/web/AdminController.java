package flower_shop.web;

import flower_shop.admin.service.AdminService;
import flower_shop.user.model.User;
import flower_shop.user.model.UserRole;
import flower_shop.web.dto.UserResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

import static flower_shop.web.Paths.API_V1_BASE_PATH;

@RestController
@RequestMapping(API_V1_BASE_PATH + "/admin")
public class AdminController {

    private final AdminService adminService;

    @Autowired
    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    @GetMapping("/users")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UserResponse>> getAllUsers() {

        List<User> users = adminService.getAllUsers();
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
