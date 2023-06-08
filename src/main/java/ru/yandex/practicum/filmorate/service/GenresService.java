package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
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
        return genresStorage.findAll();
    }

    public Genre findGenreById(int genreId) {
        return genresStorage.findById(genreId);
    }

    public void addFilmGenresToDB(List<Genre> genres, int filmId) {
        genresStorage.saveFilmGenresToDB(genres, filmId);
    }

    public void removeFilmGenres(int filmId) {
        genresStorage.removeFilmGenres(filmId);
    }

    public List<Genre> saveGenresToFilmFromDB(int filmId) {
        return genresStorage.saveGenresToFilmFromDB(filmId);
    }
}