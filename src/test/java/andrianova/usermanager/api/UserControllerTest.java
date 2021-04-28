package andrianova.usermanager.api;

import andrianova.usermanager.Application;
import andrianova.usermanager.domain.Password;
import andrianova.usermanager.domain.Role;
import andrianova.usermanager.domain.User;
import andrianova.usermanager.domain.UserDao;
import com.datastax.oss.driver.shaded.guava.common.hash.Hashing;
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

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Test for {@link UserController}
 */
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK,
        classes = Application.class)
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private UserDao userDao;

    @BeforeEach
    public void clear() {
        userDao.getUsers().stream().map(User::getId)
                .forEach(userDao::delete);
    }

    @Test
    public void should_getUsers() throws Exception {
        userDao.create(User.builder()
                .withLastName("lastName")
                .withFirstName("firstName")
                .withEmail("email@test.com")
                .withPassword(Password.of("123456".getBytes(StandardCharsets.UTF_8)))
                .withRole(Role.USER)
                .build());

        mockMvc.perform(get("/user"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.users", hasSize(1)))
                .andExpect(jsonPath("$.data.users[0].id", notNullValue()))
                .andExpect(jsonPath("$.data.users[0].lastName", is("lastName")))
                .andExpect(jsonPath("$.data.users[0].firstName", is("firstName")))
                .andExpect(jsonPath("$.data.users[0].email", is("email@test.com")))
                .andExpect(jsonPath("$.data.users[0].role", is("user")));
    }

    @Test
    public void should_getUser() throws Exception {
        userDao.create(User.builder()
                .withLastName("lastName")
                .withFirstName("firstName")
                .withEmail("email@test.com")
                .withPassword(Password.of("123456".getBytes(StandardCharsets.UTF_8)))
                .withRole(Role.USER)
                .withAvatar(getAvatar())
                .build());
        Optional<User> user = userDao.getUsers().stream().findFirst();
        assertThat(user.isPresent(), is(true));

        mockMvc.perform(get("/user/" + user.get().getId()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.user.lastName", is("lastName")))
                .andExpect(jsonPath("$.data.user.firstName", is("firstName")))
                .andExpect(jsonPath("$.data.user.email", is("email@test.com")))
                .andExpect(jsonPath("$.data.user.password.hash",
                        is("8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92")))
                .andExpect(jsonPath("$.data.user.role", is("user")))
                .andExpect(jsonPath("$.data.user.avatar", is(Base64.getEncoder().encodeToString(getAvatar()))));
    }

    @Test
    public void should_createUser() throws Exception {
        mockMvc.perform(post("/user")
                .content("{\"email\": \"email@email.com\", " +
                        "\"password\": \"" + Base64.getEncoder().encodeToString("password".getBytes(StandardCharsets.UTF_8)) + "\", " +
                        "\"role\": \"user\", " +
                        "\"firstName\": \"firstName\", " +
                        "\"lastName\": \"lastName\", " +
                        "\"avatar\": \"" + Base64.getEncoder().encodeToString(getAvatar()) + "\"" +
                        "}")
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());

        List<User> users = userDao.getUsers();
        Optional<User> userOpt = users.stream()
                .filter(user1 -> user1.getEmail().equals("email@email.com"))
                .findFirst();
        assertThat(userOpt.isPresent(), is(true));

        User user = userDao.findById(userOpt.get().getId()).get();
        assertThat(user.getPassword().asString(),
                is(Hashing.sha256().hashString("password", StandardCharsets.UTF_8).toString()));
        assertThat(user.getRole(), is(Role.USER));
        assertThat(user.getId(), notNullValue());
        assertThat(user.getLastName(), is("lastName"));
        assertThat(user.getFirstName(), is("firstName"));
        assertThat(user.getAvatar(), is(getAvatar()));
    }

    @Test
    public void should_validateEmail_when_createUser() throws Exception {
        mockMvc.perform(post("/user")
                .content("{\"email\": \"email\", " +
                        "\"password\": \"password\", " +
                        "\"role\": \"user\", " +
                        "\"firstName\": \"firstName\", " +
                        "\"lastName\": \"lastName\" " +
                        "}")
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    public void should_validateRole_when_createUser() throws Exception {
        mockMvc.perform(post("/user")
                .content("{\"email\": \"email@mail.com\", " +
                        "\"password\": \"password\", " +
                        "\"role\": \"fakeRole\", " +
                        "}")
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    public void should_validateEmptyEmail_when_createUser() throws Exception {
        mockMvc.perform(post("/user")
                .content("{\"password\": \"password\", " +
                        "\"role\": \"user\", " +
                        "}")
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    public void should_validate_existingEmail_when_createUser() throws Exception {
        userDao.create(User.builder()
                .withLastName("lastName")
                .withFirstName("firstName")
                .withEmail("email@test.com")
                .withPassword(Password.of("123456".getBytes(StandardCharsets.UTF_8)))
                .withRole(Role.USER)
                .withAvatar(getAvatar())
                .build());

        mockMvc.perform(post("/user")
                .content("{\"email\": \"email@test.com\", " +
                        "\"password\": \"password\", " +
                        "\"role\": \"user\", " +
                        "}")
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    public void should_updateUser() throws Exception {
        userDao.create(User.builder()
                .withLastName("lastName")
                .withFirstName("firstName")
                .withEmail("user@test.com")
                .withPassword(Password.of("123456".getBytes(StandardCharsets.UTF_8)))
                .withRole(Role.USER)
                .build());
        List<User> users = userDao.getUsers();
        Optional<User> user = users.stream()
                .filter(user1 -> user1.getEmail().equals("user@test.com"))
                .findFirst();
        assertThat(user.isPresent(), is(true));

        mockMvc.perform(put("/user/" + user.get().getId().toString())
                .content("{\"lastName\": \"Smith\", " +
                        "\"firstName\": \"John\", " +
                        "\"email\": \"smith@test.com\", " +
                        "\"password\": \"" + Base64.getEncoder().encodeToString("123456".getBytes(StandardCharsets.UTF_8)) + "\", " +
                        "\"role\": \"user\"}")
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());

        user = userDao.findById(user.get().getId());
        assertThat(user.isPresent(), is(true));
        assertThat(user.get().getFirstName(), is("John"));
        assertThat(user.get().getLastName(), is("Smith"));
        assertThat(user.get().getRole(), is(Role.USER));
        assertThat(user.get().getEmail(), is("smith@test.com"));
        assertThat(user.get().getPassword().asString(),
                is(Hashing.sha256().hashString("123456", StandardCharsets.UTF_8).toString()));
    }

    @Test
    public void should_validateEmail_when_updateUser() throws Exception {
        userDao.create(User.builder()
                .withLastName("lastName")
                .withFirstName("firstName")
                .withEmail("user@test.com")
                .withPassword(Password.of("123456".getBytes(StandardCharsets.UTF_8)))
                .withRole(Role.USER)
                .build());
        List<User> users = userDao.getUsers();
        Optional<User> user = users.stream()
                .filter(user1 -> user1.getEmail().equals("user@test.com"))
                .findFirst();
        assertThat(user.isPresent(), is(true));

        mockMvc.perform(put("/user/" + user.get().getId().toString())
                .content("{\"lastName\": \"Smith\", " +
                        "\"firstName\": \"John\", " +
                        "\"email\": \"smith\", " +
                        "\"password\": \"123456\", " +
                        "\"role\": \"user\"}")
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    public void should_validateExistingEmail_when_updateUser() throws Exception {
        userDao.create(User.builder()
                .withEmail("user@test.com")
                .withPassword(Password.of("123456".getBytes(StandardCharsets.UTF_8)))
                .withRole(Role.USER)
                .build());
        List<User> users = userDao.getUsers();
        Optional<User> user = users.stream()
                .filter(user1 -> user1.getEmail().equals("user@test.com"))
                .findFirst();
        assertThat(user.isPresent(), is(true));

        userDao.create(User.builder()
                .withEmail("smith@test.com")
                .withPassword(Password.of("123456".getBytes(StandardCharsets.UTF_8)))
                .withRole(Role.USER)
                .build());

        mockMvc.perform(put("/user/" + user.get().getId().toString())
                .content("{\"lastName\": \"Smith\", " +
                        "\"firstName\": \"John\", " +
                        "\"email\": \"smith@test.com\", " +
                        "\"password\": \"123456\", " +
                        "\"role\": \"user\"}")
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    public void should_validateUserNotExists_when_updateUser() throws Exception {
        mockMvc.perform(put("/user/1234")
                .content("{\"lastName\": \"Smith\", " +
                        "\"firstName\": \"John\", " +
                        "\"email\": \"smith@test.com\", " +
                        "\"password\": \"123456\", " +
                        "\"role\": \"user\"}")
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    public void should_validateUserNotExists_when_deleteUser() throws Exception {
        mockMvc.perform(delete("/user/1234")
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    public void should_deleteUser() throws Exception {
        userDao.create(User.builder()
                .withEmail("user@test.com")
                .withPassword(Password.of("123456".getBytes(StandardCharsets.UTF_8)))
                .withRole(Role.USER)
                .build());
        List<User> users = userDao.getUsers();
        Optional<User> user = users.stream()
                .filter(user1 -> user1.getEmail().equals("user@test.com"))
                .findFirst();
        assertThat(user.isPresent(), is(true));

        mockMvc.perform(delete("/user/" + user.get().getId())
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());
        assertThat(userDao.findById(user.get().getId()).isPresent(), is(false));
    }

    private byte[] getAvatar() throws IOException {
        return getClass().getResourceAsStream("avatar.png").readAllBytes();
    }
}
