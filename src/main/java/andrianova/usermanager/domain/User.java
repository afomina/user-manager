package andrianova.usermanager.domain;

import io.leangen.graphql.annotations.GraphQLQuery;

import java.util.UUID;

/**
 * User entity
 */
public class User {
    /**
     * User id
     */
    private final UUID id;
    /**
     * Email
     */
    private final String email;
    /**
     * Password
     */
    private final Password password;
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
    private final Role role;

    private User(UUID id,
                 String email,
                 Password password,
                 String firstName,
                 String lastName,
                 byte[] avatar,
                 Role role) {
        this.id = id;
        this.email = email;
        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
        this.avatar = avatar;
        this.role = role;
    }

    @GraphQLQuery(name = "id")
    public UUID getId() {
        return id;
    }

    @GraphQLQuery(name = "email")
    public String getEmail() {
        return email;
    }

    @GraphQLQuery(name = "password")
    public Password getPassword() {
        return password;
    }

    @GraphQLQuery(name = "firstName")
    public String getFirstName() {
        return firstName;
    }

    @GraphQLQuery(name = "lastName")
    public String getLastName() {
        return lastName;
    }

    @GraphQLQuery(name = "avatar")
    public byte[] getAvatar() {
        return avatar;
    }

    @GraphQLQuery(name = "role")
    public Role getRole() {
        return role;
    }
    
    public static Builder builder() {
        return new Builder();
    }
    
    public static class Builder {
        private Builder() {};

        private UUID id;
        private String email;
        private Password password;
        private String firstName;
        private String lastName;
        private byte[] avatar;
        private Role role;

        public Builder withId(UUID id) {
            this.id = id;
            return this;
        }

        public Builder withEmail(String email) {
            this.email = email;
            return this;
        }

        public Builder withPassword(Password password) {
            this.password = password;
            return this;
        }

        public Builder withFirstName(String firstName) {
            this.firstName = firstName;
            return this;
        }

        public Builder withLastName(String lastName) {
            this.lastName = lastName;
            return this;
        }

         public Builder withAvatar(byte[] avatar) {
            this.avatar = avatar;
            return this;
         }

         public Builder withRole(Role role) {
            this.role = role;
            return this;
         }

        public User build() {
            return new User(id, email, password, firstName, lastName, avatar, role);
        }
    }
}
