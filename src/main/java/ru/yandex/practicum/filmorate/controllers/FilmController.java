package ru.yandex.practicum.filmorate.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.time.Month;
import java.util.Collection;
import java.util.HashMap;

import static org.springframework.http.HttpStatus.*;

@RestController
@Slf4j
public class FilmController {

    private final HashMap<Integer, Film> films = new HashMap<>();
    private static int idFilmSequence = 1;

    @PostMapping("/films")
    public ResponseEntity<Film> addNewFilm(@RequestBody Film film) {
        log.info("Получен запрос на создание фильма.");
        try {
            if (validateFilm(film) && !films.containsValue(film)) {
                film.setId(setId());
                films.put(film.getId(), film);
                return new ResponseEntity<>(film, CREATED);
            } else if (validateFilm(film) && films.containsKey(film.getId())) {
                log.info("Такой фильм уже существует.");
                return new ResponseEntity<>(film, BAD_REQUEST);
            }
        } catch (ValidationException e) {
            log.info(e.getMessage());
            return new ResponseEntity<>(film, BAD_REQUEST);
        }
        return new ResponseEntity<>(film, BAD_REQUEST);
    }

    @PutMapping("/films")
    public ResponseEntity<Film> updateFilm(@RequestBody Film film) {
        log.info("Получен запрос на обновление фильма.");
        try {
            if (validateFilm(film) && !films.containsKey(film.getId())) {
                return new ResponseEntity<>(film, NOT_FOUND);
            } else if (validateFilm(film) && films.containsKey(film.getId())) {
                films.replace(film.getId(), film);
                return new ResponseEntity<>(film, OK);
            }
        } catch (ValidationException e) {
            log.info(e.getMessage());
            return new ResponseEntity<>(film, BAD_REQUEST);
        }
        return new ResponseEntity<>(film, BAD_REQUEST);
    }

    @GetMapping("/films")
    public ResponseEntity<Collection<Film>> getAllFilms() {
        log.info("Получен запрос на получение списка фильмов.");
        return new ResponseEntity<>(films.values(), OK);
    }

    private boolean validateFilm(Film film) throws ValidationException {
        if (film.getName().isEmpty()) {
            throw new ValidationException("Название фильма не может быть пустым.");
        } else if (film.getDescription().length() > 199) {
            throw new ValidationException("Описание фильма не может быть больше 200 символов.");
        } else if (film.getReleaseDate().isBefore(LocalDate.of(1895, Month.DECEMBER, 28))) {
            throw new ValidationException("Дата релиза фильма не может быть раньше 28 декабря 1895 года.");
        } else if (film.getDuration() <= 0) {
            throw new ValidationException("Продолжительность фильма должна быть больше нуля.");
        }
        return true;
    }

    private int setId() {
        return idFilmSequence++;
    }
}