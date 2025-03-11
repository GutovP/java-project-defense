package flower_shop.web;

import flower_shop.user.model.User;
import flower_shop.user.service.UserService;
import flower_shop.web.dto.ProfileEditRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import static flower_shop.web.Paths.API_V1_BASE_PATH;

@RestController
@RequestMapping(API_V1_BASE_PATH + "/user")
public class ProfileEditController {

    private final UserService userService;

    @Autowired
    public ProfileEditController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/profile")
    public ResponseEntity<User> getProfile(@AuthenticationPrincipal UserDetails userDetails) {
        String email = userDetails.getUsername();
        User user = userService.getUserByEmail(email);

        return ResponseEntity.ok(user);
    }

    @PutMapping("/profile")
    public ResponseEntity<User> updateProfile(@AuthenticationPrincipal UserDetails userDetails, @RequestBody @Valid ProfileEditRequest profileEditRequest) {
        String email = userDetails.getUsername();
        User updatedUser = userService.updateUserProfile(email, profileEditRequest);

        return ResponseEntity.ok(updatedUser);
    }
}
