package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;


@Component
@Slf4j
@Qualifier("FilmDbStorage")
public class FilmDbStorage implements FilmStorage {
    @Override
    public Film addNewFilm(Film film) {
        return null;
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
