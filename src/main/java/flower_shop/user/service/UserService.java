package flower_shop.user.service;

import flower_shop.exception.AuthenticationException;
import flower_shop.exception.ResourceNotFoundException;
import flower_shop.exception.UserAlreadyExistException;
import flower_shop.security.AuthenticationMetadata;
import flower_shop.security.JWTService;
import flower_shop.user.model.User;
import flower_shop.user.model.UserRole;
import flower_shop.user.repository.UserRepository;
import flower_shop.web.dto.LoginRequest;
import flower_shop.web.dto.ProfileEditRequest;
import flower_shop.web.dto.RegisterRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.Optional;

@Service
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JWTService jwtService;

    @Autowired
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, AuthenticationManager authenticationManager, JWTService jwtService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
    }

    public User register(RegisterRequest registerRequest) {

        Optional<User> optionalUser = userRepository.findByEmail(registerRequest.getEmail());

        if (optionalUser.isPresent()) {
            throw new UserAlreadyExistException("User already exists!");
        }

        User user = User.builder()
                .firstName(registerRequest.getFirstName())
                .lastName(registerRequest.getLastName())
                .email(registerRequest.getEmail())
                .password(passwordEncoder.encode(registerRequest.getPassword()))
                .role(UserRole.USER)
                .build();

        return userRepository.save(user);
    }

    public String loginAndAuthenticate(LoginRequest loginRequest) {

        Optional<User> optionalUser = userRepository.findByEmail(loginRequest.getEmail());
        if (optionalUser.isEmpty()) {
            throw new AuthenticationException("Email or password are incorrect.");
        }

        User user = optionalUser.get();

        if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
            throw new AuthenticationException("Email or password are incorrect.");
        }

        Authentication authentication = authenticationManager
                .authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword()));

        if (authentication.isAuthenticated()) {
            return jwtService.generateToken(loginRequest.getEmail(), user.getRole());
        }

        throw new AuthenticationException("Authentication failed.");
    }

    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));
    }

    public User updateUserProfile(String email, ProfileEditRequest profileEditRequest) {
        User user = getUserByEmail(email);

        if (profileEditRequest.getFirstName() != null) {
            user.setFirstName(profileEditRequest.getFirstName());
        }
        if (profileEditRequest.getLastName() != null) {
            user.setLastName(profileEditRequest.getLastName());
        }
        if (profileEditRequest.getEmail() != null) {
            user.setEmail(profileEditRequest.getEmail());
        }

        return userRepository.save(user);
    }

    public User changeUserPassword(String email, String currentPassword, String newPassword) {
        User user = getUserByEmail(email);

        if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
            throw new AuthenticationException("Current password is incorrect");
        }

        if (newPassword != null && !newPassword.isBlank()) {
            user.setPassword(passwordEncoder.encode(newPassword));

        } else {
            throw new AuthenticationException("New password cannot be empty");
        }

        return userRepository.save(user);
    }

    @Override
    public UserDetails loadUserByUsername(String email) {

        String normalizedEmail = email.toLowerCase();

        User user = getUserByEmail(normalizedEmail);

        return new AuthenticationMetadata(user.getId(), normalizedEmail, user.getPassword(), user.getRole());
    }

}
