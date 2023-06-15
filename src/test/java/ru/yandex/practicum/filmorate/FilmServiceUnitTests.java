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

    static Stream<Integer> wrongIdParameters() {
        return Stream.of(-1, 0, 99);
    }

    private final static Score scoreNormal = new Score(1, 1, 5);
    private final static Score scoreZeroFilmId = new Score(0, 1, 5);
    private final static Score scoreNegativeFilmId = new Score(-1, 1, 5);
    private final static Score scoreWrongFilmId = new Score(99, 1, 5);
    private final static Score scoreZeroUserId = new Score(1, 0, 5);
    private final static Score scoreNegativeUserId = new Score(1, -1, 5);
    private final static Score scoreWrongUserId = new Score(1, 99, 5);

    @Test
    void createFilmNormal() {
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
    @Sql(value = {"/schema.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    void getAllFilmsEmpty() {
        Assertions.assertTrue(filmService.findAll().isEmpty(), "Ошибка при получении пустого хранилища фильмов.");
    }

    @Test
    void getAllFilmsNormal() {
        Assertions.assertEquals("[Film(name=new film, description=new description, releaseDate=2000-04-22, duration=100, id=1, genres=[], mpa=Mpa(id=1, name=G), directors=[], score=0.0), " +
                        "Film(name=second film, description=second description, releaseDate=1999-04-22, duration=100, id=2, genres=[], mpa=Mpa(id=1, name=G), directors=[], score=7.5), " +
                        "Film(name=third film, description=third description, releaseDate=1976-04-22, duration=100, id=3, genres=[], mpa=Mpa(id=3, name=PG-13), directors=[], score=0.0), Film(name=final film, description=final description, releaseDate=1987-04-22, duration=100, id=4, genres=[], mpa=Mpa(id=2, name=PG), directors=[], score=3.0)]",
                filmService.findAll().toString(),
                "Ошибка при получении из хранилища существующего фильма.");
    }

    @Test
    void findByIdFilmNormal() {
        Assertions.assertEquals("Film(name=new film, description=new description, releaseDate=2000-04-22, " +
                        "duration=100, id=1, genres=[], mpa=Mpa(id=1, name=G), directors=[], score=0.0)",
                filmService.findById(1).toString(),
                "Ошибка при получении из хранилища существующего фильма.");
    }

    @Test
    void getFilmWithWrongId() {
        FilmNotFoundException exception = Assertions.assertThrows(FilmNotFoundException.class,
                () -> filmService.findById(99));
        Assertions.assertEquals("Фильм c ID 99 не найден.", exception.getMessage(),
                "Ошибка при получении из хранилища фильма с неправильным ID.");
    }

    @Test
    void addNewFilmNormal() {
        Film film = new Film("Name", "Description", LocalDate.of(1990, Month.APRIL, 13), 100L, 1, List.of(new Genre(1, "Комедия")), new Mpa(1, "G"), new ArrayList<>(), null);
        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        Assertions.assertEquals(violations.size(), 0, "Ошибка при добавлении в хранилище нормального фильма.");
    }

    @Test
    void addScoreNormal() {
        filmService.saveScore(scoreNormal);
        Assertions.assertEquals("7.5", filmService.findById(2).getScore().toString(),
                "Ошибка при нормальном добавлении лайка.");

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
    void addScoreWithWrongFilmDataId0() {
        FilmNotFoundException exception = Assertions.assertThrows(FilmNotFoundException.class,
                () -> filmService.saveScore(scoreZeroFilmId));
        Assertions.assertEquals("Фильм c ID 0 не найден.", exception.getMessage(),
                "Ошибка при добавлении лайка к фильму с ID0.");
    }

    @Test
    void addScoreWithWrongFilmDataNegativeId() {
        FilmNotFoundException exception = Assertions.assertThrows(FilmNotFoundException.class,
                () -> filmService.saveScore(scoreNegativeFilmId));
        Assertions.assertEquals("Фильм c ID -1 не найден.", exception.getMessage(),
                "Ошибка при добавлении лайка к фильму с ID-1.");
    }

    @Test
    void addScoreWithWrongFilmDataNoSuchFilm() {
        FilmNotFoundException exception = Assertions.assertThrows(FilmNotFoundException.class,
                () -> filmService.saveScore(scoreWrongFilmId));
        Assertions.assertEquals("Фильм c ID 99 не найден.", exception.getMessage(),
                "Ошибка при добавлении лайка к фильму с ID99.");
    }

    @Test
    void addScoreWithWrongUserDataId0() {
        UserNotFoundException exception = Assertions.assertThrows(UserNotFoundException.class,
                () -> filmService.saveScore(scoreZeroUserId));
        Assertions.assertEquals("Пользователь c ID 0 не найден.", exception.getMessage(),
                "Ошибка при добавлении лайка к фильму с юзером ID0.");
    }

    @Test
    void addScoreWithWrongUserDataNegativeId() {
        UserNotFoundException exception = Assertions.assertThrows(UserNotFoundException.class,
                () -> filmService.saveScore(scoreNegativeUserId));
        Assertions.assertEquals("Пользователь c ID -1 не найден.", exception.getMessage(),
                "Ошибка при добавлении лайка к фильму с юзером ID-1.");
    }

    @Test
    void addScoreWithWrongUserDataNoSuchFilm() {
        UserNotFoundException exception = Assertions.assertThrows(UserNotFoundException.class,
                () -> filmService.saveScore(scoreWrongUserId));
        Assertions.assertEquals("Пользователь c ID 99 не найден.", exception.getMessage(),
                "Ошибка при добавлении лайка к фильму с юзером ID99.");
    }

    @Test
    void removeScoreNormal() {
        Assertions.assertEquals(7.5, filmService.findById(2).getScore());
        filmService.removeScore(2, 2);
        Assertions.assertEquals(10.0, filmService.findById(2).getScore(),
                "Ошибка при нормальном удалении лайка.");

        List<Feed> feeds = new ArrayList<>(feedStorage.findFeed(2));
        Assertions.assertEquals(1, feeds.size());
        Feed feed = feeds.get(0);
        Assertions.assertEquals(1, feed.getEventId());
        Assertions.assertEquals(2, feed.getUserId());
        Assertions.assertEquals(2, feed.getEntityId());
        Assertions.assertEquals(1, feed.getEventType().getEventTypeId());
        Assertions.assertEquals(1, feed.getOperation().getOperationId());
    }

    @Test
    void removeScoreErrorWithWrongFilmDataId0() {
        FilmNotFoundException exception = Assertions.assertThrows(FilmNotFoundException.class,
                () -> filmService.removeScore(0, 2));
        Assertions.assertEquals("Фильм c ID 0 не найден.", exception.getMessage(),
                "Ошибка при удалении лайка к фильму с ID0.");
    }

    @Test
    void removeScoreErrorWithWrongFilmDataIdNegative() {
        FilmNotFoundException exception = Assertions.assertThrows(FilmNotFoundException.class,
                () -> filmService.removeScore(-1, 2));
        Assertions.assertEquals("Фильм c ID -1 не найден.", exception.getMessage(),
                "Ошибка при удалении лайка к фильму с ID-1.");
    }

    @Test
    void removeScoreErrorWithWrongFilmDataWrongId() {
        FilmNotFoundException exception = Assertions.assertThrows(FilmNotFoundException.class,
                () -> filmService.removeScore(99, 2));
        Assertions.assertEquals("Фильм c ID 99 не найден.", exception.getMessage(),
                "Ошибка при удалении лайка к фильму с ID99.");
    }

    @Test
    void removeScoreErrorWithWrongUserDataId0() {
        UserNotFoundException exception = Assertions.assertThrows(UserNotFoundException.class,
                () -> filmService.removeScore(2, 0));
        Assertions.assertEquals("Пользователь c ID 0 не найден.", exception.getMessage(),
                "Ошибка при удалении лайка к фильму с юзером ID0.");
    }

    @Test
    void removeScoreErrorWithWrongUserDataIdNegative() {
        UserNotFoundException exception = Assertions.assertThrows(UserNotFoundException.class,
                () -> filmService.removeScore(2, -1));
        Assertions.assertEquals("Пользователь c ID -1 не найден.", exception.getMessage(),
                "Ошибка при удалении лайка к фильму с юзером ID-1.");
    }

    @Test
    void removeScoreErrorWithWrongUserDataWrongId() {
        UserNotFoundException exception = Assertions.assertThrows(UserNotFoundException.class,
                () -> filmService.removeScore(2, 99));
        Assertions.assertEquals("Пользователь c ID 99 не найден.", exception.getMessage(),
                "Ошибка при удалении лайка к фильму с юзером ID99.");
    }

    @Test
    void findPopularNormalWithCount() {
        Assertions.assertEquals("[Film(name=second film, description=second description, releaseDate=1999-04-22, duration=100, id=2, genres=[], mpa=Mpa(id=1, name=G), directors=[], score=7.5)]",
                filmService.findPopular(1, Optional.empty(), Optional.empty()).toString(),
                "Ошибка при получении списка из 1 популярных фильмов.");
    }

    @Test
    void findPopularNormalWithNoCount() {
        Assertions.assertEquals("[Film(name=second film, description=second description, releaseDate=1999-04-22, duration=100, id=2, genres=[], mpa=Mpa(id=1, name=G), directors=[], score=7.5), " +
                        "Film(name=final film, description=final description, releaseDate=1987-04-22, duration=100, id=4, genres=[], mpa=Mpa(id=2, name=PG), directors=[], score=3.0), " +
                        "Film(name=new film, description=new description, releaseDate=2000-04-22, duration=100, id=1, genres=[], mpa=Mpa(id=1, name=G), directors=[], score=0.0), Film(name=third film, description=third description, releaseDate=1976-04-22, duration=100, id=3, genres=[], mpa=Mpa(id=3, name=PG-13), directors=[], score=0.0)]",
                filmService.findPopular(10, Optional.empty(), Optional.empty()).toString(),
                "Ошибка при нормальном получении списка из 10 пополурных фильмов.");
    }

    @Test
    void findPopularErrorCount0() {
        ValidationException exception = Assertions.assertThrows(ValidationException.class, () -> filmService.findPopular(0, Optional.empty(), Optional.empty()));
        Assertions.assertEquals("Значение выводимых фильмов не может быть меньше или равно нулю.", exception.getMessage(),
                "Ошибка при получении ошибки получения популярных 0 фильмов");
    }

    @Test
    void findPopularErrorCountNegative() {
        ValidationException exception = Assertions.assertThrows(ValidationException.class,
                () -> filmService.findPopular(-1, Optional.empty(), Optional.empty()));
        Assertions.assertEquals("Значение выводимых фильмов не может быть меньше или равно нулю.", exception.getMessage(),
                "Ошибка при получении ошибки получения популярных -1 фильмов");
    }

    @Test
    void findCommonFilmsNormalNull() {
        Assertions.assertEquals(filmService.findCommonFilms(Optional.of(1), Optional.of(3)).toString(), "[]",
                "Ошибка в нормальном получени пустых общих фильмов.");
    }


    @Test
    void findCommonFilmsNormal() {
        Assertions.assertEquals("[Film(name=second film, description=second description, releaseDate=1999-04-22, duration=100, id=2, genres=[], mpa=Mpa(id=1, name=G), directors=[], score=7.5), " +
                        "Film(name=final film, description=final description, releaseDate=1987-04-22, duration=100, id=4, genres=[], mpa=Mpa(id=2, name=PG), directors=[], score=3.0)]",
                filmService.findCommonFilms(Optional.of(1), Optional.of(2)).toString(),
                "Ошибка в нормальном получени общих фильмов.");
    }

    @ParameterizedTest
    @MethodSource("wrongIdParameters")
    void findCommonFilmsWrongUserId(int userId) {
        UserNotFoundException exception = Assertions.assertThrows(UserNotFoundException.class,
                () -> filmService.findCommonFilms(Optional.of(userId), Optional.of(2)));
        Assertions.assertEquals(exception.getMessage(), "Пользователь c ID " + userId + " не найден.",
                "Ошибка при поиске общих фильмов c ид юзера " + userId + ".");
    }

    @ParameterizedTest
    @MethodSource("wrongIdParameters")
    void findCommonFilmsWrongFriendId(int friendId) {
        UserNotFoundException exception = Assertions.assertThrows(UserNotFoundException.class,
                () -> filmService.findCommonFilms(Optional.of(1), Optional.of(friendId)));
        Assertions.assertEquals(exception.getMessage(), "Пользователь c ID " + friendId + " не найден.",
                "Ошибка при посике общих фильмов c ид друга " + friendId + ".");
    }

    @Test
    void findPopularByGenreAndYearNormal() {

    }

    @Test
    void findPopularByGenreAndYearNull() {

    }

    @ParameterizedTest
    @MethodSource("wrongIdParameters")
    void findPopularByGenreAndYearWrongGenre() {

    }

    @ParameterizedTest
    @MethodSource("wrongIdParameters")
    void findPopularByGenreAndYearWrongYear() {

    }

    @Test
    void findPopularByGenreAndYearCountNegative() {

    }

    @Test
    void findPopularByGenreAndYearCountZero() {

    }

    @Test
    void findPopularByGenreNormal() {

    }

    @Test
    void findPopularByGenreNormalNull() {

    }

    @ParameterizedTest
    @MethodSource("wrongIdParameters")
    void findPopularByGenreWrongGenre() {

    }

    @Test
    void findPopularByGenreCountNegative() {

    }

    @Test
    void findPopularByGenreCountZero() {

    }

    @Test
    void findPopularByYearNormal() {

    }

    @Test
    void findPopularByYearNormalNull() {

    }

    @ParameterizedTest
    @MethodSource("wrongIdParameters")
    void findPopularByYearWrongYear() {

    }

    @Test
    void findPopularByYearCountNegative() {

    }

    @Test
    void findPopularByYearCountZero() {

    }
}