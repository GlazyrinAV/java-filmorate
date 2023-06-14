package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.dao.MpaStorage;

import java.util.Collection;

@Service
@RequiredArgsConstructor
@Slf4j
public class MpaService {

    private final MpaStorage mpaStorage;

    public Collection<Mpa> findAll() {
        Collection<Mpa> ratings = mpaStorage.findAll();
        if (ratings.isEmpty()) {
            log.info("Рейтингы отсутствуют.");
        } else {
            log.info("Рейтинги найдены.");
        }
        return ratings;
    }

    public Mpa findById(int ratingId) {
        Mpa rating;
        rating = mpaStorage.findById(ratingId);
        return rating;
    }

    public Mpa saveRatingToFilmFromDB(int filmId) {
        return mpaStorage.saveRatingToFilmFromDB(filmId);
    }
}