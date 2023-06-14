package ru.yandex.practicum.filmorate.storage.dao;

import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.List;

public interface RatingStorage {

    List<Mpa> findAll();

    Mpa findById(int ratingId);

    Mpa saveRatingToFilmFromDB(int filmId);
}