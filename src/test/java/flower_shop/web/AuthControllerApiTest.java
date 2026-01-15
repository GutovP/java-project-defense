package flower_shop.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import flower_shop.exception.AuthenticationException;
import flower_shop.exception.ResourceNotFoundException;
import flower_shop.exception.UserAlreadyExistException;
import flower_shop.security.JWTService;
import flower_shop.user.model.User;
import flower_shop.user.model.UserRole;
import flower_shop.user.service.UserService;
import flower_shop.web.dto.LoginRequest;
import flower_shop.web.dto.RegisterRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(AuthController.class)
@AutoConfigureMockMvc(addFilters = false)
public class AuthControllerApiTest {

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private JWTService jwtService;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;


    @Test
    void shouldRegisterUserSuccessfully() throws Exception {

        // Given
        RegisterRequest dto = RegisterRequest.builder()
                .firstName("newFirst")
                .lastName("newLast")
                .email("new@example.com")
                .password("newPassword")
                .build();

        MockHttpServletRequestBuilder request = post("/api/v1/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsBytes(dto));

        // When & Then
        mockMvc.perform(request)
                .andExpect(status().isCreated());

        verify(userService).register(any(RegisterRequest.class));

    }

    @Test
    void shouldReturnError_whenUserAlreadyExists() throws Exception {

        //Given
        RegisterRequest dto = RegisterRequest.builder()
                .firstName("oldFirst")
                .lastName("oldLast")
                .email("old@example.com")
                .password("oldPassword")
                .build();

        doThrow(new UserAlreadyExistException("User already exists!"))
                .when(userService).register(any(RegisterRequest.class));

        MockHttpServletRequestBuilder request = post("/api/v1/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsBytes(dto));

        // When & Then
        mockMvc.perform(request)
                .andExpect(status().isBadRequest());

        verify(userService).register(any(RegisterRequest.class));
    }

    @Test
    void shouldLoginSuccessfully() throws Exception {

        // Given
        LoginRequest dto = LoginRequest.builder()
                .email("existing@example.com")
                .password("password123")
                .build();

        User user = User.builder()
                .id(UUID.randomUUID())
                .firstName("John")
                .lastName("Doe")
                .email("existing@example.com")
                .password("encodedPassword")
                .role(UserRole.USER)
                .build();

        when(userService.loginAndAuthenticate(any(LoginRequest.class)))
                .thenReturn("mocked-jwt-token");

        when(userService.getUserByEmail(user.getEmail())).thenReturn(user);

        MockHttpServletRequestBuilder request = post("/api/v1/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsBytes(dto));

        // When & Then
        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("mocked-jwt-token"))
                .andExpect(jsonPath("$.user.email").value("existing@example.com"));

        verify(userService).loginAndAuthenticate(any(LoginRequest.class));
        verify(userService).getUserByEmail(user.getEmail());
    }

    @Test
    void shouldReturnNotFound_WhenUserDoesNotExist() throws Exception {

        LoginRequest dto = LoginRequest.builder()
                .email("missing@example.com")
                .password("password123")
                .build();

        doThrow(new ResourceNotFoundException("Email or password are incorrect."))
                .when(userService).loginAndAuthenticate(any(LoginRequest.class));

        MockHttpServletRequestBuilder request = post("/api/v1/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsBytes(dto));

        mockMvc.perform(request)
                .andExpect(status().isNotFound());

        verify(userService).loginAndAuthenticate(any(LoginRequest.class));
    }

    @Test
    void shouldReturnUnauthorized_WhenPasswordIsIncorrect() throws Exception {

        LoginRequest dto = LoginRequest.builder()
                .email("existing@example.com")
                .password("wrongPassword")
                .build();

        doThrow(new AuthenticationException("Email or password are incorrect."))
                .when(userService).loginAndAuthenticate(any(LoginRequest.class));

        MockHttpServletRequestBuilder request = post("/api/v1/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsBytes(dto));

        mockMvc.perform(request)
                .andExpect(status().isUnauthorized());

        verify(userService).loginAndAuthenticate(any(LoginRequest.class));
    }



}
