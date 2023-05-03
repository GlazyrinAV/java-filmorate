package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;

public interface UserStorage {

    User addNewUser(User user);

    User updateUser(User user);

    Collection<User> findAllUsers();

    User findUser(int userId);

    void addFriend(int userId, int friendId);

    void removeFriend(int userId, int friendId);

    Collection<User> findFriends(int userId);

    Collection<User> findCommonFriends(int user1Id, int user2Id);
}