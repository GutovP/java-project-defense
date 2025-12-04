package flower_shop.user;

import flower_shop.exception.AuthenticationException;
import flower_shop.exception.ResourceNotFoundException;
import flower_shop.exception.UserAlreadyExistException;
import flower_shop.security.JWTService;
import flower_shop.user.model.User;
import flower_shop.user.model.UserRole;
import flower_shop.user.repository.UserRepository;
import flower_shop.user.service.UserService;
import flower_shop.web.dto.LoginRequest;
import flower_shop.web.dto.RegisterRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
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

    @Test
    void whenUserLoginWithWrongPassword_thenExceptionIsThrown() {

        // Given
        LoginRequest loginRequest = LoginRequest.builder()
                .email("test@example.com")
                .password("wrongPassword")
                .build();

        User existingUser = User.builder()
                .email(loginRequest.getEmail())
                .password("encodedCorrectPassword")
                .build();

        when(userRepository.findByEmail(loginRequest.getEmail())).thenReturn(Optional.of(existingUser));
        when(passwordEncoder.matches(loginRequest.getPassword(), existingUser.getPassword())).thenReturn(false);

        // When & Then
        assertThrows(AuthenticationException.class, () -> userService.loginAndAuthenticate(loginRequest));
        verify(userRepository).findByEmail(loginRequest.getEmail());
        verify(passwordEncoder).matches("wrongPassword", "encodedCorrectPassword");
        verify(userRepository, never()).save(any());
    }

    @Test
    void whenUserLoginWithNonExistingEmail_thenExceptionIsThrown() {

        // Given
        LoginRequest loginRequest = LoginRequest.builder()
                .email("nonExistingEmail@example.com")
                .password("password")
                .build();

        when(userRepository.findByEmail(loginRequest.getEmail())).thenReturn(Optional.empty());

        // When & Then
        assertThrows(ResourceNotFoundException.class, () -> userService.loginAndAuthenticate(loginRequest));
        verify(userRepository, never()).save(any());
    }

    @Test
    void whenUserLoginWithCorrectCredentials_thenTokenIsReturned() {

        // Given
        LoginRequest loginRequest = LoginRequest.builder()
                .email("test@example.com")
                .password("password")
                .build();

        User existingUser = User.builder()
                .email(loginRequest.getEmail())
                .password("encodedPassword")
                .role(UserRole.USER)
                .build();

        when(userRepository.findByEmail(loginRequest.getEmail())).thenReturn(Optional.of(existingUser));
        when(passwordEncoder.matches(loginRequest.getPassword(), existingUser.getPassword())).thenReturn(true);

        Authentication authentication = mock(Authentication.class);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(authentication);
        when(jwtService.generateToken(loginRequest.getEmail(), existingUser.getRole())).thenReturn("jwt-token");

        // When
        String token = userService.loginAndAuthenticate(loginRequest);

        // Then
        assertEquals("jwt-token", token);
        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(jwtService).generateToken("test@example.com", UserRole.USER);

    }

    @Test
    void whenAuthenticationIsFailed_thenExceptionIsThrown() {

        // Given
        LoginRequest loginRequest = LoginRequest.builder()
                .email("test@example.com")
                .password("password")
                .build();

        User existingUser = User.builder()
                .email(loginRequest.getEmail())
                .password("encodedPassword")
                .role(UserRole.USER)
                .build();

        when(userRepository.findByEmail(loginRequest.getEmail())).thenReturn(Optional.of(existingUser));
        when(passwordEncoder.matches(loginRequest.getPassword(), existingUser.getPassword())).thenReturn(true);

        Authentication authentication = mock(Authentication.class);
        when(authentication.isAuthenticated()).thenReturn(false);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(authentication);

        // When & Then
        assertThrows(AuthenticationException.class, () -> userService.loginAndAuthenticate(loginRequest));
        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(jwtService, never()).generateToken(anyString(), any());

    }

    @Test
    void whenGetUserByEmailReturnsUser() {

        // Given
        User user = User.builder()
                .email("test@example.com")
                .build();

        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));

        // When
        userService.getUserByEmail(user.getEmail());

        // Then
        assertEquals("test@example.com", user.getEmail());
        verify(userRepository).findByEmail("test@example.com");
    }

    @Test
    void whenUserDoesNotExist_thenGetUserByEmailThrowsException() {

        // Given
        when(userRepository.findByEmail("missing@example.com")).thenReturn(Optional.empty());

        // When & Then
        assertThrows(ResourceNotFoundException.class, () -> userService.getUserByEmail("missing@example.com"));
    }



}
