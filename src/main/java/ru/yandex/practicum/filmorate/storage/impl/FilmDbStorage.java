package ru.yandex.practicum.filmorate.storage.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exceptions.exceptions.NoResultDataAccessException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.dao.DirectorStorage;
import ru.yandex.practicum.filmorate.storage.dao.FilmStorage;
import ru.yandex.practicum.filmorate.storage.dao.GenresStorage;
import ru.yandex.practicum.filmorate.storage.dao.RatingStorage;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Duration;
import java.util.Collection;
import java.util.Objects;
import java.util.Optional;

@Repository
@Slf4j
@Qualifier("FilmDbStorage")
public class FilmDbStorage implements FilmStorage {

    private final JdbcTemplate jdbcTemplate;

    private final GenresStorage genresStorage;

    private final RatingStorage ratingStorage;

    private final DirectorStorage directorStorage;

    @Autowired
    public FilmDbStorage(JdbcTemplate jdbcTemplate, GenresStorage genresStorage, RatingStorage ratingStorage, DirectorStorage directorStorage) {
        this.jdbcTemplate = jdbcTemplate;
        this.genresStorage = genresStorage;
        this.ratingStorage = ratingStorage;
        this.directorStorage = directorStorage;
    }

    @Override
    public Film addNew(Film film) {
        String sqlQuery = "INSERT INTO films (name, description, release_date, duration, rating_id) " +
                "VALUES (?, ?, ?, ?, ?)";

        KeyHolder keyHolder = new GeneratedKeyHolder();
        try {
            jdbcTemplate.update(connection -> {
                PreparedStatement stmt = connection.prepareStatement(sqlQuery, new String[]{"film_id"});
                stmt.setString(1, film.getName());
                stmt.setString(2, film.getDescription());
                stmt.setDate(3, Date.valueOf(film.getReleaseDate()));
                stmt.setLong(4, film.getDuration().toMillis());
                stmt.setInt(5, film.getMpa().getId());
                return stmt;
            }, keyHolder);

        } catch (DataIntegrityViolationException e) {
            throw new DataIntegrityViolationException("В запросе неправильно указаны данные о фильме.");
        }
        Optional<Integer> filmId = Optional.of(Objects.requireNonNull(keyHolder.getKey()).intValue());

        genresStorage.addFilmGenresToDB(film, filmId.get());

        return findById(filmId.get());
    }

    @Override
    public Film update(Film film) {
        String sqlQuery = "UPDATE films SET " +
                "name = ?," +
                "description = ?," +
                "release_date = ?," +
                "duration = ?," +
                "rating_id = ?" +
                "WHERE film_id = ?";
        try {
            jdbcTemplate.update(sqlQuery,
                    film.getName(),
                    film.getDescription(),
                    Date.valueOf(film.getReleaseDate()),
                    film.getDuration().toMillis(),
                    film.getMpa().getId(),
                    film.getId());
        } catch (DataIntegrityViolationException exception) {
            throw new DataIntegrityViolationException("В запросе неправильно указаны данные о фильме.");
        }

        genresStorage.clearFilmGenres(film.getId());
        genresStorage.addFilmGenresToDB(film, film.getId());

        return findById(film.getId());
    }

    @Override
    public Collection<Film> findAll() {
        String sqlQuery = "SELECT * FROM films";
        return jdbcTemplate.query(sqlQuery, this::mapRowToFilm);
    }

    @Override
    public Film findById(int filmId) {
        String sqlQuery = "SELECT * FROM films where film_id = ?";
        try {
            return jdbcTemplate.queryForObject(sqlQuery, this::mapRowToFilm, filmId);
        } catch (EmptyResultDataAccessException exception) {
            throw new NoResultDataAccessException("Получен пустой ответ на запрос.", 1);
        }
    }

    @Override
    public Collection<Film> findPopular(int count) {
        String sqlQuery =
                "SELECT * FROM FILMS LEFT JOIN FILM_LIKES FL on FILMS.FILM_ID = FL.FILM_ID " +
                        "GROUP BY FILMS.FILM_ID ORDER BY COUNT(FL.USER_ID) DESC LIMIT ?";
        return jdbcTemplate.query(sqlQuery, this::mapRowToFilm, count);
    }

    @Override
    public void makeLike(int filmId, int userId) {
        String sqlQuery = "INSERT INTO film_likes (film_id, user_id) VALUES (?, ?)";
        try {
            jdbcTemplate.update(sqlQuery, filmId, userId);
        } catch (DataIntegrityViolationException exception) {
            throw new DataIntegrityViolationException("В запросе неправильно указаны данные для добавдения лайка.");
        }
    }

    @Override
    public void removeLike(int filmId, int userId) {
        String sqlQuery = "DELETE FROM film_likes WHERE film_id = ? AND user_id = ?";
        jdbcTemplate.update(sqlQuery, filmId, userId);
    }

    @Override
    public Collection<Integer> findLikes(int filmId) {
        String sqlQuery = "SELECT user_id FROM film_likes WHERE film_id = ?";
        return jdbcTemplate.query(sqlQuery, this::mapRowToInteger, filmId);
    }

    @Override
    public Boolean isExists(int filmId) {
        String sqlQuery = "SELECT EXISTS ( SELECT * FROM PUBLIC.films WHERE film_id =? )";
        return Boolean.TRUE.equals(jdbcTemplate.queryForObject(sqlQuery, Boolean.TYPE, filmId));
    }

    @Override
    public Collection<Film> findByDirectorId(int directorId, String sortBy) {
        if (sortBy.equals("year")) {
            String sqlQuery = "SELECT * FROM FILMS WHERE FILM_ID IN (SELECT FILM_ID FROM FILM_DIRECTOR WHERE DIRECTOR_ID = ?) " +
                    "ORDER BY EXTRACT(YEAR FROM RELEASE_DATE)";
            return jdbcTemplate.query(sqlQuery, this::mapRowToFilm, directorId);
        } else {
            String sqlQuery = "SELECT FILMS.*, SUM(FL.USER_ID) AS LIKES FROM FILMS LEFT JOIN FILM_LIKES FL on FILMS.FILM_ID = FL.FILM_ID " +
                    "WHERE FILMS.FILM_ID IN (SELECT FILM_ID FROM FILM_DIRECTOR WHERE DIRECTOR_ID = ?)\n" +
                    "group by FILMS.FILM_ID ORDER BY LIKES DESC";
            return jdbcTemplate.query(sqlQuery, this::mapRowToFilm, directorId);
        }
    }

    private Film mapRowToFilm(ResultSet resultSet, int rowNum) throws SQLException {
        return Film.builder()
                .id(resultSet.getInt("film_id"))
                .name(resultSet.getString("name"))
                .description(resultSet.getString("description"))
                .releaseDate(resultSet.getDate("release_date").toLocalDate())
                .duration(Duration.ofMillis(resultSet.getLong("duration")))
                .mpa(ratingStorage.placeRatingToFilmFromDB(resultSet.getInt("film_id")))
                .genres(genresStorage.placeGenresToFilmFromDB(resultSet.getInt("film_id")))
                .directors((directorStorage.placeDirectorsToFilmFromDB(resultSet.getInt("film_id"))))
                .build();
    }

    private Integer mapRowToInteger(ResultSet resultSet, int rowNum) throws SQLException {
        return resultSet.getInt("user_id");
    }
}