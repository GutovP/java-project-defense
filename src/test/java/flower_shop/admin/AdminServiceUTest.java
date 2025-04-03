package flower_shop.admin;

import flower_shop.admin.repository.AdminRepository;
import flower_shop.admin.service.AdminService;
import flower_shop.exception.UserNotFoundException;
import flower_shop.user.model.User;
import flower_shop.user.model.UserRole;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AdminServiceUTest {

    @Mock
    private AdminRepository adminRepository;

    @InjectMocks
    private AdminService adminService;

    @Test
    void shouldReturnAllUsersSuccessfully() {
        List<User> users = List.of(
                User.builder()
                        .id(UUID.randomUUID())
                        .firstName("John")
                        .lastName("Doe")
                        .email("john@example.com")
                        .password("securepassword")
                        .role(UserRole.USER)
                        .build()
        );

        when(adminRepository.findAll()).thenReturn(users);

        List<User> result = adminService.getAllUsers();

        assertEquals(1, result.size());
        assertEquals("John", result.get(0).getFirstName());

        verify(adminRepository).findAll();
    }

    @Test
    void shouldChangeUserRoleSuccessfully() {
        UUID userId = UUID.randomUUID();
        User user = User.builder()
                .id(userId)
                .firstName("Jane")
                .lastName("Doe")
                .email("jane@example.com")
                .password("securepassword")
                .role(UserRole.USER)
                .build();

        when(adminRepository.findById(userId)).thenReturn(Optional.of(user));
        when(adminRepository.save(any(User.class))).thenReturn(user);

        User updatedUser = adminService.changeUserRole(userId, UserRole.ADMIN);

        assertEquals(UserRole.ADMIN, updatedUser.getRole());

        verify(adminRepository).findById(userId);
        verify(adminRepository).save(user);
    }

    @Test
    void shouldThrowExceptionWhenUserNotFound() {
        UUID userId = UUID.randomUUID();

        when(adminRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> adminService.changeUserRole(userId, UserRole.ADMIN));

        verify(adminRepository).findById(userId);
    }


}

