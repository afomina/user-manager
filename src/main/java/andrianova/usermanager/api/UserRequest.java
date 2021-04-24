package andrianova.usermanager.api;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.lang.NonNull;

import javax.validation.constraints.Email;

import static java.util.Objects.requireNonNull;

/**
 * Request to create or update user
 */
public class UserRequest {
    /**
     * Email
     */
    @NonNull
    @Email
    private final String email;
    /**
     * Password
     */
    @NonNull
    private final String password;
    /**
     * First name
     */
    private final String firstName;
    /**
     * Last name
     */
    private final String lastName;
    /**
     * Avatar
     */
    private final String avatar;
    /**
     * Role
     */
    @NonNull
    private final String role;

    @JsonCreator
    public UserRequest(@JsonProperty("email") @NonNull String email,
                       @JsonProperty("password") @NonNull String password,
                       @JsonProperty("firstName") String firstName,
                       @JsonProperty("lastName") String lastName,
                       @JsonProperty("avatar") String avatar,
                       @JsonProperty("role") @NonNull String role) {
        this.email = requireNonNull(email);
        this.password = requireNonNull(password);
        this.firstName = firstName;
        this.lastName = lastName;
        this.avatar = avatar;
        this.role = requireNonNull(role);
    }

    @NonNull
    @JsonProperty("email")
    public String getEmail() {
        return email;
    }

    @JsonProperty("password")
    public String getPassword() {
        return password;
    }

    @JsonProperty("firstName")
    public String getFirstName() {
        return firstName;
    }

    @JsonProperty("lastName")
    public String getLastName() {
        return lastName;
    }

    @JsonProperty("avatar")
    public String getAvatar() {
        return avatar;
    }

    @JsonProperty("role")
    public String getRole() {
        return role;
    }
}
