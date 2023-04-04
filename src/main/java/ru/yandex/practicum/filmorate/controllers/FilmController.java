package ru.yandex.practicum.filmorate.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;

import javax.validation.Valid;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static org.springframework.http.HttpStatus.*;

@RestController
@Validated
@Slf4j
public class FilmController {

    private static int idFilmSequence = 1;
    private final Map<Integer, Film> films = new ConcurrentHashMap<>();

    @PostMapping("/films")
    public ResponseEntity<Film> addNewFilm(@Valid @RequestBody Film film) {
        log.info("Получен запрос на создание фильма.");
        if (!films.containsValue(film)) {
            film.setId(setNewId());
            films.put(film.getId(), film);
            return new ResponseEntity<>(film, CREATED);
        } else {
            log.info("Такой фильм уже существует.");
            return new ResponseEntity<>(film, BAD_REQUEST);
        }
    }

    @PutMapping("/films")
    public ResponseEntity<Film> updateFilm(@Valid @RequestBody Film film) {
        log.info("Получен запрос на обновление фильма.");
        if (films.containsKey(film.getId())) {
            films.replace(film.getId(), film);
            return new ResponseEntity<>(film, OK);
        } else {
            return new ResponseEntity<>(film, NOT_FOUND);
        }
    }

    @GetMapping("/films")
    public ResponseEntity<Collection<Film>> getAllFilms() {
        log.info("Получен запрос на получение списка фильмов.");
        return new ResponseEntity<>(films.values(), OK);
    }

    private int setNewId() {
        return idFilmSequence++;
    }
}