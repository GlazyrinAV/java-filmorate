package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.FilmAlreadyExistsException;
import ru.yandex.practicum.filmorate.exceptions.FilmNotFoundException;
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
            return film;
        } else {
            throw new FilmNotFoundException("Фильм не найден.");
        }
    }

    @Override
    public Collection<Film> findAllFilms() {
        return films.values();
    }

    @Override
    public Film findFilm(int filmId) {
        return films.get(filmId);
    }

    public void resetFilmsForTests() {
        idFilmSequence = 1;
    }

    private int setNewId() {
        return idFilmSequence++;
    }
}