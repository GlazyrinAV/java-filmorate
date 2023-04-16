package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.storage.InMemoryUserStorage;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Service
public class UserService {

    @Autowired
    InMemoryUserStorage storage;

    public void addFriend(int userId, int friendId) {
        storage.findUser(userId).getFriends().add(friendId);
    }

    public void removeFriend(int userId, int friendId) {
        storage.findUser(userId).getFriends().remove(friendId);
    }

    public Collection<Integer> findFriends(int userId) {
        return storage.findUser(userId).getFriends();
    }

    public Collection<Integer> findCommonFriends(int userId, int otherUserId) {
        return findCommons(userId, otherUserId);
    }

    private Collection<Integer> findCommons(int user1Id, int user2Id) {
        List<Integer> friendsOfUser1 = new ArrayList<>(storage.findUser(user1Id).getFriends());
        List<Integer> friendsOfUser2 = new ArrayList<>(storage.findUser(user2Id).getFriends());
        friendsOfUser1.retainAll(friendsOfUser2);
        return friendsOfUser1;
    }
}