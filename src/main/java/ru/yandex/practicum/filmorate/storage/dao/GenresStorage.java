package ru.yandex.practicum.filmorate.storage.dao;

import ru.yandex.practicum.filmorate.model.Genre;

import java.util.Collection;
import java.util.List;

public interface GenresStorage {

    void save(List<Genre> genres, int filmId);

    void removeFilmGenres(int filmId);

    List<Genre> findByFilmId(int filmId);

    Collection<Genre> findAll();

    Genre findById(int genreId);
}