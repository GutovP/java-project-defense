package flower_shop.user.service;

import flower_shop.user.model.User;
import flower_shop.user.repository.UserRepository;
import flower_shop.web.dto.LoginRequest;
import flower_shop.web.dto.ProfileEditRequest;
import flower_shop.web.dto.RegisterRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public User register(RegisterRequest registerRequest) {

        Optional<User> optionalUser = userRepository.findByEmail(registerRequest.getEmail());

        if (optionalUser.isPresent()) {
            throw new RuntimeException("User already exists!");
        }

        User user = User.builder()
                .firstName(registerRequest.getFirstName())
                .lastName(registerRequest.getLastName())
                .email(registerRequest.getEmail())
                .password(passwordEncoder.encode(registerRequest.getPassword()))
                .build();

        return userRepository.save(user);
    }

    public User login(LoginRequest loginRequest) {

        Optional<User> optionalUser = userRepository.findByEmail(loginRequest.getEmail());
        if (optionalUser.isEmpty()) {
            throw new RuntimeException("Username or password are incorrect.");
        }

        User user = optionalUser.get();

        if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
            throw new RuntimeException("Username or password are incorrect.");
        }

        return user;
    }

    public User getById(UUID userId) {

        return userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User with id [%s] does not exist.".formatted(userId)));
    }

    public User editProfile(UUID userId, ProfileEditRequest profileEditRequest) {

       User user = getById(userId);
       user.setFirstName(profileEditRequest.getFirstName());
       user.setLastName(profileEditRequest.getLastName());
       user.setPassword(passwordEncoder.encode(profileEditRequest.getPassword()));

       return userRepository.save(user);
    }
}
