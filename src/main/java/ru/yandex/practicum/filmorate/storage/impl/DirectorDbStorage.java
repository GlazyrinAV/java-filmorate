package ru.yandex.practicum.filmorate.storage.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exceptions.exceptions.NoResultDataAccessException;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.dao.DirectorStorage;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

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

    @Override
    public List<Director> placeDirectorsToFilmFromDB(int filmId) {
        String sqlQuery = "SELECT FD.DIRECTOR_ID, D2.DIRECTOR_NAME " +
                "FROM FILM_DIRECTOR AS FD JOIN DIRECTORS D2 on D2.DIRECTOR_ID = FD.DIRECTOR_ID WHERE FILM_ID = ?";
        if (!jdbcTemplate.query(sqlQuery, this::mapRowToDirector, filmId).isEmpty()) {
            return jdbcTemplate.query(sqlQuery, this::mapRowToDirector, filmId);
        } else {
            return new ArrayList<>();
        }
    }

    @Override
    public void saveFilmDirectorsToDB(Film film, int filmId) {
        String sqlQueryForGenres = "MERGE INTO FILM_DIRECTOR (film_id, DIRECTOR_ID) VALUES (?, ?)";

        if (film.getDirectors() != null) {
            try {
                jdbcTemplate.batchUpdate(sqlQueryForGenres, new BatchPreparedStatementSetter() {
                    @Override
                    public void setValues(PreparedStatement ps, int i) throws SQLException {
                        Director director = film.getDirectors().get(i);
                        ps.setInt(1, filmId);
                        ps.setInt(2, director.getId());
                    }

                    @Override
                    public int getBatchSize() {
                        return film.getDirectors().size();
                    }
                });
            } catch (DataIntegrityViolationException exception) {
                throw new DataIntegrityViolationException("В запросе неправильно указаны данные о фильме.");
            }
        }
    }

    @Override
    public void remobeByFilmId(int filmId) {
        String sqlQuery = "DELETE FROM FILM_DIRECTOR WHERE film_id = ?";
        jdbcTemplate.update(sqlQuery, filmId);
    }

    @Override
    public Boolean isExists(int directorId) {
        String sqlQuery = "SELECT EXISTS ( SELECT * FROM DIRECTORS WHERE DIRECTOR_ID =? )";
        return Boolean.TRUE.equals(jdbcTemplate.queryForObject(sqlQuery, Boolean.TYPE, directorId));
    }

    private Director mapRowToDirector(ResultSet resultSet, int rowNum) throws SQLException {
        return Director.builder()
                .id(resultSet.getInt("director_id"))
                .name(resultSet.getString("director_name"))
                .build();
    }
}