package ru.yandex.practicum.filmorate.storage.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exceptions.exceptions.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.FriendshipStatus;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.dao.UserStorage;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

@Repository
@Slf4j
@Qualifier("UserDbStorage")
public class UserDbStorage implements UserStorage {

    private final JdbcTemplate jdbcTemplate;

    private final HashMap<String, FriendshipStatus> friendshipStatuses;

    @Autowired
    public UserDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        friendshipStatuses = findAllFriendshipStatuses();
    }

    @Override
    public Integer saveNew(User user) {
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

        Optional<Integer> userId = Optional.of(Objects.requireNonNull(keyHolder.getKey()).intValue());

        return userId.get();
    }

    @Override
    public Integer update(User user) {
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

        return user.getId();
    }

    @Override
    public Collection<User> findAll() {
        String sqlQuery = "SELECT * FROM users";
        return jdbcTemplate.query(sqlQuery, this::mapRowToUser);
    }

    @Override
    public User findById(int userId) {
        String sqlQuery = "SELECT * FROM users where user_id = ?";
        return jdbcTemplate.query(sqlQuery, this::mapRowToUser, userId).stream().findFirst()
                .orElseThrow(() -> new UserNotFoundException("Пользователь c ID " + userId + " не найден."));

    }

    @Override
    public void saveFriend(int userId, int friendId) {
        String sqlQueryForMakingFriend = "INSERT INTO list_of_friends (user_id, friend_id, friendship_status_id) " +
                "VALUES (?, ?, ?)";
        String sqlQueryForCheckingFriendshipStatus = "UPDATE list_of_friends SET " +
                "friendship_status_id = ? WHERE user_id = ? AND friend_id = ?";

        if (findDidFriendMadeFriendRequest(friendId, userId)) {
            jdbcTemplate.update(sqlQueryForMakingFriend, userId, friendId, friendshipStatuses.get("approved").getStatusId());
            jdbcTemplate.update(sqlQueryForCheckingFriendshipStatus, friendshipStatuses.get("approved").getStatusId(), friendId, userId);
        } else {
            jdbcTemplate.update(sqlQueryForMakingFriend, userId, friendId, friendshipStatuses.get("request").getStatusId());
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
        String sqlQuery = "select * from USERS where USER_ID in " +
                "(select FRIEND_ID from LIST_OF_FRIENDS where " +
                "(USER_ID = ? and FRIEND_ID in " +
                "(select FRIEND_ID from LIST_OF_FRIENDS where LIST_OF_FRIENDS.USER_ID = ?)))";
        return jdbcTemplate.query(sqlQuery, this::mapRowToUser, user1Id, user2Id);
    }

    @Override
    public Collection<User> findFriends(int userId) {
        String sqlQuery = "SELECT * FROM users WHERE user_id IN (" +
                "SELECT friend_id FROM list_of_friends WHERE user_id = ?)";
        return jdbcTemplate.query(sqlQuery, this::mapRowToUser, userId);
    }

    @Override
    public void removeUser(int id) {
        String sqlQuery = "DELETE FROM USERS WHERE USER_ID = ?";
        jdbcTemplate.update(sqlQuery, id);
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

    private boolean findDidFriendMadeFriendRequest(int userId, int friendId) {
        String sqlQuery = "SELECT friendship_status_id FROM list_of_friends" +
                " WHERE user_id = ? AND friend_id = ?";
        return jdbcTemplate.queryForRowSet(sqlQuery, userId, friendId).next();
    }

    private HashMap<String, FriendshipStatus> findAllFriendshipStatuses() {
        HashMap<String, FriendshipStatus> statuses = new HashMap<>();
        String sqlQuery = "SELECT * FROM FRIENDSHIP_STATUS";
        List<FriendshipStatus> list = jdbcTemplate.query(sqlQuery, this::mapRowToFriendshipStatus);
        for (FriendshipStatus status : list) {
            statuses.put(status.getStatusName(), status);
        }
        return statuses;
    }

    private FriendshipStatus mapRowToFriendshipStatus(ResultSet resultSet, int rowNum) throws SQLException {
        return FriendshipStatus.builder()
                .statusId(resultSet.getInt("FRIENDSHIP_STATUS_ID"))
                .statusName(resultSet.getString("Friendship_status_name"))
                .build();
    }
}