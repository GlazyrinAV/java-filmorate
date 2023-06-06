package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.exceptions.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exceptions.exceptions.UserNotFoundException;
import ru.yandex.practicum.filmorate.exceptions.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.dao.FilmStorage;

import java.util.Collection;

@Service
@Slf4j
public class FilmService {

    private final FilmStorage filmStorage;
    private final UserService userService;

    @Autowired
    public FilmService(@Qualifier("FilmDbStorage") FilmStorage filmStorage, UserService userService) {
        this.filmStorage = filmStorage;
        this.userService = userService;
    }

    public Film addNew(Film film) {
        return filmStorage.addNew(film);
    }

    public Film update(Film film) {
        return filmStorage.update(film);
    }

    public Collection<Film> findAll()  {
        return filmStorage.findAll();
    }

    public Film findById(int filmId) {
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
            throw new ValidationException("Значение выводимых фильмов не может быть меньше или равно нулю.");
        } else {
            return filmStorage.findPopular(count);
        }
    }

    public Collection<Integer> findLikes(int filmId) {
        return filmStorage.findLikes(filmId);
    }

    public Collection<Film> findByDirectorId(int directorId, String sortBy) {
        return filmStorage.findByDirectorId(directorId, sortBy);
    }

    private boolean isExists(int filmId) {
        return filmStorage.isExists(filmId);
    }
}