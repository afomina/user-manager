package andrianova.usermanager.auth;

import andrianova.usermanager.config.SecurityProperties;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Service for jwt authorization
 */
@Service
public class AuthService {

    @Autowired
    private SecurityProperties securityProperties;
    @Autowired
    private UserDetailsService userDetailsService;

    /**
     * Create authorization token
     *
     * @param user user details
     * @return jwt token
     */
    public String createAuthToken(UserDetails user) {
        return JWT.create().withSubject(user.getUsername())
                .sign(Algorithm.HMAC512(securityProperties.getSigningKey()));
    }

    /**
     * Find user details by jwt token
     *
     * @param token jwt token
     * @return user details
     */
    public Optional<UserDetails> findUser(String token) {
        String username = JWT.require(Algorithm.HMAC512(securityProperties.getSigningKey()))
                .build()
                .verify(token)
                .getSubject();

        return Optional.ofNullable(userDetailsService.loadUserByUsername(username));
    }

}
