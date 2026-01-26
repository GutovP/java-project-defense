package flower_shop;

import flower_shop.exception.UserAlreadyExistException;
import flower_shop.user.model.User;
import flower_shop.user.model.UserRole;
import flower_shop.user.repository.UserRepository;
import flower_shop.user.service.UserService;
import flower_shop.web.dto.RegisterRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import static org.junit.jupiter.api.Assertions.*;


@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@SpringBootTest
public class UserRegistrationITest {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
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
