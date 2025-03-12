package flower_shop.web;

import flower_shop.exception.InvalidTokenException;
import flower_shop.exception.TokenExpiredException;
import flower_shop.security.JWTService;
import flower_shop.user.model.User;
import flower_shop.user.service.UserService;
import flower_shop.web.dto.LoginRequest;
import flower_shop.web.dto.LoginResponse;
import flower_shop.web.dto.RegisterRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.*;


import static flower_shop.web.Paths.API_V1_BASE_PATH;

@RestController
@RequestMapping(API_V1_BASE_PATH + "/auth")
public class AuthController {

    private final UserService userService;
    private final JWTService jwtService;
    private final UserDetailsService userDetailsService;

    @Autowired
    public AuthController(UserService userService, JWTService jwtService, UserDetailsService userDetailsService) {
        this.userService = userService;
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
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

        return ResponseEntity.ok(loginResponse);
    }

    @GetMapping("/validate-token")
    public ResponseEntity<?> validateToken(@RequestHeader("Authorization") String token) {
        if (token == null || !token.startsWith("Bearer ")) {
            throw new InvalidTokenException("Invalid token format");
        }

        token = token.substring(7);

        String email = jwtService.extractEmail(token);
        UserDetails userDetails = userDetailsService.loadUserByUsername(email);

        if (jwtService.validateToken(token, userDetails)) {
            return ResponseEntity.ok().body("Token is valid");

        } else {
            throw new TokenExpiredException("Token is expired");
        }
    }
}
