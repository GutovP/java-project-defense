package flower_shop.user.service;

import flower_shop.exception.AuthenticationException;
import flower_shop.exception.UserNotFoundException;
import flower_shop.security.JWTService;
import flower_shop.user.model.User;
import flower_shop.user.repository.UserRepository;
import flower_shop.web.dto.LoginRequest;
import flower_shop.web.dto.ProfileEditRequest;
import flower_shop.web.dto.RegisterRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {

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
            throw new AuthenticationException("User already exists!");
        }

        User user = User.builder()
                .firstName(registerRequest.getFirstName())
                .lastName(registerRequest.getLastName())
                .email(registerRequest.getEmail())
                .password(passwordEncoder.encode(registerRequest.getPassword()))
                .build();

        return userRepository.save(user);
    }

    public String loginAndAuthenticate(LoginRequest loginRequest) {

        Optional<User> optionalUser = userRepository.findByEmail(loginRequest.getEmail());
        if (optionalUser.isEmpty()) {
            throw new UserNotFoundException("Username or password are incorrect.");
        }

        User user = optionalUser.get();

        if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
            throw new AuthenticationException("Username or password are incorrect.");
        }

        Authentication authentication = authenticationManager
                .authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword()));

        if (authentication.isAuthenticated()) {
            return jwtService.generateToken(loginRequest.getEmail());
        }

        throw new AuthenticationException("Authentication failed.");
    }

    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found with email: " + email));
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
        if (profileEditRequest.getPassword() != null) {
            user.setPassword(passwordEncoder.encode(profileEditRequest.getPassword()));
        }

        return userRepository.save(user);
    }
}
