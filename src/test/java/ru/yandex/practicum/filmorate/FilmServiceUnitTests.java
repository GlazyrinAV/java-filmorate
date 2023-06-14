package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import ru.yandex.practicum.filmorate.exceptions.exceptions.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exceptions.exceptions.UserNotFoundException;
import ru.yandex.practicum.filmorate.exceptions.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.*;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.storage.dao.FeedStorage;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import java.time.LocalDate;
import java.time.Month;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Sql(value = {"/schema.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(value = {"/data.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(value = {"/dataForFilmTests.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
class FilmServiceUnitTests {

    private final Validator validator;
    private final FilmService filmService;
    private final FeedStorage feedStorage;

    static Stream<Film> filmWithWrongParameters() {
        return Stream.of(
                new Film("", "adipisicing", LocalDate.of(1967, Month.APRIL, 25), 100L, null, List.of(new Genre(1, null)), new Mpa(1, null), new ArrayList<>(), null),
                new Film("name", "adipisicing", LocalDate.of(1800, Month.APRIL, 25), 100L, null, List.of(new Genre(1, null)), new Mpa(1, null), new ArrayList<>(), null),
                new Film("name", "adipisicing", LocalDate.of(1967, Month.APRIL, 25), -100L, null, List.of(new Genre(1, null)), new Mpa(1, null), new ArrayList<>(), null),
                new Film("name", "nnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnn" +
                        "nnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnn",
                        LocalDate.of(1967, Month.APRIL, 25), 100L, null, List.of(new Genre(1, null)), new Mpa(1, null), List.of(new Director(1, null)), null)
        );
    }

    private final static Score scoreNormal = new Score(1, 1, 5);
    private final static Score scoreZeroFilmId = new Score(0, 1, 5);
    private final static Score scoreNegativeFilmId = new Score(-1, 1, 5);
    private final static Score scoreWrongFilmId = new Score(99, 1, 5);
    private final static Score scoreZeroUserId = new Score(1, 0, 5);
    private final static Score scoreNegativeUserId = new Score(1, -1, 5);
    private final static Score scoreWrongUserId = new Score(1, 99, 5);

    @Test
    public void createFilmNormal() {
        Film film = new Film("Name", "Description", LocalDate.now(), 100L, null, List.of(new Genre(1, null)), new Mpa(1, null), new ArrayList<>(), null);
        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        violations.stream().map(ConstraintViolation::getMessage)
                .forEach(System.out::println);
        Assertions.assertSame(0, violations.size(), "Ошибка при создании нормального фильма.");
    }

    @ParameterizedTest
    @MethodSource("filmWithWrongParameters")
    void postFilmsWithErrorData(Film film) {
        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        violations.stream().map(ConstraintViolation::getMessage)
                .forEach(System.out::println);
        Assertions.assertSame(1, violations.size(), "Ошибка при выявлении ошибок в данных фильма.");
    }

    @Test
    void getFilmsEmpty() {
        Assertions.assertTrue(filmService.findAll().isEmpty(), "Ошибка при получении пустого хранилища фильмов.");
    }

    @Test
    public void getAllFilmsNormal() {
        Assertions.assertEquals("[Film(name=Name, description=Description, releaseDate=1990-04-13, " +
                        "duration=100, id=1, genres=[Genre(id=1, name=Комедия)], " +
                        "mpa=Rating(id=1, name=G), directors=[])]", filmService.findAll().toString(),
                "Ошибка при получении из хранилища существующего фильма.");
    }

    @Test
    public void findByIdFilmNormal() {
        Assertions.assertEquals("Film(name=Name, description=Description, releaseDate=1990-04-13, " +
                        "duration=100, id=1, genres=[Genre(id=1, name=Комедия)], " +
                        "mpa=Rating(id=1, name=G), directors=[])", filmService.findById(1).toString(),
                "Ошибка при получении из хранилища существующего фильма.");
    }

    @Test
    public void getFilmWithWrongId() {
        FilmNotFoundException exception = Assertions.assertThrows(FilmNotFoundException.class,
                () -> filmService.findById(99));
        Assertions.assertEquals("Фильм c ID 99 не найден.", exception.getMessage(),
                "Ошибка при получении из хранилища фильма с неправильным ID.");
    }

    @Test
    public void addNewFilmNormal() {
        Film film = new Film("Name", "Description", LocalDate.of(1990, Month.APRIL, 13), 100L, 1, List.of(new Genre(1, "Комедия")), new Mpa(1, "G"), new ArrayList<>(), null);
        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        Assertions.assertEquals(violations.size(), 0, "Ошибка при добавлении в хранилище нормального фильма.");
    }

    @Test
    public void addScoreNormal() {
        filmService.saveScore(scoreNormal);
        Assertions.assertEquals("[1]", filmService.findScore(1).toString(), "Ошибка при нормальном добавлении лайка.");

        List<Feed> feeds = new ArrayList<>(feedStorage.findFeed(1));
        Assertions.assertEquals(1, feeds.size());
        Feed feed = feeds.get(0);
        Assertions.assertEquals(1, feed.getEventId());
        Assertions.assertEquals(1, feed.getUserId());
        Assertions.assertEquals(1, feed.getEntityId());
        Assertions.assertEquals(1, feed.getEventType().getEventTypeId());
        Assertions.assertEquals(2, feed.getOperation().getOperationId());
    }

    @Test
    public void addScoreWithWrongFilmDataId0() {
        FilmNotFoundException exception = Assertions.assertThrows(FilmNotFoundException.class,
                () -> filmService.saveScore(scoreZeroFilmId));
        Assertions.assertEquals("Фильм c ID 0 не найден.", exception.getMessage(),
                "Ошибка при добавлении лайка к фильму с ID0.");
    }

    @Test
    public void addScoreWithWrongFilmDataNegativeId() {
        FilmNotFoundException exception = Assertions.assertThrows(FilmNotFoundException.class,
                () -> filmService.saveScore(scoreNegativeFilmId));
        Assertions.assertEquals("Фильм c ID -1 не найден.", exception.getMessage(),
                "Ошибка при добавлении лайка к фильму с ID-1.");
    }

    @Test
    public void addScoreWithWrongFilmDataNoSuchFilm() {
        FilmNotFoundException exception = Assertions.assertThrows(FilmNotFoundException.class,
                () -> filmService.saveScore(scoreWrongFilmId));
        Assertions.assertEquals("Фильм c ID 99 не найден.", exception.getMessage(),
                "Ошибка при добавлении лайка к фильму с ID99.");
    }

    @Test
    public void addScoreWithWrongUserDataId0() {
        UserNotFoundException exception = Assertions.assertThrows(UserNotFoundException.class,
                () -> filmService.saveScore(scoreZeroUserId));
        Assertions.assertEquals("Пользователь c ID 0 не найден.", exception.getMessage(),
                "Ошибка при добавлении лайка к фильму с юзером ID0.");
    }

    @Test
    public void addScoreWithWrongUserDataNegativeId() {
        UserNotFoundException exception = Assertions.assertThrows(UserNotFoundException.class,
                () -> filmService.saveScore(scoreNegativeUserId));
        Assertions.assertEquals("Пользователь c ID -1 не найден.", exception.getMessage(),
                "Ошибка при добавлении лайка к фильму с юзером ID-1.");
    }

    @Test
    public void addScoreWithWrongUserDataNoSuchFilm() {
        UserNotFoundException exception = Assertions.assertThrows(UserNotFoundException.class,
                () -> filmService.saveScore(scoreWrongUserId));
        Assertions.assertEquals("Пользователь c ID 99 не найден.", exception.getMessage(),
                "Ошибка при добавлении лайка к фильму с юзером ID99.");
    }

    @Test
    public void removeScoreNormal() {
        filmService.removeScore(2, 2);
        Assertions.assertEquals(0, filmService.findScore(2),
                "Ошибка при нормальном удалении лайка.");

        List<Feed> feeds = new ArrayList<>(feedStorage.findFeed(1));
        Assertions.assertEquals(2, feeds.size());
        Feed feed = feeds.get(0);
        Assertions.assertEquals(1, feed.getEventId());
        Assertions.assertEquals(1, feed.getUserId());
        Assertions.assertEquals(1, feed.getEntityId());
        Assertions.assertEquals(1, feed.getEventType().getEventTypeId());
        Assertions.assertEquals(2, feed.getOperation().getOperationId());
        Feed secondFeed = feeds.get(1);
        Assertions.assertEquals(2, secondFeed.getEventId());
        Assertions.assertEquals(1, secondFeed.getUserId());
        Assertions.assertEquals(1, secondFeed.getEntityId());
        Assertions.assertEquals(1, secondFeed.getEventType().getEventTypeId());
        Assertions.assertEquals(1, secondFeed.getOperation().getOperationId());
    }

    @Test
    public void removeScoreErrorWithWrongFilmDataId0() {
        FilmNotFoundException exception = Assertions.assertThrows(FilmNotFoundException.class,
                () -> filmService.removeScore(0, 2));
        Assertions.assertEquals("Фильм c ID 0 не найден.", exception.getMessage(),
                "Ошибка при удалении лайка к фильму с ID0.");
    }

    @Test
    public void removeScoreErrorWithWrongFilmDataIdNegative() {
        FilmNotFoundException exception = Assertions.assertThrows(FilmNotFoundException.class,
                () -> filmService.removeScore(-1, 2));
        Assertions.assertEquals("Фильм c ID -1 не найден.", exception.getMessage(),
                "Ошибка при удалении лайка к фильму с ID-1.");
    }

    @Test
    public void removeScoreErrorWithWrongFilmDataWrongId() {
        FilmNotFoundException exception = Assertions.assertThrows(FilmNotFoundException.class,
                () -> filmService.removeScore(99, 2));
        Assertions.assertEquals("Фильм c ID 99 не найден.", exception.getMessage(),
                "Ошибка при удалении лайка к фильму с ID99.");
    }

    @Test
    public void removeScoreErrorWithWrongUserDataId0() {
        UserNotFoundException exception = Assertions.assertThrows(UserNotFoundException.class,
                () -> filmService.removeScore(2, 0));
        Assertions.assertEquals("Пользователь c ID 0 не найден.", exception.getMessage(),
                "Ошибка при удалении лайка к фильму с юзером ID0.");
    }

    @Test
    public void removeScoreErrorWithWrongUserDataIdNegative() {
        UserNotFoundException exception = Assertions.assertThrows(UserNotFoundException.class,
                () -> filmService.removeScore(2, -1));
        Assertions.assertEquals("Пользователь c ID -1 не найден.", exception.getMessage(),
                "Ошибка при удалении лайка к фильму с юзером ID-1.");
    }

    @Test
    public void removeScoreErrorWithWrongUserDataWrongId() {
        UserNotFoundException exception = Assertions.assertThrows(UserNotFoundException.class,
                () -> filmService.removeScore(2, 99));
        Assertions.assertEquals("Пользователь c ID 99 не найден.", exception.getMessage(),
                "Ошибка при удалении лайка к фильму с юзером ID99.");
    }

    @Test
    public void findPopularNormalWithCount() {
        Assertions.assertEquals(filmService.findPopular(1, Optional.empty(), Optional.empty()).toString(),
                "",
                "Ошибка при получении списка из 1 популярных фильмов.");
    }

    @Test
    public void findPopularNormalWithNoCount() {
        Assertions.assertEquals(filmService.findPopular(10, Optional.empty(), Optional.empty()).toString(),
                "",
                "Ошибка при нормальном получении списка из 10 пополурных фильмов.");
    }

    @Test
    public void findPopularErrorCount0() {
        ValidationException exception = Assertions.assertThrows(ValidationException.class, () -> filmService.findPopular(0, Optional.empty(), Optional.empty()));
        Assertions.assertEquals("Значение выводимых фильмов не может быть меньше или равно нулю.", exception.getMessage(),
                "Ошибка при получении ошибки получения популярных 0 фильмов");
    }

    @Test
    public void findPopularErrorCountNegative() {
        ValidationException exception = Assertions.assertThrows(ValidationException.class,
                () -> filmService.findPopular(-1, Optional.empty(), Optional.empty()));
        Assertions.assertEquals("Значение выводимых фильмов не может быть меньше или равно нулю.", exception.getMessage(),
                "Ошибка при получении ошибки получения популярных -1 фильмов");
    }
}