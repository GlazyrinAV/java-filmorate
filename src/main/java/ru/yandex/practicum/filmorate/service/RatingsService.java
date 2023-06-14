package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.dao.RatingStorage;

import java.util.Collection;

@Service
@RequiredArgsConstructor
@Slf4j
public class RatingsService {

    private final RatingStorage ratingStorage;

    public Collection<Mpa> findAll() {
        Collection<Mpa> ratings = ratingStorage.findAll();
        if (ratings.isEmpty()) {
            log.info("Рейтингы отсутствуют.");
        } else {
            log.info("Рейтинги найдены.");
        }
        return ratings;
    }

    public Mpa findById(int ratingId) {
        Mpa rating;
        rating = ratingStorage.findById(ratingId);
        return rating;
    }

    public Mpa saveRatingToFilmFromDB(int filmId) {
        return ratingStorage.saveRatingToFilmFromDB(filmId);
    }
}