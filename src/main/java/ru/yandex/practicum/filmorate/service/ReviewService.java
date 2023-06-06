package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.exceptions.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exceptions.exceptions.ReviewNotFoundException;
import ru.yandex.practicum.filmorate.exceptions.exceptions.UserNotFoundException;
import ru.yandex.practicum.filmorate.exceptions.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.storage.dao.FilmStorage;
import ru.yandex.practicum.filmorate.storage.dao.ReviewStorage;
import ru.yandex.practicum.filmorate.storage.dao.UserStorage;

import java.util.Collection;

@Service
@Slf4j
public class ReviewService {
    private final ReviewStorage reviewStorage;
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;

    @Autowired
    public ReviewService(ReviewStorage reviewStorage, @Qualifier("FilmDbStorage") FilmStorage filmStorage,
                         @Qualifier("UserDbStorage") UserStorage userStorage) {
        this.reviewStorage = reviewStorage;
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
    }

    public Review saveNew(Review review) {
        if (!filmStorage.isExists(review.getFilmId())) {
            log.info("Фильм c ID " + review.getFilmId() + " не найден.");
            throw new FilmNotFoundException("Фильм c ID " + review.getFilmId() + " не найден.");
        } else if (!userStorage.isExists(review.getUserId())) {
            log.info("Юзер c ID " + review.getUserId() + " не найден.");
            throw new UserNotFoundException("Юзер c ID " + review.getUserId() + " не найден.");
        } else {
            log.info("Отзыв добавлен.");
            return reviewStorage.saveNew(review);
        }
    }

    public Review update(Review review) {
        if (!filmStorage.isExists(review.getFilmId())) {
            log.info("Фильм c ID " + review.getFilmId() + " не найден.");
            throw new FilmNotFoundException("Фильм c ID " + review.getFilmId() + " не найден.");
        } else if (!userStorage.isExists(review.getUserId())) {
            log.info("Юзер c ID " + review.getUserId() + " не найден.");
            throw new UserNotFoundException("Юзер c ID " + review.getUserId() + " не найден.");
        } else {
            log.info("Отзыв обновлен.");
            return reviewStorage.update(review);
        }
    }

    public void delete(int reviewId) {
        if (reviewId <= 0) {
            log.info("Указанный ID отзыва меньше или равен нулю.");
            throw new ValidationException("ID отзыва не может быть меньше или равно нулю.");
        } else if (reviewStorage.isExists(reviewId)) {
            log.info("Отзыв удален.");
            reviewStorage.delete(reviewId);
        } else {
            log.info("Отзыв c ID " + reviewId + " не найден.");
            throw new ReviewNotFoundException("Отзыв c ID " + reviewId + " не найден.");
        }
    }

    public Review findById(int reviewId) {
        if (reviewId <= 0) {
            log.info("Указанный ID отзыва меньше или равен нулю.");
            throw new ValidationException("ID отзыва не может быть меньше или равно нулю.");
        } else if (reviewStorage.isExists(reviewId)) {
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
        if (filmId <= 0) {
            log.info("Указанный ID фильма меньше или равен нулю.");
            throw new ValidationException("ID фильма не может быть меньше или равно нулю.");
        } else if (count <= 0) {
            throw new ValidationException("Значение выводимых отзывов не может быть меньше или равно нулю.");
        } else if (!filmStorage.isExists(filmId)) {
            log.info("Фильм c ID " + filmId + " не найден.");
            throw new FilmNotFoundException("Фильм c ID " + filmId + " не найден.");
        } else {
            log.info("Отзывы найдены.");
            return reviewStorage.findByFilmId(filmId, count);
        }
    }

    public void saveLike(int userId, int reviewId, boolean like) {
        if (userId <= 0) {
            log.info("Указанный ID юзера меньше или равен нулю.");
            throw new ValidationException("ID юзера не может быть меньше или равно нулю.");
        } else if (reviewId <= 0) {
            log.info("Указанный ID отзыва меньше или равен нулю.");
            throw new ValidationException("ID отзыва не может быть меньше или равно нулю.");
        } else if (!reviewStorage.isExists(reviewId)) {
            log.info("Отзыв c ID " + reviewId + " не найден.");
            throw new ReviewNotFoundException("Отзыв c ID " + reviewId + " не найден.");
        } else if (!userStorage.isExists(userId)) {
            log.info("Юзер c ID " + userId + " не найден.");
            throw new UserNotFoundException("Юзер c ID " + userId + " не найден.");
        } else {
            log.info("Отзыву поставлена оценка.");
            reviewStorage.saveLike(userId, reviewId, like);
        }
    }

    public void removeLike(int userId, int reviewId) {
        if (userId <= 0) {
            log.info("Указанный ID юзера меньше или равен нулю.");
            throw new ValidationException("ID юзера не может быть меньше или равно нулю.");
        } else if (reviewId <= 0) {
            log.info("Указанный ID отзыва меньше или равен нулю.");
            throw new ValidationException("ID отзыва не может быть меньше или равно нулю.");
        } else if (!reviewStorage.isExists(reviewId)) {
            log.info("Отзыв c ID " + reviewId + " не найден.");
            throw new ReviewNotFoundException("Отзыв c ID " + reviewId + " не найден.");
        } else if (!userStorage.isExists(userId)) {
            log.info("Юзер c ID " + userId + " не найден.");
            throw new UserNotFoundException("Юзер c ID " + userId + " не найден.");
        } else {
            log.info("У отзыва удалена оценка.");
            reviewStorage.removeLike(userId, reviewId);
        }
    }
}