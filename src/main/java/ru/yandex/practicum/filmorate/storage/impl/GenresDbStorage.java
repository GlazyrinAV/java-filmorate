package ru.yandex.practicum.filmorate.storage.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exceptions.exceptions.NoResultDataAccessException;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.dao.GenresStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Repository
public class GenresDbStorage implements GenresStorage {

    private final JdbcTemplate jdbcTemplate;

    private final List<Genre> genresInMemory;

    @Autowired
    public GenresDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        genresInMemory = new ArrayList<>(findAll());
    }

    @Override
    public void saveFilmGenresToDB(List<Genre> genres, int filmId) {
        String sqlQueryForGenres = "MERGE INTO film_genres (film_id, genre_id) VALUES (?, ?)";
        try {
            jdbcTemplate.batchUpdate(sqlQueryForGenres, genres, genres.size(), (ps, genre) -> {
                ps.setInt(1, filmId);
                ps.setInt(2, genre.getId());
            });
        } catch (DataIntegrityViolationException exception) {
            throw new DataIntegrityViolationException("В запросе неправильно указаны данные о фильме.");
        }
    }

    @Override
    public void removeFilmGenres(int filmId) {
        String sqlQuery = "DELETE FROM film_genres WHERE film_id = ?";
        jdbcTemplate.update(sqlQuery, filmId);
    }

    @Override
    public List<Genre> saveGenresToFilmFromDB(int filmId) {
        String sqlQuery = "SELECT FG.GENRE_ID, G2.GENRE_NAME " +
                "FROM FILM_GENRES AS FG JOIN GENRES G2 on G2.GENRE_ID = FG.GENRE_ID WHERE FILM_ID = ?";
        if (!jdbcTemplate.query(sqlQuery, this::mapRowToGenre, filmId).isEmpty()) {
            return jdbcTemplate.query(sqlQuery, this::mapRowToGenre, filmId);
        } else {
            return new ArrayList<>();
        }
    }

    @Override
    public Collection<Genre> findAll() {
        String sqlQuery = "SELECT * FROM GENRES";
        return jdbcTemplate.query(sqlQuery, this::mapRowToGenre);
    }

    @Override
    public Genre findById(int genreId) {
        if (genreId >= genresInMemory.size() || genresInMemory.get(genreId - 1) == null) {
            String sqlQuery = "SELECT * FROM GENRES WHERE genre_id = ?";
            try {
                return jdbcTemplate.queryForObject(sqlQuery, this::mapRowToGenre, genreId);
            } catch (EmptyResultDataAccessException exception) {
                throw new NoResultDataAccessException("Запрос на получение жанра получен пустой ответ.", 1);
            }
        } else {
            return genresInMemory.get(genreId - 1);
        }
    }

    private Genre mapRowToGenre(ResultSet resultSet, int rowNum) throws SQLException {
        return Genre.builder()
                .id(resultSet.getInt("genre_id"))
                .name(resultSet.getString("genre_name"))
                .build();
    }
}