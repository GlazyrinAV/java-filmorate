package ru.yandex.practicum.filmorate.storage.dao;

import ru.yandex.practicum.filmorate.model.Review;

import java.util.Collection;

public interface ReviewStorage {

    Review saveNew(Review review);

    Review update(Review review);

    void delete(int reviewId);

    Review findById(int reviewId);

    Collection<Review> findAll(int count);

    Collection<Review> findByFilmId(int filmId, int count);

    void saveLike(int userId, int reviewId, boolean like);

    void removeLike(int userId, int reviewId);

    Boolean isExists(int reviewId);
}