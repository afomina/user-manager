package andrianova.usermanager.service;

import andrianova.usermanager.domain.User;
import andrianova.usermanager.domain.UserDao;
import io.leangen.graphql.annotations.GraphQLArgument;
import io.leangen.graphql.annotations.GraphQLQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
    public boolean create(User user) {
        if (userDao.exists(user.getEmail())) {
            return false;
        }

        userDao.create(user);
        return true;
    }

    /**
     * Update user
     *
     * @param userId user id
     * @param user updated user info
     * @return true if user was updated
     */
    public boolean updateUser(UUID userId, User user) {
        return userDao.update(userId, user);
    }

    /**
     * Delete user
     *
     * @param userId user id
     * @return true if user was deleted
     */
    public boolean deleteUser(UUID userId) {
        return userDao.delete(userId);
    }
}
