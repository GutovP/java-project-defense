package flower_shop;

import flower_shop.exception.AuthenticationException;
import flower_shop.security.JWTService;
import flower_shop.user.model.User;
import flower_shop.user.model.UserRole;
import flower_shop.user.repository.UserRepository;
import flower_shop.user.service.UserService;
import flower_shop.web.dto.LoginRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;


@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
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
