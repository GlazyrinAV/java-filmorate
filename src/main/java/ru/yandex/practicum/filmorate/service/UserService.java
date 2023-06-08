package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
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

    public User addNew(User user) {
        checkName(user);
        return findById(storage.addNew(user));
    }

    public User update(User user) {
        return findById(storage.update(user));
    }

    public Collection<User> findAll() {
        return storage.findAll();
    }

    public User findById(int  userId) {
        if (!isExists(userId)) {
            log.info("Пользователь c ID " + userId + " не найден.");
            throw new UserNotFoundException("Пользователь c ID " + userId + " не найден.");
        } else {
            return storage.findById(userId);
        }
    }

    public void addFriend(int userId, int friendId) {
        if (!isExists(userId)) {
            log.info("Пользователь c ID " + userId + " не найден.");
            throw new UserNotFoundException("Пользователь c ID " + userId + " не найден.");
        } else if (!isExists(friendId)) {
            log.info("Пользователь c ID " + friendId + " не найден.");
            throw new UserNotFoundException("Пользователь c ID " + friendId + " не найден.");
        } else if (storage.findFriends(userId).contains(storage.findById(friendId))) {
            log.info("Друг уже в друзьях.");
            throw new FriendAlreadyExistException("Пользователь с ID " + userId +
                    " уже добавил в друзья пользователя c ID " + friendId);
        }
        else {
            log.info("Друг добавлен.");
            storage.makeFriend(userId, friendId);
        }
    }

    public void removeFriend(int userId, int friendId) {
        if (!isExists(userId)) {
            log.info("Пользователь c ID " + userId + " не найден.");
            throw new UserNotFoundException("Пользователь c ID " + userId + " не найден.");
        } else if (!isExists(friendId)) {
            log.info("Пользователь c ID " + friendId + " не найден.");
            throw new UserNotFoundException("Пользователь c ID " + friendId + " не найден.");
        } else if (!storage.findFriends(userId).contains(findById(friendId))) {
            log.info("У юзера нет такого друга.");
            throw new FriendAlreadyExistException("Пользователь с ID " + userId +
                    " не имеет в друзьях пользователя c ID " + friendId);
        } else {
            log.info("Друг удален.");
            storage.removeFriend(userId, friendId);
        }
    }

    public Collection<User> findFriends(int userId) {
        if (!isExists(userId)) {
            log.info("Пользователь c ID " + userId + " не найден.");
            throw new UserNotFoundException("Пользователь c ID " + userId + " не найден.");
        } else {
            return storage.findFriends(userId);
        }
    }

    public Collection<User> findCommonFriends(int userId, int otherUserId) {
        if (!isExists(userId)) {
            log.info("Пользователь c ID " + userId + " не найден.");
            throw new UserNotFoundException("Пользователь c ID " + userId + " не найден.");
        } else if (!isExists(otherUserId)) {
            log.info("Пользователь c ID " + otherUserId + " не найден.");
            throw new UserNotFoundException("Пользователь c ID " + otherUserId + " не найден.");
        } else {
            return storage.findCommonFriends(userId, otherUserId);
        }
    }

    public void removeUser(int userId) {
        if (!isExists(userId)) {
            log.info("Пользователь c ID " + userId + " не найден.");
            throw new UserNotFoundException("Пользователь c ID " + userId + " не найден.");
        } else {
            log.info("Пользователь удален.");
            storage.removeUser(userId);
        }
    }

    protected Boolean isExists(int userID) {
        return storage.isExists(userID);
    }

    private void checkName(User user) {
        if (user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
    }
}