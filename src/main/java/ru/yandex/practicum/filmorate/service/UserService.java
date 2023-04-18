package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.UserNotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.InMemoryUserStorage;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class UserService {

    @Autowired
    private InMemoryUserStorage storage;

    public User addFriend(int userId, int friendId) {
        if (userId <= 0 || friendId <= 0) {
            log.info("Указан ID меньше или равный нулю.");
            throw new ValidationException("ID не может быть меньше или равно нулю.");
        } else if (storage.findUser(userId) == null) {
            log.info("Пользователь c ID " + userId + " не найден.");
            throw new UserNotFoundException("Пользователь c ID " + userId + " не найден.");
        } else if (storage.findUser(friendId) == null) {
            log.info("Пользователь c ID " + friendId + " не найден.");
            throw new UserNotFoundException("Пользователь c ID " + friendId + " не найден.");
        } else {
            log.info("Друг добавлен.");
            storage.findUser(userId).getFriends().add(friendId);
            storage.findUser(friendId).getFriends().add(userId);
            return storage.findUser(friendId);
        }
    }

    public User removeFriend(int userId, int friendId) {
        if (userId <= 0 || friendId <= 0) {
            log.info("Указан ID меньше или равный нулю.");
            throw new ValidationException("ID не может быть меньше или равно нулю.");
        } else if (storage.findUser(userId) == null) {
            log.info("Пользователь c ID " + userId + " не найден.");
            throw new UserNotFoundException("Пользователь c ID " + userId + " не найден.");
        } else if (storage.findUser(friendId) == null) {
            log.info("Пользователь c ID " + friendId + " не найден.");
            throw new UserNotFoundException("Пользователь c ID " + friendId + " не найден.");
        } else {
            log.info("Друг удален.");
            storage.findUser(userId).getFriends().remove(friendId);
            storage.findUser(friendId).getFriends().remove(userId);
            return storage.findUser(friendId);
        }
    }

    public Collection<User> findFriends(int userId) {
        return storage.getStorage().values().stream()
                .filter(user -> storage.findUser(userId).getFriends().contains(user.getId()))
                .collect(Collectors.toList());
    }

    public Collection<User> findCommonFriends(int userId, int otherUserId) {
        return findCommons(userId, otherUserId);
    }

    private Collection<User> findCommons(int user1Id, int user2Id) {
        List<Integer> friendsOfUser1 = new ArrayList<>(storage.findUser(user1Id).getFriends());
        List<Integer> friendsOfUser2 = new ArrayList<>(storage.findUser(user2Id).getFriends());
        friendsOfUser1.retainAll(friendsOfUser2);
        return storage.getStorage().values().stream()
                .filter(user -> friendsOfUser1.contains(user.getId()))
                .collect(Collectors.toList());
    }
}