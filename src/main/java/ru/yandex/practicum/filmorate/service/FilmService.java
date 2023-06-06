package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.exceptions.DirectorNotFoundException;
import ru.yandex.practicum.filmorate.exceptions.exceptions.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exceptions.exceptions.UserNotFoundException;
import ru.yandex.practicum.filmorate.exceptions.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.dao.DirectorStorage;
import ru.yandex.practicum.filmorate.storage.dao.FilmStorage;

import java.util.Collection;

@Service
@Slf4j
public class FilmService {

    private final FilmStorage filmStorage;
    private final DirectorStorage directorStorage;
    private final UserService userService;

    @Autowired
    public FilmService(@Qualifier("FilmDbStorage") FilmStorage filmStorage, DirectorStorage directorStorage, UserService userService) {
        this.filmStorage = filmStorage;
        this.directorStorage = directorStorage;
        this.userService = userService;
    }

    public Film addNew(Film film) {
        log.info("Фильм добавлен.");
        return filmStorage.addNew(film);
    }

    public Film update(Film film) {
        log.info("Фильм обновлен.");
        return filmStorage.update(film);
    }

    public Collection<Film> findAll()  {
        log.info("Фильмы найдены.");
        return filmStorage.findAll();
    }

    public Film findById(int filmId) {
        log.info("Фильм найден.");
        return filmStorage.findById(filmId);
    }

    public void makeLike(int filmId, int userId) {
        if (filmId <= 0) {
            log.info("Указанный ID фильма меньше или равен нулю.");
            throw new ValidationException("ID фильма не может быть меньше или равно нулю.");
        } else if (!isExists(filmId)) {
            log.info("Фильм c ID " + filmId + " не найден.");
            throw new FilmNotFoundException("Фильм c ID " + filmId + " не найден.");
        } else if (userId <= 0) {
            log.info("Указанный ID юзера меньше или равен нулю.");
            throw new ValidationException("ID юзера не может быть меньше или равно нулю.");
        } else if (!userService.isExists(userId)) {
            log.info("Фильм c ID " + filmId + " не найден.");
            throw new UserNotFoundException("Юзер c ID " + userId + " не найден.");
        } else {
            log.info("К фильму добавлен лайк.");
            filmStorage.makeLike(filmId, userId);
        }
    }

    public void removeLike(int filmId, int userId) {
        if (filmId <= 0) {
            log.info("Указанный ID фильма меньше или равен нулю.");
            throw new ValidationException("ID фильма не может быть меньше или равно нулю.");
        } else if (!isExists(filmId)) {
            log.info("Фильм c ID " + filmId + " не найден.");
            throw new FilmNotFoundException("Фильм c ID " + filmId + " не найден.");
        } else if (userId <= 0) {
            log.info("Указанный ID юзера меньше или равен нулю.");
            throw new ValidationException("ID юзера не может быть меньше или равно нулю.");
        } else if (!userService.isExists(userId)) {
            log.info("Фильм c ID " + filmId + " не найден.");
            throw new UserNotFoundException("Юзер c ID " + userId + " не найден.");
        } else {
            log.info("У фильма удален лайк.");
            filmStorage.removeLike(filmId, userId);
        }
    }

    public Collection<Film> findPopular(int count) {
        if (count <= 0) {
            log.info("Count меньше или равен нулю.");
            throw new ValidationException("Значение выводимых фильмов не может быть меньше или равно нулю.");
        } else {
            log.info("Популярные фильмы найдены.");
            return filmStorage.findPopular(count);
        }
    }

    public Collection<Integer> findLikes(int filmId) {
        log.info("Лайки к фильму найдены.");
        return filmStorage.findLikes(filmId);
    }

    public Collection<Film> findByDirectorId(int directorId, String sortBy) {
        if (directorStorage.isExists(directorId) && (sortBy.equals("year") || sortBy.equals("likes"))) {
            log.info("Фильмы по указанному режиссеру найдены.");
            return filmStorage.findByDirectorId(directorId, sortBy);
        } else if (!directorStorage.isExists(directorId) && (sortBy.equals("year") || sortBy.equals("likes"))) {
            log.info("Режиссер c ID " + directorId + " не найден.");
            throw new DirectorNotFoundException("Режиссер c ID " + directorId + " не найден.");
        } else {
            log.info("Недопустимый параметр запроса.");
            throw new ValidationException ("Недопустимый параметр запроса.");
        }
    }

    private boolean isExists(int filmId) {
        return filmStorage.isExists(filmId);
    }
}