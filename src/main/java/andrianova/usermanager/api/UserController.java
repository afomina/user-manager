package andrianova.usermanager.api;

import andrianova.usermanager.domain.Password;
import andrianova.usermanager.domain.Role;
import andrianova.usermanager.domain.User;
import andrianova.usermanager.service.UserService;
import graphql.ExecutionResult;
import graphql.GraphQL;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Base64;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

/**
 * Rest controller to manage {@link User}
 */
@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;
    @Autowired
    private GraphQL graphQl;

    /**
     * Lists all users
     */
    @GetMapping
    public Map<String, Object> list() {
        ExecutionResult result = graphQl.execute(
                "{ users {" +
                        " id, email, firstName, lastName, role }}");
        return result.toSpecification();
    }

    /**
     * Get user by id
     */
    @GetMapping("/{id}")
    public Map<String, Object> getById(@PathVariable("id") String id) {
        ExecutionResult result = graphQl.execute(
                "{ user (id: \"" + id + "\") {" +
                        " email, password { hash }, firstName, lastName, avatar, role }}");
        return result.toSpecification();
    }

    /**
     * Creates new user
     *
     * @param request user create request
     * @return HttpStatus.BAD_REQUEST if user already exists or request is not valid
     */
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity create(@RequestBody @Valid UserRequest request) {
        if (userService.create(toUser(request))) {
            return ResponseEntity.ok().build();
        }

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    }

    /**
     * Updates a user
     *
     * @param id user id
     * @param request new user info
     * @return HttpStatus.BAD_REQUEST if user not exists or request is not valid
     */
    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity update(@PathVariable("id") UUID id,
                                 @RequestBody @Valid UserRequest request) {
        if (userService.updateUser(id, toUser(request))) {
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    }

    /**
     * Deletes user
     *
     * @param id user id
     * @return HttpStatus.BAD_REQUEST if user not exists
     */
    @DeleteMapping("/{id}")
    public ResponseEntity delete(@PathVariable("id") UUID id) {
        if (userService.deleteUser(id)) {
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    }

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    private void handleException() {

    }

    private User toUser(UserRequest request) {
        return User.builder()
                .withEmail(request.getEmail())
                .withFirstName(request.getFirstName())
                .withLastName(request.getLastName())
                .withAvatar(Optional.ofNullable(request.getAvatar())
                        .map(Base64.getDecoder()::decode).orElse(null))
                .withPassword(Password.of(Base64.getDecoder().decode(request.getPassword())))
                .withRole(Role.findByName(request.getRole()))
                .build();
    }
}
