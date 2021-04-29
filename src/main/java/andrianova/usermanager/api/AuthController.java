package andrianova.usermanager.api;

import andrianova.usermanager.config.SecurityProperties;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

/**
 * Authorization api
 */
@RestController
public class AuthController {

    @Autowired
    private SecurityProperties securityProperties;

    @Autowired
    private AuthenticationManager authenticationManager;

    /**
     * Login with email and password
     *
     * @param request login request
     * @return response with Authorization header set or Unauthorized error
     */
    @PostMapping("login")
    public ResponseEntity login(@RequestBody @Valid LoginRequest request) {
        try {
            Authentication authenticate = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));

            UserDetails user = (UserDetails) authenticate.getPrincipal();

            return ResponseEntity.ok().header(HttpHeaders.AUTHORIZATION,
                    JWT.create().withSubject(user.getUsername())
                            .sign(Algorithm.HMAC512(securityProperties.getSigningKey()))).build();
        } catch (BadCredentialsException ex) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }
}
