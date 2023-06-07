package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.exceptions.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.dao.FilmStorage;

import java.util.Collection;

@Service
@Slf4j
public class FilmService {

    private final FilmStorage filmStorage;
    private final DirectorsService directorsService;
    private final UserService userService;
    private final GenresService genresService;
    private final RatingsService ratingsService;

    @Autowired
    public FilmService(@Qualifier("FilmDbStorage") FilmStorage filmStorage, UserService userService,
                       GenresService genresService, RatingsService ratingsService, DirectorsService directorsService) {
        this.filmStorage = filmStorage;
        this.directorsService = directorsService;
        this.userService = userService;
        this.genresService = genresService;
        this.ratingsService = ratingsService;
    }

    public Film addNew(Film film) {
        int filmId = filmStorage.addNew(film);
        if (isGenresExists(film)) {
            genresService.addFilmGenresToDB(film.getGenres(), filmId);
        }
        if (isDirectorsExists(film)) {
            directorsService.saveFilmDirectorsToDB(film.getDirectors(), filmId);
        }
        return findById(filmId);
    }

    public Film update(Film film) {
        int filmId = filmStorage.update(film);
        genresService.clearFilmGenres(filmId);
        directorsService.removeByFilmId(filmId);
        if (isGenresExists(film)) {
            genresService.addFilmGenresToDB(film.getGenres(), filmId);
        }
        if (isDirectorsExists(film)) {
            directorsService.saveFilmDirectorsToDB(film.getDirectors(), filmId);
        }
        return findById(filmId);
    }

    public Collection<Film> findAll() {
        Collection<Film> films = filmStorage.findAll();
        for (Film film : films) {
            film.setGenres(genresService.placeGenresToFilmFromDB(film.getId()));
            film.setMpa(ratingsService.placeRatingToFilmFromDB(film.getId()));
            film.setDirectors(directorsService.placeDirectorsToFilmFromDB(film.getId()));
        }
        return films;
    }

    public Film findById(int filmId) {
        Film film = filmStorage.findById(filmId);
        film.setGenres(genresService.placeGenresToFilmFromDB(filmId));
        film.setMpa(ratingsService.placeRatingToFilmFromDB(filmId));
        film.setDirectors(directorsService.placeDirectorsToFilmFromDB(film.getId()));
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
            log.info("Юзер c ID " + filmId + " не найден.");
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
            log.info("Count меньше или равен нулю.");
            throw new ValidationException("Значение выводимых фильмов не может быть меньше или равно нулю.");
        } else {
            Collection<Film> films = filmStorage.findPopular(count);
            for (Film film : films) {
                film.setGenres(genresService.placeGenresToFilmFromDB(film.getId()));
                film.setMpa(ratingsService.placeRatingToFilmFromDB(film.getId()));
                film.setDirectors(directorsService.placeDirectorsToFilmFromDB(film.getId()));
            }
            return films;
        }
    }

    public Collection<Integer> findLikes(int filmId) {
        log.info("Лайки к фильму найдены.");
        return filmStorage.findLikes(filmId);
    }

    public Collection<Film> findByDirectorId(int directorId, String sortBy) {
        if (directorsService.isExists(directorId) && (sortBy.equals("year") || sortBy.equals("likes"))) {
            log.info("Фильмы по указанному режиссеру найдены.");
            Collection<Film> films = filmStorage.findByDirectorId(directorId, sortBy);
            for (Film film : films) {
                film.setGenres(genresService.placeGenresToFilmFromDB(film.getId()));
                film.setMpa(ratingsService.placeRatingToFilmFromDB(film.getId()));
                film.setDirectors(directorsService.placeDirectorsToFilmFromDB(film.getId()));
            }
            return films;
        } else if (!directorsService.isExists(directorId) && (sortBy.equals("year") || sortBy.equals("likes"))) {
            log.info("Режиссер c ID " + directorId + " не найден.");
            throw new DirectorNotFoundException("Режиссер c ID " + directorId + " не найден.");
        } else {
            log.info("Недопустимый параметр запроса.");
            throw new ValidationException("Недопустимый параметр запроса.");
        }
    }

    private boolean isExists(int filmId) {
        return filmStorage.isExists(filmId);
    }

    private boolean isGenresExists(Film film) {
        return film.getGenres() != null;
    }

    private boolean isDirectorsExists(Film film) {
        return film.getDirectors() != null;
    }
}