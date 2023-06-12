package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.UserNotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.Collection;

@Service
@Slf4j
public class UserService {

    private final UserStorage storage;

    public UserService(@Qualifier("UserDbStorage") UserStorage storage) {
        this.storage = storage;
    }

    public User addNew(User user) {
        return storage.addNew(user);
    }

    public User update(User user) {
        return storage.update(user);
    }

    public Collection<User> findAll() {
        return storage.findAll();
    }

    public User findById(int  userId) {
        if (userId <= 0) {
            log.info("Указан ID меньше или равный нулю.");
            throw new ValidationException("ID не может быть меньше или равно нулю.");
        } else {
            return storage.findById(userId);
        }
    }

    public void addFriend(int userId, int friendId) {
        if (userId <= 0 || friendId <= 0) {
            log.info("Указан ID меньше или равный нулю.");
            throw new ValidationException("ID не может быть меньше или равно нулю.");
        } else if (!isExists(userId)) {
            log.info("Пользователь c ID " + userId + " не найден.");
            throw new UserNotFoundException("Пользователь c ID " + userId + " не найден.");
        } else if (!isExists(friendId)) {
            log.info("Пользователь c ID " + friendId + " не найден.");
            throw new UserNotFoundException("Пользователь c ID " + friendId + " не найден.");
        } else {
            log.info("Друг добавлен.");
            storage.addFriend(userId, friendId);
        }
    }

    public void removeFriend(int userId, int friendId) {
        if (userId <= 0 || friendId <= 0) {
            log.info("Указан ID меньше или равный нулю.");
            throw new ValidationException("ID не может быть меньше или равно нулю.");
        } else if (!isExists(userId)) {
            log.info("Пользователь c ID " + userId + " не найден.");
            throw new UserNotFoundException("Пользователь c ID " + userId + " не найден.");
        } else if (!isExists(friendId)) {
            log.info("Пользователь c ID " + friendId + " не найден.");
            throw new UserNotFoundException("Пользователь c ID " + friendId + " не найден.");
        } else {
            log.info("Друг удален.");
            storage.removeFriend(userId, friendId);
        }
    }

    public Collection<User> findFriends(int userId) {
        if (userId <= 0) {
            log.info("Указан ID меньше или равный нулю.");
            throw new ValidationException("ID не может быть меньше или равно нулю.");
        } else if (!isExists(userId)) {
            log.info("Пользователь c ID " + userId + " не найден.");
            throw new UserNotFoundException("Пользователь c ID " + userId + " не найден.");
        } else {
            return storage.findFriends(userId);
        }
    }

    public Collection<User> findCommonFriends(int userId, int otherUserId) {
        if (userId <= 0 || otherUserId <= 0) {
            log.info("Указан ID меньше или равный нулю.");
            throw new ValidationException("ID не может быть меньше или равно нулю.");
        } else if (!isExists(userId)) {
            log.info("Пользователь c ID " + userId + " не найден.");
            throw new UserNotFoundException("Пользователь c ID " + userId + " не найден.");
        } else if (!isExists(otherUserId)) {
            log.info("Пользователь c ID " + otherUserId + " не найден.");
            throw new UserNotFoundException("Пользователь c ID " + otherUserId + " не найден.");
        } else {
            return storage.findCommonFriends(userId, otherUserId);
        }
    }

    protected Boolean isExists(int userID) {
        return storage.isExists(userID);
    }
}