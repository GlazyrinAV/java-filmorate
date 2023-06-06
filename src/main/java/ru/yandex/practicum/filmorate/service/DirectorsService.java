package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.storage.dao.DirectorStorage;

import java.util.Collection;

@Service
@Slf4j
public class DirectorsService {

    private final DirectorStorage directorStorage;

    @Autowired
    public DirectorsService(DirectorStorage directorStorage) {
        this.directorStorage = directorStorage;
    }

    public Director saveNew(Director director) {
        return directorStorage.saveNew(director);
    }

    public Director update(Director director) {
        return directorStorage.update(director);
    }

    public Collection<Director> findAll() {
        return directorStorage.findAll();
    }

    public Director findById(int id) {
        return directorStorage.findById(id);
    }

    public void removeById(int id) {
        directorStorage.removeById(id);
    }
}
