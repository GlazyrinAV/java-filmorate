package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.exceptions.FriendAlreadyExistException;
import ru.yandex.practicum.filmorate.exceptions.exceptions.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.dao.UserStorage;

import java.util.Collection;

@Service
@Slf4j
public class UserService {

    private final UserStorage storage;

    @Autowired
    public UserService(@Qualifier("UserDbStorage") UserStorage storage) {
        this.storage = storage;
    }

    public User saveNew(User user) {
        checkName(user);
        return findById(storage.saveNew(user));
    }

    public User update(User user) {
        return findById(storage.update(user));
    }

    public Collection<User> findAll() {
        return storage.findAll();
    }

    public User findById(int userId) {
        User user;
        try {
            user = storage.findById(userId);
        } catch (EmptyResultDataAccessException exception) {
            throw new UserNotFoundException("Пользователь c ID " + userId + " не найден.");
        }
        return user;
    }

    public void saveFriend(int userId, int friendId) {
        findById(userId);
        findById(friendId);
        if (storage.findFriends(userId).contains(storage.findById(friendId))) {
            throw new FriendAlreadyExistException("Пользователь с ID " + userId +
                    " уже добавил в друзья пользователя c ID " + friendId);
        } else {
            log.info("Друг добавлен.");
            storage.saveFriend(userId, friendId);
        }
    }

    public void removeFriend(int userId, int friendId) {
        findById(userId);
        findById(friendId);
        if (!storage.findFriends(userId).contains(findById(friendId))) {
            throw new FriendAlreadyExistException("Пользователь с ID " + userId +
                    " не имеет в друзьях пользователя c ID " + friendId);
        } else {
            log.info("Друг удален.");
            storage.removeFriend(userId, friendId);
        }
    }

    public Collection<User> findFriends(int userId) {
        findById(userId);
        return storage.findFriends(userId);
    }

    public Collection<User> findCommonFriends(int userId, int otherUserId) {
        findById(userId);
        findById(otherUserId);
        return storage.findCommonFriends(userId, otherUserId);
    }

    public void removeUser(int userId) {
        User user;
        try {
            user = storage.findById(userId);
        } catch (EmptyResultDataAccessException exception) {
            throw new UserNotFoundException("Пользователь c ID " + userId + " не найден.");
        }
        log.info("Пользователь удален.");
        storage.removeUser(userId);

    }


    private void checkName(User user) {
        if (user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
    }
}