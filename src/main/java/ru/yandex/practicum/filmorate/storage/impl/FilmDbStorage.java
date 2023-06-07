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
import ru.yandex.practicum.filmorate.storage.dao.FilmStorage;

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

    @Autowired
    public FilmDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Integer addNew(Film film) {
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

        return filmId.get();
    }

    @Override
    public Integer update(Film film) {
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
        return film.getId();
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
                "SELECT F.FILM_ID, F.NAME, F.DESCRIPTION, F.RELEASE_DATE, F.DURATION " +
                        "FROM FILMS AS F " +
                        "LEFT JOIN FILM_LIKES AS FL ON F.FILM_ID = FL.FILM_ID " +
                        "GROUP BY F.FILM_ID ORDER BY COUNT(FL.FILM_ID) DESC LIMIT ?";
        return jdbcTemplate.query(sqlQuery, this::mapRowToFilm, count);
    }

    @Override
    public Collection<Film> findPopularByGenreAndYear(int count, int genreId, int year) {
        String sql = "SELECT F.FILM_ID, F.NAME, F.DESCRIPTION, F.RELEASE_DATE, F.DURATION\n" +
                "FROM FILMS AS F\n" +
                "LEFT JOIN FILM_GENRES AS FG ON F.FILM_ID = FG.FILM_ID\n" +
                "LEFT JOIN FILM_LIKES AS FL ON F.FILM_ID = FL.FILM_ID\n" +
                "WHERE FG.GENRE_ID = ?\n" +
                "AND EXTRACT(YEAR FROM CAST(F.RELEASE_DATE AS DATE)) = ?\n" +
                "GROUP BY F.FILM_ID\n" +
                "ORDER BY COUNT(FL.USER_ID) DESC\n" +
                "LIMIT ?";
        return jdbcTemplate.query(sql, this::mapRowToFilm, genreId, year, count);
    }

    @Override
    public Collection<Film> findPopularByGenre(int count, int genreId) {
        String sql = "SELECT F.FILM_ID, F.NAME, F.DESCRIPTION, F.RELEASE_DATE, F.DURATION\n" +
                "FROM FILMS AS F\n" +
                "LEFT JOIN FILM_GENRES AS FG ON F.FILM_ID = FG.FILM_ID\n" +
                "LEFT JOIN FILM_LIKES AS FL ON F.FILM_ID = FL.FILM_ID\n" +
                "WHERE FG.GENRE_ID = ?\n" +
                "GROUP BY F.FILM_ID\n" +
                "ORDER BY COUNT(FL.USER_ID) DESC\n" +
                "LIMIT ?";
        return jdbcTemplate.query(sql, this::mapRowToFilm, genreId, count);
    }

    @Override
    public Collection<Film> findPopularByYear(int count, int year) {
        String sql = "SELECT F.FILM_ID, F.NAME, F.DESCRIPTION, F.RELEASE_DATE, F.DURATION\n" +
                "FROM FILMS AS F\n" +
                "LEFT JOIN FILM_GENRES AS FG ON F.FILM_ID = FG.FILM_ID\n" +
                "LEFT JOIN FILM_LIKES AS FL ON F.FILM_ID = FL.FILM_ID\n" +
                "WHERE EXTRACT(YEAR FROM CAST(F.RELEASE_DATE AS DATE)) = ?\n" +
                "GROUP BY F.FILM_ID\n" +
                "ORDER BY COUNT(FL.USER_ID) DESC\n" +
                "LIMIT ?";
        return jdbcTemplate.query(sql, this::mapRowToFilm, year, count);
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
        return jdbcTemplate.query(sqlQuery, this::mapRowToUserId, filmId);
    }

    @Override
    public Boolean isExists(int filmId) {
        String sqlQuery = "SELECT EXISTS ( SELECT * FROM PUBLIC.films WHERE film_id =? )";
        return Boolean.TRUE.equals(jdbcTemplate.queryForObject(sqlQuery, Boolean.TYPE, filmId));
    }

    private Film mapRowToFilm(ResultSet resultSet, int rowNum) throws SQLException {
        return Film.builder()
                .id(resultSet.getInt("films.film_id"))
                .name(resultSet.getString("films.name"))
                .description(resultSet.getString("films.description"))
                .releaseDate(resultSet.getDate("films.release_date").toLocalDate())
                .duration(Duration.ofMillis(resultSet.getLong("films.duration")))
                .build();
    }

    private Integer mapRowToUserId(ResultSet resultSet, int rowNum) throws SQLException {
        return resultSet.getInt("user_id");
    }
}