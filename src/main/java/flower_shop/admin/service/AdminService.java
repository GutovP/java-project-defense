package flower_shop.admin.service;

import flower_shop.admin.repository.AdminRepository;
import flower_shop.exception.UserNotFoundException;
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

    public List<User> getAllUsers() {

        return adminRepository.findAll();
    }

    public User changeUserRole(UUID userId, UserRole newRole) {
        User user = adminRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        user.setRole(newRole);
        return adminRepository.save(user);
    }
}
