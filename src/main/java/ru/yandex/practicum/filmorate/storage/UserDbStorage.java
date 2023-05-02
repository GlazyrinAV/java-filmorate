package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;

@Component
@Slf4j
@Qualifier("UserDbStorage")
public class UserDbStorage implements UserStorage {
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

    @Override
    public User findUser(int userId) {
        return null;
    }

    @Override
    public User addFriend(int userId, int friendId) {
        return null;
    }

    @Override
    public User removeFriend(int userId, int friendId) {
        return null;
    }

    @Override
    public Collection<User> findCommonFriends(int user1Id, int user2Id) {
        return null;
    }
}
