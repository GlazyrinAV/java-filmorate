package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.exceptions.GenreNotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.dao.GenresStorage;

import java.util.Collection;
import java.util.List;

@Service
@Slf4j
public class GenresService {

    private final GenresStorage genresStorage;

    @Autowired
    public GenresService(GenresStorage genresStorage) {
        this.genresStorage = genresStorage;
    }

    public Collection<Genre> findAllGenres() {
        Collection<Genre> genres = genresStorage.findAll();
        if (genres.isEmpty()) {
            log.info("Жанры не найдены.");
        } else {
            log.info("Жанры найдены.");
        }
        return genres;
    }

    public Genre findGenreById(int genreId) {
        Genre genre;
        try {
            genre = genresStorage.findById(genreId);
        } catch (EmptyResultDataAccessException exception) {
            throw new GenreNotFoundException("Жанр с ID " + genreId + " не найден.");
        }
        return genre;
    }

    public void saveGenresToDBFromFilm(List<Genre> genres, int filmId) {
        try {
            genresStorage.saveGenresToDBFromFilm(genres, filmId);
        } catch (DataIntegrityViolationException exception) {
            throw new DataIntegrityViolationException("В запросе неправильно указаны данные о фильме.");
        }
    }

    public void removeFilmGenres(int filmId) {
        genresStorage.removeFilmGenres(filmId);
    }

    public List<Genre> saveGenresToFilmFromDB(int filmId) {
        return genresStorage.saveGenresToFilmFromDB(filmId);
    }
}