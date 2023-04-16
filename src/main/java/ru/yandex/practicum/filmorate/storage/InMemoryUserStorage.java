package ru.yandex.practicum.filmorate.storage;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.UserAlreadyExistsException;
import ru.yandex.practicum.filmorate.exceptions.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class InMemoryUserStorage implements UserStorage {

    private final Map<Integer, User> users = new ConcurrentHashMap<>();

    private static int idUserSequence = 1;

    @Override
    public User addNewUser(User user) {
        if (!users.containsValue(user)) {
            if (user.getName() == null) {
                user.setName(user.getLogin());
            }
            user.setId(setNewId());
            users.put(user.getId(), user);
            return user;
        } else {
            throw new UserAlreadyExistsException("Такой пользователь уже существует.");
        }
    }

    @Override
    public User updateUser(User user) {
        if (!users.containsKey(user.getId())) {
            throw new UserNotFoundException("Пользователь не найден.");
        } else {
            users.replace(user.getId(), user);
            return user;
        }
    }

    @Override
    public Collection<User> findAllUsers() {
        return users.values();
    }

    @Override
    public User findFilm(int userId) {
        return users.get(userId);
    }

    public void resetUsersForTest() {
        idUserSequence = 1;
    }

    private int setNewId() {
        return idUserSequence++;
    }
}