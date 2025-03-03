package flower_shop.web;


import flower_shop.user.model.User;
import flower_shop.user.service.UserService;
import flower_shop.web.dto.RegisterRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static flower_shop.web.Paths.API_V1_BASE_PATH;

@CrossOrigin(origins = "http://localhost:4200") // Replace with https://yourdomain.com when in production
@RestController
@RequestMapping(API_V1_BASE_PATH + "/auth")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public ResponseEntity<User> register(@RequestBody RegisterRequest registerRequest) {

        User user = userService.register(registerRequest);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(user);
    }
}
