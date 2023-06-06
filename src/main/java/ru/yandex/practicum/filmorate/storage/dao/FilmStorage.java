package ru.yandex.practicum.filmorate.storage.dao;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;

public interface FilmStorage {

    Film addNew(Film film);

    Film update(Film film);

    Collection<Film> findAll();

    Film findById(int filmId);

    Collection<Film> findPopular(int count);

    void makeLike(int filmId, int userId);

    void removeLike(int filmId, int userId);

    Collection<Integer> findLikes(int filmId);

    Boolean isExists(int filmId);

    Collection<Film> findByDirectorId(int directorId, String sortBy);
}