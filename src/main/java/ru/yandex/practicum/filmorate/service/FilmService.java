package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exceptions.UserNotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.util.Collection;

@Service
@Slf4j
public class FilmService {

    @Autowired
    @Qualifier("FilmDbStorage")
    private FilmStorage filmStorage;

    @Autowired
    private UserService userService;

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

    public void addLike(int filmId, int userId) {
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
            filmStorage.addLike(filmId, userId);
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

    public Collection<Film> getPopular(int count) {
        if (count <= 0) {
            throw new ValidationException("Значение выводимых фильмов не может быть меньше или равно нулю.");
        } else {
            return filmStorage.findPopular(count);
        }
    }

    public Collection<Integer> gelKikes(int filmId) {
        return filmStorage.getLikes(filmId);
    }

    private boolean isExists(int filmId) {
        return filmStorage.isExists(filmId);
    }
}