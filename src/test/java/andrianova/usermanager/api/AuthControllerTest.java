package andrianova.usermanager.api;

import andrianova.usermanager.Application;
import andrianova.usermanager.auth.AuthService;
import andrianova.usermanager.auth.UserDetailsImpl;
import andrianova.usermanager.domain.Password;
import andrianova.usermanager.domain.Role;
import andrianova.usermanager.domain.User;
import andrianova.usermanager.domain.UserDao;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.nio.charset.StandardCharsets;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Test for {@link AuthController}
 */
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK,
        classes = Application.class)
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private UserDao userDao;
    @Autowired
    private AuthService authService;

    @BeforeEach
    public void clear() {
        userDao.getUsers().stream().map(User::getId)
                .forEach(userDao::delete);
    }

    @Test
    public void should_login() throws Exception {
        User user = User.builder()
                .withLastName("lastName")
                .withFirstName("firstName")
                .withEmail("email@test.com")
                .withPassword(Password.of("123456".getBytes(StandardCharsets.UTF_8)))
                .withRole(Role.USER)
                .build();
        userDao.create(user);
        String auth = authService.createAuthToken(new UserDetailsImpl(user));

        mockMvc.perform(post("/login")
                .content("{\"email\": \"email@test.com\", " +
                        "\"password\": \"123456\" }")
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token", is(auth)))
                .andExpect(jsonPath("$.authorities", hasSize(1)))
                .andExpect(jsonPath("$.authorities[0]", is(user.getRole().getName())));
    }

    @Test
    public void should_denyLogin_when_userNotExists() throws Exception {
        mockMvc.perform(post("/login")
                .content("{\"email\": \"fake@test.com\", " +
                        "\"password\": \"wrongPass\" }")
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void should_denyLogin_when_wrongPassword() throws Exception {
        User user = User.builder()
                .withLastName("lastName")
                .withFirstName("firstName")
                .withEmail("email@test.com")
                .withPassword(Password.of("123456".getBytes(StandardCharsets.UTF_8)))
                .withRole(Role.USER)
                .build();
        userDao.create(user);

        mockMvc.perform(post("/login")
                .content("{\"email\": \"email@test.com\", " +
                        "\"password\": \"wrongPass\" }")
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }
}
