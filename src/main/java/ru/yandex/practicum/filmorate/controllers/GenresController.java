package ru.yandex.practicum.filmorate.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.service.GenresService;

import java.util.Collection;

@RestController
@Slf4j
public class GenresController {

    private final GenresService genresService;

    public GenresController(GenresService genresService) {
        this.genresService = genresService;
    }

    @GetMapping("/genres")
    @ResponseStatus(HttpStatus.OK)
    public Collection<Genre> findAllGenres() {
        log.info("Получен запрос на получение списка всех доступных жанров фильмов.");
        return genresService.findAllGenres();
    }

    @GetMapping("/genres/{genreId}")
    @ResponseStatus(HttpStatus.OK)
    public Genre findGenreById(@PathVariable int genreId) {
        log.info("Получен запрос на получение жанра фильмов под номером" + genreId + ".");
        return genresService.findGenreById(genreId);
    }
}