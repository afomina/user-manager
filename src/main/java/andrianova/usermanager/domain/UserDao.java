package andrianova.usermanager.domain;

import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.cql.Row;
import com.datastax.oss.driver.api.core.uuid.Uuids;
import com.datastax.oss.protocol.internal.util.Bytes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.nio.ByteBuffer;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Data access layer for {@link User}
 */
@Repository
public class UserDao {

    private static final String COLUMNS = "id, email, password, first_name, last_name, avatar, role";

    private static final Function<Row, User> ROW_MAPPER = row -> User.builder()
            .withId(row.getUuid("id"))
            .withEmail(row.getString("email"))
            .withPassword(Password.ofHash(row.getString("password")))
            .withFirstName(row.getString("first_name"))
            .withLastName(row.getString("last_name"))
            .withAvatar(Optional.ofNullable(row.getByteBuffer("avatar"))
                    .map(Bytes::getArray).orElse(null))
            .withRole(Role.findByCode(row.getInt("role")).orElse(null))
            .build();

    @Autowired
    private CqlSession cqlSession;

    /**
     * Get all users
     *
     * @return users
     */
    public List<User> getUsers() {
        return cqlSession.execute("select * from user")
                .map(ROW_MAPPER).all();
    }

    /**
     * Find user by id
     *
     * @param userId user id
     * @return user
     */
    public Optional<User> findById(UUID userId) {
        return Optional.ofNullable(
                cqlSession.execute("select * from user where id=?", userId)
                        .map(ROW_MAPPER).one());
    }

    /**
     * Stores user in the database
     *
     * @param user user to store
     */
    public void create(User user) {
        UUID uuid = Uuids.timeBased();
        cqlSession.execute("begin batch " +
                        "insert into user (" +
                        COLUMNS +
                        ") values (?, ?, ?, ?, ?, ?, ?); " +
                        "insert into user_email (id, email) " +
                        "values (?, ?); " +
                        "apply batch;",
                uuid, user.getEmail(), user.getPassword().asString(),
                user.getFirstName(), user.getLastName(),
                Optional.ofNullable(user.getAvatar()).map(ByteBuffer::wrap).orElse(null),
                user.getRole().getCode(),
                uuid, user.getEmail());
    }

    /**
     * Checks if user with {@code email} exists in the database
     *
     * @param email user email
     * @return true if user exists in the database
     */
    public boolean exists(String email) {
        return Optional.ofNullable(
                cqlSession.execute("select * from user_email where email=?", email)
                        .one())
                .isPresent();
    }

    /**
     * Updates user information in the database
     *
     * @param userId
     * @param user   user to update
     */
    @Transactional
    public boolean update(UUID userId, User user) {
        Optional<User> oldUserOpt = findById(userId);
        if (oldUserOpt.isEmpty()) {
            return false;
        }
        User oldUser = oldUserOpt.get();
        Map<String, Object> updatedValues = getUpdatedColumns(user, oldUser);

        if (!Objects.equals(oldUser.getEmail(), user.getEmail())) {
            if (!updateEmail(userId, oldUser.getEmail(), user.getEmail())) {
                return false;
            }
        } else if (updatedValues.isEmpty()) {
            return false;
        }

        String columns = updatedValues.keySet()
                .stream()
                .map(s -> s + "=?")
                .collect(Collectors.joining(", "));
        updatedValues.put("id", userId);

        cqlSession.execute("update user set " +
                columns +
                " where id=?", updatedValues);
        return true;
    }

    private boolean updateEmail(UUID userId, String oldEmail, String newEmail) {
        if (exists(newEmail)) {
            return false;
        }
        cqlSession.execute("begin batch " +
                        "update user set email=? where id=?; " +
                        "delete from user_email where id=? and email=?; " +
                        "insert into user_email(id, email) values(?, ?); " +
                        "apply batch;",
                newEmail, userId, userId, oldEmail, userId, newEmail);
        return true;
    }

    private Map<String, Object> getUpdatedColumns(User user, User oldUser) {
        Map<String, Object> values = new HashMap<>();
        if (!Objects.equals(user.getEmail(), oldUser.getEmail())) {
            values.put("email", user.getEmail());
        }
        if (!Objects.equals(user.getPassword(), oldUser.getPassword())) {
            values.put("password", user.getPassword().asString());
        }
        if (!Objects.equals(user.getFirstName(), oldUser.getFirstName())) {
            values.put("first_name", user.getFirstName());
        }
        if (!Objects.equals(user.getLastName(), oldUser.getLastName())) {
            values.put("last_name", user.getLastName());
        }
        if (!Arrays.equals(user.getAvatar(), oldUser.getAvatar())) {
            values.put("avatar", user.getAvatar());
        }
        if (!Objects.equals(user.getRole(), oldUser.getRole())) {
            values.put("role", user.getRole().getCode());
        }
        return values;
    }

    /**
     * Delete user
     *
     * @param userId user id
     */
    public boolean delete(UUID userId) {
        Optional<String> email = findEmail(userId);
        if (email.isEmpty()) {
            return false;
        }
        cqlSession.execute("begin batch " +
                "delete from user where id=?; " +
                "delete from user_email where id=? and email=?; " +
                "apply batch;",
                userId, userId, email.get());
        return true;
    }

    private Optional<String> findEmail(UUID userId) {
        return Optional.ofNullable(
                cqlSession.execute("select email from user where id=?", userId)
                        .map(row -> row.getString("email")).one());
    }

}
