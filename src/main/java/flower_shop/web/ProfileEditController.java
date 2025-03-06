package flower_shop.web;


import flower_shop.user.model.User;
import flower_shop.user.service.UserService;
import flower_shop.web.dto.ProfileEditRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

import static flower_shop.web.Paths.API_V1_BASE_PATH;

@RestController
@RequestMapping(API_V1_BASE_PATH + "/users")
public class ProfileEditController {

    private final UserService userService;

    @Autowired
    public ProfileEditController(UserService userService) {
        this.userService = userService;
    }

    @PutMapping("/{id}/profile")
    public ResponseEntity<User> updateProfile(@PathVariable UUID id, @RequestBody ProfileEditRequest profileEditRequest) {

        User user = userService.editProfile(id, profileEditRequest);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(user);
    }
}
