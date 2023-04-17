package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.InMemoryFilmStorage;
import ru.yandex.practicum.filmorate.storage.InMemoryUserStorage;

import java.util.Collection;
import java.util.stream.Collectors;

@Service
@Slf4j
public class FilmService {

    @Autowired
    InMemoryFilmStorage filmStorage;

    @Autowired
    InMemoryUserStorage userStorage;

    public void addLike(int filmId, int userId) {
        if (filmId <= 0) {
            log.info("Указанный ID фильма меньше или равен нулю.");
            throw new ValidationException("ID фильма не может быть меньше или равно нулю.");
        } else if (filmStorage.findFilm(filmId) == null) {
            log.info("Фильм c ID " + filmId + " не найден.");
            throw new FilmNotFoundException("Фильм c ID " + filmId + " не найден.");
        } if (userId <= 0) {
            log.info("Указанный ID юзера меньше или равен нулю.");
            throw new ValidationException("ID юзера не может быть меньше или равно нулю.");
        } else if (userStorage.findUser(userId) == null) {
            log.info("Фильм c ID " + filmId + " не найден.");
            throw new FilmNotFoundException("Фильм c ID " + filmId + " не найден.");
        } else {
            log.info("К фильму добавлен лайк.");
            filmStorage.findFilm(filmId).getLiked().add(userId);
        }
    }

    public void removeLike(int filmId, int userId) {
        if (filmId <= 0) {
            log.info("Указанный ID фильма меньше или равен нулю.");
            throw new ValidationException("ID фильма не может быть меньше или равно нулю.");
        } else if (filmStorage.findFilm(filmId) == null) {
            log.info("Фильм c ID " + filmId + " не найден.");
            throw new FilmNotFoundException("Фильм c ID " + filmId + " не найден.");
        } if (userId <= 0) {
            log.info("Указанный ID юзера меньше или равен нулю.");
            throw new ValidationException("ID юзера не может быть меньше или равно нулю.");
        } else if (userStorage.findUser(userId) == null) {
            log.info("Фильм c ID " + filmId + " не найден.");
            throw new FilmNotFoundException("Фильм c ID " + filmId + " не найден.");
        } else {
            log.info("У фильма удален лайк.");
            filmStorage.findFilm(filmId).getLiked().remove(userId);
        }
    }

    public Collection<Film> getPopularFilms(int count) {
        if (count <= 0) {
            throw new ValidationException("Значение выводимых фильмов не может быть меньше или равно нулю.");
        } else {
            return filmStorage.findAllFilms().stream()
                    .sorted(this::compare)
                    .limit(count)
                    .collect(Collectors.toList());
        }
    }

    private int compare(Film p0, Film p1) {
        return p1.getLiked().size() - (p0.getLiked().size());
    }
}