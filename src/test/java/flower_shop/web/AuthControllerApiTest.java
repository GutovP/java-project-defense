package flower_shop.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import flower_shop.exception.AuthenticationException;
import flower_shop.exception.UserAlreadyExistException;
import flower_shop.security.JWTService;
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
import static flower_shop.TestBuilder.aRandomUser;
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
    void postRequestToRegisterEndpoint_shouldRegisterUserSuccessfully() throws Exception {

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

        verify(userService, times(1)).register(any());

    }

    @Test
    void postRequestToRegisterEndpoint_shouldReturnErrorWhenUserAlreadyExists() throws Exception {

        //Given
        RegisterRequest dto = RegisterRequest.builder()
                .firstName("existingFirst")
                .lastName("existingLast")
                .email("existing@example.com")
                .password("existingPassword")
                .build();

        when(userService.register(dto)).thenThrow(new UserAlreadyExistException("User already exists!"));

        MockHttpServletRequestBuilder request = post("/api/v1/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsBytes(dto));

        // When & Then
        mockMvc.perform(request)
                .andExpect(status().isBadRequest());

        verify(userService, times(1)).register(any());
    }

    @Test
    void postRequestToLoginEndpoint_shouldLoginSuccessfully() throws Exception {

        // Given
        LoginRequest dto = LoginRequest.builder()
                .email("existing@example.com")
                .password("password123")
                .build();

        when(userService.loginAndAuthenticate(any())).thenReturn("mocked-jwt-token");
        when(userService.getUserByEmail(any())).thenReturn(aRandomUser());

        MockHttpServletRequestBuilder request = post("/api/v1/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsBytes(dto));

        // When & Then
        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("mocked-jwt-token"))
                .andExpect(jsonPath("$.user.email").value(aRandomUser().getEmail()));

        verify(userService, times(1)).loginAndAuthenticate(any());
        verify(userService).getUserByEmail(aRandomUser().getEmail());
    }

    @Test
    void postRequestToLoginEndpoint_shouldReturnNotFoundWhenUserDoesNotExist() throws Exception {

        LoginRequest dto = LoginRequest.builder()
                .email("missing@example.com")
                .password("password123")
                .build();

        when(userService.loginAndAuthenticate(any())).thenThrow(new AuthenticationException("Email or password are incorrect."));

        MockHttpServletRequestBuilder request = post("/api/v1/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsBytes(dto));

        mockMvc.perform(request)
                .andExpect(status().isUnauthorized());

        verify(userService, times(1)).loginAndAuthenticate(any());
    }

    @Test
    void postRequestToLoginEndpoint_shouldReturnUnauthorizedWhenPasswordIsIncorrect() throws Exception {

        LoginRequest dto = LoginRequest.builder()
                .email("existing@example.com")
                .password("wrongPassword")
                .build();

        when(userService.loginAndAuthenticate(any())).thenThrow(new AuthenticationException("Email or password are incorrect."));

        MockHttpServletRequestBuilder request = post("/api/v1/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsBytes(dto));

        mockMvc.perform(request)
                .andExpect(status().isUnauthorized());

        verify(userService, times(1)).loginAndAuthenticate(any());
    }


}
