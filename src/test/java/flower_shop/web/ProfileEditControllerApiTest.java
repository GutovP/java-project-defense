package flower_shop.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import flower_shop.security.JWTService;
import flower_shop.user.model.User;
import flower_shop.user.model.UserRole;
import flower_shop.user.service.CustomUserDetailsService;
import flower_shop.user.service.UserService;
import flower_shop.web.dto.PasswordChangeRequest;
import flower_shop.web.dto.ProfileEditRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;

import static org.mockito.Mockito.*;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
class ProfileEditControllerApiTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private JWTService jwtService;

    @MockitoBean
    private CustomUserDetailsService customUserDetailsService;


    private void setAuthentication(String email) {
        UserDetails userDetails = new org.springframework.security.core.userdetails.User(
                email, "password", List.of(new SimpleGrantedAuthority("ROLE_USER"))
        );
        SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
        securityContext.setAuthentication(new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities()));
        SecurityContextHolder.setContext(securityContext);
    }

    @Test
    void shouldReturnProfileSuccessfully() throws Exception {
        User user = User.builder()
                .firstName("John")
                .lastName("Doe")
                .email("john@example.com")
                .role(UserRole.USER)
                .build();

        setAuthentication(user.getEmail()); // ✅ Manually inject authentication

        when(userService.getUserByEmail(user.getEmail())).thenReturn(user);

        mockMvc.perform(get("/api/v1/user/profile")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value(user.getEmail()))
                .andExpect(jsonPath("$.firstName").value(user.getFirstName()));

        verify(userService).getUserByEmail(user.getEmail());
    }

    @Test
    void shouldUpdateProfileSuccessfully() throws Exception {
        User user = User.builder()
                .firstName("John")
                .lastName("Doe")
                .email("john@example.com")
                .role(UserRole.USER)
                .build();

        ProfileEditRequest request = ProfileEditRequest.builder()
                .firstName("UpdatedFirstName")
                .lastName("UpdatedLastName")
                .build();

        User updatedUser = User.builder()
                .firstName("UpdatedFirstName")
                .lastName("UpdatedLastName")
                .email(user.getEmail())
                .role(UserRole.USER)
                .build();

        setAuthentication(user.getEmail()); // ✅ Manually inject authentication

        when(userService.updateUserProfile(user.getEmail(), request)).thenReturn(updatedUser);

        mockMvc.perform(put("/api/v1/user/profile")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value(updatedUser.getFirstName()))
                .andExpect(jsonPath("$.lastName").value(updatedUser.getLastName()));

        verify(userService).updateUserProfile(user.getEmail(), request);
    }

    @Test
    void shouldChangePasswordSuccessfully() throws Exception {
        User user = User.builder()
                .email("john@example.com")
                .role(UserRole.USER)
                .build();

        PasswordChangeRequest request = PasswordChangeRequest.builder()
                .currentPassword("oldPassword")
                .newPassword("newPassword")
                .build();

        setAuthentication(user.getEmail()); // ✅ Manually inject authentication

        mockMvc.perform(put("/api/v1/user/change-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Password changed successfully"));

        verify(userService).changeUserPassword(user.getEmail(), request.getCurrentPassword(), request.getNewPassword());
    }

    @Test
    void shouldValidateTokenSuccessfully() throws Exception {
        String token = "Bearer valid.token";
        String extractedEmail = "john@example.com";

        setAuthentication(extractedEmail); // ✅ Manually inject authentication

        when(jwtService.extractEmail("valid.token")).thenReturn(extractedEmail);
        when(customUserDetailsService.loadUserByUsername(extractedEmail))
                .thenReturn(new org.springframework.security.core.userdetails.User(
                        extractedEmail, "password", List.of(new SimpleGrantedAuthority("ROLE_USER"))
                ));
        when(jwtService.validateToken("valid.token", customUserDetailsService.loadUserByUsername(extractedEmail)))
                .thenReturn(true);

        mockMvc.perform(get("/api/v1/user/validate-token")
                        .header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string("Token is valid"));

        verify(jwtService).extractEmail("valid.token");
        verify(customUserDetailsService, times(2)).loadUserByUsername(extractedEmail);
        verify(jwtService).validateToken("valid.token", customUserDetailsService.loadUserByUsername(extractedEmail));
    }

}

