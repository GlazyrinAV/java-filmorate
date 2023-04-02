package ru.yandex.practicum.filmorate.controllers;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.time.Month;
import java.util.Collection;
import java.util.HashMap;

import static org.springframework.http.HttpStatus.*;

@RestController
@Slf4j
public class FilmController {

    private static int idFilmSequence = 1;
    private final HashMap<Integer, Film> films = new HashMap<>();

    @PostMapping("/films")
    public ResponseEntity<Film> addNewFilm(@Valid @RequestBody Film film) {
        log.info("Получен запрос на создание фильма.");
        if (!films.containsValue(film) && film.getReleaseDate().isAfter(LocalDate.of(1895, Month.DECEMBER, 28))) {
            film.setId(setId());
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
        if (!films.containsKey(film.getId())) {
            return new ResponseEntity<>(film, NOT_FOUND);
        } else {
            films.replace(film.getId(), film);
            return new ResponseEntity<>(film, OK);
        }
    }

    @GetMapping("/films")
    public ResponseEntity<Collection<Film>> getAllFilms() {
        log.info("Получен запрос на получение списка фильмов.");
        return new ResponseEntity<>(films.values(), OK);
    }

    private int setId() {
        return idFilmSequence++;
    }
}