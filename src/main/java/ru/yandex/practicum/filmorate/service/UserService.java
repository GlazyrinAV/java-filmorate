package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.storage.InMemoryUserStorage;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Service
public class UserService {

    @Autowired
    InMemoryUserStorage storage;

    public void addFriend(int userId, int friendId) {
        if (userId <= 0 || friendId <= 0) {
            throw new ValidationException("ID не может быть меньше или равно нулю.");
        } else if (storage.findUser(userId) == null) {
            throw new ValidationException("Пользователь c ID " + userId + " не найден.");
        } else if (storage.findUser(friendId) == null) {
            throw new ValidationException("Пользователь c ID " + friendId + " не найден.");
        } else {
            storage.findUser(userId).getFriends().add(friendId);
        }
    }

    public void removeFriend(int userId, int friendId) {
        if (userId <= 0 || friendId <= 0) {
            throw new ValidationException("ID не может быть меньше или равно нулю.");
        } else if (storage.findUser(userId) == null) {
            throw new ValidationException("Пользователь c ID " + userId + " не найден.");
        } else if (storage.findUser(friendId) == null) {
            throw new ValidationException("Пользователь c ID " + friendId + " не найден.");
        } else {
            storage.findUser(userId).getFriends().remove(friendId);
        }
    }

    public Collection<Integer> findFriends(int userId) {
        if (userId <= 0) {
            throw new ValidationException("ID не может быть меньше или равно нулю.");
        } else if (storage.findUser(userId) == null) {
            throw new ValidationException("Пользователь c ID " + userId + " не найден.");
        } else {
            return storage.findUser(userId).getFriends();
        }
    }

    public Collection<Integer> findCommonFriends(int userId, int otherUserId) {
        if (userId <= 0 || otherUserId <= 0) {
            throw new ValidationException("ID не может быть меньше или равно нулю.");
        } else if (storage.findUser(userId) == null) {
            throw new ValidationException("Пользователь c ID " + userId + " не найден.");
        } else if (storage.findUser(otherUserId) == null) {
            throw new ValidationException("Пользователь c ID " + otherUserId + " не найден.");
        } else {
            return findCommons(userId, otherUserId);
        }
    }

    private Collection<Integer> findCommons(int user1Id, int user2Id) {
        List<Integer> friendsOfUser1 = new ArrayList<>(storage.findUser(user1Id).getFriends());
        List<Integer> friendsOfUser2 = new ArrayList<>(storage.findUser(user2Id).getFriends());
        friendsOfUser1.retainAll(friendsOfUser2);
        return friendsOfUser1;
    }
}