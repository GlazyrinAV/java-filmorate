package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.exceptions.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exceptions.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.dao.FeedStorage;
import ru.yandex.practicum.filmorate.storage.dao.FilmStorage;

import java.util.Collection;
import java.util.Optional;

@Service
@Slf4j
public class FilmService {

    private final FilmStorage filmStorage;
    private final DirectorsService directorsService;
    private final UserService userService;
    private final GenresService genresService;
    private final RatingsService ratingsService;
    private final FeedStorage feedStorage;

    @Autowired
    public FilmService(@Qualifier("FilmDbStorage") FilmStorage filmStorage, UserService userService,
                       GenresService genresService, RatingsService ratingsService, DirectorsService directorsService,
                       FeedStorage feedStorage) {
        this.filmStorage = filmStorage;
        this.directorsService = directorsService;
        this.userService = userService;
        this.genresService = genresService;
        this.ratingsService = ratingsService;
        this.feedStorage = feedStorage;
    }

    public Film saveNew(Film film) {
        int filmId = filmStorage.saveNew(film);
        saveAdditionalInfoToDb(film, filmId);
        return findById(filmId);
    }

    public Film update(Film film) {
        int filmId = filmStorage.update(film);
        genresService.removeFilmGenres(filmId);
        directorsService.removeFromFilmByFilmId(filmId);
        saveAdditionalInfoToDb(film, filmId);
        return findById(filmId);
    }

    public Collection<Film> findAll() {
        Collection<Film> films = filmStorage.findAll();
        for (Film film : films) {
            saveAdditionalInfoFromDb(film);
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
        saveAdditionalInfoFromDb(film);
        return film;
    }

    public void makeLike(int filmId, int userId) {
        userService.findById(userId);
        findById(filmId);
        log.info("К фильму добавлен лайк.");
        filmStorage.makeLike(filmId, userId);
        feedStorage.saveFeed(userId, filmId, 1, 2);
    }

    public void removeLike(int filmId, int userId) {
        userService.findById(userId);
        findById(filmId);
        log.info("У фильма удален лайк.");
        filmStorage.removeLike(filmId, userId);
        feedStorage.saveFeed(userId, filmId, 1, 1);
    }

    public Collection<Film> findPopular(int count, Optional<Integer> genreId, Optional<Integer> year) {
        Collection<Film> films;
        if (count <= 0) {
            throw new ValidationException("Значение выводимых фильмов не может быть меньше или равно нулю.");
        } else if (genreId.isPresent() & year.isPresent()) {
            films = filmStorage.findPopularByGenreAndYear(count, genreId.get(), year.get());
        } else if (genreId.isPresent()) {
            films = filmStorage.findPopularByGenre(count, genreId.get());
        } else if (year.isPresent()) {
            films = filmStorage.findPopularByYear(count, year.get());
        } else {
            films = filmStorage.findPopular(count);
        }
        for (Film film : films) {
            saveAdditionalInfoFromDb(film);
        }
        log.info("Популярные фильмы найдены.");
        return films;
    }

    public Collection<Integer> findLikes(int filmId) {
        log.info("Лайки к фильму найдены.");
        return filmStorage.findLikes(filmId);
    }

    public Collection<Film> findByDirectorId(int directorId, String sortBy) {
        directorsService.findById(directorId);
        if (!(sortBy.equals("year") || sortBy.equals("likes"))) {
            throw new ValidationException("Недопустимый параметр запроса.");
        }
        Collection<Film> films = filmStorage.findByDirectorId(directorId, sortBy);
        if (films.isEmpty()) {
            log.info("Фильмы по указанному режиссеру не найдены.");
        } else {
            log.info("Фильмы по указанному режиссеру найдены.");
            for (Film film : films) {
                saveAdditionalInfoFromDb(film);
            }
        }
        return films;
    }

    private void saveAdditionalInfoFromDb(Film film) {
        film.setGenres(genresService.saveGenresToFilmFromDB(film.getId()));
        film.setMpa(ratingsService.saveRatingToFilmFromDB(film.getId()));
        film.setDirectors(directorsService.saveDirectorsToFilmFromDB(film.getId()));
    }

    private void saveAdditionalInfoToDb(Film film, int filmId) {
        genresService.saveGenresToDBFromFilm(Optional.ofNullable(film.getGenres()), filmId);
        directorsService.saveDirectorsToDBFromFilm(Optional.ofNullable(film.getDirectors()), filmId);
    }

    public void removeFilm(int filmId) {
        findById(filmId);
        log.info("Фильм удален.");
        filmStorage.removeFilm(filmId);
    }

    public Collection<Film> findCommonFilms(int userId, int friendId) {

        userService.findById(userId);
        userService.findById(friendId);

        if (userId == friendId) {
            throw new ValidationException("Не допустимый параметр запроса. Пользователь сравнивается сам с собой");
        }

        Collection<Film> films = filmStorage.findCommonFilms(userId, friendId);
        for (Film film : films) {
            saveAdditionalInfoFromDb(film);
        }
        return films;

    }

    public Collection<Film> getRecommendation(int id) {
        userService.findById(id);
        Collection<Film> films = filmStorage.getRecommendation(id);
        if (films.isEmpty()) {
            log.info("Рекомендации по указанном пользователю не найдены.");
        } else {
            log.info("Рекомендации по указанном пользователю найдены.");
        }
        for (Film film : films) {
            saveAdditionalInfoFromDb(film);
        }

        return films;
    }
}

