package ru.yandex.practicum.filmorate.storage.dao;

import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.List;

public interface DirectorStorage {
    Director saveNew(Director director);
    Director update (Director director);
    Collection<Director> findAll();
    Director findById(int id);
    void removeById(int id);
    List<Director> placeDirectorsToFilmFromDB(int filmId);
    void addFilmDirectorsToDB(Film film, int filmId);
}