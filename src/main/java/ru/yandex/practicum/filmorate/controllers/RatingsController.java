package ru.yandex.practicum.filmorate.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Rating;
import ru.yandex.practicum.filmorate.service.RatingsService;

import java.util.Collection;

@RestController
@RequestMapping("/mpa")
@Slf4j
public class RatingsController {

    private final RatingsService ratingsService;

    public RatingsController(RatingsService ratingsService) {
        this.ratingsService = ratingsService;
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public Collection<Rating> findAll() {
        return ratingsService.findAll();
    }

    @GetMapping("/{ratingId}")
    @ResponseStatus(HttpStatus.OK)
    public Rating findById(@PathVariable int ratingId) {
        return ratingsService.findById(ratingId);
    }
}