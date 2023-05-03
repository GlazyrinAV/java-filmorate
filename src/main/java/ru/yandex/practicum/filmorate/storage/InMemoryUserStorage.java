package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.UserAlreadyExistsException;
import ru.yandex.practicum.filmorate.exceptions.UserNotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Component
@Slf4j
@Qualifier("InMemoryUserStorage")
public class InMemoryUserStorage implements UserStorage {

    private int idUserSequence = 1;
    private final Map<Integer, User> users = new ConcurrentHashMap<>();
    private final Map<Integer, Set<Integer>> friends = new ConcurrentHashMap<>();

    @Override
    public User addNewUser(User user) {
        if (!users.containsValue(user)) {
            if (user.getName().isBlank()) {
                user.setName(user.getLogin());
            }
            user.setId(setNewId());
            users.put(user.getId(), user);
            log.info("Пользователь добавлен.");
            return user;
        } else {
            log.info("Такой пользователь уже существует.");
            throw new UserAlreadyExistsException("Такой пользователь уже существует.");
        }
    }

    @Override
    public User updateUser(User user) {
        if (!users.containsKey(user.getId())) {
            log.info("Пользователь c ID " + user.getId() + " не найден.");
            throw new UserNotFoundException("Пользователь c ID " + user.getId() + " не найден.");
        } else {
            users.replace(user.getId(), user);
            log.info("Данные о пользователе с ID " + user.getId() + " обновлены.");
            return user;
        }
    }

    @Override
    public Collection<User> findAllUsers() {
        return users.values();
    }

    @Override
    public User findUser(int userId) {
        if (userId <= 0) {
            log.info("Указанный ID меньше или равен нулю.");
            throw new ValidationException("ID не может быть меньше или равно нулю.");
        } else if (!users.containsKey(userId)) {
            log.info("Пользователь c ID " + userId + " не найден.");
            throw new UserNotFoundException("Пользователь c ID " + userId + " не найден.");
        } else {
            return users.get(userId);
        }
    }

    private Set<Integer> putFriend(int userId, int friendId) {
        friends.getOrDefault(userId, new HashSet<>()).add(friendId);
        return friends.get(userId);
    }

    @Override
    public void addFriend(int userId, int friendId) {
        friends.put(userId, putFriend(userId, friendId));
        friends.put(friendId, putFriend(friendId, userId));
    }

    @Override
    public void removeFriend(int userId, int friendId) {
        friends.get(userId).remove(friendId);
        friends.get(friendId).remove(userId);
    }

    @Override
    public Collection<User> findFriends(int userId) {
        return users.values().stream()
                .filter(user -> friends.get(userId).contains(userId))
                .collect(Collectors.toList());
    }

    @Override
    public Collection<User> findCommonFriends(int user1Id, int user2Id) {
        List<Integer> friendsOfUser1 = new ArrayList<>(friends.get(user1Id));
        List<Integer> friendsOfUser2 = new ArrayList<>(friends.get(user2Id));
        friendsOfUser1.retainAll(friendsOfUser2);
        return users.values().stream()
                .filter(user -> friendsOfUser1.contains(user.getId()))
                .collect(Collectors.toList());
    }

    public void resetUsersForTest() {
        idUserSequence = 1;
    }

    private int setNewId() {
        return idUserSequence++;
    }

    public void resetCounter() {
        idUserSequence = 1;
    }
}