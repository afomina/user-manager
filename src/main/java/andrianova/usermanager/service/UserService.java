package andrianova.usermanager.service;

import andrianova.usermanager.api.UserRequest;
import andrianova.usermanager.domain.Password;
import andrianova.usermanager.domain.Role;
import andrianova.usermanager.domain.User;
import andrianova.usermanager.domain.UserDao;
import io.leangen.graphql.annotations.GraphQLArgument;
import io.leangen.graphql.annotations.GraphQLMutation;
import io.leangen.graphql.annotations.GraphQLQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Base64;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Service to work with {@link User}
 */
@Service
public class UserService {

    @Autowired
    private UserDao userDao;

    /**
     * List users
     *
     * @return users
     */
    @GraphQLQuery(name = "users")
    public List<User> getUsers() {
        return userDao.getUsers();
    }

    /**
     * Get user by id
     *
     * @param userId user id
     * @return user
     */
    @GraphQLQuery(name = "user")
    public Optional<User> getUser(@GraphQLArgument(name = "id") UUID userId) {
        return userDao.findById(userId);
    }

    /**
     * Creates user if it didn't exist
     *
     * @param user user to create
     * @return true if user created
     */
    @GraphQLMutation(name = "createUser")
    public Optional<User> create(@GraphQLArgument(name = "user") UserRequest user) {
        if (userDao.exists(user.getEmail())) {
            return Optional.empty();
        }

        Optional<UUID> userId = userDao.create(toUser(user));
        return userId.flatMap(this::getUser);
    }

    /**
     * Update user
     *
     * @param userId user id
     * @param user updated user info
     * @return true if user was updated
     */
    @GraphQLMutation(name = "updateUser")
    public boolean updateUser(@GraphQLArgument(name = "id") UUID userId,
                              @GraphQLArgument(name = "user") UserRequest user) {
        return userDao.update(userId, toUser(user));
    }

    private User toUser(UserRequest request) {
        return User.builder()
                .withEmail(request.getEmail())
                .withFirstName(request.getFirstName())
                .withLastName(request.getLastName())
                .withAvatar(Optional.ofNullable(request.getAvatar())
                        .map(Base64.getDecoder()::decode).orElse(null))
                .withPassword(Password.of(Base64.getDecoder().decode(request.getPassword())))
                .withRole(Role.findByName(request.getRole()))
                .build();
    }

    /**
     * Delete user
     *
     * @param userId user id
     * @return true if user was deleted
     */
    @GraphQLMutation(name = "deleteUser")
    public boolean deleteUser(@GraphQLArgument(name = "id") UUID userId) {
        return userDao.delete(userId);
    }
}
