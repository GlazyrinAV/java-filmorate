package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.User;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Objects;
import java.util.Optional;

@Repository
@Slf4j
@Qualifier("UserDbStorage")
public class UserDbStorage implements UserStorage {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public UserDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public User addNewUser(User user) {
        String sqlQuery = "INSERT INTO users (name, login, email, birthday) values (?, ?, ?, ?)";

        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement stmt = connection.prepareStatement(sqlQuery, new String[]{"user_id"});
            stmt.setString(1, checkName(user));
            stmt.setString(2, user.getLogin());
            stmt.setString(3, user.getEmail());
            stmt.setDate(4, Date.valueOf(user.getBirthday()));
            return stmt;
        }, keyHolder);

        Optional<Integer> userId = Optional.of(Objects.requireNonNull(keyHolder.getKey()).intValue());

        return findUser(userId.get());
    }

    private String checkName(User user) {
        if (user.getName().isBlank()) {
            return user.getLogin();
        } else {
            return user.getName();
        }
    }

    @Override
    public User updateUser(User user) {
        String sqlQuery = "UPDATE users SET " +
                "name = ?," +
                "login = ?," +
                "email = ?," +
                "birthday = ?" +
                "WHERE user_id = ?";
        jdbcTemplate.update(sqlQuery,
                user.getName(),
                user.getLogin(),
                user.getEmail(),
                Date.valueOf(user.getBirthday()),
                user.getId());
        return findUser(user.getId());
    }

    @Override
    public Collection<User> findAllUsers() {
        String sqlQuery = "SELECT * FROM users";
        return jdbcTemplate.query(sqlQuery, this::mapRowToUser);
    }

    @Override
    public User findUser(int userId) {
        String sqlQuery = "SELECT * FROM users where user_id = ?";
        return jdbcTemplate.queryForObject(sqlQuery, this::mapRowToUser, userId);
    }

    @Override
    public void addFriend(int userId, int friendId) {
        String sqlQuery = "INSERT INTO list_of_friends (user_id, friend_id, friendship_status_id) " +
                "VALUES (?, ?, ?)";
        int friendshipStatus;
        if (findFriendshipStatus(friendId, userId) == 1) {
            friendshipStatus = 2;
        } else {
            friendshipStatus = 1;
        }
        jdbcTemplate.update(sqlQuery, userId, friendId, friendshipStatus);
        if (friendshipStatus == 2) {
            String sqlQuery2 = "UPDATE list_of_friends SET " +
                    "friendship_status_id = ? WHERE user_id = ? AND friend_id = ?";
            jdbcTemplate.update(sqlQuery2, friendId, userId, friendshipStatus);
        }
    }

    @Override
    public void removeFriend(int userId, int friendId) {
        String sqlQuery = "DELETE FROM list_of_friends WHERE (user_id = ? AND friend_id = ?) OR" +
                "(user_id = ? AND friend_id = ?)";
        jdbcTemplate.update(sqlQuery, userId, friendId, friendId, userId);
    }

    @Override
    public Collection<User> findCommonFriends(int user1Id, int user2Id) {
        String sqlQuery = "SELECT * FROM USERS WHERE " +
                "(USER_ID IN (SELECT F2.FRIEND_ID FROM LIST_OF_FRIENDS AS F2 " +
                "JOIN (SELECT FRIEND_ID FROM LIST_OF_FRIENDS WHERE USER_ID = ?)" +
                " AS F1 ON F1.FRIEND_ID = F2.FRIEND_ID" +
                " )) AND USER_ID NOT IN (?)";
        return jdbcTemplate.query(sqlQuery, this::mapRowToUser, user1Id, user2Id);
    }

    @Override
    public Collection<User> findFriends(int userId) {
        String sqlQuery = "SELECT * FROM users WHERE user_id IN (" +
                "SELECT friend_id FROM list_of_friends WHERE user_id = ?)";
        return jdbcTemplate.query(sqlQuery, this::mapRowToUser, userId);
    }

    private User mapRowToUser(ResultSet resultSet, int rowNum) throws SQLException {
        return User.builder()
                .id(resultSet.getInt("user_id"))
                .name(resultSet.getString("name"))
                .login(resultSet.getString("login"))
                .email(resultSet.getString("email"))
                .birthday(resultSet.getDate("birthday").toLocalDate())
                .build();
    }

    private int findFriendshipStatus(int userId, int friendId) {
        String sqlQuery = "SELECT friendship_status_id FROM list_of_friends" +
                " WHERE user_id = ? AND friend_id = ?";
        if (!jdbcTemplate.queryForRowSet(sqlQuery, userId, friendId).next()) {
            return 0;
        } else {
            return 1;
        }
    }
}