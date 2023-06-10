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
import ru.yandex.practicum.filmorate.storage.dao.FeedStorage;
import ru.yandex.practicum.filmorate.storage.dao.ReviewStorage;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Optional;

@Service
@Slf4j
public class ReviewService {
    private final ReviewStorage reviewStorage;
    private final FilmService filmService;
    private final UserService userService;
    private final FeedStorage feedStorage;

    @Autowired
    public ReviewService(ReviewStorage reviewStorage, FilmService filmService, UserService userService,
                         FeedStorage feedStorage) {
        this.reviewStorage = reviewStorage;
        this.filmService = filmService;
        this.userService = userService;
        this.feedStorage = feedStorage;
    }

    public Review saveNew(Review review) {
        filmService.findById(review.getFilmId());
        userService.findById(review.getUserId());
        if (isExists(review)) {
            log.info("Такой отзыв уже существует.");
            throw new ReviewAlreadyExistsException("Такой отзыв уже существует.");
        } else {
            log.info("Отзыв добавлен.");
            int id = reviewStorage.saveNew(review);
            feedStorage.saveFeed(review.getUserId(), id, 2, 2);
            return reviewStorage.findById(id);
        }
    }

    public Review update(Review review) {
        findById(review.getReviewId());
        int id = reviewStorage.update(review);
        log.info("Отзыв обновлен.");
        Review updatedReviews = reviewStorage.findById(id);
        feedStorage.saveFeed(updatedReviews.getUserId(), id, 2, 3);
        return updatedReviews;
    }

    public void remove(int reviewId) {
        Review reviewToDelete = findById(reviewId);
        log.info("Отзыв удален.");
        reviewStorage.remove(reviewId);
        feedStorage.saveFeed(reviewToDelete.getUserId(), reviewId, 2, 1);
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

    public void saveLike(int userId, int reviewId, Optional<String> like) {
        userService.findById(userId);
        findById(reviewId);
        if (like.isEmpty()) {
            throw new ValidationException("Ошибка в виде оценке отзыва.");
        } else if (reviewStorage.isLikeExists(userId, reviewId)) {
            throw new LikeAlreadyExistsException("Оценка отзыву уже поставлена.");
        }
        int opinion;
        if (like.get().equals("like")) {
            opinion = 1;
            log.info("Отзыву поставлен лайк.");
        } else if (like.get().equals("dislike")) {
            opinion = -1;
            log.info("Отзыву поставлен дисклайк.");
        } else {
            throw new ValidationException("Ошибка в виде оценке отзыва.");
        }
        reviewStorage.saveLike(userId, reviewId, opinion);
    }

    public void removeLike(int userId, int reviewId, Optional<String> like) {
        userService.findById(userId);
        findById(reviewId);
        if (like.isEmpty()) {
            throw new ValidationException("Ошибка в форме запроса на удаление лайука у отзыва.");
        } else if (!(like.get().equals("like") || like.get().equals("dislike"))) {
            throw new ValidationException("Ошибка в виде оценке отзыва.");
        } else if (!reviewStorage.isLikeExists(userId, reviewId)) {
            throw new LikeAlreadyExistsException("Оценка отзыву еще не поставлена.");
        } else {
            log.info("У отзыва удалена оценка.");
        }
        reviewStorage.removeLike(userId, reviewId);
    }

    public void removeDislike(int userId, int reviewId) {
        userService.findById(userId);
        findById(reviewId);
        if (!reviewStorage.isDislikeExists(userId, reviewId)) {
            throw new LikeAlreadyExistsException("У отзыва отсутствует отрицательная оценка от пользователя.");
        }

        log.info("У отзыва удалена отрицательная оценка от пользователя.");
        reviewStorage.removeLike(userId, reviewId);
    }

    public Boolean isExists(Review review) {
        return reviewStorage.isExists(review);
    }
}