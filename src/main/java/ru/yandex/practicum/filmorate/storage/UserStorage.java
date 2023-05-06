package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;

public interface UserStorage {

    User addNew(User user);

    User update(User user);

    Collection<User> findAll();

    User findById(int userId);

    void addFriend(int userId, int friendId);

    void removeFriend(int userId, int friendId);

    Collection<User> findFriends(int userId);

    Collection<User> findCommonFriends(int user1Id, int user2Id);

    boolean isExists(int userId);
}