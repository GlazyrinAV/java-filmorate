package ru.yandex.practicum.filmorate.storage;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class InMemoryUserStorage implements UserStorage {

    private final Map<Integer, User> users = new ConcurrentHashMap<>();

    @Override
    public User addNewUser(User user) {
        return null;
    }

    @Override
    public User updateUser(User user) {
        return null;
    }

    @Override
    public Collection<User> findAllUsers() {
        return null;
    }
}