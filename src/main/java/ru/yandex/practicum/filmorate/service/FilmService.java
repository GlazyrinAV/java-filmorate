package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.InMemoryFilmStorage;

import java.util.Collection;

@Service
@Slf4j
public class FilmService {

    @Autowired
    private InMemoryFilmStorage filmStorage;

    @Autowired
    private UserService userService;

    public Film addNewFilm(Film film) {
        return filmStorage.addNewFilm(film);
    }

    public Film updateFilm(Film film) {
        return filmStorage.updateFilm(film);
    }

    public Collection<Film> findAllFilms()  {
        return filmStorage.findAllFilms();
    }

    public Film findFilm(int filmId) {
        return filmStorage.findFilm(filmId);
    }

    public void addLike(int filmId, int userId) {
        if (filmId <= 0) {
            log.info("Указанный ID фильма меньше или равен нулю.");
            throw new ValidationException("ID фильма не может быть меньше или равно нулю.");
        } else if (filmStorage.findFilm(filmId) == null) {
            log.info("Фильм c ID " + filmId + " не найден.");
            throw new FilmNotFoundException("Фильм c ID " + filmId + " не найден.");
        } else if (userId <= 0) {
            log.info("Указанный ID юзера меньше или равен нулю.");
            throw new ValidationException("ID юзера не может быть меньше или равно нулю.");
        } else if (userService.findUserById(userId) != null) {
            log.info("К фильму добавлен лайк.");
            filmStorage.addLike(filmId, userId);
        }
    }

    public void removeLike(int filmId, int userId) {
        if (filmId <= 0) {
            log.info("Указанный ID фильма меньше или равен нулю.");
            throw new ValidationException("ID фильма не может быть меньше или равно нулю.");
        } else if (filmStorage.findFilm(filmId) == null) {
            log.info("Фильм c ID " + filmId + " не найден.");
            throw new FilmNotFoundException("Фильм c ID " + filmId + " не найден.");
        } else if (userId <= 0) {
            log.info("Указанный ID юзера меньше или равен нулю.");
            throw new ValidationException("ID юзера не может быть меньше или равно нулю.");
        } else if (userService.findUserById(userId) != null) {
            log.info("У фильма удален лайк.");
            filmStorage.removeLike(filmId, userId);
        }
    }

    public Collection<Film> getPopularFilms(int count) {
        if (count <= 0) {
            throw new ValidationException("Значение выводимых фильмов не может быть меньше или равно нулю.");
        } else {
            return filmStorage.findPopular(count);
        }
    }
}