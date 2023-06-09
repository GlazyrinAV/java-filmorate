package ru.yandex.practicum.filmorate.storage.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.EventType;
import ru.yandex.practicum.filmorate.model.Feed;
import ru.yandex.practicum.filmorate.model.Operation;
import ru.yandex.practicum.filmorate.storage.dao.FeedStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
public class FeedDbStorage implements FeedStorage {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public FeedDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void saveFeed(int userId, int entityId, int eventTypeId, int operationId) {
        String sql = "INSERT INTO FEEDS (TIMESTAMP, USER_ID, ENTITY_ID, EVENT_TYPE_ID, OPERATION_ID)\n" +
                "VALUES (NOW(), ?, ?, ?, ?)";
        jdbcTemplate.update(sql, userId, entityId, eventTypeId, operationId);
    }

    @Override
    public List<Feed> findFeed(int userId) {
        String sql = "SELECT * FROM FEEDS F " +
                "LEFT JOIN EVENT_TYPE AS ET ON F.EVENT_TYPE_ID = ET.EVENT_TYPE_ID " +
                "LEFT JOIN OPERATION_FOR_FEEDS AS OFF ON F.OPERATION_ID = OFF.OPERATION_ID " +
                "WHERE USER_ID = ? " +
                "ORDER BY F.TIMESTAMP ASC";
        return jdbcTemplate.query(sql, this::mapRowToFeed, userId);
    }

    private Feed mapRowToFeed(ResultSet rs, int rowNum) throws SQLException {
        return Feed.builder()
                .eventId(rs.getInt("feeds.event_id"))
                .timestamp(rs.getTimestamp("feeds.timestamp"))
                .userId(rs.getInt("feeds.user_id"))
                .entityId(rs.getInt("feeds.entity_id"))
                .eventType(new EventType(rs.getInt("event_type.event_type_id"),
                        rs.getString("event_type.event_type")))
                .operation(new Operation(rs.getInt("operation_for_feeds.operation_id"),
                        rs.getString("operation_for_feeds.operation")))
                .build();
    }
}
