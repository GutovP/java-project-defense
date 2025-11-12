package flower_shop.admin.service;

import flower_shop.admin.repository.AdminRepository;
import flower_shop.exception.AuthorizationDeniedException;
import flower_shop.exception.ResourceNotFoundException;
import flower_shop.user.model.User;
import flower_shop.user.model.UserRole;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.UUID;

@Service
public class AdminService {

    private final AdminRepository adminRepository;

    @Autowired
    public AdminService(AdminRepository adminRepository) {
        this.adminRepository = adminRepository;
    }

    public List<User> getAllUsers(UserRole userRole) {

        if (userRole != UserRole.ADMIN) {
            throw new AuthorizationDeniedException("You are not allowed to access this resource");
        }

        return adminRepository.findAll();
    }

    public void changeUserRole(UUID userId, UserRole newRole) {
        User user = adminRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        user.setRole(newRole);
        adminRepository.save(user);
    }
}
