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

    public void addLike(int filmId) {
        if (filmId <= 0) {
            throw new ValidationException("ID не может быть меньше или равно нулю.");
        } else if (storage.findFilm(filmId) == null) {
            throw new ValidationException("Фильм c ID " + filmId + " не найден.");
        } else {
            storage.findFilm(filmId).getLiked().add(filmId);
        }
    }

    public void removeLike(int filmId) {
        if (filmId <= 0) {
            throw new ValidationException("ID не может быть меньше или равно нулю.");
        } else if (storage.findFilm(filmId) == null) {
            throw new ValidationException("Фильм c ID " + filmId + " не найден.");
        } else {
            storage.findFilm(filmId).getLiked().remove(filmId);
        }
    }

    public Collection<Film> getLikes(int count) {
        if (count <= 0) {
            throw new ValidationException("ID не может быть меньше или равно нулю.");
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