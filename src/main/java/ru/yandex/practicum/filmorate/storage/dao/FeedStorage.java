package ru.yandex.practicum.filmorate.storage.dao;

import ru.yandex.practicum.filmorate.model.Feed;

import java.util.List;

public interface FeedStorage {

    void saveFeed(int userId, int entityId, int eventTypeId, int operationId);
    List<Feed> findFeed(int userId);
}
