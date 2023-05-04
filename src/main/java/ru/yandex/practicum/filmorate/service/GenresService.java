package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.util.Collection;

@Service
@Slf4j
public class GenresService {

    private final FilmStorage filmStorage;

    public GenresService(@Qualifier("FilmDbStorage") FilmStorage filmStorage) {
        this.filmStorage = filmStorage;
    }

    public Collection<Genre> findAllGenres() {
        return filmStorage.findAllGenres();
    }

    public Genre findGenreById(int genreId) {
        return filmStorage.findGenreById(genreId);
    }

}
