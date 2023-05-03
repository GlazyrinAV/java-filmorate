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
            stmt.setString(1, user.getName());
            stmt.setString(2, user.getLogin());
            stmt.setString(3, user.getEmail());
            stmt.setDate(4, Date.valueOf(user.getBirthday()));
            return stmt;
        }, keyHolder);

        Optional<Integer> user_id = Optional.of(Objects.requireNonNull(keyHolder.getKey()).intValue());

        return findUser(user_id.get());
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
        int friendship_status;
        if (findFriendshipStatus(friendId, userId) == 1) {
            friendship_status = 2;
        } else {
            friendship_status = 1;
        }
        jdbcTemplate.update(sqlQuery, userId, friendId, friendship_status);
        if (friendship_status == 2) {
            String sqlQuery2 = "UPDATE list_of_friends SET " +
                    "friendship_status_id = ? WHERE user_id = ? AND friend_id = ?";
            jdbcTemplate.update(sqlQuery2, friendId, userId, friendship_status);
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
        return null;
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
            return 1;
        } else {
            return jdbcTemplate.queryForRowSet(sqlQuery, userId, friendId).findColumn("friendship_status_id");
        }
    }
}