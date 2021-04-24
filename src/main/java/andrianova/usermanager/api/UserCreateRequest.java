package andrianova.usermanager.api;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.lang.NonNull;

/**
 * Request to create a new user
 */
public class UserCreateRequest {
    /**
     * Email
     */
    @NonNull
    private final String email;
    /**
     * Password
     */
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
    private final byte[] avatar;
    /**
     * Role
     */
    private final String role;

    @JsonCreator
    public UserCreateRequest(@JsonProperty("email") @NonNull String email,
                             @JsonProperty("password") @NonNull String password,
                             @JsonProperty("firstName") String firstName,
                             @JsonProperty("lastName") String lastName,
                             @JsonProperty("avatar") byte[] avatar,
                             @JsonProperty("role") String role) {
        this.email = email;
        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
        this.avatar = avatar;
        this.role = role;
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
    public byte[] getAvatar() {
        return avatar;
    }

    @JsonProperty("role")
    public String getRole() {
        return role;
    }
}
