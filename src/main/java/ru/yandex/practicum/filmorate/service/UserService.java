package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.exceptions.FriendAlreadyExistException;
import ru.yandex.practicum.filmorate.model.Feed;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.dao.FeedStorage;
import ru.yandex.practicum.filmorate.storage.dao.UserStorage;

import java.util.Collection;

@Service
@Slf4j
public class UserService {

    private final UserStorage userStorage;
    private final FeedStorage feedStorage;

    @Autowired
    public UserService(@Qualifier("UserDbStorage") UserStorage userStorage,
                       FeedStorage feedStorage) {
        this.userStorage = userStorage;
        this.feedStorage = feedStorage;
    }

    public User saveNew(User user) {
        checkName(user);
        return findById(userStorage.saveNew(user));
    }

    public User update(User user) {
        return findById(userStorage.update(user));
    }

    public Collection<User> findAll() {
        return userStorage.findAll();
    }

    public User findById(int userId) {
        User user;
        user = userStorage.findById(userId);
        return user;
    }

    public void saveFriend(int userId, int friendId) {
        findById(userId);
        findById(friendId);
        if (userStorage.findFriends(userId).contains(userStorage.findById(friendId))) {
            throw new FriendAlreadyExistException("Пользователь с ID " + userId +
                    " уже добавил в друзья пользователя c ID " + friendId);
        } else {
            log.info("Друг добавлен.");
            userStorage.saveFriend(userId, friendId);
            feedStorage.saveFeed(userId, friendId, 3, 2);
        }
    }

    public void removeFriend(int userId, int friendId) {
        findById(userId);
        findById(friendId);
        if (!userStorage.findFriends(userId).contains(findById(friendId))) {
            throw new FriendAlreadyExistException("Пользователь с ID " + userId +
                    " не имеет в друзьях пользователя c ID " + friendId);
        } else {
            log.info("Друг удален.");
            userStorage.removeFriend(userId, friendId);
            feedStorage.saveFeed(userId, friendId, 3, 1);
        }
    }

    public Collection<User> findFriends(int userId) {
        findById(userId);
        return userStorage.findFriends(userId);
    }

    public Collection<User> findCommonFriends(int userId, int otherUserId) {
        findById(userId);
        findById(otherUserId);
        return userStorage.findCommonFriends(userId, otherUserId);
    }

    public Collection<Feed> findFeed(int userId) {
        findById(userId);
        return feedStorage.findFeed(userId);
    }

    public void removeUser(int userId) {
        findById(userId);
        log.info("Пользователь удален.");
        userStorage.removeUser(userId);
    }

    private void checkName(User user) {
        if (user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
    }
}