package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.exceptions.NoResultDataAccessException;
import ru.yandex.practicum.filmorate.model.Rating;
import ru.yandex.practicum.filmorate.storage.dao.RatingStorage;

import java.util.Collection;

@Service
@Slf4j
public class RatingsService {

    private final RatingStorage ratingStorage;

    @Autowired
    public RatingsService(RatingStorage ratingStorage) {
        this.ratingStorage = ratingStorage;
    }

    public Collection<Rating> findAll() {
        Collection<Rating> ratings = ratingStorage.findAll();
        if (ratings.isEmpty()) {
            log.info("Рейтингы отсутствуют.");
        } else {
            log.info("Рейтинги найдены.");
        }
        return ratings;
    }

    public Rating findById(int ratingId) {
        Rating rating;
        try {
            rating = ratingStorage.findById(ratingId);
        } catch (EmptyResultDataAccessException exception) {
            throw new NoResultDataAccessException("Запрос на получение рейтинга вернул пустой результат.", 1);
        }
        return rating;
    }

    public Rating saveRatingToFilmFromDB(int filmId) {
        return ratingStorage.saveRatingToFilmFromDB(filmId);
    }
}