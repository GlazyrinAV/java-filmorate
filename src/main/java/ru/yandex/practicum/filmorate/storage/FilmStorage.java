package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;

public interface FilmStorage {

    Film addNewFilm(Film film);

    Film updateFilm(Film film);

    Collection<Film> findAllFilms();

    Film findFilm(int filmId);

    Collection<Film> findPopular(int count);

    void addLike(int filmId, int userId);

    void removeLike(int filmId, int userId);
}