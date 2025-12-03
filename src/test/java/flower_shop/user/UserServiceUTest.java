package flower_shop.user;

import flower_shop.exception.UserAlreadyExistException;
import flower_shop.security.JWTService;
import flower_shop.user.model.User;
import flower_shop.user.model.UserRole;
import flower_shop.user.repository.UserRepository;
import flower_shop.user.service.UserService;
import flower_shop.web.dto.RegisterRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
public class UserServiceUTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private AuthenticationManager authenticationManager;
    @Mock
    private JWTService jwtService;

    @InjectMocks
    private UserService userService;


    @Test
    void whenRegisterAndUserAlreadyExists_thenExceptionIsThrown() {

        // Given
        RegisterRequest request = RegisterRequest.builder()
                .firstName("First Name")
                .lastName("Last Name")
                .email("test@example.com")
                .password("password")
                .build();

        User existingUser = User.builder()
                .email(request.getEmail())
                .build();

        when(userRepository.findByEmail(existingUser.getEmail())).thenReturn(Optional.of(existingUser));

        // When & Then
        assertThrows(UserAlreadyExistException.class, () -> userService.register(request));
        verify(userRepository, never()).save(any());

    }

    @Test
    void whenUserRegisteredSuccessfully() {

        // Given
        RegisterRequest request = RegisterRequest.builder()
                .firstName("First Name")
                .lastName("Last Name")
                .email("test@example.com")
                .password("password")
                .build();

        when(userRepository.findByEmail(request.getEmail())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(request.getPassword())).thenReturn("encodedPassword");

        //When
        userService.register(request);

        //Then
        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(captor.capture());

        User user = captor.getValue();
        assertEquals("First Name", user.getFirstName());
        assertEquals("Last Name", user.getLastName());
        assertEquals("test@example.com", user.getEmail());
        assertEquals("encodedPassword", user.getPassword());
        assertEquals(UserRole.USER, user.getRole());
    }
}
