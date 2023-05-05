package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.FilmAlreadyExistsException;
import ru.yandex.practicum.filmorate.exceptions.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Rating;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Component
@Slf4j
@Qualifier("InMemoryFilmStorage")
public class InMemoryFilmStorage implements FilmStorage {

    private int idFilmSequence = 1;
    private final Map<Integer, Film> films = new ConcurrentHashMap<>();

    @Override
    public Film addNewFilm(Film film) {
        if (!films.containsValue(film)) {
            film.setId(setNewId());
            films.put(film.getId(), film);
            log.info("Фильм добавлен.");
            return film;
        } else {
            log.info("Такой фильм уже существует.");
            throw new FilmAlreadyExistsException("Такой фильм уже существует.");
        }
    }

    @Override
    public Film updateFilm(Film film) {
        if (films.containsKey(film.getId())) {
            films.replace(film.getId(), film);
            log.info("Данные о фильме c ID " + film.getId() + " обновлены.");
            return film;
        } else {
            log.info("Фильм c ID " + film.getId() + " не найден.");
            throw new FilmNotFoundException("Фильм c ID " + film.getId() + " не найден.");
        }
    }

    @Override
    public Collection<Film> findAllFilms() {
        return films.values();
    }

    @Override
    public Film findFilm(int filmId) {
        if (filmId <= 0) {
            log.info("Указанный ID меньше или равен нулю.");
            throw new ValidationException("ID не может быть меньше или равно нулю.");
        } else if (!films.containsKey(filmId)) {
            log.info("Фильм c ID " + filmId + " не найден.");
            throw new FilmNotFoundException("Фильм c ID " + filmId + " не найден.");
        } else {
            return films.get(filmId);
        }
    }

    @Override
    public Collection<Film> findPopular(int count) {
        return films.values().stream()
                .sorted(this::compare)
                .limit(count)
                .collect(Collectors.toList());
    }

    @Override
    public void addLike(int filmId, int userId) {
        films.get(filmId).getLiked().add(userId);
    }

    @Override
    public void removeLike(int filmId, int userId) {
        films.get(filmId).getLiked().remove(userId);
    }

    @Override
    public Collection<Rating> findAllFilmRatings() {
        return null;
    }

    @Override
    public Rating findRatingById(int ratingId) {
        return null;
    }

    @Override
    public Collection<Genre> findAllGenres() {
        return null;
    }

    @Override
    public Genre findGenreById(int genreId) {
        return null;
    }

    @Override
    public Collection<Integer> getLikes(int filmId) {
        return null;
    }

    public void resetFilmsForTests() {
        idFilmSequence = 1;
    }

    private int setNewId() {
        return idFilmSequence++;
    }

    private int compare(Film p0, Film p1) {
        return p1.getLiked().size() - (p0.getLiked().size());
    }

    public void resetCounter() {
        idFilmSequence = 1;
    }
}