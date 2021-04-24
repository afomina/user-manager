package andrianova.usermanager.domain;

import io.leangen.graphql.annotations.GraphQLQuery;

import java.util.Objects;

/**
 *
 */
public class Password {
    /**
     *
     */
    private final String hash;

    private Password(String hash) {
        this.hash = hash;
    }

    /**
     *
     * @param hash
     * @return
     */
    public static Password of(String hash) {
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
