package andrianova.usermanager.api;

/**
 * Login response
 */
public class LoginResponse {
    /**
     * Jwt token
     */
    private final String token;

    public LoginResponse(String token) {
        this.token = token;
    }

    public String getToken() {
        return token;
    }
}
