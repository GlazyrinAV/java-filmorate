package ru.yandex.practicum.filmorate.storage.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exceptions.exceptions.GenreNotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.dao.GenresStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class GenresDbStorage implements GenresStorage {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public void save(List<Genre> genres, int filmId) {
        String sqlQueryForGenres = "MERGE INTO film_genres (film_id, genre_id) VALUES (?, ?)";
        jdbcTemplate.batchUpdate(sqlQueryForGenres, genres, genres.size(), (ps, genre) -> {
            ps.setInt(1, filmId);
            ps.setInt(2, genre.getId());
        });
    }

    @Override
    public void removeFilmGenres(int filmId) {
        String sqlQuery = "DELETE FROM film_genres WHERE film_id = ?";
        jdbcTemplate.update(sqlQuery, filmId);
    }

    @Override
    public List<Genre> findByFilmId(int filmId) {
        String sqlQuery = "SELECT FG.GENRE_ID, G2.GENRE_NAME " +
                "FROM FILM_GENRES AS FG JOIN GENRES G2 on G2.GENRE_ID = FG.GENRE_ID WHERE FILM_ID = ?";
        return jdbcTemplate.query(sqlQuery, this::mapRowToGenre, filmId);
    }

    @Override
    public Collection<Genre> findAll() {
        String sqlQuery = "SELECT * FROM GENRES";
        return jdbcTemplate.query(sqlQuery, this::mapRowToGenre);
    }

    @Override
    public Genre findById(int genreId) {
        String sqlQuery = "SELECT * FROM GENRES WHERE genre_id = ?";
        return jdbcTemplate.query(sqlQuery, this::mapRowToGenre, genreId).stream().findFirst()
                .orElseThrow(() -> new GenreNotFoundException("Жанр с ID " + genreId + " не найден."));
    }

    private Genre mapRowToGenre(ResultSet resultSet, int rowNum) throws SQLException {
        return Genre.builder()
                .id(resultSet.getInt("genre_id"))
                .name(resultSet.getString("genre_name"))
                .build();
    }
}