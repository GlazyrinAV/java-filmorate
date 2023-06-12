package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.exceptions.DirectorNotFoundException;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.storage.dao.DirectorStorage;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

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
        findById(director.getId());
        int id = directorStorage.update(director);
        log.info("Режиссер обновлен.");
        return findById(id);
    }

    public Collection<Director> findAll() {
        log.info("Режиссеры найдены.");
        return directorStorage.findAll();
    }

    public Director findById(int id) {
        Director director;
        try {
            director = directorStorage.findById(id);
        } catch (EmptyResultDataAccessException exception) {
            throw new DirectorNotFoundException("Режиссер c ID " + id + " не найден.");
        }
        log.info("Режиссер найден.");
        return director;
    }

    public void removeById(int id) {
        findById(id);
        log.info("Режиссер удален.");
        directorStorage.removeById(id);
    }

    public List<Director> saveDirectorsToFilmFromDB(int filmId) {
        return directorStorage.saveDirectorsToFilmFromDB(filmId);
    }

    public void saveDirectorsToDBFromFilm(Optional<List<Director>> directors, int filmId) {
        directors.ifPresent(directorList -> directorStorage.saveDirectorsToDBFromFilm(directorList, filmId));
    }

    public void removeFromFilmByFilmId(int filmId) {
        directorStorage.removeFromFilmByFilmID(filmId);
    }
}