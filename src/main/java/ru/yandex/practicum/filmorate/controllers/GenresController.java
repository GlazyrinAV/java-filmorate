package ru.yandex.practicum.filmorate.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.service.GenresService;

import java.util.Collection;

@RestController
@RequestMapping("/genres")
@Slf4j
public class GenresController {

    private final GenresService genresService;

    public GenresController(GenresService genresService) {
        this.genresService = genresService;
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public Collection<Genre> findAll() {
        log.info("Получен запрос на получение списка всех доступных жанров фильмов.");
        return genresService.findAllGenres();
    }

    @GetMapping("/{genreId}")
    @ResponseStatus(HttpStatus.OK)
    public Genre findById(@PathVariable int genreId) {
        log.info("Получен запрос на получение жанра фильмов под номером" + genreId + ".");
        return genresService.findGenreById(genreId);
    }
}