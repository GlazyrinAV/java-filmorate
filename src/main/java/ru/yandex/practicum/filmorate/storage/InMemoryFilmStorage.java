package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.FilmAlreadyExistsException;
import ru.yandex.practicum.filmorate.exceptions.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
@Slf4j
public class InMemoryFilmStorage implements FilmStorage {

    private static int idFilmSequence = 1;
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

    public void resetFilmsForTests() {
        idFilmSequence = 1;
    }

    private int setNewId() {
        return idFilmSequence++;
    }
}