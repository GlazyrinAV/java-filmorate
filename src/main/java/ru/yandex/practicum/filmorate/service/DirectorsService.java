package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.exceptions.DirectorNotFoundException;
import ru.yandex.practicum.filmorate.exceptions.exceptions.ValidationException;
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
        log.info("Режиссер добавлен.");
        return directorStorage.saveNew(director);
    }

    public Director update(Director director) {
        log.info("Режиссер обновлен.");
        return directorStorage.update(director);
    }

    public Collection<Director> findAll() {
        log.info("Режиссеры найдены.");
        return directorStorage.findAll();
    }

    public Director findById(int id) {
        if (id <= 0) {
            log.info("Указанный ID режиссера меньше или равен нулю.");
            throw new ValidationException("ID режиссера не может быть меньше или равно нулю.");
        } else if (!directorStorage.isExists(id)) {
            log.info("Режиссер c ID " + id + " не найден.");
            throw new DirectorNotFoundException("Режиссер c ID " + id + " не найден.");
        } else {
            log.info("Режиссер найден.");
            return directorStorage.findById(id);
        }
    }

    public void removeById(int id) {
        if (id <= 0) {
            log.info("Указанный ID режиссера меньше или равен нулю.");
            throw new ValidationException("ID режиссера не может быть меньше или равно нулю.");
        } else if (!directorStorage.isExists(id)) {
            log.info("Режиссер c ID " + id + " не найден.");
            throw new DirectorNotFoundException("Режиссер c ID " + id + " не найден.");
        } else {
            log.info("Режиссер удален.");
            directorStorage.removeById(id);
        }
    }
}
