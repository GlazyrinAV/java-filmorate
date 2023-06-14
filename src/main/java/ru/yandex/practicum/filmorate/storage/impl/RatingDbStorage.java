package ru.yandex.practicum.filmorate.storage.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exceptions.exceptions.RatingNotFoundException;
import ru.yandex.practicum.filmorate.model.Rating;
import ru.yandex.practicum.filmorate.storage.dao.RatingStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class RatingDbStorage implements RatingStorage {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public List<Rating> findAll() {
        String sqlQuery = "SELECT * FROM MPA";
        return jdbcTemplate.query(sqlQuery, this::mapRowToRating);
    }

    @Override
    public Rating findById(int ratingId) {
        String sqlQuery = "SELECT * FROM MPA WHERE MPA_ID = ?";
        return jdbcTemplate.query(sqlQuery, this::mapRowToRating, ratingId).stream().findFirst()
                .orElseThrow(() -> new RatingNotFoundException("Рейтинг с ID " + ratingId + " не найден."));
    }

    @Override
    public Rating saveRatingToFilmFromDB(int filmId) {
        String sqlQuery = "SELECT R.MPA_ID, R.MPA_NAME FROM MPA AS R " +
                "JOIN FILMS F ON R.MPA_ID = F.MPA_ID WHERE F.FILM_ID = ?";
        return jdbcTemplate.queryForObject(sqlQuery, this::mapRowToRating, filmId);
    }

    private Rating mapRowToRating(ResultSet resultSet, int rowNum) throws SQLException {
        return Rating.builder()
                .id(resultSet.getInt("rating_id"))
                .name(resultSet.getString("rating_name"))
                .build();
    }
}