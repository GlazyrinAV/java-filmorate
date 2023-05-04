package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Rating;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.util.Collection;

@Service
@Slf4j
public class RatingsService {

    private final FilmStorage filmStorage;

    public RatingsService(@Qualifier("FilmDbStorage") FilmStorage filmStorage) {
        this.filmStorage = filmStorage;
    }

    public Collection<Rating> findAllFilmRatings() {
        return filmStorage.findAllFilmRatings();
    }

    public Rating findRatingById(int ratingId) {
        return filmStorage.findRatingById(ratingId);
    }
}
