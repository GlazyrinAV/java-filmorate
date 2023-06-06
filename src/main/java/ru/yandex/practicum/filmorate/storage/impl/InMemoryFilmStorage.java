package ru.yandex.practicum.filmorate.storage.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.exceptions.FilmAlreadyExistsException;
import ru.yandex.practicum.filmorate.exceptions.exceptions.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exceptions.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.dao.FilmStorage;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Component
@Slf4j
@Qualifier("InMemoryFilmStorage")
public class InMemoryFilmStorage implements FilmStorage {

    private int idFilmSequence = 1;
    private final Map<Integer, Film> films = new ConcurrentHashMap<>();

    private final Map<Integer, Set<Integer>> likes = new HashMap<>();

    @Override
    public Film addNew(Film film) {
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
    public Film update(Film film) {
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
    public Collection<Film> findAll() {
        return films.values();
    }

    @Override
    public Film findById(int filmId) {
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
    public void makeLike(int filmId, int userId) {
        likes.put(filmId, putUserToLikes(filmId, userId));
    }

    @Override
    public void removeLike(int filmId, int userId) {
        likes.get(filmId).remove(userId);
    }

    @Override
    public Collection<Integer> findLikes(int filmId) {
        return likes.get(filmId);
    }

    @Override
    public Boolean isExists(int filmId) {
        return films.get(filmId) != null;
    }

    @Override
    public Collection<Film> findByDirectorId(int directorId, String sortBy) {
        return null;
    }

    private Set<Integer> putUserToLikes(int filmId, int userId) {
        likes.get(filmId).add(userId);
        return likes.get(filmId);
    }

    public void resetFilmsForTests() {
        idFilmSequence = 1;
    }

    private int setNewId() {
        return idFilmSequence++;
    }

    private int compare(Film p0, Film p1) {
        return likes.get(p1.getId()).size() - (likes.get(p0.getId()).size());
    }

    public void resetCounter() {
        idFilmSequence = 1;
    }
}