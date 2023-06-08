package ru.yandex.practicum.filmorate.storage.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Rating;
import ru.yandex.practicum.filmorate.storage.dao.RatingStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
public class RatingDbStorage implements RatingStorage {

    private final JdbcTemplate jdbcTemplate;

    private final List<Rating> ratingsInMemory;

    @Autowired
    public RatingDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        ratingsInMemory = findAll();
    }

    @Override
    public List<Rating> findAll() {
        String sqlQuery = "SELECT * FROM RATINGS";
        return jdbcTemplate.query(sqlQuery, this::mapRowToRating);
    }

    @Override
    public Rating findById(int ratingId) {
        if (ratingId >= ratingsInMemory.size() || ratingsInMemory.get(ratingId - 1) == null) {
            String sqlQuery = "SELECT * FROM RATINGS WHERE rating_id = ?";
            return jdbcTemplate.queryForObject(sqlQuery, this::mapRowToRating, ratingId);
        } else {
            return ratingsInMemory.get(ratingId - 1);
        }
    }

    @Override
    public Rating saveRatingToFilmFromDB(int filmId) {
        String sqlQuery = "SELECT R.RATING_ID, R.RATING_NAME FROM RATINGS AS R " +
                "JOIN FILMS F ON R.RATING_ID = F.RATING_ID WHERE F.FILM_ID = ?";
        return jdbcTemplate.queryForObject(sqlQuery, this::mapRowToRating, filmId);
    }

    private Rating mapRowToRating(ResultSet resultSet, int rowNum) throws SQLException {
        return Rating.builder()
                .id(resultSet.getInt("rating_id"))
                .name(resultSet.getString("rating_name"))
                .build();
    }
}