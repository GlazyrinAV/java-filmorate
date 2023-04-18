package ru.yandex.practicum.filmorate.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import javax.validation.Valid;
import java.util.Collection;

@RestController
@Slf4j
public class FilmController {

    @Autowired
    private FilmService filmService;

    @PostMapping("/films")
    @ResponseStatus(HttpStatus.CREATED)
    public Film addNewFilm(@Valid @RequestBody Film film) {
        log.info("Получен запрос на создание фильма.");
        return filmService.addNewFilm(film);
    }

    @PutMapping("/films")
    @ResponseStatus(HttpStatus.OK)
    public Film updateFilm(@Valid @RequestBody Film film) {
        log.info("Получен запрос на обновление фильма.");
        return filmService.updateFilm(film);
    }

    @GetMapping("/films")
    @ResponseStatus(HttpStatus.OK)
    public Collection<Film> getAllFilms() {
        log.info("Получен запрос на получение списка фильмов.");
        return filmService.findAllFilms();
    }

    @GetMapping("/films/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Film findFilmById(@PathVariable int id) {
        log.info("Получен запрос на поиск фильма с ID" + id + ".");
        return filmService.findFilm(id);
    }

    @PutMapping("/films/{id}/like/{userId}")
    @ResponseStatus(HttpStatus.OK)
    public void addNewLike(@PathVariable int id, @PathVariable int userId) {
        log.info("Получен запрос на добавление лайка к фильму ");
        filmService.addLike(id, userId);
    }

    @DeleteMapping("/films/{id}/like/{userId}")
    @ResponseStatus(HttpStatus.OK)
    public void removeLike(@PathVariable int id, @PathVariable int userId) {
        log.info("Получен запрос на удаление лайка к фильму ");
        filmService.removeLike(id, userId);
    }

    @GetMapping("/films/popular")
    @ResponseStatus(HttpStatus.OK)
    public Collection<Film> findPopularFilms(@RequestParam(defaultValue = "10") int count) {
        log.info("Получен запрос на получение первых " + count + " популярных фильмов.");
        return filmService.getPopularFilms(count);
    }
}