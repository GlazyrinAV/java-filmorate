package ru.yandex.practicum.filmorate.storage.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.storage.dao.ScoreStorage;

import java.sql.ResultSet;
import java.sql.SQLException;

@Repository
@RequiredArgsConstructor
public class ScoreDbStorage implements ScoreStorage {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public void saveScore(int filmId, int userId, int score) {
        String sqlQuery = "MERGE INTO FILM_SCORE (film_id, user_id, SCORE) VALUES (?, ?, ?)";
        try {
            jdbcTemplate.update(sqlQuery, filmId, userId, score);
        } catch (DataIntegrityViolationException exception) {
            throw new DataIntegrityViolationException("В запросе неправильно указаны данные для добавдения оценки.");
        }
    }

    @Override
    public void removeScore(int filmId, int userId) {
        String sqlQuery = "DELETE FROM FILM_SCORE WHERE film_id = ? AND user_id = ?";
        jdbcTemplate.update(sqlQuery, filmId, userId);
    }

    @Override
    public Double findScore(int filmId) {
        String sqlQuery = "SELECT AVG(SCORE) AS score FROM FILM_SCORE WHERE film_id = ?";
        return jdbcTemplate.queryForObject(sqlQuery, this::mapRowToScore, filmId);
    }

    private Double mapRowToScore(ResultSet resultSet, int rowNum) throws SQLException {
        return resultSet.getDouble("score");
    }
}