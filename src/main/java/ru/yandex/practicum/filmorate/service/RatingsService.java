package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Rating;
import ru.yandex.practicum.filmorate.storage.dao.RatingStorage;

import java.util.Collection;

@Service
@RequiredArgsConstructor
@Slf4j
public class RatingsService {

    private final RatingStorage ratingStorage;

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
        rating = ratingStorage.findById(ratingId);
        return rating;
    }

    public Rating saveRatingToFilmFromDB(int filmId) {
        return ratingStorage.saveRatingToFilmFromDB(filmId);
    }
}