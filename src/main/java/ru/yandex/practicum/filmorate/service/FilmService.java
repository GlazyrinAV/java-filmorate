package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.exceptions.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exceptions.exceptions.LikeNotFoundException;
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

    private final GenresService genresService;

    private final RatingsService ratingsService;

    @Autowired
    public FilmService(@Qualifier("FilmDbStorage") FilmStorage filmStorage, UserService userService, GenresService genresService, RatingsService ratingsService) {
        this.filmStorage = filmStorage;
        this.userService = userService;
        this.genresService = genresService;
        this.ratingsService = ratingsService;
    }

    public Film addNew(Film film) {
        int filmId = filmStorage.addNew(film);
        if (isGenresExists(film)) {
            genresService.addFilmGenresToDB(film.getGenres(), filmId);
        }
        return findById(filmId);
    }

    public Film update(Film film) {
        int filmId = filmStorage.update(film);
        genresService.clearFilmGenres(filmId);
        if (isGenresExists(film)) {
            genresService.addFilmGenresToDB(film.getGenres(), filmId);
        }
        return findById(filmId);
    }

    public Collection<Film> findAll() {
        Collection<Film> films = filmStorage.findAll();
        for (Film film : films) {
            film.setGenres(genresService.placeGenresToFilmFromDB(film.getId()));
            film.setMpa(ratingsService.placeRatingToFilmFromDB(film.getId()));
        }
        return films;
    }

    public Film findById(int filmId) {
        Film film = filmStorage.findById(filmId);
        film.setGenres(genresService.placeGenresToFilmFromDB(filmId));
        film.setMpa(ratingsService.placeRatingToFilmFromDB(filmId));
        return film;
    }

    public void makeLike(int filmId, int userId) {
        if (!isExists(filmId)) {
            log.info("Фильм c ID " + filmId + " не найден.");
            throw new FilmNotFoundException("Фильм c ID " + filmId + " не найден.");
        } else if (!userService.isExists(userId)) {
            log.info("Юзер c ID " + filmId + " не найден.");
            throw new UserNotFoundException("Юзер c ID " + userId + " не найден.");
        } else {
            log.info("К фильму добавлен лайк.");
            filmStorage.makeLike(filmId, userId);
        }
    }

    public void removeLike(int filmId, int userId) {
        if (!isExists(filmId)) {
            log.info("Фильм c ID " + filmId + " не найден.");
            throw new FilmNotFoundException("Фильм c ID " + filmId + " не найден.");
        } else if (!userService.isExists(userId)) {
            log.info("Юзер c ID " + userId + " не найден.");
            throw new UserNotFoundException("Юзер c ID " + userId + " не найден.");
        } else if (!filmStorage.findLikes(filmId).contains(userId)) {
            log.info("Юзер с ID " + userId + " не ставил лайк данному фильму.");
            throw new LikeNotFoundException("Юзер с ID " + userId + " не ставил лайк данному фильму.");
        } else {
            log.info("У фильма удален лайк.");
            filmStorage.removeLike(filmId, userId);
        }
    }

    public Collection<Film> findPopular(int count) {
        if (count <= 0) {
            throw new ValidationException("Значение выводимых фильмов не может быть меньше или равно нулю.");
        } else {
            Collection<Film> films = filmStorage.findPopular(count);
            for (Film film : films) {
                film.setGenres(genresService.placeGenresToFilmFromDB(film.getId()));
                film.setMpa(ratingsService.placeRatingToFilmFromDB(film.getId()));
            }
            return films;
        }
    }

    public Collection<Integer> findLikes(int filmId) {
        return filmStorage.findLikes(filmId);
    }

    public boolean isExists(int filmId) {
        return filmStorage.isExists(filmId);
    }

    private boolean isGenresExists(Film film) {
        return film.getGenres() != null;
    }
}