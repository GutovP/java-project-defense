package flower_shop;

import flower_shop.exception.AuthenticationException;
import flower_shop.user.model.User;
import flower_shop.user.model.UserRole;
import flower_shop.user.repository.UserRepository;
import flower_shop.user.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import static org.junit.jupiter.api.Assertions.*;


@ActiveProfiles("test")
@SpringBootTest
public class UserPasswordChangeITest {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    public void setUp() {
        userRepository.deleteAll();
    }


    @Test
    void shouldChangePasswordSuccessfully() {

        // Given
        User user = User.builder()
                .firstName("FistName")
                .lastName("LastName")
                .email("existing@gmail.com")
                .password(passwordEncoder.encode("oldPassword"))
                .role(UserRole.USER)
                .build();

        userRepository.save(user);

        // When
        User updated = userService.changeUserPassword(
                "existing@gmail.com",
                "oldPassword",
                "newSecurePassword"
        );

        // Then
        assertNotEquals("newSecurePassword", updated.getPassword());
        assertTrue(passwordEncoder.matches("newSecurePassword", updated.getPassword()));

        assertFalse(passwordEncoder.matches("oldPassword", updated.getPassword()));

        User fromDb = userRepository.findByEmail("existing@gmail.com").orElseThrow();
        assertTrue(passwordEncoder.matches("newSecurePassword", fromDb.getPassword()));
    }

    @Test
    void shouldThrowExceptionWhenCurrentPasswordIsIncorrect() {

        // Given
        User user = User.builder()
                .firstName("FirstName")
                .lastName("LastName")
                .email("existing@gmail.com")
                .password(passwordEncoder.encode("correctPassword"))
                .role(UserRole.USER)
                .build();

        userRepository.save(user);

        // When & Then
        assertThrows(AuthenticationException.class,
                () -> userService.changeUserPassword(
                        "existing@gmail.com",
                        "wrongPassword",
                        "newPassword"
                ));
    }

    @Test
    void shouldThrowExceptionWhenNewPasswordIsEmpty() {

        // Given
        User user = User.builder()
                .firstName("FirstName")
                .lastName("LastName")
                .email("existing@gmail.com")
                .password(passwordEncoder.encode("correctPassword"))
                .role(UserRole.USER)
                .build();

        userRepository.save(user);

        // When + Then
        assertThrows(AuthenticationException.class,
                () -> userService.changeUserPassword(
                        "existing@gmail.com",
                        "correctPassword",
                        "   "
                ));
    }

}
