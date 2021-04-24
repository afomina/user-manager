package andrianova.usermanager.domain;

import java.util.Arrays;
import java.util.Optional;

/**
 * User roles
 */
public enum Role {
    USER(1, "user"),

    ADMIN(2, "admin");

    private final int code;
    private final String name;

    Role(int code, String name) {
        this.code = code;
        this.name = name;
    }

    public int getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public static Role findByName(String name) {
        return Arrays.stream(values())
                .filter(role -> name.equals(role.getName()))
                .findFirst()
                .orElseThrow();
    }

    public static Optional<Role> findByCode(int code) {
        return Arrays.stream(values())
                .filter(role -> code == role.getCode())
                .findFirst();
    }
}
