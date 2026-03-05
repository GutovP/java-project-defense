package app.web;

import app.security.AuthenticationMetadata;
import app.user.model.User;
import app.user.service.UserService;
import app.web.dto.PasswordChangeRequest;
import app.web.dto.ProfileEditRequest;
import app.web.dto.ProfileResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;


import static app.web.Paths.API_V1_BASE_PATH;

@RestController
@RequestMapping(API_V1_BASE_PATH + "/user")
@Tag(name = "Profile Endpoints", description = "endpoints related to user's profile")
public class ProfileEditController {

    private final UserService userService;

    @Autowired
    public ProfileEditController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/profile")
    public ResponseEntity<ProfileResponse> getProfile(@AuthenticationPrincipal AuthenticationMetadata  authenticationMetadata) {

        String email = authenticationMetadata.getUsername();
        User user = userService.getUserByEmail(email);
        ProfileResponse profileResponse = new ProfileResponse(user);

        return ResponseEntity.ok(profileResponse);
    }

    @PutMapping("/profile")
    public ResponseEntity<ProfileResponse> updateProfile(@AuthenticationPrincipal AuthenticationMetadata authenticationMetadata, @RequestBody @Valid ProfileEditRequest profileEditRequest) {

        String email = authenticationMetadata.getUsername();
        User updatedUser = userService.updateUserProfile(email, profileEditRequest);
        ProfileResponse profileResponse = new ProfileResponse(updatedUser);

        return ResponseEntity.ok(profileResponse);
    }

    @PutMapping("/change-password")
    public ResponseEntity<ProfileResponse> changePassword(@AuthenticationPrincipal AuthenticationMetadata authenticationMetadata, @RequestBody @Valid PasswordChangeRequest passwordChangeRequest) {

        String email = authenticationMetadata.getUsername();

        User changedUserPassword = userService.changeUserPassword(email, passwordChangeRequest.getCurrentPassword(), passwordChangeRequest.getNewPassword());
        ProfileResponse profileResponse = new ProfileResponse(changedUserPassword);

        return ResponseEntity.ok(profileResponse);
    }

}
