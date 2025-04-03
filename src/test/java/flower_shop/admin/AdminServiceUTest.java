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
    void shouldGetAllUsersSuccessfully() {
        User user1 = User.builder()
                .id(UUID.randomUUID())
                .firstName("Petar")
                .lastName("Gutov")
                .email("admin@gmail.com")
                .role(UserRole.USER)
                .build();

        User user2 = User.builder()
                .id(UUID.randomUUID())
                .firstName("Ivan")
                .lastName("Ivanov")
                .email("user2@gmail.com")
                .role(UserRole.ADMIN)
                .build();

        List<User> mockUsers = List.of(user1, user2);
        when(adminRepository.findAll()).thenReturn(mockUsers);

        List<User> users = adminService.getAllUsers();

        assertEquals(2, users.size());
        assertEquals("Petar", users.get(0).getFirstName());
        assertEquals("Ivan", users.get(1).getFirstName());
        verify(adminRepository).findAll();
    }

    @Test
    void shouldChangeUserRoleSuccessfully() {
        UUID userId = UUID.randomUUID();
        User user = User.builder()
                .id(userId)
                .firstName("Petar")
                .lastName("Gutov")
                .email("admin@gmail.com")
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
        verify(adminRepository, never()).save(any(User.class));
    }
}

