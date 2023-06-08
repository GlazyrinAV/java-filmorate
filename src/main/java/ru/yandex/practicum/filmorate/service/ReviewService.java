package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.exceptions.*;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.storage.dao.ReviewStorage;

import java.util.Collection;
import java.util.Objects;

@Service
@Slf4j
public class ReviewService {
    private final ReviewStorage reviewStorage;

    private final FilmService filmService;

    private final UserService userService;

    @Autowired
    public ReviewService(ReviewStorage reviewStorage, FilmService filmService, UserService userService) {
        this.reviewStorage = reviewStorage;
        this.filmService = filmService;
        this.userService = userService;
    }

    public Review saveNew(Review review) {
        if (!filmService.isExists(review.getFilmId())) {
            throw new FilmNotFoundException("Фильм c ID " + review.getFilmId() + " не найден.");
        } else if (!userService.isExists(review.getUserId())) {
            throw new UserNotFoundException("Юзер c ID " + review.getUserId() + " не найден.");
        } else if (isExists(review)) {
            log.info("Такой отзыв уже существует.");
            throw new ReviewAlreadyExistsException("Такой отзыв уже существует.");
        } else {
            log.info("Отзыв добавлен.");
            int id = reviewStorage.saveNew(review);
            return reviewStorage.findById(id);
        }
    }

    public Review update(Review review) {
        if (!isExistsByReviewId(review.getReviewId())) {
            throw new ReviewNotFoundException("Такой отзыв не существует.");
        } else {
            int id = reviewStorage.update(review);
            log.info("Отзыв обновлен.");
            return reviewStorage.findById(id);
        }
    }

    public void remove(int reviewId) {
        if (isExistsByReviewId(reviewId)) {
            log.info("Отзыв удален.");
            reviewStorage.remove(reviewId);
        } else if (!isExists(reviewStorage.findById(reviewId))) {
            throw new ReviewNotFoundException("Такой отзыв не существует.");
        } else {
            throw new ReviewNotFoundException("Отзыв c ID " + reviewId + " не найден.");
        }
    }

    public Review findById(int reviewId) {
        Review review;
        try {
            review = reviewStorage.findById(reviewId);
        } catch (EmptyResultDataAccessException exception) {
            throw new ReviewNotFoundException("Отзыв c ID " + reviewId + " не найден.");
        }
        log.info("Отзыв найден.");
        return review;
    }

    public Collection<Review> findAll(int count) {
        if (count <= 0) {
            throw new ValidationException("Значение выводимых отзывов не может быть меньше или равно нулю.");
        } else {
            Collection<Review> reviews = reviewStorage.findAll(count);
            if (reviews.isEmpty()) {
                log.info("Отзывы не найдены.");
            } else {
                log.info("Отзывы найдены.");
            }
            return reviews;
        }
    }

    public Collection<Review> findByFilmId(int filmId, int count) {
        if (count <= 0) {
            throw new ValidationException("Значение выводимых отзывов не может быть меньше или равно нулю.");
        } else if (!filmService.isExists(filmId)) {
            throw new FilmNotFoundException("Фильм c ID " + filmId + " не найден.");
        } else {
            log.info("Отзывы найдены.");
            return reviewStorage.findByFilmId(filmId, count);
        }
    }

    public void saveLike(int userId, int reviewId, String like) {
        if (!isExistsByReviewId(reviewId)) {
            throw new ReviewNotFoundException("Отзыв c ID " + reviewId + " не найден.");
        } else if (!userService.isExists(userId)) {
            throw new UserNotFoundException("Юзер c ID " + userId + " не найден.");
        } else if (reviewStorage.isLikeExists(userId, reviewId)) {
            throw new LikeAlreadyExistsException("Оценка отзыву уже поставлена.");
        } else {
            boolean opinion;
            if (like.equals("like")) {
                opinion = true;
                log.info("Отзыву поставлен лайк.");
            } else if (like.equals("dislike")) {
                opinion = false;
                log.info("Отзыву поставлен дисклайк.");
            } else {
                throw new ValidationException("Ошибка в виде оценке отзыва.");
            }
            reviewStorage.saveLike(userId, reviewId, opinion);
        }
    }

    public void removeLike(int userId, int reviewId, String like) {
        if (!isExistsByReviewId(reviewId)) {
            throw new ReviewNotFoundException("Отзыв c ID " + reviewId + " не найден.");
        } else if (!userService.isExists(userId)) {
            throw new UserNotFoundException("Юзер c ID " + userId + " не найден.");
        } else if (!reviewStorage.isLikeExists(userId, reviewId)) {
            throw new LikeAlreadyExistsException("Оценка отзыву еще не поставлена.");
        } else {
            if (like.equals("like") || like.equals("dislike")) {
                log.info("У отзыва удалена оценка.");
            } else {
                throw new ValidationException("Ошибка в виде оценке отзыва.");
            }
            reviewStorage.removeLike(userId, reviewId);
        }
    }

    public void removeDislike(int userId, int reviewId) {
        if (!isExistsByReviewId(reviewId)) {
            throw new ReviewNotFoundException("Отзыв c ID " + reviewId + " не найден.");
        } else if (!userService.isExists(userId)) {
            throw new UserNotFoundException("Юзер c ID " + userId + " не найден.");
        } else if (!reviewStorage.isDislikeExists(userId, reviewId)) {
            throw new LikeAlreadyExistsException("У отзыва отсутствует отрицательная оценка от пользователя.");
        } else {
            log.info("У отзыва удалена отрицательная оценка от пользователя.");
            reviewStorage.removeLike(userId, reviewId);
        }
    }

    public Boolean isExists(Review review) {
        return reviewStorage.isExists(review);
    }

    private Boolean isExistsByReviewId(int reviewId) {
        return Objects.equals(findById(reviewId).getReviewId(), reviewId);
    }
}