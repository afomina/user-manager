package andrianova.usermanager.api;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * Login response
 */
public class LoginResponse {
    /**
     * Jwt token
     */
    private final String token;
    /**
     * User authorities
     */
    private final List<String> authorities;

    @JsonCreator
    public LoginResponse(@JsonProperty("token") String token,
                         @JsonProperty("authorities") List<String> authorities) {
        this.token = token;
        this.authorities = authorities;
    }

    @JsonProperty("token")
    public String getToken() {
        return token;
    }

    @JsonProperty("authorities")
    public List<String> getAuthorities() {
        return authorities;
    }
}
