package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;

public interface FilmStorage {

    Film addNewFilm(Film film);

    Film updateFilm(Film film);

    Collection<Film> findAllFilms();

    Film findFilm(int filmId);
}