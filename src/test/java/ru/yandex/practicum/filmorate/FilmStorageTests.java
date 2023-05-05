package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Rating;
import ru.yandex.practicum.filmorate.storage.FilmDbStorage;

import java.time.Duration;
import java.time.LocalDate;
import java.util.List;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class FilmStorageTests {

    @Autowired
    FilmDbStorage filmStorage;

    @Test
    public void createFilmNormal() {
        Film film = new Film("abc", "ddd", LocalDate.of(2020, 1, 1),
                Duration.ofMinutes(100), null, List.of(new Genre(1, null)), new Rating(1, null));
        filmStorage.addNewFilm(film);
        Assertions.assertEquals("Film(liked=[], name=abc, description=ddd, releaseDate=2020-01-01," +
                " duration=PT1H40M, id=1, genres=[Genre(id=1, name=Комедия)], mpa=Rating(id=1, name=G))",
                filmStorage.findFilm(1).toString());
    }
}
