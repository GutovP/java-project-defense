package flower_shop.web;


import com.fasterxml.jackson.databind.ObjectMapper;
import flower_shop.exception.UserAlreadyExistException;
import flower_shop.exception.UserNotFoundException;
import flower_shop.user.model.User;
import flower_shop.user.model.UserRole;
import flower_shop.user.service.UserService;
import flower_shop.web.dto.LoginRequest;
import flower_shop.web.dto.RegisterRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import flower_shop.exception.AuthenticationException;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;



@SpringBootTest
@AutoConfigureMockMvc
public class AuthControllerApiTest {

    @MockitoBean
    private UserService userService;

    @Autowired
    private MockMvc mockMvc;

    @Test
    void shouldRegisterUserSuccessfully() throws Exception {
        RegisterRequest request = RegisterRequest.builder()
                .firstName("Petar")
                .lastName("Gutov")
                .email("admin@google.com")
                .password("securepassword")
                .build();

        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(request)))
                .andExpect(status().isCreated());

        verify(userService).register(any(RegisterRequest.class));
    }

    @Test
    void shouldFailRegistrationIfUserExists() throws Exception {
        RegisterRequest request = RegisterRequest.builder()
                .firstName("Petar")
                .lastName("Gutov")
                .email("admin@google.com")
                .password("securepassword")
                .build();

        doThrow(new UserAlreadyExistException("User already exists"))
                .when(userService).register(any(RegisterRequest.class));

        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        verify(userService).register(any(RegisterRequest.class));
    }

    @Test
    void shouldLoginUserSuccessfully() throws Exception {
        LoginRequest request = LoginRequest.builder()
                .email("admin@gmail.com")
                .password("securepassword")
                .build();

        User user = User.builder()
                .id(UUID.randomUUID())
                .firstName("Petar")
                .lastName("Gutov")
                .email(request.getEmail())
                .role(UserRole.USER)
                .build();

        String mockToken = "mocked.jwt.token";
        when(userService.loginAndAuthenticate(any(LoginRequest.class))).thenReturn(mockToken);
        when(userService.getUserByEmail(anyString())).thenReturn(user);

        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value(mockToken))
                .andExpect(jsonPath("$.user.email").value(user.getEmail()));

        verify(userService).loginAndAuthenticate(any(LoginRequest.class));
        verify(userService).getUserByEmail(anyString());
    }

    @Test
    void shouldFailLoginIfUserNotFound() throws Exception {
        LoginRequest request = LoginRequest.builder()
                .email("nonexistent@gmail.com")
                .password("securepassword")
                .build();

        when(userService.loginAndAuthenticate(any(LoginRequest.class)))
                .thenThrow(new UserNotFoundException("User not found"));

        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(request)))
                .andExpect(status().isNotFound());

        verify(userService).loginAndAuthenticate(any(LoginRequest.class));
    }

    @Test
    void shouldFailLoginIfPasswordIsIncorrect() throws Exception {
        LoginRequest request = LoginRequest.builder()
                .email("admin@gmail.com")
                .password("wrongpassword")
                .build();

        when(userService.loginAndAuthenticate(any(LoginRequest.class)))
                .thenThrow(new AuthenticationException("Invalid credentials"));

        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(request)))
                .andExpect(status().isUnauthorized());

        verify(userService).loginAndAuthenticate(any(LoginRequest.class));
    }


}
