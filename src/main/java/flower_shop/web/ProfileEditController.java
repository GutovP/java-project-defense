package flower_shop.web;

import flower_shop.user.model.User;
import flower_shop.user.service.UserService;
import flower_shop.web.dto.PasswordChangeRequest;
import flower_shop.web.dto.ProfileEditRequest;
import flower_shop.web.dto.ProfileResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

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
    public ResponseEntity<ProfileResponse> getProfile(@AuthenticationPrincipal UserDetails userDetails) {
        String email = userDetails.getUsername();
        User user = userService.getUserByEmail(email);
        ProfileResponse profileResponse = new ProfileResponse(user);

        return ResponseEntity.ok(profileResponse);
    }

    @PutMapping("/profile")
    public ResponseEntity<User> updateProfile(@AuthenticationPrincipal UserDetails userDetails, @RequestBody @Valid ProfileEditRequest profileEditRequest) {
        String email = userDetails.getUsername();
        User updatedUser = userService.updateUserProfile(email, profileEditRequest);

        return ResponseEntity.ok(updatedUser);
    }

    @PutMapping("/change-password")
    public ResponseEntity<?> changePassword(@AuthenticationPrincipal UserDetails userDetails, @RequestBody @Valid PasswordChangeRequest passwordChangeRequest) {
        String email = userDetails.getUsername();

        userService.changeUserPassword(email, passwordChangeRequest.getCurrentPassword(), passwordChangeRequest.getNewPassword());

        return ResponseEntity.ok().body(Map.of("message", "Password changed successfully"));
    }

//    @GetMapping("/validate-token")
//    public ResponseEntity<?> validateToken(@RequestHeader("Authorization") String token) {
//        if (token == null || !token.startsWith("Bearer ")) {
//            throw new InvalidTokenException("Invalid token format");
//        }
//
//        token = token.substring(7);
//
//        String email = jwtService.extractEmail(token);
//        UserDetails userDetails = userDetailsService.loadUserByUsername(email);
//
//        if (jwtService.validateToken(token, userDetails)) {
//            return ResponseEntity.ok().body("Token is valid");
//
//        } else {
//            throw new TokenExpiredException("Token is expired");
//        }
//    }

}
