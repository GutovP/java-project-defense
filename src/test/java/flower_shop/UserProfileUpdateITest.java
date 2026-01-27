package flower_shop;

import flower_shop.exception.ResourceNotFoundException;
import flower_shop.user.model.User;
import flower_shop.user.model.UserRole;
import flower_shop.user.repository.UserRepository;
import flower_shop.user.service.UserService;
import flower_shop.web.dto.ProfileEditRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;


@ActiveProfiles("test")
@SpringBootTest
public class UserProfileUpdateITest {

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
    void shouldUpdateAllUserFieldsSuccessfully() {

        // Given
        User user = User.builder()
                .firstName("FirstName")
                .lastName("LastName")
                .email("existing@gmail.com")
                .password(passwordEncoder.encode("pass"))
                .role(UserRole.USER)
                .build();

        userRepository.save(user);

        ProfileEditRequest editRequest = ProfileEditRequest.builder()
                .firstName("UpdatedFirst")
                .lastName("UpdatedLast")
                .email("updated@gmail.com")
                .build();

        // When
        User updated = userService.updateUserProfile("existing@gmail.com", editRequest);

        // Then
        assertEquals("UpdatedFirst", updated.getFirstName());
        assertEquals("UpdatedLast", updated.getLastName());
        assertEquals("updated@gmail.com", updated.getEmail());

        User fromDb = userRepository.findByEmail("updated@gmail.com").orElseThrow();
        assertEquals("UpdatedFirst", fromDb.getFirstName());
    }

    @Test
    void shouldUpdateOnlyProvidedFields() {

        // Given
        User user = User.builder()
                .firstName("FirstName")
                .lastName("LastName")
                .email("existing@gmail.com")
                .password(passwordEncoder.encode("pass"))
                .role(UserRole.USER)
                .build();

        userRepository.save(user);

        ProfileEditRequest editRequest = ProfileEditRequest.builder()
                .firstName("NewFirstName")
                .build();

        // When
        User updated = userService.updateUserProfile("existing@gmail.com", editRequest);

        // Then
        assertEquals("NewFirstName", updated.getFirstName());
        assertEquals("LastName", updated.getLastName());
        assertEquals("existing@gmail.com", updated.getEmail());
    }

    @Test
    void shouldThrowExceptionWhenUserDoesNotExist() {

        ProfileEditRequest editRequest = ProfileEditRequest.builder()
                .firstName("Whatever")
                .build();

        // When & Then
        assertThrows(ResourceNotFoundException.class,
                () -> userService.updateUserProfile("missing@gmail.com", editRequest));
    }


}
