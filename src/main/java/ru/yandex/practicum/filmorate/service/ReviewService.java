package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.storage.dao.ReviewStorage;

import java.util.Collection;

@Service
@Slf4j
public class ReviewService {
    private final ReviewStorage reviewStorage;

    @Autowired
    public ReviewService(ReviewStorage reviewStorage) {
        this.reviewStorage = reviewStorage;
    }

    public Review saveNew(Review review) {
        return reviewStorage.saveNew(review);
    }

    public Review update(Review review) {
        return reviewStorage.update(review);
    }

    public void delete(int reviewId) {
        reviewStorage.delete(reviewId);
    }

    public Review findById(int reviewId) {
        return reviewStorage.findById(reviewId);
    }

    public Collection<Review> findAll(int count) {
        return reviewStorage.findAll(count);
    }

    public Collection<Review> findByFilmId(int filmId, int count) {
        return reviewStorage.findByFilmId(filmId, count);
    }

    public void saveLike(int userId, int reviewId, boolean like) {
        reviewStorage.saveLike(userId, reviewId, like);
    }

    public void removeLike(int userId, int reviewId, boolean like) {
        reviewStorage.removeLike(userId, reviewId, like);
    }
}