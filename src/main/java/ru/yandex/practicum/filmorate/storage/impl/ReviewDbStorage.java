package ru.yandex.practicum.filmorate.storage.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exceptions.exceptions.ReviewNotFoundException;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.storage.dao.ReviewStorage;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Objects;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class ReviewDbStorage implements ReviewStorage {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public Integer saveNew(Review review) {
        String sqlQuery = "INSERT INTO REVIEWS (content, user_id, film_id, is_positive) " +
                "VALUES (?, ?, ?, ?)";

        KeyHolder keyHolder = new GeneratedKeyHolder();

        try {
            jdbcTemplate.update(connection -> {
                PreparedStatement stmt = connection.prepareStatement(sqlQuery, new String[]{"review_id"});
                stmt.setString(1, review.getContent());
                stmt.setInt(2, review.getUserId());
                stmt.setInt(3, review.getFilmId());
                stmt.setBoolean(4, review.getIsPositive());
                return stmt;
            }, keyHolder);

        } catch (DataIntegrityViolationException e) {
            throw new DataIntegrityViolationException("В запросе неправильно указаны данные по отзыву.");
        }
        Optional<Integer> reviewId = Optional.of(Objects.requireNonNull(keyHolder.getKey()).intValue());

        return reviewId.get();
    }

    @Override
    public Integer update(Review review) {

        String sqlQuery = "UPDATE REVIEWS SET " +
                "CONTENT = ?," +
                "IS_POSITIVE = ?" +
                "WHERE REVIEW_ID = ?";
        try {
            jdbcTemplate.update(sqlQuery,
                    review.getContent(),
                    review.getIsPositive(),
                    review.getReviewId()
            );
        } catch (DataIntegrityViolationException exception) {
            throw new DataIntegrityViolationException("В запросе неправильно указаны данные об отзыве.");
        }

        return review.getReviewId();
    }

    @Override
    public void remove(int reviewId) {
        String sqlQuery = "DELETE FROM REVIEWS WHERE review_id = ?";
        jdbcTemplate.update(sqlQuery, reviewId);
    }

    @Override
    public Review findById(int reviewId) {
        String sqlQuery = "SELECT * FROM REVIEWS WHERE review_id = ?";
        return jdbcTemplate.query(sqlQuery, this::mapRowToReview, reviewId).stream().findFirst()
                .orElseThrow(() -> new ReviewNotFoundException("Отзыв c ID " + reviewId + " не найден."));
    }

    @Override
    public Collection<Review> findAll(int count) {
        String sqlQuery = "SELECT * FROM REVIEWS LIMIT ?";
        return jdbcTemplate.query(sqlQuery, this::mapRowToReview, count);
    }

    @Override
    public Collection<Review> findByFilmId(int filmId, int count) {
        String sqlQuery = "SELECT R.REVIEW_ID, R.CONTENT, R.USER_ID, R.FILM_ID, R.IS_POSITIVE, SUM(RL.USEFUL) as rating\n" +
                "FROM REVIEWS as R left join REVIEWS_LIKES RL on R.REVIEW_ID = RL.REVIEW_ID where FILM_ID = ?\n" +
                "group by R.USER_ID, R.CONTENT, R.USER_ID, R.FILM_ID, R.IS_POSITIVE order by rating DESC\n" +
                "LIMIT ?";
        return jdbcTemplate.query(sqlQuery, this::mapRowToReview, filmId, count);
    }

    @Override
    public void saveLike(int userId, int reviewId, int like) {
        String sqlQuery = "INSERT INTO REVIEWS_LIKES (USER_ID, REVIEW_ID, USEFUL) VALUES ( ?, ?, ? )";
        jdbcTemplate.update(sqlQuery, userId, reviewId, like);
    }

    @Override
    public void removeLike(int userId, int reviewId) {
        String sqlQuery = "DELETE FROM REVIEWS_LIKES WHERE USER_ID = ? AND REVIEW_ID = ?";
        jdbcTemplate.update(sqlQuery, userId, reviewId);
    }

    @Override
    public Boolean isExists(Review review) {
        String sqlQuery = "SELECT EXISTS ( SELECT * FROM REVIEWS WHERE USER_ID = ? AND FILM_ID = ? AND CONTENT = ? )";
        return Boolean.TRUE.equals(jdbcTemplate.queryForObject(sqlQuery, Boolean.TYPE, review.getUserId(),
                review.getFilmId(), review.getContent()));
    }

    @Override
    public Boolean isLikeExists(int userId, int reviewId) {
        String sqlQuery = "SELECT EXISTS ( SELECT * FROM REVIEWS_LIKES WHERE USER_ID = ? AND REVIEW_ID = ? )";
        return Boolean.TRUE.equals(jdbcTemplate.queryForObject(sqlQuery, Boolean.TYPE, userId, reviewId));
    }

    @Override
    public Boolean isDislikeExists(int userId, int reviewId) {
        String sqlQuery = "SELECT EXISTS ( SELECT * FROM REVIEWS_LIKES WHERE USER_ID = ? AND REVIEW_ID = ? AND USEFUL = -1)";
        return Boolean.TRUE.equals(jdbcTemplate.queryForObject(sqlQuery, Boolean.TYPE, userId, reviewId));
    }

    private Review mapRowToReview(ResultSet resultSet, int rowNum) throws SQLException {
        return Review.builder()
                .reviewId(resultSet.getInt("review_id"))
                .content(resultSet.getString("content"))
                .userId(resultSet.getInt("user_id"))
                .filmId(resultSet.getInt("film_id"))
                .isPositive(resultSet.getBoolean("is_positive"))
                .useful(findUseful(resultSet.getInt("review_id")))
                .build();
    }

    private int findUseful(int reviewId) {
        String sqlQuery = "SELECT SUM(USEFUL) FROM REVIEWS_LIKES WHERE review_id = ?";
        Optional<Integer> useful = Optional.ofNullable(jdbcTemplate.queryForObject(sqlQuery, Integer.class, reviewId));
        return useful.orElse(0);
    }
}