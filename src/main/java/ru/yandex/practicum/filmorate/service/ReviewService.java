package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.exceptions.LikeAlreadyExistsException;
import ru.yandex.practicum.filmorate.exceptions.exceptions.ReviewAlreadyExistsException;
import ru.yandex.practicum.filmorate.exceptions.exceptions.ReviewNotFoundException;
import ru.yandex.practicum.filmorate.exceptions.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.storage.dao.ReviewStorage;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;

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
        filmService.findById(review.getFilmId());
        userService.findById(review.getUserId());
        if (isExists(review)) {
            log.info("Такой отзыв уже существует.");
            throw new ReviewAlreadyExistsException("Такой отзыв уже существует.");
        }

        log.info("Отзыв добавлен.");
        int id = reviewStorage.saveNew(review);
        return reviewStorage.findById(id);
    }

    public Review update(Review review) {
        findById(review.getReviewId());
        int id = reviewStorage.update(review);
        log.info("Отзыв обновлен.");
        return reviewStorage.findById(id);
    }

    public void remove(int reviewId) {
        findById(reviewId);
        log.info("Отзыв удален.");
        reviewStorage.remove(reviewId);
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
        }
        ArrayList<Review> reviews = new ArrayList<>(reviewStorage.findAll(count));
        reviews.sort(Comparator.comparing(Review::getUseful).reversed().thenComparing(Review::getReviewId));
        return reviews;
    }

    public Collection<Review> findByFilmId(int filmId, int count) {
        if (count <= 0) {
            throw new ValidationException("Значение выводимых отзывов не может быть меньше или равно нулю.");
        }
        filmService.findById(filmId);

        log.info("Отзывы найдены.");
        return reviewStorage.findByFilmId(filmId, count);
    }

    public void saveLike(int userId, int reviewId, String like) {
        userService.findById(userId);
        if (reviewStorage.isLikeExists(userId, reviewId)) {
            throw new LikeAlreadyExistsException("Оценка отзыву уже поставлена.");
        }
        findById(reviewId);

        int opinion;
        if (like.equals("like")) {
            opinion = 1;
            log.info("Отзыву поставлен лайк.");
        } else if (like.equals("dislike")) {
            opinion = -1;
            log.info("Отзыву поставлен дисклайк.");
        } else {
            throw new ValidationException("Ошибка в виде оценке отзыва.");
        }
        reviewStorage.saveLike(userId, reviewId, opinion);
    }

    public void removeLike(int userId, int reviewId, String like) {
        userService.findById(userId);
        if (!reviewStorage.isLikeExists(userId, reviewId)) {
            throw new LikeAlreadyExistsException("Оценка отзыву еще не поставлена.");
        }
        findById(reviewId);

        if (like.equals("like") || like.equals("dislike")) {
            log.info("У отзыва удалена оценка.");
        } else {
            throw new ValidationException("Ошибка в виде оценке отзыва.");
        }
        reviewStorage.removeLike(userId, reviewId);
    }

    public void removeDislike(int userId, int reviewId) {
        userService.findById(userId);
        if (!reviewStorage.isDislikeExists(userId, reviewId)) {
            throw new LikeAlreadyExistsException("У отзыва отсутствует отрицательная оценка от пользователя.");
        }
        findById(reviewId);

        log.info("У отзыва удалена отрицательная оценка от пользователя.");
        reviewStorage.removeLike(userId, reviewId);
    }

    public Boolean isExists(Review review) {
        return reviewStorage.isExists(review);
    }
}