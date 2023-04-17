package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.InMemoryFilmStorage;

import java.util.Collection;
import java.util.stream.Collectors;

@Service
public class FilmService {

    @Autowired
    InMemoryFilmStorage storage;

    public void addLike(int filmId, int userId) {
        storage.findFilm(filmId).getLiked().add(userId);
    }

    public void removeLike(int filmId, int userId) {
        storage.findFilm(filmId).getLiked().remove(userId);
    }

    public Collection<Film> getPopularFilms(int count) {
        if (count <= 0) {
            throw new ValidationException("Значение выводимых фильмов не может быть меньше или равно нулю.");
        } else {
            return storage.findAllFilms().stream()
                    .sorted(this::compare)
                    .limit(count)
                    .collect(Collectors.toList());
        }
    }

    private int compare(Film p0, Film p1) {
        return p0.getLiked().size() - (p1.getLiked().size());
    }
}