package ru.yandex.practicum.filmorate.storage.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exceptions.exceptions.NoResultDataAccessException;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.storage.dao.DirectorStorage;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Objects;
import java.util.Optional;

@Repository
@Slf4j
public class DirectorDbStorage implements DirectorStorage {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public DirectorDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Director saveNew(Director director) {
        String sqlQuery = "INSERT INTO DIRECTORS (DIRECTOR_NAME) values (?)";

        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement stmt = connection.prepareStatement(sqlQuery, new String[]{"director_id"});
            stmt.setString(1, director.getName());
            return stmt;
        }, keyHolder);

        Optional<Integer> directorId = Optional.of(Objects.requireNonNull(keyHolder.getKey()).intValue());

        return findById(directorId.get());
    }

    @Override
    public Director update(Director director) {
        String sqlQuery = "UPDATE DIRECTORS SET " +
                "DIRECTOR_NAME = ?" +
                "WHERE DIRECTOR_ID = ?";
        jdbcTemplate.update(sqlQuery,
                director.getName(),
                director.getId());

        return findById(director.getId());
    }

    @Override
    public Collection<Director> findAll() {
        String sqlQuery = "SELECT * FROM DIRECTORS";
        return jdbcTemplate.query(sqlQuery, this::mapRowToDirector);
    }

    @Override
    public Director findById(int id) {
        String sqlQuery = "SELECT * FROM DIRECTORS WHERE DIRECTOR_ID = ?";
        try {
            return jdbcTemplate.queryForObject(sqlQuery, this::mapRowToDirector, id);
        } catch (EmptyResultDataAccessException exception) {
            throw new NoResultDataAccessException("Запрос на получение режиссера получен пустой ответ.", 1);
        }
    }

    @Override
    public void removeById(int id) {
        String sqlQuery = "DELETE FROM DIRECTORS WHERE director_id = ?";
        jdbcTemplate.update(sqlQuery, id);
    }

    private Director mapRowToDirector(ResultSet resultSet, int rowNum) throws SQLException {
        return Director.builder()
                .id(resultSet.getInt("director_id"))
                .name(resultSet.getString("director_name"))
                .build();
    }
}