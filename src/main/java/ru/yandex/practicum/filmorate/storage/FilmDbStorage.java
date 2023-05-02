package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;


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
        String sqlQuery = "INSERT INTO films (name, description, release_date, duration) " +
                "values (?, ?, ?, ?, ?)";
        jdbcTemplate.update(sqlQuery, film.getName(), film.getDescription(), film.getReleaseDate(), film.getDuration());
        return film;
    }

    @Override
    public Film updateFilm(Film film) {
        return null;
    }

    @Override
    public Collection<Film> findAllFilms() {
        return null;
    }

    @Override
    public Film findFilm(int filmId) {
        return null;
    }

    @Override
    public Collection<Film> findPopular(int count) {
        return null;
    }

    @Override
    public void addLike(int filmId, int userId) {

    }

    @Override
    public void removeLike(int filmId, int userId) {

    }
}
