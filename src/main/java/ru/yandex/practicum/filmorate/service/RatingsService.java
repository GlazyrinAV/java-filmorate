package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Rating;
import ru.yandex.practicum.filmorate.storage.dao.RatingStorage;

import java.util.Collection;

@Service
@Slf4j
public class RatingsService {

    private final RatingStorage ratingStorage;

    public RatingsService(RatingStorage ratingStorage) {
        this.ratingStorage = ratingStorage;
    }

    public Collection<Rating> findAll() {
        return ratingStorage.findAll();
    }

    public Rating findById(int ratingId) {
        return ratingStorage.findById(ratingId);
    }
}