package ru.yandex.practicum.filmorate.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import javax.validation.Valid;
import java.util.Collection;
import java.util.Optional;

@RestController
@RequestMapping("/films")
@Slf4j
public class FilmController {

    private final FilmService filmService;

    public FilmController(FilmService filmService) {
        this.filmService = filmService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Film saveNew(@Valid @RequestBody Film film) {
        return filmService.saveNew(film);
    }

    @PutMapping
    @ResponseStatus(HttpStatus.OK)
    public Film update(@Valid @RequestBody Film film) {
        return filmService.update(film);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public Collection<Film> findAll() {
        return filmService.findAll();
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Film findById(@PathVariable int id) {
        return filmService.findById(id);
    }

    @PutMapping("/{id}/like/{userId}")
    @ResponseStatus(HttpStatus.OK)
    public void makeNewLike(@PathVariable int id, @PathVariable int userId) {
        filmService.makeLike(id, userId);
    }

    @DeleteMapping("/{id}/like/{userId}")
    @ResponseStatus(HttpStatus.OK)
    public void removeLike(@PathVariable int id, @PathVariable int userId) {
        filmService.removeLike(id, userId);
    }

    @GetMapping("/popular")
    @ResponseStatus(HttpStatus.OK)
    public Collection<Film> findPopularFilms(@RequestParam(defaultValue = "10") int count,
                                             @RequestParam Optional<Integer> genreId,
                                             @RequestParam Optional<Integer> year
    ) {
        return filmService.findPopular(count, genreId, year);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public void removeFilm(@PathVariable int id) {
        filmService.removeFilm(id);
    }

    @GetMapping("/director/{directorId}")
    @ResponseStatus(HttpStatus.OK)
    public Collection<Film> findByDirectorId(@PathVariable int directorId, @RequestParam String sortBy) {
        return filmService.findByDirectorId(directorId, sortBy);
    }

    @GetMapping("/common")
    @ResponseStatus(HttpStatus.OK)
    public Collection<Film> findCommonFilms(@RequestParam int userId, @RequestParam int friendId) {
        log.info("Получен запрос на получение общих фильмов у юзера ID" + userId + " и юзера ID" + friendId);
        return filmService.findCommonFilms(userId, friendId);
    }
}