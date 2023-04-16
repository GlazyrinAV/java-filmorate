package ru.yandex.practicum.filmorate.storage;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.UserAlreadyExistsException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static org.springframework.http.HttpStatus.*;

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
            return new ResponseEntity<>(user, CREATED);
        } else {
            throw new UserAlreadyExistsException("Такой пользователь уже существует.");
        }
    }

    @Override
    public User updateUser(User user) {
        if (!users.containsKey(user.getId())) {
            return new ResponseEntity<>(user, NOT_FOUND);
        } else {
            users.replace(user.getId(), user);
            return new ResponseEntity<>(user, OK);
        }
    }

    @Override
    public Collection<User> findAllUsers() {
        return new ResponseEntity<>(users.values(), OK);
    }

    public void resetUsersForTest() {
        idUserSequence = 1;
    }

    private int setNewId() {
        return idUserSequence++;
    }
}