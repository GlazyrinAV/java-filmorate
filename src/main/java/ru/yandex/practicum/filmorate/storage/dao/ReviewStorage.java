package ru.yandex.practicum.filmorate.storage.dao;

import ru.yandex.practicum.filmorate.model.Review;

import java.util.Collection;

public interface ReviewStorage {

    Integer saveNew(Review review);

    Integer update(Review review);

    void remove(int reviewId);

    Review findById(int reviewId);

    Collection<Review> findAll(int count);

    Collection<Review> findByFilmId(int filmId, int count);

    void saveLike(int userId, int reviewId, boolean like);

    void removeLike(int userId, int reviewId);

    Boolean isExists(Review review);

    Boolean isLikeExists(int userId, int reviewId);

    Boolean isDislikeExists(int userId, int reviewId);
}