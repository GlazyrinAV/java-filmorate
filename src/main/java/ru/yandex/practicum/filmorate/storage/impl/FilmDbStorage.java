package ru.yandex.practicum.filmorate.storage.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exceptions.exceptions.FilmNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.SortType;
import ru.yandex.practicum.filmorate.storage.dao.FilmStorage;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
@Slf4j
@Qualifier("FilmDbStorage")
public class FilmDbStorage implements FilmStorage {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public Integer saveNew(Film film) {
        String sqlQuery = "INSERT INTO films (name, description, release_date, duration, MPA_ID) " +
                "VALUES (?, ?, ?, ?, ?)";

        KeyHolder keyHolder = new GeneratedKeyHolder();
        try {
            jdbcTemplate.update(connection -> {
                PreparedStatement stmt = connection.prepareStatement(sqlQuery, new String[]{"film_id"});
                stmt.setString(1, film.getName());
                stmt.setString(2, film.getDescription());
                stmt.setDate(3, Date.valueOf(film.getReleaseDate()));
                stmt.setLong(4, film.getDuration());
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
                "MPA_ID = ?" +
                "WHERE film_id = ?";
        try {
            jdbcTemplate.update(sqlQuery,
                    film.getName(),
                    film.getDescription(),
                    Date.valueOf(film.getReleaseDate()),
                    film.getDuration(),
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
        return jdbcTemplate.query(sqlQuery, this::mapRowToFilm, filmId).stream().findFirst()
                .orElseThrow(() -> new FilmNotFoundException("Фильм c ID " + filmId + " не найден."));
    }

    @Override
    public Collection<Film> findPopular(int count) {
        String sqlQuery =
                "SELECT F.FILM_ID, F.NAME, F.DESCRIPTION, F.RELEASE_DATE, F.DURATION " +
                        "FROM FILMS AS F " +
                        "LEFT JOIN FILM_SCORE AS FL ON F.FILM_ID = FL.FILM_ID " +
                        "GROUP BY F.FILM_ID ORDER BY COUNT(FL.FILM_ID) DESC LIMIT ?";
        return jdbcTemplate.query(sqlQuery, this::mapRowToFilm, count);
    }

    @Override
    public Collection<Film> findPopularByGenreAndYear(int count, int genreId, int year) {
        String sql = "SELECT F.FILM_ID, F.NAME, F.DESCRIPTION, F.RELEASE_DATE, F.DURATION\n" +
                "FROM FILMS AS F\n" +
                "LEFT JOIN FILM_GENRES AS FG ON F.FILM_ID = FG.FILM_ID\n" +
                "LEFT JOIN FILM_SCORE AS FL ON F.FILM_ID = FL.FILM_ID\n" +
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
                "LEFT JOIN FILM_SCORE AS FL ON F.FILM_ID = FL.FILM_ID\n" +
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
                "LEFT JOIN FILM_SCORE AS FL ON F.FILM_ID = FL.FILM_ID\n" +
                "WHERE EXTRACT(YEAR FROM CAST(F.RELEASE_DATE AS DATE)) = ?\n" +
                "GROUP BY F.FILM_ID\n" +
                "ORDER BY COUNT(FL.USER_ID) DESC\n" +
                "LIMIT ?";
        return jdbcTemplate.query(sql, this::mapRowToFilm, year, count);
    }

    @Override
    public void saveScore(int filmId, int userId, int score) {
        String sqlQuery = "MERGE INTO FILM_SCORE (film_id, user_id, SCORE) VALUES (?, ?, ?)";
        try {
            jdbcTemplate.update(sqlQuery, filmId, userId, score);
        } catch (DataIntegrityViolationException exception) {
            throw new DataIntegrityViolationException("В запросе неправильно указаны данные для добавдения лайка.");
        }
    }

    @Override
    public void removeScore(int filmId, int userId) {
        String sqlQuery = "DELETE FROM FILM_SCORE WHERE film_id = ? AND user_id = ?";
        jdbcTemplate.update(sqlQuery, filmId, userId);
    }

    @Override
    public Double findScore(int filmId) {
        String sqlQuery = "SELECT AVG(SCORE) FROM FILM_SCORE WHERE film_id = ?";
        return jdbcTemplate.queryForObject(sqlQuery, Double.class, filmId);
    }

    @Override
    public void removeFilm(int filmId) {
        String sqlQuery = "DELETE FROM FILMS WHERE film_id = ?";
        jdbcTemplate.update(sqlQuery, filmId);
    }

    @Override
    public Collection<Film> findByDirectorId(int directorId, SortType sortBy) {
        String sqlQuery;
        if (sortBy.equals(SortType.year)) {
            sqlQuery = "SELECT * FROM FILMS WHERE FILM_ID IN (SELECT FILM_ID FROM FILM_DIRECTOR WHERE DIRECTOR_ID = ?) " +
                    "ORDER BY EXTRACT(YEAR FROM RELEASE_DATE)";
        } else {
            sqlQuery = "SELECT FILMS.*, AVG(SCORE) AS LIKES FROM FILMS LEFT JOIN FILM_SCORE FL on FILMS.FILM_ID = FL.FILM_ID " +
                    "WHERE FILMS.FILM_ID IN (SELECT FILM_ID FROM FILM_DIRECTOR WHERE DIRECTOR_ID = ?)\n" +
                    "group by FILMS.FILM_ID ORDER BY LIKES DESC";
        }
        Integer resultCheck = jdbcTemplate.query(sqlQuery, (rs, rowNum) ->
                rs.getInt("FILM_ID"), directorId).stream().findFirst().orElse(null);
        if (resultCheck == null) {
            return new ArrayList<>();
        }

        return jdbcTemplate.query(sqlQuery, this::mapRowToFilm, directorId);
    }

    @Override
    public Collection<Film> findCommonFilms(int userId, int friendId) {
        String sqlQuery = "SELECT FILMS.FILM_ID, FILMS.NAME, FILMS.DESCRIPTION, FILMS.DURATION, FILMS.RELEASE_DATE, COUNT(FL.USER_ID) " +
                " FROM FILMS LEFT JOIN FILM_SCORE FL on FILMS.FILM_ID = FL.FILM_ID WHERE Films.FILM_ID in (select FILM_ID from  FILM_SCORE " +
                " where USER_ID = ? AND FILM_ID in (select FILM_ID from FILM_SCORE where USER_ID = ?))" +
                "GROUP BY FILMS.FILM_ID ORDER BY COUNT(FL.USER_ID) desc ";

        return jdbcTemplate.query(sqlQuery, this::mapRowToFilm, userId, friendId);
    }

    public Collection<Film> getRecommendation(int id) {

        String sqlQuery = "SELECT *" +
                " FROM FILM_SCORE as F  WHERE F.FILM_ID in (select FILM_ID from  FILM_SCORE where USER_ID = ?) and not F.USER_ID = ?" +
                "GROUP BY F.USER_ID ORDER BY COUNT(FILM_ID) desc LIMIT 1";

        Integer idRecommendationUser = jdbcTemplate.query(sqlQuery, (rs, rowNum) ->
                rs.getInt("USER_ID"), id, id).stream().findFirst().orElse(null);
        if (idRecommendationUser == null)
            return new ArrayList<>();

        String sqlQuery2 = "SELECT *" +
                " FROM FILMS LEFT JOIN FILM_SCORE FL on FILMS.FILM_ID = FL.FILM_ID WHERE Films.FILM_ID in (select FILM_ID from  FILM_SCORE " +
                " where USER_ID = ? AND FILM_ID not in (select FILM_ID from FILM_SCORE where USER_ID = ?))" +
                "GROUP BY FILMS.FILM_ID ";

        return jdbcTemplate.query(sqlQuery2, this::mapRowToFilm, idRecommendationUser, id);
    }

    @Override
    public Collection<Film> searchByTitle(String query) {
        String searchByTitle = "SELECT F.*, COUNT(FL.FILM_ID) AS LIKES_COUNT " +
                "FROM FILMS AS F " +
                "LEFT OUTER JOIN FILM_SCORE AS FL ON F.FILM_ID = FL.FILM_ID " +
                "WHERE UPPER(F.NAME) LIKE UPPER(CONCAT('%', ?, '%')) " +
                "GROUP BY F.FILM_ID " +
                "ORDER BY F.FILM_ID DESC";

        return jdbcTemplate.query(searchByTitle, this::mapRowToFilm, query);
    }

    @Override
    public Collection<Film> searchByDirector(String query) {
        String searchByDir = "SELECT F.*, COUNT(FL.FILM_ID) AS LIKES_COUNT " +
                "FROM FILMS F " +
                "LEFT OUTER JOIN FILM_DIRECTOR FD ON F.FILM_ID = FD.FILM_ID " +
                "LEFT OUTER JOIN DIRECTORS D ON FD.DIRECTOR_ID = D.DIRECTOR_ID " +
                "LEFT OUTER JOIN FILM_SCORE FL ON F.FILM_ID = FL.FILM_ID " +
                "WHERE UPPER(D.DIRECTOR_NAME) LIKE UPPER(CONCAT('%', ?, '%')) " +
                "GROUP BY F.FILM_ID " +
                "ORDER BY F.FILM_ID DESC";

        return jdbcTemplate.query(searchByDir, this::mapRowToFilm, query);
    }

    @Override
    public Collection<Film> searchByFilmAndDirector(String query) {
        String search = "SELECT F.*, D.DIRECTOR_NAME, COUNT(FL.FILM_ID) AS LIKES_COUNT " +
                "FROM FILMS AS F " +
                "LEFT OUTER JOIN FILM_DIRECTOR AS FD ON F.FILM_ID = FD.FILM_ID " +
                "LEFT OUTER JOIN DIRECTORS AS D ON FD.DIRECTOR_ID = D.DIRECTOR_ID " +
                "LEFT OUTER JOIN FILM_SCORE AS FL ON F.FILM_ID = FL.FILM_ID " +
                "WHERE UPPER(F.NAME) LIKE UPPER(CONCAT('%', ?, '%')) OR UPPER(D.DIRECTOR_NAME) LIKE UPPER(CONCAT('%', ?, '%')) " +
                "GROUP BY F.FILM_ID, D.DIRECTOR_NAME " +
                "ORDER BY F.FILM_ID DESC";

        return jdbcTemplate.query(search, this::mapRowToFilm, query, query);
    }

    private Film mapRowToFilm(ResultSet resultSet, int rowNum) throws SQLException {
        return Film.builder()
                .id(resultSet.getInt("film_id"))
                .name(resultSet.getString("name"))
                .description(resultSet.getString("description"))
                .releaseDate(resultSet.getDate("release_date").toLocalDate())
                .duration((resultSet.getLong("duration")))
                .score(findScore(resultSet.getInt("film_id")))
                .build();
    }
}