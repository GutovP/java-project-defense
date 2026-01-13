package flower_shop.user;

import flower_shop.exception.AuthenticationException;
import flower_shop.exception.ResourceNotFoundException;
import flower_shop.exception.UserAlreadyExistException;
import flower_shop.security.AuthenticationMetadata;
import flower_shop.security.JWTService;
import flower_shop.user.model.User;
import flower_shop.user.model.UserRole;
import flower_shop.user.repository.UserRepository;
import flower_shop.user.service.UserService;
import flower_shop.web.dto.LoginRequest;
import flower_shop.web.dto.ProfileEditRequest;
import flower_shop.web.dto.RegisterRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
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
        when(passwordEncoder.encode("password")).thenReturn("encodedPassword");

        //When
        userService.register(request);

        //Then
        verify(userRepository).save(argThat(savedUser ->
                savedUser.getFirstName().equals("First Name") &&
                savedUser.getLastName().equals("Last Name") &&
                savedUser.getEmail().equals("test@example.com") &&
                savedUser.getPassword().equals("encodedPassword")
        ));
    }

    @Test
    void whenUserLoginWithWrongPassword_thenExceptionIsThrown() {

        // Given
        LoginRequest dto = LoginRequest.builder()
                .email("test@example.com")
                .password("wrongPassword")
                .build();

        User user = User.builder()
                .password("encodedCorrectPassword")
                .build();

        when(userRepository.findByEmail(dto.getEmail())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(dto.getPassword(), user.getPassword())).thenReturn(false);

        // When & Then
        assertThrows(AuthenticationException.class, () -> userService.loginAndAuthenticate(dto));
        verify(userRepository).findByEmail(dto.getEmail());
        verify(passwordEncoder).matches("wrongPassword", "encodedCorrectPassword");
        verify(userRepository, never()).save(any());
    }

    @Test
    void whenUserLoginWithNonExistingEmail_thenExceptionIsThrown() {

        // Given
        LoginRequest dto = LoginRequest.builder()
                .email("nonExistingEmail@example.com")
                .build();

        when(userRepository.findByEmail(dto.getEmail())).thenReturn(Optional.empty());

        // When & Then
        assertThrows(ResourceNotFoundException.class, () -> userService.loginAndAuthenticate(dto));
        verify(userRepository, never()).save(any());
    }

    @Test
    void whenUserLoginWithCorrectCredentials_thenTokenIsReturned() {

        // Given
        LoginRequest dto = LoginRequest.builder()
                .email("test@example.com")
                .password("password")
                .build();

        User user = User.builder()
                .password("encodedPassword")
                .role(UserRole.USER)
                .build();

        when(userRepository.findByEmail(dto.getEmail())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(dto.getPassword(), user.getPassword())).thenReturn(true);

        Authentication authentication = mock(Authentication.class);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(authentication);
        when(jwtService.generateToken(dto.getEmail(), user.getRole())).thenReturn("jwt-token");

        // When
        String token = userService.loginAndAuthenticate(dto);

        // Then
        assertEquals("jwt-token", token);
        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(jwtService).generateToken("test@example.com", UserRole.USER);

    }

    @Test
    void whenAuthenticationIsFailed_thenExceptionIsThrown() {

        // Given
        LoginRequest dto = LoginRequest.builder()
                .email("test@example.com")
                .password("password")
                .build();

        User user = User.builder()
                .password("encodedPassword")
                .role(UserRole.USER)
                .build();

        when(userRepository.findByEmail(dto.getEmail())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(dto.getPassword(), user.getPassword())).thenReturn(true);

        Authentication authentication = mock(Authentication.class);
        when(authentication.isAuthenticated()).thenReturn(false);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(authentication);

        // When & Then
        assertThrows(AuthenticationException.class, () -> userService.loginAndAuthenticate(dto));
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

    @Test
    void whenProfileEditRequestHasAllFields_thenAllAreUpdated() {

        // Given
        ProfileEditRequest dto = ProfileEditRequest.builder()
                .firstName("FirstName")
                .lastName("LastName")
                .email("new@example.com")
                .build();

        User user = User.builder().build();

        when(userRepository.findByEmail("email@example.com")).thenReturn(Optional.of(user));

        // When
        userService.updateUserProfile("email@example.com", dto);

        // Then
        assertEquals("FirstName", user.getFirstName());
        assertEquals("LastName", user.getLastName());
        assertEquals("new@example.com", user.getEmail());
        verify(userRepository, times(1)).save(user);

    }

    @Test
    void whenProfileEditRequestHasPartialFields_thenOnlyThoseAreUpdated() {

        // Given
        ProfileEditRequest dto = ProfileEditRequest.builder()
                .firstName("NewFirst")
                .build();

        User user = User.builder().build();

        when(userRepository.findByEmail("old@example.com")).thenReturn(Optional.of(user));

        // When
        userService.updateUserProfile("old@example.com", dto);

        // Then
        assertEquals("NewFirst", user.getFirstName());
        verify(userRepository, times(1)).save(user);
    }

    @Test
    void whenUserDoesNotExist_thenExceptionIsThrown() {

        // Given
        ProfileEditRequest dto = ProfileEditRequest.builder().build();

        when(userRepository.findByEmail("missing@example.com")).thenReturn(Optional.empty());

        // When & Then
        assertThrows(ResourceNotFoundException.class, () -> userService.updateUserProfile("missing@example.com", dto));
        verify(userRepository, never()).save(any());
        }

    @ParameterizedTest
    @MethodSource("nullFieldCases")
    void whenFieldIsNull_thenItIsNotUpdated(
            String firstName, String lastName, String email,
            String expectedFirst, String expectedLast, String expectedEmail) {

        // Given
        User existingUser = User.builder()
                .firstName("OriginalFirst")
                .lastName("OriginalLast")
                .email("original@example.com")
                .build();

        ProfileEditRequest editRequest = ProfileEditRequest.builder()
                .firstName(firstName)
                .lastName(lastName)
                .email(email)
                .build();

        when(userRepository.findByEmail("original@example.com")).thenReturn(Optional.of(existingUser));
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        User updatedUser = userService.updateUserProfile("original@example.com", editRequest);

        // Then
        assertEquals(expectedFirst, updatedUser.getFirstName());
        assertEquals(expectedLast, updatedUser.getLastName());
        assertEquals(expectedEmail, updatedUser.getEmail());

    }

    private static Stream<Arguments> nullFieldCases() {
        return Stream.of(
                Arguments.of(
                        null, "NewLast", "new@example.com",
                        "OriginalFirst", "NewLast", "new@example.com"
                ),
                Arguments.of(
                        "NewFirst", null, "new@example.com",
                        "NewFirst", "OriginalLast", "new@example.com"
                ),
                Arguments.of(
                        "NewFirst", "NewLast", null,
                        "NewFirst", "NewLast", "original@example.com"
                ),
                Arguments.of(
                        null, null, null,
                        "OriginalFirst", "OriginalLast", "original@example.com"
                )
        );
    }

    @Test
    void whenCurrentPasswordMatchesAndNewPasswordValid_thenPasswordIsUpdated() {

        // Given
        User user = User.builder()
                .password("encodedOldPassword")
                .build();

        when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("oldPassword", "encodedOldPassword")).thenReturn(true);
        when(passwordEncoder.encode("newPassword")).thenReturn("encodedNewPassword");

        // When
        userService.changeUserPassword("user@example.com", "oldPassword", "newPassword");

        // Then
        assertEquals("encodedNewPassword", user.getPassword());
        verify(userRepository, times(1)).save(user);
    }

    @Test
    void whenCurrentPasswordDoesNotMatch_thenExceptionIsThrown() {

        // Given
        User user = User.builder()
                .password("encodedOldPassword")
                .build();

        when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("wrongPassword", "encodedOldPassword")).thenReturn(false);

        // When & Then
        assertThrows(AuthenticationException.class,() -> userService.changeUserPassword("user@example.com", "wrongPassword", "newPassword"));
        verify(userRepository, never()).save(any());
    }

    @Test
    void whenNewPasswordIsNullOrBlank_thenExceptionIsThrown() {

        // Given
        User user = User.builder()
                .password("encodedOldPassword")
                .build();

        when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("oldPassword", "encodedOldPassword")).thenReturn(true);

        // When & Then
        assertThrows(AuthenticationException.class,() -> userService.changeUserPassword("user@example.com", "oldPassword", null));
        assertThrows(AuthenticationException.class,() -> userService.changeUserPassword("user@example.com", "oldPassword", "  "));
        verify(userRepository, never()).save(any());
    }

    @Test
    void whenUserIsLoadedByUsername_thenUserDetailsAreReturned() {
        // Given
        String email = "Normalized@Example.com";
        User user = User.builder()
                .id(UUID.randomUUID())
                .email("normalized@example.com")
                .password("encodedPassword")
                .role(UserRole.USER)
                .build();

        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));

        // When
        UserDetails authenticationMetadata = userService.loadUserByUsername(email);

        // Then
        assertInstanceOf(AuthenticationMetadata.class, authenticationMetadata);
        AuthenticationMetadata result = (AuthenticationMetadata) authenticationMetadata;

        assertEquals(user.getId(), result.getUserId());
        assertEquals(user.getEmail(), result.getUsername());
        assertEquals(user.getPassword(), result.getPassword());
        assertEquals(user.getRole(), result.getUserRole());
        assertThat(result.getAuthorities()).hasSize(1);
        assertEquals("ROLE_USER", result.getAuthorities().iterator().next().getAuthority());
        verify(userRepository).findByEmail(user.getEmail());
    }

}
