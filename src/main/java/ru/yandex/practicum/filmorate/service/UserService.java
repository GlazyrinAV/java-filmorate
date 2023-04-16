package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.storage.InMemoryUserStorage;

import java.util.Collection;

@Service
public class UserService {

    @Autowired
    InMemoryUserStorage storage;

    public void addFriend(int userId, int friendId) {

    }

    public void removeFriend(int userId, int friendId) {

    }

    public Collection<Integer> findFriends(int userId) {

    }

    public Collection<Integer> findCommonFriends(int userId, int otherUserId) {

    }
}