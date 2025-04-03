package flower_shop.user;

import flower_shop.exception.AuthenticationException;
import flower_shop.exception.UserAlreadyExistException;
import flower_shop.exception.UserNotFoundException;
import flower_shop.security.JWTService;
import flower_shop.user.model.User;
import flower_shop.user.model.UserRole;
import flower_shop.user.repository.UserRepository;
import flower_shop.user.service.UserService;
import flower_shop.web.dto.LoginRequest;
import flower_shop.web.dto.PasswordChangeRequest;
import flower_shop.web.dto.ProfileEditRequest;
import flower_shop.web.dto.RegisterRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

// 1. Create the test class
// 2. Annotate the class with @ExtendWith(MockitoExtension.class)
// 3. Get the class you want recipient test
// 4. Get all dependencies of that class and annotate them with @Mock
// 5. Inject all those dependencies recipient the class we test with annotation @InjectMocks

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
    void shouldThrowExceptionWhenUserAlreadyExists() {
        RegisterRequest registerRequest = RegisterRequest.builder()
                .firstName("Petar")
                .lastName("Gutov")
                .email("admin@gmail.com")
                .password("123123")
                .build();

        when(userRepository.findByEmail(registerRequest.getEmail())).thenReturn(Optional.of(new User()));

        assertThrows(UserAlreadyExistException.class, () -> userService.register(registerRequest));

        verify(userRepository).findByEmail(registerRequest.getEmail());
        verify(userRepository, never()).save(any());
    }

    @Test
    void shouldRegisterUserSuccessfully() {
        RegisterRequest registerRequest = RegisterRequest.builder()
                .firstName("Petar")
                .lastName("Gutov")
                .email("admin@gmail.com")
                .password("123123")
                .build();

        when(userRepository.findByEmail(registerRequest.getEmail())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(registerRequest.getPassword())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        userService.register(registerRequest);

        verify(userRepository).findByEmail(registerRequest.getEmail());
        verify(passwordEncoder).encode(registerRequest.getPassword());
        verify(userRepository).save(any(User.class));
    }

    @Test
    void shouldAuthenticateUserSuccessfully() {
        LoginRequest loginRequest = LoginRequest.builder()
                .email("admin@gmail.com")
                .password("123123")
                .build();

        User user = User.builder()
                .firstName("Petar")
                .lastName("Gutov")
                .email(loginRequest.getEmail())
                .password("hashedPassword")
                .role(UserRole.USER)
                .build();

        Authentication authenticationMock = mock(Authentication.class);

        when(authenticationManager.authenticate(any())).thenReturn(authenticationMock);
        when(authenticationMock.isAuthenticated()).thenReturn(true);

        when(userRepository.findByEmail(loginRequest.getEmail())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())).thenReturn(true);
        when(authenticationManager.authenticate(any())).thenReturn(authenticationMock);
        when(jwtService.generateToken(loginRequest.getEmail(), user.getRole())).thenReturn("mockJwtToken");

        String token = userService.loginAndAuthenticate(loginRequest);

        assertEquals("mockJwtToken", token);
        verify(userRepository).findByEmail(loginRequest.getEmail());
        verify(passwordEncoder).matches(loginRequest.getPassword(), user.getPassword());
        verify(authenticationManager).authenticate(any());
        verify(jwtService).generateToken(loginRequest.getEmail(), user.getRole());
    }

    @Test
    void shouldThrowExceptionWhenUserNotFoundDuringLogin() {
        LoginRequest loginRequest = LoginRequest.builder()
                .email("nonexistent@gmail.com")
                .password("123456")
                .build();

        when(userRepository.findByEmail(loginRequest.getEmail())).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userService.loginAndAuthenticate(loginRequest));

        verify(userRepository).findByEmail(loginRequest.getEmail());
        verify(authenticationManager, never()).authenticate(any());
    }

    @Test
    void shouldThrowExceptionWhenPasswordIsIncorrect() {
        LoginRequest loginRequest = LoginRequest.builder()
                .email("admin@gmail.com")
                .password("wrongpassword")
                .build();

        User user = User.builder()
                .firstName("Petar")
                .lastName("Gutov")
                .email(loginRequest.getEmail())
                .password("hashedPassword")
                .role(UserRole.USER)
                .build();

        when(userRepository.findByEmail(loginRequest.getEmail())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())).thenReturn(false);

        assertThrows(AuthenticationException.class, () -> userService.loginAndAuthenticate(loginRequest));

        verify(userRepository).findByEmail(loginRequest.getEmail());
        verify(passwordEncoder).matches(loginRequest.getPassword(), user.getPassword());
        verify(authenticationManager, never()).authenticate(any());
        verify(jwtService, never()).generateToken(any(), any());
    }

    @Test
    void shouldUpdateUserProfileSuccessfully() {
        String email = "admin@gmail.com";

        ProfileEditRequest profileEditRequest = ProfileEditRequest.builder()
                .firstName("Petar1")
                .lastName("Gutov1")
                .build();

        User user = User.builder()
                .firstName("Petar")
                .lastName("Gutov")
                .email(email)
                .password("hashedPassword")
                .role(UserRole.USER)
                .build();

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        User updatedUser = userService.updateUserProfile(email, profileEditRequest);

        assertEquals("Petar1", updatedUser.getFirstName());
        assertEquals("Gutov1", updatedUser.getLastName());

        verify(userRepository).findByEmail(email);
        verify(userRepository).save(any(User.class));
    }

    @Test
    void shouldThrowExceptionWhenUpdatingProfileOfNonexistentUser() {
        String email = "nonexistent@gmail.com";

        ProfileEditRequest profileEditRequest = ProfileEditRequest.builder()
                .firstName("New")
                .lastName("Name")
                .build();

        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userService.updateUserProfile(email, profileEditRequest));

        verify(userRepository).findByEmail(email);
        verify(userRepository, never()).save(any());
    }

    @Test
    void shouldChangeUserPasswordSuccessfully() {
        String email = "admin@gmail.com";
        PasswordChangeRequest passwordChangeRequest = PasswordChangeRequest.builder()
                .currentPassword("123123")
                .newPassword("newPassword")
                .build();

        User user = User.builder()
                .firstName("Petar")
                .lastName("Gutov")
                .email(email)
                .password("hashedPassword")
                .role(UserRole.USER)
                .build();

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(passwordChangeRequest.getCurrentPassword(), user.getPassword())).thenReturn(true);
        when(passwordEncoder.encode(passwordChangeRequest.getNewPassword())).thenReturn("newEncodedPassword");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        userService.changeUserPassword(email, passwordChangeRequest.getCurrentPassword(), passwordChangeRequest.getNewPassword());

        assertEquals("newEncodedPassword", user.getPassword());

        verify(userRepository).findByEmail(email);

        verify(passwordEncoder).encode(passwordChangeRequest.getNewPassword());
        verify(userRepository).save(any(User.class));
    }



}
