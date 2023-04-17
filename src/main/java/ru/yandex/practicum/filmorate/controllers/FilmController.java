package ru.yandex.practicum.filmorate.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.storage.InMemoryFilmStorage;

import javax.validation.Valid;
import java.util.Collection;

@RestController
@Validated
@Slf4j
public class FilmController {

    @Autowired
    FilmService filmService;

    @Autowired
    InMemoryFilmStorage storage;

    @PostMapping("/films")
    @ResponseStatus(HttpStatus.CREATED)
    public Film addNewFilm(@Valid @RequestBody Film film) {
        log.info("Получен запрос на создание фильма.");
        return storage.addNewFilm(film);
    }

    @PutMapping("/films")
    @ResponseStatus(HttpStatus.OK)
    public Film updateFilm(@Valid @RequestBody Film film) {
        log.info("Получен запрос на обновление фильма.");
        return storage.updateFilm(film);
    }

    @GetMapping("/films")
    @ResponseStatus(HttpStatus.OK)
    public Collection<Film> getAllFilms() {
        log.info("Получен запрос на получение списка фильмов.");
        return storage.findAllFilms();
    }

    @PutMapping("/films/{id}/like/{userId}")
    @ResponseStatus(HttpStatus.OK)
    public void addNewLike(@PathVariable int id, @PathVariable int userId) {
        log.info("Получен запрос на добавление лайка к фильму " + storage.findFilm(id).getName());
        filmService.addLike(id, userId);
    }

    @DeleteMapping("/films/{id}/like/{userId}")
    @ResponseStatus(HttpStatus.OK)
    public void removeLike(@PathVariable int id, @PathVariable int userId) {
        log.info("Получен запрос на удаление лайка к фильму " + storage.findFilm(id).getName());
        filmService.removeLike(id, userId);
    }

    @GetMapping("/films/popular")
    @ResponseStatus(HttpStatus.OK)
    public Collection<Film> findPopularFilms(@RequestParam(defaultValue = "10") int count) {
        log.info("Получен запрос на получение первых " + count + " популярных фиьмов.");
        return filmService.getPopularFilms(count);
    }

    @DeleteMapping("/resetFilms")
    @ResponseStatus(HttpStatus.OK)
    public void resetForTests() {
        storage.resetFilmsForTests();
    }
}