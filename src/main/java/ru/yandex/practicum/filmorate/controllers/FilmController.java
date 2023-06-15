package ru.yandex.practicum.filmorate.controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Score;
import ru.yandex.practicum.filmorate.model.SortType;
import ru.yandex.practicum.filmorate.service.FilmService;

import javax.validation.Valid;
import java.util.Collection;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@Slf4j
public class FilmController {

    private final FilmService filmService;

    @PostMapping("/films")
    @ResponseStatus(HttpStatus.CREATED)
    public Film saveNew(@Valid @RequestBody Film film) {
        return filmService.saveNew(film);
    }

    @PutMapping("/films")
    @ResponseStatus(HttpStatus.OK)
    public Film update(@Valid @RequestBody Film film) {
        return filmService.update(film);
    }

    @GetMapping("/films")
    @ResponseStatus(HttpStatus.OK)
    public Collection<Film> findAll() {
        return filmService.findAll();
    }

    @GetMapping("/films/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Film findById(@PathVariable int id) {
        return filmService.findById(id);
    }

    @PutMapping("films/score")
    @ResponseStatus(HttpStatus.OK)
    public void saveScore(@Valid @RequestBody Score score) {
        filmService.saveScore(score);
    }

    @DeleteMapping("/films/{id}/score/{userId}")
    @ResponseStatus(HttpStatus.OK)
    public void removeScore(@PathVariable int id, @PathVariable int userId) {
        filmService.removeScore(id, userId);
    }

    @GetMapping("/films/popular")
    @ResponseStatus(HttpStatus.OK)
    public Collection<Film> findPopularFilms(@RequestParam(defaultValue = "10") int count,
                                             @RequestParam Optional<Integer> genreId,
                                             @RequestParam Optional<Integer> year) {
        return filmService.findPopular(count, genreId, year);
    }

    @DeleteMapping("/films/{id}")
    @ResponseStatus(HttpStatus.OK)
    public void removeFilm(@PathVariable int id) {
        filmService.removeFilm(id);
    }

    @GetMapping("/films/director/{directorId}")
    @ResponseStatus(HttpStatus.OK)
    public Collection<Film> findByDirectorId(@PathVariable Integer directorId, @RequestParam("sortBy") SortType sortBy) {
        return filmService.findByDirectorId(directorId, sortBy);
    }

    @GetMapping("/films/common")
    @ResponseStatus(HttpStatus.OK)
    public Collection<Film> findCommonFilms(@RequestParam Optional<Integer> userId, @RequestParam Optional<Integer> friendId) {
        return filmService.findCommonFilms(userId, friendId);
    }

    @GetMapping("/users/{id}/recommendations")
    @ResponseStatus(HttpStatus.OK)
    public Collection<Film> getRecommendation(@PathVariable int id) {
        return filmService.getRecommendation(id);
    }

    @GetMapping("/films/search")
    @ResponseStatus(HttpStatus.OK)
    public Collection<Film> searchByFilmAndDirector(@RequestParam(required = false) String query,
                                                    @RequestParam(required = false) String by) {
        return filmService.searchByFilmAndDirector(query, by);
    }
}