package flower_shop.web;

import flower_shop.admin.service.AdminService;
import flower_shop.user.model.User;
import flower_shop.user.model.UserRole;
import flower_shop.web.dto.UserResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

import static flower_shop.web.Paths.API_V1_BASE_PATH;

@RestController
@RequestMapping(API_V1_BASE_PATH + "/admin")
@PreAuthorize("hasAuthority('ADMIN')")
public class AdminController {

    private final AdminService adminService;

    @Autowired
    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    @GetMapping("/users")
    public ResponseEntity<List<UserResponse>> getAllUsers() {

        List<User> users = adminService.getAllUsers();
        List<UserResponse> userResponses = users.stream().map(UserResponse::fromUserEntity).toList();

        return ResponseEntity.ok(userResponses);
    }

    @PutMapping("/{userId}/role")
    public ResponseEntity<String> updateUserRole(@PathVariable UUID userId, @RequestParam UserRole newRole) {

        adminService.changeUserRole(userId, newRole);

        return ResponseEntity.ok("Role updated successfully");
    }
}
