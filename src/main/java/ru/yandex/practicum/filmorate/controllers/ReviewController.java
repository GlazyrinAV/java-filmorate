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
        return reviewService.saveNew(review);
    }

    @PutMapping
    @ResponseStatus(HttpStatus.OK)
    public Review update(@Valid @RequestBody Review review) {
        return reviewService.update(review);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void remove(@PathVariable int id) {
        reviewService.remove(id);
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Review findById(@PathVariable int id) {
        return reviewService.findById(id);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public Collection<Review> findByFilmId(@RequestParam Optional<Integer> filmId,
                                           @RequestParam(defaultValue = "10") int count
    ) {
        if (filmId.isEmpty()) {
            return reviewService.findAll(count);
        } else {
            return reviewService.findByFilmId(filmId.get(), count);
        }
    }

    @PutMapping("/{id}/{like}/{userId}")
    @ResponseStatus(HttpStatus.OK)
    public void saveLike(@PathVariable int id,
                         @PathVariable int userId,
                         @PathVariable String like
    ) {
        reviewService.saveLike(userId, id, like);
    }

    @DeleteMapping("/{id}/{like}/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeLike(@PathVariable int id,
                           @PathVariable int userId,
                           @PathVariable String like
    ) {
        reviewService.removeLike(userId, id, like);
    }

    @DeleteMapping("/{id}/dislike/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeDislike(@PathVariable int id,
                              @PathVariable int userId
    ) {
        reviewService.removeDislike(userId, id);
    }
}