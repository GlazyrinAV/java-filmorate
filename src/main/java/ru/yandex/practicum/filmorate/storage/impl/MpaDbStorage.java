package ru.yandex.practicum.filmorate.storage.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exceptions.exceptions.RatingNotFoundException;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.dao.MpaStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class MpaDbStorage implements MpaStorage {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public List<Mpa> findAll() {
        String sqlQuery = "SELECT * FROM MPA";
        return jdbcTemplate.query(sqlQuery, this::mapRowToMpa);
    }

    @Override
    public Mpa findById(int ratingId) {
        String sqlQuery = "SELECT * FROM MPA WHERE MPA_ID = ?";
        return jdbcTemplate.query(sqlQuery, this::mapRowToMpa, ratingId).stream().findFirst()
                .orElseThrow(() -> new RatingNotFoundException("Рейтинг с ID " + ratingId + " не найден."));
    }

    @Override
    public Mpa findByFilmId(int filmId) {
        String sqlQuery = "SELECT R.MPA_ID, R.MPA_NAME FROM MPA AS R " +
                "JOIN FILMS F ON R.MPA_ID = F.MPA_ID WHERE F.FILM_ID = ?";
        return jdbcTemplate.queryForObject(sqlQuery, this::mapRowToMpa, filmId);
    }

    private Mpa mapRowToMpa(ResultSet resultSet, int rowNum) throws SQLException {
        return Mpa.builder()
                .id(resultSet.getInt("mpa_id"))
                .name(resultSet.getString("mpa_name"))
                .build();
    }
}