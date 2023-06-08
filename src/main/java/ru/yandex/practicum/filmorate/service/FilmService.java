package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.exceptions.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exceptions.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.dao.FilmStorage;

import java.util.Collection;

@Service
@Slf4j
public class FilmService {

    private final FilmStorage filmStorage;
    private final UserService userService;
    private final GenresService genresService;
    private final RatingsService ratingsService;

    @Autowired
    public FilmService(@Qualifier("FilmDbStorage") FilmStorage filmStorage, UserService userService, GenresService genresService, RatingsService ratingsService) {
        this.filmStorage = filmStorage;
        this.userService = userService;
        this.genresService = genresService;
        this.ratingsService = ratingsService;
    }

    public Film saveNew(Film film) {
        int filmId = filmStorage.saveNew(film);
        saveAdditionalInfoFromFilm(film, filmId);
        return findById(filmId);
    }

    public Film update(Film film) {
        int filmId = filmStorage.update(film);
        genresService.removeFilmGenres(filmId);
        saveAdditionalInfoFromFilm(film, filmId);
        return findById(filmId);
    }

    public Collection<Film> findAll() {
        Collection<Film> films = filmStorage.findAll();
        for (Film film : films) {
            saveAdditionalInfoToFilm(film);
        }
        return films;
    }

    public Film findById(int filmId) {
        Film film;
        try {
            film = filmStorage.findById(filmId);
        } catch (EmptyResultDataAccessException exception) {
            throw new FilmNotFoundException("Фильм c ID " + filmId + " не найден.");
        }
        saveAdditionalInfoToFilm(film);
        return film;
    }

    public void makeLike(int filmId, int userId) {
        userService.findById(userId);
        findById(filmId);
        log.info("К фильму добавлен лайк.");
        filmStorage.makeLike(filmId, userId);
    }

    public void removeLike(int filmId, int userId) {
        userService.findById(userId);
        findById(filmId);
        log.info("У фильма удален лайк.");
        filmStorage.removeLike(filmId, userId);
    }

    public Collection<Film> findPopular(int count) {
        if (count <= 0) {
            throw new ValidationException("Значение выводимых фильмов не может быть меньше или равно нулю.");
        } else {
            Collection<Film> films = filmStorage.findPopular(count);
            for (Film film : films) {
                saveAdditionalInfoToFilm(film);
            }
            log.info("Популярные фильмы найдены.");
            return films;
        }
    }

    public Collection<Integer> findLikes(int filmId) {
        return filmStorage.findLikes(filmId);
    }

    private boolean isGenresExists(Film film) {
        return film.getGenres() != null;
    }

    private void saveAdditionalInfoToFilm(Film film) {
        film.setGenres(genresService.saveGenresToFilmFromDB(film.getId()));
        film.setMpa(ratingsService.saveRatingToFilmFromDB(film.getId()));
    }

    private void saveAdditionalInfoFromFilm(Film film, int filmId) {
        if (isGenresExists(film)) {
            genresService.saveGenresToDBFromFilm(film.getGenres(), filmId);
        }
    }

    public void removeFilm(int filmId) {
        if (filmId <= 0) {
            log.info("Указанный ID фильма меньше или равен нулю.");
            throw new ValidationException("ID фильма не может быть меньше или равно нулю.");
        } else if (!isExists(filmId)) {
            log.info("Фильм c ID " + filmId + " не найден.");
            throw new FilmNotFoundException("Фильм c ID " + filmId + " не найден.");
        } else {
            log.info("Фильм удален.");
            filmStorage.removeFilm(filmId);
        }
    }
}