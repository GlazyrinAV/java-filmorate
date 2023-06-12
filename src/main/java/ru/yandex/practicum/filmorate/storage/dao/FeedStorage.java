package ru.yandex.practicum.filmorate.storage.dao;

import ru.yandex.practicum.filmorate.model.Feed;

import java.util.Collection;

public interface FeedStorage {

    void saveFeed(int userId, int entityId, int eventTypeId, int operationId);

    Collection<Feed> findFeed(int userId);
}
