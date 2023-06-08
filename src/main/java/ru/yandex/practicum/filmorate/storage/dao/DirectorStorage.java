package ru.yandex.practicum.filmorate.storage.dao;

import ru.yandex.practicum.filmorate.model.Director;

import java.util.Collection;
import java.util.List;

public interface DirectorStorage {
    Integer saveNew(Director director);

    Integer update(Director director);

    Collection<Director> findAll();

    Director findById(int id);

    void removeById(int id);

    List<Director> saveDirectorsToFilmFromDB(int filmId);

    void saveDirectorsToDBFromFilm(List<Director> directors, int filmId);

    void removeByFilmId(int filmId);

    Boolean isExists(int directorId);
}