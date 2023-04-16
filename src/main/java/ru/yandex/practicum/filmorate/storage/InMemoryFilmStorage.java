package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static org.springframework.http.HttpStatus.*;

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
            return new ResponseEntity<>(film, CREATED);
        } else {
            log.info("Такой фильм уже существует.");
            return new ResponseEntity<>(film, BAD_REQUEST);
        }
    }

    @Override
    public Film updateFilm(Film film) {
        if (films.containsKey(film.getId())) {
            films.replace(film.getId(), film);
            return new ResponseEntity<>(film, OK);
        } else {
            return new ResponseEntity<>(film, NOT_FOUND);
        }
    }

    @Override
    public Collection<Film> findAllFilms() {
        return new ResponseEntity<>(films.values(), OK);
    }

    public void resetFilmsForTests() {
        idFilmSequence = 1;
    }

    private int setNewId() {
        return idFilmSequence++;
    }
}