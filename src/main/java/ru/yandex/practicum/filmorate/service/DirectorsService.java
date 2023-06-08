package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.exceptions.DirectorNotFoundException;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.storage.dao.DirectorStorage;

import java.util.Collection;
import java.util.List;

@Service
@Slf4j
public class DirectorsService {

    private final DirectorStorage directorStorage;

    @Autowired
    public DirectorsService(DirectorStorage directorStorage) {
        this.directorStorage = directorStorage;
    }

    public Director saveNew(Director director) {
        int id = directorStorage.saveNew(director);
        log.info("Режиссер добавлен.");
        return findById(id);
    }

    public Director update(Director director) {
        if (!directorStorage.isExists(director.getId())) {
            log.info("Режиссер не найден.");
            throw new DirectorNotFoundException("Режиссер не найден.");
        } else {
            int id = directorStorage.update(director);
            log.info("Режиссер обновлен.");
            return findById(id);
        }
    }

    public Collection<Director> findAll() {
        log.info("Режиссеры найдены.");
        return directorStorage.findAll();
    }

    public Director findById(int id) {
        if (!directorStorage.isExists(id)) {
            log.info("Режиссер c ID " + id + " не найден.");
            throw new DirectorNotFoundException("Режиссер c ID " + id + " не найден.");
        } else {
            log.info("Режиссер найден.");
            return directorStorage.findById(id);
        }
    }

    public void removeById(int id) {
        if (!directorStorage.isExists(id)) {
            log.info("Режиссер c ID " + id + " не найден.");
            throw new DirectorNotFoundException("Режиссер c ID " + id + " не найден.");
        } else {
            log.info("Режиссер удален.");
            directorStorage.removeById(id);
        }
    }

    public boolean isExists(int directorId) {
        return directorStorage.isExists(directorId);
    }

    public List<Director> saveDirectorsToFilmFromDB(int filmId) {
        return directorStorage.saveDirectorsToFilmFromDB(filmId);
    }

    public void saveDirectorsToDBFromFilm(List<Director> directors, int filmId) {
        directorStorage.saveDirectorsToDBFromFilm(directors, filmId);
    }

    public void removeByFilmId(int filmId) {
        directorStorage.removeByFilmId(filmId);
    }
}
