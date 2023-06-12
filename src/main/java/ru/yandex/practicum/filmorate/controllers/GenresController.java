package ru.yandex.practicum.filmorate.controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.service.GenresService;

import java.util.Collection;

@RestController
@RequiredArgsConstructor
@RequestMapping("/genres")
@Slf4j
public class GenresController {

    private final GenresService genresService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public Collection<Genre> findAll() {
        return genresService.findAllGenres();
    }

    @GetMapping("/{genreId}")
    @ResponseStatus(HttpStatus.OK)
    public Genre findById(@PathVariable int genreId) {
        return genresService.findById(genreId);
    }
}