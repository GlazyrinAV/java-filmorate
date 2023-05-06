package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Rating;

import java.util.Collection;

public interface FilmStorage {

    Film addNew(Film film);

    Film update(Film film);

    Collection<Film> findAll();

    Film findById(int filmId);

    Collection<Film> findPopular(int count);

    void addLike(int filmId, int userId);

    void removeLike(int filmId, int userId);

    Collection<Rating> findAllRatings();

    Rating findRatingById(int ratingId);

    Collection<Genre> findAllGenres();

    Genre findGenreById(int genreId);

    Collection<Integer> getLikes(int filmId);

    Boolean isExists(int filmId);
}