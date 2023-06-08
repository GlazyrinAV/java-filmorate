package ru.yandex.practicum.filmorate.storage.dao;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;

public interface FilmStorage {

    Integer saveNew(Film film);

    Integer update(Film film);

    Collection<Film> findAll();

    Film findById(int filmId);

    Collection<Film> findPopular(int count);

    void makeLike(int filmId, int userId);

    void removeLike(int filmId, int userId);

    Collection<Integer> findLikes(int filmId);

    void removeFilm(int filmId);

    Collection<Film> findByDirectorId(int directorId, String sortBy);

    //  Boolean isExists(int filmId);

    Collection<Film> findCommonFilms(int userId, int friendId);

}