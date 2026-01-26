package flower_shop.web;


import flower_shop.admin.service.AdminService;
import flower_shop.exception.AuthorizationDeniedException;
import flower_shop.security.AuthenticationMetadata;
import flower_shop.security.JWTService;
import flower_shop.user.model.User;
import flower_shop.user.model.UserRole;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import java.util.List;
import java.util.UUID;

import static flower_shop.TestBuilder.aRandomUser;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AdminController.class)
public class AdminControllerApiTest {

    @MockitoBean
    private AdminService adminService;

    @MockitoBean
    private JWTService jwtService;

    @MockitoBean
    private UserDetailsService userDetailsService;

    @Autowired
    private MockMvc mockMvc;


    @Test
    void getRequestToUsersEndpoint_shouldReturnAllUsersWhenUserIsAdmin() throws Exception{

        User randomUser = aRandomUser();

        when(adminService.getAllUsers(UserRole.ADMIN)).thenReturn(List.of(randomUser));

        UUID userId = UUID.randomUUID();
        AuthenticationMetadata principal = new AuthenticationMetadata(userId,"email@test.com","123123", UserRole.ADMIN);

        MockHttpServletRequestBuilder request = get("/api/v1/admin/users")
                .with(user(principal));

        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(jsonPath("[0].id").isNotEmpty())
                .andExpect(jsonPath("[0].email").isNotEmpty())
                .andExpect(jsonPath("[0].role").isNotEmpty());

        verify(adminService, times(1)).getAllUsers(UserRole.ADMIN);
    }

    @Test
    void getRequestToUsersEndpoint_shouldReturnForbiddenWhenUserIsNotAdmin() throws Exception {

        UUID userId = UUID.randomUUID();
        AuthenticationMetadata principal = new AuthenticationMetadata(userId, "email@test.com", "123123", UserRole.USER);

        when(adminService.getAllUsers(UserRole.USER))
                .thenThrow(new AuthorizationDeniedException("You are not allowed to access this resource"));

        MockHttpServletRequestBuilder request = get("/api/v1/admin/users")
                .with(user(principal));

        mockMvc.perform(request)
                        .andExpect(status().isForbidden());
    }

    @Test
    void putRequestToUpdateUserRole_shouldReturnOkWhenAdminUpdatesRole() throws Exception {

        UUID userId = UUID.randomUUID();
        AuthenticationMetadata principal =
                new AuthenticationMetadata(userId, "admin@test.com", "123", UserRole.ADMIN);

        MockHttpServletRequestBuilder request = put("/api/v1/admin/{userId}/role", userId)
                .param("newRole", "USER")
                .with(user(principal))
                .with(csrf());

        mockMvc.perform(request)
                .andExpect(status().isOk());

        verify(adminService, times(1)).changeUserRole(userId, UserRole.USER);
    }

    @Test
    void putRequestToUpdateUserRole_shouldReturnForbiddenWhenUserIsNotAdmin() throws Exception {

        UUID userId = UUID.randomUUID();
        AuthenticationMetadata principal =
                new AuthenticationMetadata(userId, "user@test.com", "123", UserRole.USER);

        MockHttpServletRequestBuilder request = put("/api/v1/admin/{userId}/role", userId)
                .param("newRole", "ADMIN")
                .with(user(principal));

        mockMvc.perform(request)
                .andExpect(status().isForbidden());

        verify(adminService, never()).changeUserRole(any(), any());
    }

}
