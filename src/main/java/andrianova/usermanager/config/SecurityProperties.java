package andrianova.usermanager.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

/**
 * Security properties
 */
@Configuration
@PropertySource("classpath:security.properties")
public class SecurityProperties {
    /**
     * Secret key for signing JWT token
     */
    @Value("${security.signing-key}")
    private String signingKey;

    public String getSigningKey() {
        return signingKey;
    }

    public void setSigningKey(String signingKey) {
        this.signingKey = signingKey;
    }
}
