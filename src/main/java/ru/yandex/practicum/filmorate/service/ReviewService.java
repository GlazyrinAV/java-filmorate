package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.exceptions.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exceptions.exceptions.ReviewNotFoundException;
import ru.yandex.practicum.filmorate.exceptions.exceptions.UserNotFoundException;
import ru.yandex.practicum.filmorate.exceptions.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.storage.dao.ReviewStorage;

import java.util.Collection;

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
            log.info("Фильм c ID " + review.getFilmId() + " не найден.");
            throw new FilmNotFoundException("Фильм c ID " + review.getFilmId() + " не найден.");
        } else if (!userService.isExists(review.getUserId())) {
            log.info("Юзер c ID " + review.getUserId() + " не найден.");
            throw new UserNotFoundException("Юзер c ID " + review.getUserId() + " не найден.");
        } else {
            log.info("Отзыв добавлен.");
            int id = reviewStorage.saveNew(review);
            return reviewStorage.findById(id);
        }
    }

    public Review update(Review review) {
        if (!filmService.isExists(review.getFilmId())) {
            log.info("Фильм c ID " + review.getFilmId() + " не найден.");
            throw new FilmNotFoundException("Фильм c ID " + review.getFilmId() + " не найден.");
        } else if (!userService.isExists(review.getUserId())) {
            log.info("Юзер c ID " + review.getUserId() + " не найден.");
            throw new UserNotFoundException("Юзер c ID " + review.getUserId() + " не найден.");
        } else {
            int id = reviewStorage.update(review);
            log.info("Отзыв обновлен.");
            return reviewStorage.findById(id);
        }
    }

    public void delete(int reviewId) {
        if (reviewStorage.isExists(reviewId)) {
            log.info("Отзыв удален.");
            reviewStorage.delete(reviewId);
        } else {
            log.info("Отзыв c ID " + reviewId + " не найден.");
            throw new ReviewNotFoundException("Отзыв c ID " + reviewId + " не найден.");
        }
    }

    public Review findById(int reviewId) {
        if (reviewStorage.isExists(reviewId)) {
            log.info("Отзыв найден.");
            return reviewStorage.findById(reviewId);
        } else {
            log.info("Отзыв c ID " + reviewId + " не найден.");
            throw new ReviewNotFoundException("Отзыв c ID " + reviewId + " не найден.");
        }
    }

    public Collection<Review> findAll(int count) {
        if (count <= 0) {
            throw new ValidationException("Значение выводимых отзывов не может быть меньше или равно нулю.");
        } else {
            log.info("Отзывы найдены.");
            return reviewStorage.findAll(count);
        }
    }

    public Collection<Review> findByFilmId(int filmId, int count) {
        if (count <= 0) {
            throw new ValidationException("Значение выводимых отзывов не может быть меньше или равно нулю.");
        } else if (!filmService.isExists(filmId)) {
            log.info("Фильм c ID " + filmId + " не найден.");
            throw new FilmNotFoundException("Фильм c ID " + filmId + " не найден.");
        } else {
            log.info("Отзывы найдены.");
            return reviewStorage.findByFilmId(filmId, count);
        }
    }

    public void saveLike(int userId, int reviewId, boolean like) {
        if (!reviewStorage.isExists(reviewId)) {
            log.info("Отзыв c ID " + reviewId + " не найден.");
            throw new ReviewNotFoundException("Отзыв c ID " + reviewId + " не найден.");
        } else if (!userService.isExists(userId)) {
            log.info("Юзер c ID " + userId + " не найден.");
            throw new UserNotFoundException("Юзер c ID " + userId + " не найден.");
        } else {
            log.info("Отзыву поставлена оценка.");
            reviewStorage.saveLike(userId, reviewId, like);
        }
    }

    public void removeLike(int userId, int reviewId) {
        if (!reviewStorage.isExists(reviewId)) {
            log.info("Отзыв c ID " + reviewId + " не найден.");
            throw new ReviewNotFoundException("Отзыв c ID " + reviewId + " не найден.");
        } else if (!userService.isExists(userId)) {
            log.info("Юзер c ID " + userId + " не найден.");
            throw new UserNotFoundException("Юзер c ID " + userId + " не найден.");
        } else {
            log.info("У отзыва удалена оценка.");
            reviewStorage.removeLike(userId, reviewId);
        }
    }
}