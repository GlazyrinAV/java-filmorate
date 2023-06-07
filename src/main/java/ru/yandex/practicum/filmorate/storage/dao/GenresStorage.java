package ru.yandex.practicum.filmorate.storage.dao;

import ru.yandex.practicum.filmorate.model.Genre;

import java.util.Collection;
import java.util.List;

public interface GenresStorage {

    void addFilmGenresToDB(List<Genre> genres, int filmId);

    void clearFilmGenres(int filmId);

    List<Genre> placeGenresToFilmFromDB(int filmId);

    Collection<Genre> findAll();

    Genre findById(int genreId);
}