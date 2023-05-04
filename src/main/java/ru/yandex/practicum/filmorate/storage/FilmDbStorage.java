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
import java.util.Collection;
import java.util.List;
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
    public Film addNewFilm(Film film) {
        String sqlQuery = "INSERT INTO films (name, description, release_date, duration, rating_id) " +
                "VALUES (?, ?, ?, ?, ?)";

        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement stmt = connection.prepareStatement(sqlQuery, new String[]{"film_id"});
            stmt.setString(1, film.getName());
            stmt.setString(2, film.getDescription());
            stmt.setDate(3, Date.valueOf(film.getReleaseDate()));
            stmt.setLong(4, film.getDuration().toMinutes());
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
                "duration = ?" +
                "WHERE film_id = ?";
        jdbcTemplate.update(sqlQuery,
                film.getName(),
                film.getDescription(),
                Date.valueOf(film.getReleaseDate()),
                film.getDuration().toMinutes(),
                film.getId());
        return findFilm(film.getId());
    }

    @Override
    public Collection<Film> findAllFilms() {
        String sqlQuery = "SELECT * FROM films";
        return jdbcTemplate.query(sqlQuery, this::mapRowToFilm);
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
                "SELECT * FROM films " +
                        "WHERE film_id IN (" +
                        "SELECT COUNT(film_id) " +
                        "FROM film_likes " +
                        "GROUP BY film_id " +
                        "Limit ?)";
        return jdbcTemplate.query(sqlQuery, this::mapRowToFilm, count);
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

    private Film mapRowToFilm(ResultSet resultSet, int rowNum) throws SQLException {
        return Film.builder()
                .id(resultSet.getInt("film_id"))
                .name(resultSet.getString("name"))
                .description(resultSet.getString("description"))
                .releaseDate(resultSet.getDate("release_date").toLocalDate())
                .duration(Duration.ofMinutes(resultSet.getLong("duration")))
                .build();
    }

    private Genre mapRowToGenre(ResultSet resultSet, int rowNum) throws SQLException {
        return Genre.builder()
                .id(resultSet.getInt("genre_id"))
                .name(resultSet.getString("genre_name"))
                .build();
    }

    private void addFilmGenresToDB(Film film, int filmId) {
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

    private List<Genre> getGenresToFilmFromDB(int filmId) {
        String sqlQuery = "SELECT FG.GENRE_ID, G2.GENRE_NAME " +
                "FROM FILM_GENRES AS FG JOIN GENRES G2 on G2.GENRE_ID = FG.GENRE_ID WHERE FILM_ID = ?";
        return jdbcTemplate.query(sqlQuery, this::mapRowToGenre, filmId);
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