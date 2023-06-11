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
import ru.yandex.practicum.filmorate.service.UserService;
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
class FilmServiceUnitTests {

    private final Validator validator;
    private final FilmService filmService;
    private final UserService userService;
    private final FeedStorage feedStorage;

    static Stream<Film> filmWithWrongParameters() {
        return Stream.of(
                new Film("", "adipisicing", LocalDate.of(1967, Month.APRIL, 25), 100L, null, List.of(new Genre(1, null)), new Rating(1, null), new ArrayList<>()),
                new Film("name", "adipisicing", LocalDate.of(1800, Month.APRIL, 25), 100L, null, List.of(new Genre(1, null)), new Rating(1, null), new ArrayList<>()),
                new Film("name", "adipisicing", LocalDate.of(1967, Month.APRIL, 25), 0L, null, List.of(new Genre(1, null)), new Rating(1, null), new ArrayList<>()),
                new Film("name", "nnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnn" +
                        "nnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnn",
                        LocalDate.of(1967, Month.APRIL, 25), 100L, null, List.of(new Genre(1, null)), new Rating(1, null), List.of(new Director(1, null)))
        );
    }

    @Test
    void createFilmNormal() {
        Film film = new Film("Name", "Description", LocalDate.now(), 100L, null, List.of(new Genre(1, null)), new Rating(1, null), new ArrayList<>());
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
    void getFilmsNormal() {
        Film film = new Film("Name", "Description", LocalDate.of(1990, Month.APRIL, 13), 100L, null, List.of(new Genre(1, null)), new Rating(1, null), new ArrayList<>());
        validator.validate(film);
        filmService.saveNew(film);
        Assertions.assertEquals("[Film(name=Name, description=Description, releaseDate=1990-04-13, " +
                        "duration=100, id=1, genres=[Genre(id=1, name=Комедия)], " +
                        "mpa=Rating(id=1, name=G), directors=[])]", filmService.findAll().toString(),
                "Ошибка при получении из хранилища существующего фильма.");
    }

    @Test
    void findFilmNormal() {
        Film film = new Film("Name", "Description", LocalDate.of(1990, Month.APRIL, 13), 100L, null, List.of(new Genre(1, null)), new Rating(1, null), new ArrayList<>());
        validator.validate(film);
        filmService.saveNew(film);
        Assertions.assertEquals("Film(name=Name, description=Description, releaseDate=1990-04-13, " +
                        "duration=100, id=1, genres=[Genre(id=1, name=Комедия)], " +
                        "mpa=Rating(id=1, name=G), directors=[])", filmService.findById(1).toString(),
                "Ошибка при получении из хранилища существующего фильма.");
    }

    @Test
    void getFilmWithWrongId() {
        Film film = new Film("Name", "Description", LocalDate.of(1990, Month.APRIL, 13), 100L, null, List.of(new Genre(1, null)), new Rating(1, null), new ArrayList<>());
        validator.validate(film);
        filmService.saveNew(film);
        FilmNotFoundException exception = Assertions.assertThrows(FilmNotFoundException.class, () -> filmService.findById(99));
        Assertions.assertEquals("Фильм c ID 99 не найден.", exception.getMessage(),
                "Ошибка при получении из хранилища фильма с неправильным ID.");
    }

    @Test
    void addNewFilmNormal() {
        Film film = new Film("Name", "Description", LocalDate.of(1990, Month.APRIL, 13), 100L, 1, List.of(new Genre(1, "Комедия")), new Rating(1, "G"), new ArrayList<>());
        validator.validate(film);
        Assertions.assertEquals(filmService.saveNew(film), film, "Ошибка при добавлении в хранилище нормального фильма.");
    }

    @Test
    void addLikeNormal() {
        Film film = new Film("Name", "Description", LocalDate.of(1990, Month.APRIL, 13), 100L, null, List.of(new Genre(1, "Комедия")), new Rating(1, "G"), new ArrayList<>());
        filmService.saveNew(film);
        User user = new User("abc@acb.ru", "login", "name", LocalDate.of(1986, Month.APRIL, 13), 1);
        userService.saveNew(user);
        filmService.makeLike(1, 1);
        Assertions.assertEquals("[1]", filmService.findLikes(1).toString(), "Ошибка при нормальном добавлении лайка.");

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
    void addLikeWithWrongFilmDataId0() {
        Film film = new Film("Name", "Description", LocalDate.of(1990, Month.APRIL, 13), 100L, null, List.of(new Genre(1, null)), new Rating(1, null), new ArrayList<>());
        filmService.saveNew(film);
        User user = new User("abc@acb.ru", "login", "name", LocalDate.of(1986, Month.APRIL, 13), 1);
        userService.saveNew(user);
        FilmNotFoundException exception = Assertions.assertThrows(FilmNotFoundException.class, () -> filmService.makeLike(0, 1));
        Assertions.assertEquals("Фильм c ID 0 не найден.", exception.getMessage(),
                "Ошибка при добавлении лайка к фильму с ID0.");
    }

    @Test
    void addLikeWithWrongFilmDataNegativeId() {
        Film film = new Film("Name", "Description", LocalDate.of(1990, Month.APRIL, 13), 100L, null, List.of(new Genre(1, null)), new Rating(1, null), new ArrayList<>());
        filmService.saveNew(film);
        User user = new User("abc@acb.ru", "login", "name", LocalDate.of(1986, Month.APRIL, 13), 1);
        userService.saveNew(user);
        FilmNotFoundException exception = Assertions.assertThrows(FilmNotFoundException.class, () -> filmService.makeLike(-1, 1));
        Assertions.assertEquals("Фильм c ID -1 не найден.", exception.getMessage(),
                "Ошибка при добавлении лайка к фильму с ID-1.");
    }

    @Test
    void addLikeWithWrongFilmDataNoSuchFilm() {
        Film film = new Film("Name", "Description", LocalDate.of(1990, Month.APRIL, 13), 100L, null, List.of(new Genre(1, null)), new Rating(1, null), new ArrayList<>());
        filmService.saveNew(film);
        User user = new User("abc@acb.ru", "login", "name", LocalDate.of(1986, Month.APRIL, 13), 1);
        userService.saveNew(user);
        FilmNotFoundException exception = Assertions.assertThrows(FilmNotFoundException.class, () -> filmService.makeLike(99, 1));
        Assertions.assertEquals("Фильм c ID 99 не найден.", exception.getMessage(),
                "Ошибка при добавлении лайка к фильму с ID99.");
    }

    @Test
    void addLikeWithWrongUserDataId0() {
        Film film = new Film("Name", "Description", LocalDate.of(1990, Month.APRIL, 13), 100L, null, List.of(new Genre(1, null)), new Rating(1, null), new ArrayList<>());
        filmService.saveNew(film);
        User user = new User("abc@acb.ru", "login", "name", LocalDate.of(1986, Month.APRIL, 13), 1);
        userService.saveNew(user);
        UserNotFoundException exception = Assertions.assertThrows(UserNotFoundException.class, () -> filmService.makeLike(1, 0));
        Assertions.assertEquals("Пользователь c ID 0 не найден.", exception.getMessage(),
                "Ошибка при добавлении лайка к фильму с юзером ID0.");
    }

    @Test
    void addLikeWithWrongUserDataNegativeId() {
        Film film = new Film("Name", "Description", LocalDate.of(1990, Month.APRIL, 13), 100L, null, List.of(new Genre(1, null)), new Rating(1, null), new ArrayList<>());
        filmService.saveNew(film);
        User user = new User("abc@acb.ru", "login", "name", LocalDate.of(1986, Month.APRIL, 13), 1);
        userService.saveNew(user);
        UserNotFoundException exception = Assertions.assertThrows(UserNotFoundException.class, () -> filmService.makeLike(1, -1));
        Assertions.assertEquals("Пользователь c ID -1 не найден.", exception.getMessage(),
                "Ошибка при добавлении лайка к фильму с юзером ID-1.");
    }

    @Test
    void addLikeWithWrongUserDataNoSuchFilm() {
        Film film = new Film("Name", "Description", LocalDate.of(1990, Month.APRIL, 13), 100L, null, List.of(new Genre(1, null)), new Rating(1, null), new ArrayList<>());
        filmService.saveNew(film);
        User user = new User("abc@acb.ru", "login", "name", LocalDate.of(1986, Month.APRIL, 13), 1);
        userService.saveNew(user);
        UserNotFoundException exception = Assertions.assertThrows(UserNotFoundException.class, () -> filmService.makeLike(1, 99));
        Assertions.assertEquals("Пользователь c ID 99 не найден.", exception.getMessage(),
                "Ошибка при добавлении лайка к фильму с юзером ID99.");
    }

    @Test
    void removeLikeNormal() {
        Film film = new Film("Name", "Description", LocalDate.of(1990, Month.APRIL, 13), 100L, null, List.of(new Genre(1, null)), new Rating(1, null), new ArrayList<>());
        filmService.saveNew(film);
        User user = new User("abc@acb.ru", "login", "name", LocalDate.of(1986, Month.APRIL, 13), 1);
        userService.saveNew(user);
        filmService.makeLike(1, 1);
        filmService.removeLike(1, 1);
        Assertions.assertTrue(filmService.findLikes(1).isEmpty(), "Ошибка при нормальном удалении лайка.");

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
    void removeLikeErrorWithWrongFilmDataId0() {
        Film film = new Film("Name", "Description", LocalDate.of(1990, Month.APRIL, 13), 100L, null, List.of(new Genre(1, null)), new Rating(1, null), new ArrayList<>());
        filmService.saveNew(film);
        User user = new User("abc@acb.ru", "login", "name", LocalDate.of(1986, Month.APRIL, 13), 1);
        userService.saveNew(user);
        filmService.makeLike(1, 1);
        FilmNotFoundException exception = Assertions.assertThrows(FilmNotFoundException.class, () -> filmService.removeLike(0, 1));
        Assertions.assertEquals("Фильм c ID 0 не найден.", exception.getMessage(),
                "Ошибка при удалении лайка к фильму с ID0.");
    }

    @Test
    void removeLikeErrorWithWrongFilmDataIdNegative() {
        Film film = new Film("Name", "Description", LocalDate.of(1990, Month.APRIL, 13), 100L, null, List.of(new Genre(1, null)), new Rating(1, null), new ArrayList<>());
        filmService.saveNew(film);
        User user = new User("abc@acb.ru", "login", "name", LocalDate.of(1986, Month.APRIL, 13), 1);
        userService.saveNew(user);
        filmService.makeLike(1, 1);
        FilmNotFoundException exception = Assertions.assertThrows(FilmNotFoundException.class, () -> filmService.removeLike(-1, 1));
        Assertions.assertEquals("Фильм c ID -1 не найден.", exception.getMessage(),
                "Ошибка при удалении лайка к фильму с ID-1.");
    }

    @Test
    void removeLikeErrorWithWrongFilmDataWrongId() {
        Film film = new Film("Name", "Description", LocalDate.of(1990, Month.APRIL, 13), 100L, null, List.of(new Genre(1, null)), new Rating(1, null), new ArrayList<>());
        filmService.saveNew(film);
        User user = new User("abc@acb.ru", "login", "name", LocalDate.of(1986, Month.APRIL, 13), 1);
        userService.saveNew(user);
        filmService.makeLike(1, 1);
        FilmNotFoundException exception = Assertions.assertThrows(FilmNotFoundException.class, () -> filmService.removeLike(99, 1));
        Assertions.assertEquals("Фильм c ID 99 не найден.", exception.getMessage(),
                "Ошибка при удалении лайка к фильму с ID99.");
    }

    @Test
    void removeLikeErrorWithWrongUserDataId0() {
        Film film = new Film("Name", "Description", LocalDate.of(1990, Month.APRIL, 13), 100L, null, List.of(new Genre(1, null)), new Rating(1, null), new ArrayList<>());
        filmService.saveNew(film);
        User user = new User("abc@acb.ru", "login", "name", LocalDate.of(1986, Month.APRIL, 13), 1);
        userService.saveNew(user);
        filmService.makeLike(1, 1);
        UserNotFoundException exception = Assertions.assertThrows(UserNotFoundException.class, () -> filmService.removeLike(1, 0));
        Assertions.assertEquals("Пользователь c ID 0 не найден.", exception.getMessage(),
                "Ошибка при удалении лайка к фильму с юзером ID0.");
    }

    @Test
    void removeLikeErrorWithWrongUserDataIdNegative() {
        Film film = new Film("Name", "Description", LocalDate.of(1990, Month.APRIL, 13), 100L, null, List.of(new Genre(1, null)), new Rating(1, null), new ArrayList<>());
        filmService.saveNew(film);
        User user = new User("abc@acb.ru", "login", "name", LocalDate.of(1986, Month.APRIL, 13), 1);
        userService.saveNew(user);
        filmService.makeLike(1, 1);
        UserNotFoundException exception = Assertions.assertThrows(UserNotFoundException.class, () -> filmService.removeLike(1, -1));
        Assertions.assertEquals("Пользователь c ID -1 не найден.", exception.getMessage(),
                "Ошибка при удалении лайка к фильму с юзером ID-1.");
    }

    @Test
    void removeLikeErrorWithWrongUserDataWrongId() {
        Film film = new Film("Name", "Description", LocalDate.of(1990, Month.APRIL, 13), 100L, null, List.of(new Genre(1, null)), new Rating(1, null), new ArrayList<>());
        filmService.saveNew(film);
        User user = new User("abc@acb.ru", "login", "name", LocalDate.of(1986, Month.APRIL, 13), null);
        userService.saveNew(user);
        filmService.makeLike(1, 1);
        UserNotFoundException exception = Assertions.assertThrows(UserNotFoundException.class, () -> filmService.removeLike(1, 99));
        Assertions.assertEquals("Пользователь c ID 99 не найден.", exception.getMessage(),
                "Ошибка при удалении лайка к фильму с юзером ID99.");
    }

    @Test
    void findPopularNormalWithCount() {
        Film film = new Film("Name", "Description", LocalDate.of(1990, Month.APRIL, 13), 100L, 1, List.of(new Genre(1, "Комедия")), new Rating(1, "G"), new ArrayList<>());
        filmService.saveNew(film);
        Film film2 = new Film("Name2", "Description2", LocalDate.of(1990, Month.APRIL, 13), 100L, null, List.of(new Genre(1, null)), new Rating(1, null), new ArrayList<>());
        filmService.saveNew(film2);
        User user = new User("abc@acb.ru", "login", "name", LocalDate.of(1986, Month.APRIL, 13), 1);
        userService.saveNew(user);
        filmService.makeLike(1, 1);
        Assertions.assertEquals(filmService.findPopular(1, Optional.empty(), Optional.empty()), new ArrayList<>(List.of(film)),
                "Ошибка при получении списка из 1 популярных фильмов.");
    }

    @Test
    void findPopularNormalWithNoCount() {
        Film film = new Film("Name", "Description", LocalDate.of(1990, Month.APRIL, 13), 100L, 1, List.of(new Genre(1, "Комедия")), new Rating(1, "G"), new ArrayList<>());
        filmService.saveNew(film);
        Film film2 = new Film("Name2", "Description2", LocalDate.of(1990, Month.APRIL, 13), 100L, 2, List.of(new Genre(1, "Комедия")), new Rating(1, "G"), new ArrayList<>());
        filmService.saveNew(film2);
        User user = new User("abc@acb.ru", "login", "name", LocalDate.of(1986, Month.APRIL, 13), null);
        userService.saveNew(user);
        filmService.makeLike(1, 1);
        Assertions.assertEquals(filmService.findPopular(10, Optional.empty(), Optional.empty()), new ArrayList<>(List.of(film, film2)),
                "Ошибка при нормальном получении списка из 10 пополурных фильмов.");
    }

    @Test
    void findPopularErrorCount0() {
        Film film = new Film("Name", "Description", LocalDate.of(1990, Month.APRIL, 13), 100L, null, List.of(new Genre(1, null)), new Rating(1, null), new ArrayList<>());
        filmService.saveNew(film);
        Film film2 = new Film("Name2", "Description2", LocalDate.of(1990, Month.APRIL, 13), 100L, null, List.of(new Genre(1, null)), new Rating(1, null), new ArrayList<>());
        filmService.saveNew(film2);
        User user = new User("abc@acb.ru", "login", "name", LocalDate.of(1986, Month.APRIL, 13), 1);
        userService.saveNew(user);
        filmService.makeLike(1, 1);
        ValidationException exception = Assertions.assertThrows(ValidationException.class, () -> filmService.findPopular(0, Optional.empty(), Optional.empty()));
        Assertions.assertEquals("Значение выводимых фильмов не может быть меньше или равно нулю.", exception.getMessage(),
                "Ошибка при получении ошибки получения популярных 0 фильмов");
    }

    @Test
    void findPopularErrorCountNegative() {
        Film film = new Film("Name", "Description", LocalDate.of(1990, Month.APRIL, 13), 100L, null, List.of(new Genre(1, null)), new Rating(1, null), new ArrayList<>());
        filmService.saveNew(film);
        Film film2 = new Film("Name2", "Description2", LocalDate.of(1990, Month.APRIL, 13), 100L, null, List.of(new Genre(1, null)), new Rating(1, null), new ArrayList<>());
        filmService.saveNew(film2);
        User user = new User("abc@acb.ru", "login", "name", LocalDate.of(1986, Month.APRIL, 13), 1);
        userService.saveNew(user);
        filmService.makeLike(1, 1);
        ValidationException exception = Assertions.assertThrows(ValidationException.class, () -> filmService.findPopular(-1, Optional.empty(), Optional.empty()));
        Assertions.assertEquals("Значение выводимых фильмов не может быть меньше или равно нулю.", exception.getMessage(),
                "Ошибка при получении ошибки получения популярных -1 фильмов");
    }
}