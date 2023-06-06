package ru.yandex.practicum.filmorate.storage.dao;

import ru.yandex.practicum.filmorate.model.Director;

import java.util.Collection;

public interface DirectorStorage {
    Director saveNew(Director director);
    Director update (Director director);
    Collection<Director> findAll();
    Director findById(int id);
    void removeById(int id);
}