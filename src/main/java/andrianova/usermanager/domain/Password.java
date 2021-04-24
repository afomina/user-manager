package andrianova.usermanager.domain;

import com.datastax.oss.driver.shaded.guava.common.hash.Hashing;
import io.leangen.graphql.annotations.GraphQLQuery;
import org.springframework.lang.NonNull;

import java.util.Objects;

/**
 * Password representation
 */
public class Password {
    /**
     * Password SHA-256 hash
     */
    private final String hash;

    private Password(@NonNull String hash) {
        this.hash = Objects.requireNonNull(hash);
    }

    /**
     * Create {@link Password}
     *
     * @param password plain-text password
     * @return new Password entity with hashed password
     */
    public static Password of(byte[] password) {
        return new Password(Hashing.sha256().hashBytes(password).toString());
    }

    /**
     * Create {@link Password}
     *
     * @param hash password hash
     * @return new Password entity
     */
    public static Password ofHash(String hash) {
        return new Password(hash);
    }

    @GraphQLQuery(name = "password")
    public String asString() {
        return hash;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Password password = (Password) o;
        return hash.equals(password.hash);
    }

    @Override
    public int hashCode() {
        return Objects.hash(hash);
    }
}
