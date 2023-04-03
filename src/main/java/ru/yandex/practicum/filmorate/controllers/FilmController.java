package ru.yandex.practicum.filmorate.controllers;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.time.Month;
import java.util.Collection;
import java.util.HashMap;

import static org.springframework.http.HttpStatus.*;

@RestController
@Validated
@Slf4j
public class FilmController {

    private static int idFilmSequence = 1;
    private final LocalDate FIRST_FILM = LocalDate.of(1895, Month.DECEMBER, 28);
    private final HashMap<Integer, Film> films = new HashMap<>();

    @PostMapping("/films")
    public ResponseEntity<Film> addNewFilm(@Valid @RequestBody Film film) {
        log.info("Получен запрос на создание фильма.");
        if (!films.containsValue(film) && film.getReleaseDate().isAfter(FIRST_FILM)) {
            film.setId(setNewId());
            films.put(film.getId(), film);
            return new ResponseEntity<>(film, CREATED);
        } else if (!films.containsValue(film) && film.getReleaseDate().isBefore(FIRST_FILM)) {
            log.info("Дата выхода фильма не может быть раньше 28.12.1895.");
            return new ResponseEntity<>(film, BAD_REQUEST);
        } else {
            log.info("Такой фильм уже существует.");
            return new ResponseEntity<>(film, BAD_REQUEST);
        }
    }

    @PutMapping("/films")
    public ResponseEntity<Film> updateFilm(@Valid @RequestBody Film film) {
        log.info("Получен запрос на обновление фильма.");
        if (films.containsKey(film.getId()) && film.getReleaseDate().isAfter(FIRST_FILM)) {
            films.replace(film.getId(), film);
            return new ResponseEntity<>(film, OK);
        } else if (films.containsValue(film) && film.getReleaseDate().isBefore(FIRST_FILM)) {
            log.info("Дата выхода фильма не может быть раньше 28.12.1895.");
            return new ResponseEntity<>(film, BAD_REQUEST);
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