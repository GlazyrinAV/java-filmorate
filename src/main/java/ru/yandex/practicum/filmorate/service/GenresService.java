package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.dao.GenresStorage;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

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

    public Genre findById(int genreId) {
        Genre genre;
        genre = genresStorage.findById(genreId);
        return genre;
    }

    public void saveGenresToDBFromFilm(Optional<List<Genre>> genres, int filmId) {
        genres.ifPresent(genreList -> genresStorage.saveGenresToDBFromFilm(genreList, filmId));
    }

    public void removeFilmGenres(int filmId) {
        genresStorage.removeFilmGenres(filmId);
    }

    public List<Genre> saveGenresToFilmFromDB(int filmId) {
        return genresStorage.saveGenresToFilmFromDB(filmId);
    }
}