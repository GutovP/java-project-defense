package flower_shop.web;

import flower_shop.user.model.User;
import flower_shop.user.service.UserService;
import flower_shop.web.dto.LoginRequest;
import flower_shop.web.dto.LoginResponse;
import flower_shop.web.dto.RegisterRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import static flower_shop.web.Paths.API_V1_BASE_PATH;

@RestController
@RequestMapping(API_V1_BASE_PATH + "/auth")
public class AuthController {

    private final UserService userService;

    @Autowired
    public AuthController(UserService userService) {
        this.userService = userService;

    }

    @PostMapping("/register")
    public ResponseEntity<Void> register(@RequestBody RegisterRequest registerRequest) {

        userService.register(registerRequest);

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest loginRequest) {

        String token = userService.loginAndAuthenticate(loginRequest);
        User user = userService.getUserByEmail(loginRequest.getEmail());
        LoginResponse loginResponse = new LoginResponse(token, user);

        return ResponseEntity.status(HttpStatus.OK).body(loginResponse);
    }

}
