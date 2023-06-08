package ru.yandex.practicum.filmorate.storage.dao;

import ru.yandex.practicum.filmorate.model.Rating;

import java.util.List;

public interface RatingStorage {

    List<Rating> findAll();

    Rating findById(int ratingId);

    Rating saveRatingToFilmFromDB(int filmId);
}