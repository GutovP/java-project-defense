package app.user.service;

import app.event.UserRegisteredEventProducer;
import app.event.payload.UserRegisteredEvent;
import app.exception.AuthenticationException;
import app.exception.ResourceNotFoundException;
import app.exception.UserAlreadyExistException;
import app.security.AuthenticationMetadata;
import app.security.JWTService;
import app.user.model.User;
import app.user.model.UserRole;
import app.user.repository.UserRepository;
import app.web.dto.LoginRequest;
import app.web.dto.ProfileEditRequest;
import app.web.dto.RegisterRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JWTService jwtService;
    private final UserRegisteredEventProducer userRegisteredEventProducer;

    @Autowired
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, AuthenticationManager authenticationManager, JWTService jwtService, UserRegisteredEventProducer userRegisteredEventProducer) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.userRegisteredEventProducer = userRegisteredEventProducer;
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

        userRepository.save(user);

        UserRegisteredEvent userRegisteredEvent = UserRegisteredEvent.builder()
                .userId(user.getId())
                .createdOn(LocalDateTime.now())
                .build();

        userRegisteredEventProducer.sendEvent(userRegisteredEvent);

        return user;
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
