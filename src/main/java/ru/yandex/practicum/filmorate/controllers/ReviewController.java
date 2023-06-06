package ru.yandex.practicum.filmorate.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.service.ReviewService;

import javax.validation.Valid;
import java.util.Collection;
import java.util.Optional;

@RestController
@RequestMapping("/reviews")
@Slf4j
public class ReviewController {

    private final ReviewService reviewService;

    @Autowired
    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Review saveNew(@Valid @RequestBody Review review) {
        log.info("Получен запрос на создание отзыва.");
        return reviewService.saveNew(review);
    }

    @PutMapping
    @ResponseStatus(HttpStatus.OK)
    public Review update(@Valid @RequestBody Review review) {
        log.info("Получен запрос на обновление отзыва.");
        return reviewService.update(review);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable int id) {
        log.info("Получен запрос на удаление отзыва.");
        reviewService.delete(id);
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Review findById(@PathVariable int id) {
        log.info("Получен запрос на поиск отзыва.");
        return reviewService.findById(id);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public Collection<Review> findByFilmId(@RequestParam Optional<Integer> filmId, @RequestParam(defaultValue = "10") int count) {
        log.info("Получен запрос на поиск отзыва на фильм.");
        if (filmId.isEmpty()) {
            return reviewService.findAll(count);
        } else {
            return reviewService.findByFilmId(filmId.get(), count);
        }
    }

    @PutMapping("/{id}/{like}/{userId}")
    @ResponseStatus(HttpStatus.OK)
    public void saveLike(@PathVariable int id, @PathVariable int userId, @PathVariable String like) {
        log.info("Получен запрос на проставление лайка отзыву.");
        if (like.equals("like")) {
            reviewService.saveLike(userId, id, true);
        } else if (like.equals("dislike")) {
            reviewService.saveLike(userId, id, false);
        }
    }

    @DeleteMapping("/{id}/{like}/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeLike(@PathVariable int id, @PathVariable int userId, @PathVariable String like) {
        log.info("Получен запрос на удадение лайка отзыву.");
        if (like.equals("like") || like.equals("dislike") ) {
            reviewService.removeLike(userId, id);
        }
    }
}