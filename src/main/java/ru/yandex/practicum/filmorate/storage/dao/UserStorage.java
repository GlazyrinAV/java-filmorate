package ru.yandex.practicum.filmorate.storage.dao;

import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;

public interface UserStorage {

    Integer saveNew(User user);

    Integer update(User user);

    Collection<User> findAll();

    User findById(int userId);

    void saveFriend(int userId, int friendId);

    void removeFriend(int userId, int friendId);

    Collection<User> findFriends(int userId);

    Collection<User> findCommonFriends(int user1Id, int user2Id);

    void removeUser(int userId);
}