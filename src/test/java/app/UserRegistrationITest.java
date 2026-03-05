package app;

import app.exception.UserAlreadyExistException;
import app.user.model.User;
import app.user.model.UserRole;
import app.user.repository.UserRepository;
import app.user.service.UserService;
import app.web.dto.RegisterRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import static org.junit.jupiter.api.Assertions.*;


@ActiveProfiles("test")
@SpringBootTest
public class UserRegistrationITest {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    public void setUp() {
        userRepository.deleteAll();
    }


    @Test
    void shouldRegisterUserSuccessfully() {

        // Given
        RegisterRequest registerRequest = RegisterRequest.builder()
                .firstName("FirstName")
                .lastName("LastName")
                .email("admin@gmail.com")
                .password("password")
                .build();

        // When
        User user = userService.register(registerRequest);

        // Then
        assertNotNull(user);

        assertEquals("FirstName", user.getFirstName());
        assertEquals("LastName", user.getLastName());
        assertEquals("admin@gmail.com", user.getEmail());
        assertEquals(UserRole.USER, user.getRole());

        assertNotEquals("encodedPassword", user.getPassword());

        assertTrue(userRepository.findByEmail("admin@gmail.com").isPresent());


    }

    @Test
    void shouldNotRegisterUserWithExistingEmail() {

        // Given
        User existingUser = User.builder()
                .firstName("FirstName")
                .lastName("LastName")
                .email("admin@gmail.com")
                .password("password")
                .role(UserRole.USER)
                .build();

        userRepository.save(existingUser);

        RegisterRequest requestDto = RegisterRequest.builder()
                .firstName("FirstNewName")
                .lastName("LastNewName")
                .email("admin@gmail.com")
                .password("password")
                .build();

        // When & Then
        assertThrows(UserAlreadyExistException.class, () -> userService.register(requestDto));


    }

}
