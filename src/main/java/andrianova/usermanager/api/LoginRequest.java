package andrianova.usermanager.api;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.lang.NonNull;

import javax.validation.constraints.Email;

/**
 * Login request
 */
public class LoginRequest {
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

    @JsonCreator
    public LoginRequest(@JsonProperty("email") @NonNull String email,
                        @JsonProperty("password") @NonNull String password) {
        this.email = email;
        this.password = password;
    }

    @JsonProperty("email") @NonNull
    public String getEmail() {
        return email;
    }

    @JsonProperty("password") @NonNull
    public String getPassword() {
        return password;
    }
}
