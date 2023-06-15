package ru.yandex.practicum.filmorate.storage.dao;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.SortType;

import java.util.Collection;

public interface FilmStorage {

    Integer saveNew(Film film);

    Integer update(Film film);

    Collection<Film> findAll();

    Film findById(int filmId);

    Collection<Film> findPopular(int count);

    Collection<Film> findPopularByGenreAndYear(int count, int genreId, int year);

    Collection<Film> findPopularByGenre(int count, int genreId);

    Collection<Film> findPopularByYear(int count, int year);

    void removeFilm(int filmId);

    Collection<Film> findByDirectorId(int directorId, SortType sortBy);

    void saveScore(int filmId, int userId, int score);

    void removeScore(int filmId, int userId);

    Double findScore(int filmId);

    Collection<Film> findCommonFilms(int userId, int friendId);

    Collection<Film> getRecommendation(int id);

    Collection<Film> searchByFilmAndDirector(String query);

    Collection<Film> searchByTitle(String query);

    Collection<Film> searchByDirector(String query);

}