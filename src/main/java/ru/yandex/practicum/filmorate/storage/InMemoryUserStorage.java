package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.UserAlreadyExistsException;
import ru.yandex.practicum.filmorate.exceptions.UserNotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
@Slf4j
public class InMemoryUserStorage implements UserStorage {

    private static int idUserSequence = 1;
    private final Map<Integer, User> users = new ConcurrentHashMap<>();

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

    public void resetUsersForTest() {
        idUserSequence = 1;
    }

    private int setNewId() {
        return idUserSequence++;
    }

    public Map<Integer, User> getStorage() {
        return users;
    }
}