package ru.yandex.practicum.filmorate.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.time.Month;
import java.util.HashMap;

@RestController
@Slf4j
public class FilmController {

    private int idFilmSequence = 1;
    private final HashMap<Integer, Film> films = new HashMap<>();

    @PostMapping("/films")
    public Film addNewFilm(@RequestBody Film film) {
        film.setId(setId());
        try {
            if (validateFilm(film) && !films.containsValue(film)) {
                films.put(film.getId(), film);
                return film;
            } else if (validateFilm(film) && films.containsKey(film.getId())) {
                log.info("Такой фильм уже существует.");
            }
        } catch (ValidationException e) {
            log.info(e.getMessage());
        }
        return film;
    }

    @PutMapping("/films")
    public Film updateFilm(@RequestBody Film film) {
        try {
            if (validateFilm(film) && !films.containsKey(film.getId())) {
                film.setId(setId());
                films.put(film.getId(), film);
                return film;
            } else if (validateFilm(film) && films.containsKey(film.getId())) {
                films.replace(film.getId(), film);
                return film;
            }
        } catch (ValidationException e) {
            log.info(e.getMessage());
        }
        return film;
    }

    @GetMapping("/films")
    public HashMap<Integer, Film> getAllFilms() {
        return films;
    }

    private boolean validateFilm(Film film) throws ValidationException {
        if (film.getName().isEmpty()) {
            throw new ValidationException("Название фильма не может быть пустым.");
        } else if (film.getDescription().length() > 200) {
            throw new ValidationException("Описание фильма не может быть больше 200 символов.");
        } else if (film.getReleaseDate().isBefore(LocalDate.of(1895, Month.DECEMBER, 28))) {
            throw new ValidationException("Дата релиза фильма не может быть раньше 28 декабря 1895 года.");
        } else if (film.getDuration().isNegative() || film.getDuration().isZero()) {
            throw new ValidationException("Продолжительность фильма должна быть больше нуля.");
        }
        return true;
    }
    private int setId() {
        return idFilmSequence++;
    }
}