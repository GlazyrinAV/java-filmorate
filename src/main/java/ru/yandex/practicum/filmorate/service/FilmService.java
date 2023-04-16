package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.storage.InMemoryFilmStorage;

@Service
public class FilmService {

    @Autowired
    InMemoryFilmStorage storage;

}