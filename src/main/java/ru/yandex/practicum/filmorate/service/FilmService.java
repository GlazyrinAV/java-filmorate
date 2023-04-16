package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.storage.InMemoryFilmStorage;

import java.util.Collection;

@Service
public class FilmService {

    @Autowired
    InMemoryFilmStorage storage;

    public void addLike(int filmId) {

    }

    public void removeLike(int filmId) {

    }

    public Collection<Integer> getLikes(int filmId, int count) {

    }
}