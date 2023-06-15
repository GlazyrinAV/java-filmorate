package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.*;
import ru.yandex.practicum.filmorate.storage.dao.FeedStorage;
import ru.yandex.practicum.filmorate.storage.dao.FilmStorage;

import java.util.Collection;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class FilmService {

    private final FilmStorage filmStorage;
    private final DirectorsService directorsService;
    private final UserService userService;
    private final GenresService genresService;
    private final MpaService mpaService;
    private final FeedStorage feedStorage;

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
        return filmStorage.findAll().stream().peek(this::saveAdditionalInfoFromDb).collect(Collectors.toList());
    }

    public Film findById(int filmId) {
        Film film;
        film = filmStorage.findById(filmId);
        saveAdditionalInfoFromDb(film);
        return film;
    }

    public void saveScore(Score score) {
        userService.findById(score.getUserId());
        findById(score.getFilmId());
        log.info("К фильму добавлен лайк.");
        filmStorage.saveScore(score.getFilmId(), score.getUserId(), score.getScore());
        feedStorage.saveFeed(score.getUserId(), score.getFilmId(), EventType.SCORE.getEventTypeId(), Operation.ADD.getOperationId());
    }

    public void removeScore(int filmId, int userId) {
        userService.findById(userId);
        findById(filmId);
        log.info("У фильма удален лайк.");
        filmStorage.removeScore(filmId, userId);
        feedStorage.saveFeed(userId, filmId, EventType.SCORE.getEventTypeId(), Operation.REMOVE.getOperationId());
    }

    public Collection<Film> findPopular(int count, Optional<Integer> genreId, Optional<Integer> year) {
        Collection<Film> films;
        if (count <= 0) {
            throw new ValidationException("Значение выводимых фильмов не может быть меньше или равно нулю.");
        } else if (genreId.isPresent() && year.isPresent()) {
            films = filmStorage.findPopularByGenreAndYear(count, genreId.get(), year.get());
        } else if (genreId.isPresent()) {
            films = filmStorage.findPopularByGenre(count, genreId.get());
        } else if (year.isPresent()) {
            films = filmStorage.findPopularByYear(count, year.get());
        } else {
            films = filmStorage.findPopular(count);
        }

        log.info("Популярные фильмы найдены.");
        return films.stream().peek(this::saveAdditionalInfoFromDb).collect(Collectors.toList());
    }

    public Collection<Film> findByDirectorId(Integer directorId, SortType sortBy) {
        directorsService.findById(directorId);
        if (!(sortBy.equals(SortType.year) || sortBy.equals(SortType.ratings))) {
            throw new ValidationException("Недопустимый параметр сортировки.");
        }

        return filmStorage.findByDirectorId(directorId, sortBy).stream().peek(this::saveAdditionalInfoFromDb)
                .collect(Collectors.toList());
    }

    private void saveAdditionalInfoFromDb(Film film) {
        film.setGenres(genresService.saveGenresToFilmFromDB(film.getId()));
        film.setMpa(mpaService.saveRatingToFilmFromDB(film.getId()));
        film.setDirectors(directorsService.saveDirectorsToFilmFromDB(film.getId()));
        film.setRating(filmStorage.findScore(film.getId()));
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

    public Collection<Film> findCommonFilms(Optional<Integer> userId, Optional<Integer> friendId) {
        int intUserId = userId.orElseThrow(() -> new ValidationException("Недопустимый параметр запроса."));
        int intFriendId = friendId.orElseThrow(() -> new ValidationException("Недопустимый параметр запроса."));
        if (intUserId == intFriendId) {
            throw new ValidationException("Не допустимый параметр запроса. Пользователь сравнивается сам с собой.");
        }
        userService.findById(intUserId);
        userService.findById(intFriendId);

        return filmStorage.findCommonFilms(intUserId, intFriendId).stream().peek(this::saveAdditionalInfoFromDb)
                .collect(Collectors.toList());
    }

    public Collection<Film> getRecommendation(int id) {
        userService.findById(id);
        Collection<Film> films = filmStorage.getRecommendation(id);
        if (films.isEmpty()) {
            log.info("Рекомендации по указанном пользователю не найдены.");
        } else {
            log.info("Рекомендации по указанном пользователю найдены.");
        }

        return films.stream().peek(this::saveAdditionalInfoFromDb)
                .collect(Collectors.toList());
    }

    public Collection<Film> searchByFilmAndDirector(String query, String by) {
        Collection<Film> films = null;
        switch (by) {
            case "director":
                films = filmStorage.searchByDirector(query);
                break;
            case "title":
                films = filmStorage.searchByTitle(query);
                break;
            case "director,title":
            case "title,director":
                films = filmStorage.searchByFilmAndDirector(query);
                break;
            default:
                throw new ValidationException("Недопустимый параметр запроса. Поиск по" + by + " еще не реализован.");
        }
        if (films.isEmpty()) {
            log.info("Фильмы не найдены.");
        } else {
            log.info("Фильмы по поиску найдены.");
            for (Film film : films) {
                saveAdditionalInfoFromDb(film);
            }
        }
        return films;
    }
}
