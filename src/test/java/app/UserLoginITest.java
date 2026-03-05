package app;

import app.exception.AuthenticationException;
import app.security.JWTService;
import app.user.model.User;
import app.user.model.UserRole;
import app.user.repository.UserRepository;
import app.user.service.UserService;
import app.web.dto.LoginRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import static org.junit.jupiter.api.Assertions.*;


@ActiveProfiles("test")
@SpringBootTest
public class UserLoginITest {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JWTService jwtService;

    @BeforeEach
    public void setUp() {
        userRepository.deleteAll();
    }


    @Test
    void shouldLoginSuccessfullyAndReturnJwtToken() {

        // Given
        User user = User.builder()
                .firstName("FirstName")
                .lastName("LastName")
                .email("existing@example.com")
                .password(passwordEncoder.encode("password123"))
                .role(UserRole.USER)
                .build();

        userRepository.save(user);

        LoginRequest loginRequest = LoginRequest.builder()
                .email("existing@example.com")
                .password("password123")
                .build();

        // When
        String token = userService.loginAndAuthenticate(loginRequest);

        // Then
        assertNotNull(token);
        assertFalse(token.isBlank());
        String extractedEmail = jwtService.extractEmail(token);
        assertEquals("existing@example.com", extractedEmail);
    }

    @Test
    void shouldThrowExceptionWhenEmailDoesNotExist() {

        // Given
        LoginRequest loginRequest = LoginRequest.builder()
                .email("missing@example.com")
                .password("password123")
                .build();

        // When & Then
        assertThrows(AuthenticationException.class, () -> userService.loginAndAuthenticate(loginRequest));
    }

    @Test
    void shouldThrowExceptionWhenPasswordIsIncorrect() {

        // Given
        User user = User.builder()
                .firstName("FirstName")
                .lastName("LastName")
                .email("existing@example.com")
                .password(passwordEncoder.encode("correctPassword"))
                .role(UserRole.USER)
                .build();

        userRepository.save(user);

        LoginRequest loginRequest = LoginRequest.builder()
                .email("existing@example.com")
                .password("wrongPassword")
                .build();

        // When & Then
        assertThrows(AuthenticationException.class, () -> userService.loginAndAuthenticate(loginRequest));
    }



}
