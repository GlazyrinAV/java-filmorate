package ru.yandex.practicum.filmorate.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.Rating;
import ru.yandex.practicum.filmorate.service.RatingsService;

import java.util.Collection;

@RestController
@Slf4j
public class RatingsController {

    private final RatingsService ratingsService;

    public RatingsController(RatingsService ratingsService) {
        this.ratingsService = ratingsService;
    }

    @GetMapping("/mpa")
    @ResponseStatus(HttpStatus.OK)
    public Collection<Rating> findAllFilmRatings() {
        log.info("Получен запрос на получение списка всех доступных рейтингов фильмов.");
        return ratingsService.findAllFilmRatings();
    }

    @GetMapping("/mpa/{ratingId}")
    @ResponseStatus(HttpStatus.OK)
    public Rating findRatingById(@PathVariable int ratingId) {
        log.info("Получен запрос на получение рейтина фильмов под номером" + ratingId + ".");
        return ratingsService.findRatingById(ratingId);
    }
}