package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Rating;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Duration;
import java.util.*;

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
    public Film addNewFilm(Film film) {
        String sqlQuery = "INSERT INTO films (name, description, release_date, duration, rating_id) " +
                "VALUES (?, ?, ?, ?, ?)";

        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement stmt = connection.prepareStatement(sqlQuery, new String[]{"film_id"});
            stmt.setString(1, film.getName());
            stmt.setString(2, film.getDescription());
            stmt.setDate(3, Date.valueOf(film.getReleaseDate()));
            stmt.setLong(4, film.getDuration().toMillis());
            stmt.setInt(5, film.getMpa().getId());
            return stmt;
        }, keyHolder);

        Optional<Integer> filmId = Optional.of(Objects.requireNonNull(keyHolder.getKey()).intValue());
        addFilmGenresToDB(film, filmId.get());

        return findFilm(filmId.get());
    }

    @Override
    public Film updateFilm(Film film) {
        String sqlQuery = "UPDATE films SET " +
                "name = ?," +
                "description = ?," +
                "release_date = ?," +
                "duration = ?," +
                "rating_id = ?" +
                "WHERE film_id = ?";
        jdbcTemplate.update(sqlQuery,
                film.getName(),
                film.getDescription(),
                Date.valueOf(film.getReleaseDate()),
                film.getDuration().toMillis(),
                film.getMpa().getId(),
                film.getId());
        addFilmGenresToDB(film, film.getId());
        return findFilm(film.getId());
    }

    @Override
    public Collection<Film> findAllFilms() {
        String sqlQuery = "SELECT * FROM films";
        Collection<Film> films = jdbcTemplate.query(sqlQuery, this::mapRowToFilm);
        for (Film film : films) {
            film.setGenres(getGenresToFilmFromDB(film.getId()));
            film.setMpa(getRatingToFilmFromDB(film.getId()));
        }
        return films;
    }

    @Override
    public Film findFilm(int filmId) {
        String sqlQuery = "SELECT * FROM films where film_id = ?";
        Film film = jdbcTemplate.queryForObject(sqlQuery, this::mapRowToFilm, filmId);
        film.setGenres(getGenresToFilmFromDB(filmId));
        film.setMpa(getRatingToFilmFromDB(filmId));
        return film;
    }

    @Override
    public Collection<Film> findPopular(int count) {
        String sqlQuery =
                "SELECT * FROM FILMS LEFT JOIN FILM_LIKES FL on FILMS.FILM_ID = FL.FILM_ID " +
                        "GROUP BY FILMS.FILM_ID ORDER BY COUNT(FL.USER_ID) DESC LIMIT ?";
        Collection<Film> films = jdbcTemplate.query(sqlQuery, this::mapRowToFilm, count);
        for (Film film : films) {
            film.setGenres(getGenresToFilmFromDB(film.getId()));
            film.setMpa(getRatingToFilmFromDB(film.getId()));
        }
        return films;
    }

    @Override
    public void addLike(int filmId, int userId) {
        String sqlQuery = "INSERT INTO film_likes (film_id, user_id) VALUES (?, ?)";
        jdbcTemplate.update(sqlQuery, filmId, userId);
    }

    @Override
    public void removeLike(int filmId, int userId) {
        String sqlQuery = "DELETE FROM film_likes WHERE film_id = ? AND user_id = ?";
        jdbcTemplate.update(sqlQuery, filmId, userId);
    }

    @Override
    public List<Rating> findAllFilmRatings() {
        String sqlQuery = "SELECT * FROM RATINGS";
        return jdbcTemplate.query(sqlQuery, this::mapRowToRating);
    }

    @Override
    public Rating findRatingById(int ratingId) {
        String sqlQuery = "SELECT * FROM RATINGS WHERE rating_id = ?";
        return jdbcTemplate.queryForObject(sqlQuery, this::mapRowToRating, ratingId);
    }

    @Override
    public Collection<Genre> findAllGenres() {
        String sqlQuery = "SELECT * FROM GENRES";
        return jdbcTemplate.query(sqlQuery, this::mapRowToGenre);
    }

    @Override
    public Genre findGenreById(int genreId) {
        String sqlQuery = "SELECT * FROM GENRES WHERE genre_id = ?";
        return jdbcTemplate.queryForObject(sqlQuery, this::mapRowToGenre, genreId);
    }

    private Film mapRowToFilm(ResultSet resultSet, int rowNum) throws SQLException {
        return Film.builder()
                .id(resultSet.getInt("film_id"))
                .name(resultSet.getString("name"))
                .description(resultSet.getString("description"))
                .releaseDate(resultSet.getDate("release_date").toLocalDate())
                .duration(Duration.ofMillis(resultSet.getLong("duration")))
                .build();
    }

    private Genre mapRowToGenre(ResultSet resultSet, int rowNum) throws SQLException {
        return Genre.builder()
                .id(resultSet.getInt("genre_id"))
                .name(resultSet.getString("genre_name"))
                .build();
    }

    private void addFilmGenresToDB(Film film, int filmId) {
        clearFilmGenres(filmId);

        String sqlQueryForGenres = "INSERT INTO film_genres (film_id, genre_id) VALUES (?, ?)";
        if (film.getGenres() != null) {
            jdbcTemplate.batchUpdate(sqlQueryForGenres, new BatchPreparedStatementSetter() {
                @Override
                public void setValues(PreparedStatement ps, int i) throws SQLException {
                    Genre genre = film.getGenres().get(i);
                    ps.setInt(1, filmId);
                    ps.setInt(2, genre.getId());
                }

                @Override
                public int getBatchSize() {
                    return film.getGenres().size();
                }
            });
        }
    }

    private void clearFilmGenres(int filmId) {
        String sqlQuery = "DELETE FROM film_genres WHERE film_id = ?";
        jdbcTemplate.update(sqlQuery, filmId);

    }

    private List<Genre> getGenresToFilmFromDB(int filmId) {
        String sqlQuery = "SELECT FG.GENRE_ID, G2.GENRE_NAME " +
                "FROM FILM_GENRES AS FG JOIN GENRES G2 on G2.GENRE_ID = FG.GENRE_ID WHERE FILM_ID = ?";
        if (!jdbcTemplate.query(sqlQuery, this::mapRowToGenre, filmId).isEmpty()) {
            return jdbcTemplate.query(sqlQuery, this::mapRowToGenre, filmId);
        } else {
            return new ArrayList<>();
        }
    }

    private Rating getRatingToFilmFromDB(int filmId) {
        String sqlQuery = "SELECT R.RATING_ID, R.RATING_NAME FROM RATINGS AS R " +
                "JOIN FILMS F ON R.RATING_ID = F.RATING_ID WHERE F.FILM_ID IN (?)";
        return jdbcTemplate.queryForObject(sqlQuery, this::mapRowToRating, filmId);
    }

    private Rating mapRowToRating(ResultSet resultSet, int rowNum) throws SQLException {
        return Rating.builder()
                .id(resultSet.getInt("rating_id"))
                .name(resultSet.getString("rating_name"))
                .build();
    }
}