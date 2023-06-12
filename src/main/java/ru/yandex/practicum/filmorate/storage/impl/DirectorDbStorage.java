package ru.yandex.practicum.filmorate.storage.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exceptions.exceptions.DirectorNotFoundException;
import ru.yandex.practicum.filmorate.model.Director;
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
    public Integer saveNew(Director director) {
        String sqlQuery = "INSERT INTO DIRECTORS (DIRECTOR_NAME) values (?)";

        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement stmt = connection.prepareStatement(sqlQuery, new String[]{"director_id"});
            stmt.setString(1, director.getName());
            return stmt;
        }, keyHolder);

        Optional<Integer> directorId = Optional.of(Objects.requireNonNull(keyHolder.getKey()).intValue());

        return directorId.get();
    }

    @Override
    public Integer update(Director director) {
        String sqlQuery = "UPDATE DIRECTORS SET " +
                "DIRECTOR_NAME = ?" +
                "WHERE DIRECTOR_ID = ?";
        jdbcTemplate.update(sqlQuery,
                director.getName(),
                director.getId());
        return director.getId();
    }

    @Override
    public Collection<Director> findAll() {
        String sqlQuery = "SELECT * FROM DIRECTORS";
        return jdbcTemplate.query(sqlQuery, this::mapRowToDirector);
    }

    @Override
    public Director findById(int id) {
        String sqlQuery = "SELECT * FROM DIRECTORS WHERE DIRECTOR_ID = ?";
        return  jdbcTemplate.query(sqlQuery, this::mapRowToDirector, id).stream().findFirst()
                .orElseThrow(()->new DirectorNotFoundException("Режиссер c ID " + id + " не найден."));
    }

    @Override
    public void removeById(int id) {
        String sqlQuery = "DELETE FROM DIRECTORS WHERE director_id = ?";
        jdbcTemplate.update(sqlQuery, id);
    }

    @Override
    public List<Director> saveDirectorsToFilmFromDB(int filmId) {
        String sqlQuery = "SELECT FD.DIRECTOR_ID, D2.DIRECTOR_NAME " +
                "FROM FILM_DIRECTOR AS FD JOIN DIRECTORS D2 on D2.DIRECTOR_ID = FD.DIRECTOR_ID WHERE FILM_ID = ?";
        if (!jdbcTemplate.query(sqlQuery, this::mapRowToDirector, filmId).isEmpty()) {
            return jdbcTemplate.query(sqlQuery, this::mapRowToDirector, filmId);
        } else {
            return new ArrayList<>();
        }
    }

    @Override
    public void saveDirectorsToDBFromFilm(List<Director> directors, int filmId) {
        String sqlQueryForGenres = "MERGE INTO FILM_DIRECTOR (film_id, DIRECTOR_ID) VALUES (?, ?)";
        try {
            jdbcTemplate.batchUpdate(sqlQueryForGenres, directors, directors.size(), (ps, director) -> {
                ps.setInt(1, filmId);
                ps.setInt(2, director.getId());
            });
        } catch (DataIntegrityViolationException exception) {
            throw new DataIntegrityViolationException("В запросе неправильно указаны данные о фильме.");
        }
    }

    @Override
    public void removeFromFilmByFilmID(int filmId) {
        String sqlQuery = "DELETE FROM FILM_DIRECTOR WHERE film_id = ?";
        jdbcTemplate.update(sqlQuery, filmId);
    }

    private Director mapRowToDirector(ResultSet resultSet, int rowNum) throws SQLException {
        return Director.builder()
                .id(resultSet.getInt("director_id"))
                .name(resultSet.getString("director_name"))
                .build();
    }
}