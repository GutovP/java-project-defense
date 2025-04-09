package flower_shop;

import com.fasterxml.jackson.databind.ObjectMapper;
import flower_shop.user.model.User;
import flower_shop.user.model.UserRole;
import flower_shop.user.repository.UserRepository;
import flower_shop.web.dto.RegisterRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@AutoConfigureMockMvc
@SpringBootTest
public class UserRegistrationITest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;


    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
    }

    @Test
    void shouldRegisterUserSuccessfully() throws Exception {
        RegisterRequest registerRequest = RegisterRequest.builder()
                .firstName("Ivan")
                .lastName("Ivanov")
                .email("admin@gmail.com")
                .password("securepassword")
                .build();

        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(registerRequest)))
                .andExpect(status().isCreated());

        assertTrue(userRepository.findByEmail(registerRequest.getEmail()).isPresent());
    }

    @Test
    void shouldNotRegisterUserWithExistingEmail() throws Exception {
        User existingUser = User.builder()
                .firstName("Ivan")
                .lastName("Ivanov")
                .email("admin@gmail.com")
                .password("securepassword")
                .role(UserRole.USER)
                .build();
        userRepository.save(existingUser);

        RegisterRequest request = RegisterRequest.builder()
                .firstName("Pesho")
                .lastName("Peshev")
                .email("admin@gmail.com")
                .password("securepassword")
                .build();

        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

}
