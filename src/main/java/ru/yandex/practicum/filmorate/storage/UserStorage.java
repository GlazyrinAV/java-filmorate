package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;

public interface UserStorage {

    public User addNewUser(User user);

    public User updateUser(User user);

    public Collection<User> findAllUsers();

}