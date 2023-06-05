package ru.yandex.practicum.filmorate.storage.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.storage.dao.ReviewStorage;

import java.sql.PreparedStatement;
import java.util.Collection;
import java.util.Objects;
import java.util.Optional;

@Repository
public class ReviewDbStorage implements ReviewStorage {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public ReviewDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Review saveNew(Review review) {
        String sqlQuery = "INSERT INTO REVIEWS (content, user_id, film_id, is_positive) " +
                "VALUES (?, ?, ?, ?)";

        KeyHolder keyHolder = new GeneratedKeyHolder();

        try {
            jdbcTemplate.update(connection -> {
                PreparedStatement stmt = connection.prepareStatement(sqlQuery, new String[]{"review_id"});
                stmt.setString(1, review.getContent());
                stmt.setInt(2, review.getUserId());
                stmt.setInt(3, review.getFilmId());
                stmt.setBoolean(4, review.isPositive());
                return stmt;
            }, keyHolder);

        } catch (DataIntegrityViolationException e) {
            throw new DataIntegrityViolationException("В запросе неправильно указаны данные по отзыву.");
        }
        Optional<Integer> reviewId = Optional.of(Objects.requireNonNull(keyHolder.getKey()).intValue());

        review.setReviewId(reviewId.get());

        return review;
    }

    @Override
    public Review update(Review review) {
        return null;
    }

    @Override
    public void delete(int reviewId) {

    }

    @Override
    public Review findById(int reviewId) {
        return null;
    }

    @Override
    public Collection<Review> findAll(int count) {
        return null;
    }

    @Override
    public Collection<Review> findByFilmId(int filmId, int count) {
        return null;
    }

    @Override
    public void saveLike(int userId, int reviewId, boolean like) {

    }

    @Override
    public void removeLike(int userId, int reviewId, boolean like) {

    }
}