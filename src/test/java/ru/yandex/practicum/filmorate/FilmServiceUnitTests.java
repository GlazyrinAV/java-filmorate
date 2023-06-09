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

import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import java.time.Duration;
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
public class FilmServiceUnitTests {

    private final Validator validator;

    private final FilmService filmService;

    private final UserService userService;

    static Stream<Film> filmWithWrongParameters() {
        return Stream.of(
                new Film("", "adipisicing", LocalDate.of(1967, Month.APRIL, 25), Duration.ofMinutes(100), null, List.of(new Genre(1, null)), new Rating(1, null), new ArrayList<>()),
                new Film("name", "adipisicing", LocalDate.of(1800, Month.APRIL, 25), Duration.ofMinutes(100), null, List.of(new Genre(1, null)), new Rating(1, null), new ArrayList<>()),
                new Film("name", "adipisicing", LocalDate.of(1967, Month.APRIL, 25), Duration.ofMinutes(-100), null, List.of(new Genre(1, null)), new Rating(1, null), new ArrayList<>()),
                new Film("name", "nnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnn" +
                        "nnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnn",
                        LocalDate.of(1967, Month.APRIL, 25), Duration.ofMinutes(100), null, List.of(new Genre(1, null)), new Rating(1, null), List.of(new Director(1, null))),
                new Film("name", "adipisicing", LocalDate.of(1967, Month.APRIL, 25), Duration.ofMinutes(100), null, List.of(new Genre(99, null)), new Rating(1, null), new ArrayList<>()),
                new Film("name", "adipisicing", LocalDate.of(1967, Month.APRIL, 25), Duration.ofMinutes(100), null, List.of(new Genre(1, null)), new Rating(99, null), new ArrayList<>())
        );
    }

    @Test
    public void createFilmNormal() {
        Film film = new Film("Name", "Description", LocalDate.now(), Duration.ofMinutes(100), null, List.of(new Genre(1, null)), new Rating(1, null), new ArrayList<>());
        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        violations.stream().map(ConstraintViolation::getMessage)
                .forEach(System.out::println);
        Assertions.assertSame(0, violations.size(), "Ошибка при создании нормального фильма.");
    }

    @ParameterizedTest
    @MethodSource("filmWithWrongParameters")
    public void postFilmsWithErrorData(Film film) {
        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        violations.stream().map(ConstraintViolation::getMessage)
                .forEach(System.out::println);
        Assertions.assertSame(1, violations.size(), "Ошибка при выявлении ошибок в данных фильма.");
    }

    @Test
    public void getFilmsEmpty() {
        Assertions.assertTrue(filmService.findAll().isEmpty(), "Ошибка при получении пустого хранилища фильмов.");
    }

    @Test
    public void getFilmsNormal() {
        Film film = new Film("Name", "Description", LocalDate.of(1990, Month.APRIL, 13), Duration.ofMinutes(100), null, List.of(new Genre(1, null)), new Rating(1, null), new ArrayList<>());
        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        filmService.saveNew(film);
        Assertions.assertEquals("[Film(name=Name, description=Description, releaseDate=1990-04-13, " +
                        "duration=PT1H40M, id=1, genres=[Genre(id=1, name=Комедия)], " +
                        "mpa=Rating(id=1, name=G), directors=[])]", filmService.findAll().toString(),
                "Ошибка при получении из хранилища существующего фильма.");
    }

    @Test
    public void findFilmNormal() {
        Film film = new Film("Name", "Description", LocalDate.of(1990, Month.APRIL, 13), Duration.ofMinutes(100), null, List.of(new Genre(1, null)), new Rating(1, null), new ArrayList<>());
        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        filmService.saveNew(film);
        Assertions.assertEquals("Film(name=Name, description=Description, releaseDate=1990-04-13, " +
                        "duration=PT1H40M, id=1, genres=[Genre(id=1, name=Комедия)], " +
                        "mpa=Rating(id=1, name=G), directors=[])", filmService.findById(1).toString(),
                "Ошибка при получении из хранилища существующего фильма.");
    }

    @Test
    public void getFilmWithWrongId() {
        Film film = new Film("Name", "Description", LocalDate.of(1990, Month.APRIL, 13), Duration.ofMinutes(100), null, List.of(new Genre(1, null)), new Rating(1, null), new ArrayList<>());
        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        filmService.saveNew(film);
        FilmNotFoundException exception = Assertions.assertThrows(FilmNotFoundException.class, () -> filmService.findById(99));
        Assertions.assertEquals("Фильм c ID 99 не найден.", exception.getMessage(),
                "Ошибка при получении из хранилища фильма с неправильным ID.");
    }

    @Test
    public void addNewFilmNormal() {
        Film film = new Film("Name", "Description", LocalDate.of(1990, Month.APRIL, 13), Duration.ofMinutes(100), 1, List.of(new Genre(1, "Комедия")), new Rating(1, "G"), new ArrayList<>());
        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        Assertions.assertEquals(filmService.saveNew(film), film, "Ошибка при добавлении в хранилище нормального фильма.");
    }

    @Test
    public void addLikeNormal() {
        Film film = new Film("Name", "Description", LocalDate.of(1990, Month.APRIL, 13), Duration.ofMinutes(100), null, List.of(new Genre(1, "Комедия")), new Rating(1, "G"), new ArrayList<>());
        filmService.saveNew(film);
        User user = new User("abc@acb.ru", "login", "name", LocalDate.of(1986, Month.APRIL, 13), 1);
        userService.saveNew(user);
        filmService.makeLike(1, 1);
        Assertions.assertEquals(filmService.findLikes(1).toString(), "[1]", "Ошибка при нормальном добавлении лайка.");
    }

    @Test
    public void addLikeWithWrongFilmDataId0() {
        Film film = new Film("Name", "Description", LocalDate.of(1990, Month.APRIL, 13), Duration.ofMinutes(100), null, List.of(new Genre(1, null)), new Rating(1, null), new ArrayList<>());
        filmService.saveNew(film);
        User user = new User("abc@acb.ru", "login", "name", LocalDate.of(1986, Month.APRIL, 13), 1);
        userService.saveNew(user);
        FilmNotFoundException exception = Assertions.assertThrows(FilmNotFoundException.class, () -> filmService.makeLike(0, 1));
        Assertions.assertEquals(exception.getMessage(), "Фильм c ID 0 не найден.",
                "Ошибка при добавлении лайка к фильму с ID0.");
    }

    @Test
    public void addLikeWithWrongFilmDataNegativeId() {
        Film film = new Film("Name", "Description", LocalDate.of(1990, Month.APRIL, 13), Duration.ofMinutes(100), null, List.of(new Genre(1, null)), new Rating(1, null), new ArrayList<>());
        filmService.saveNew(film);
        User user = new User("abc@acb.ru", "login", "name", LocalDate.of(1986, Month.APRIL, 13), 1);
        userService.saveNew(user);
        FilmNotFoundException exception = Assertions.assertThrows(FilmNotFoundException.class, () -> filmService.makeLike(-1, 1));
        Assertions.assertEquals(exception.getMessage(), "Фильм c ID -1 не найден.",
                "Ошибка при добавлении лайка к фильму с ID-1.");
    }

    @Test
    public void addLikeWithWrongFilmDataNoSuchFilm() {
        Film film = new Film("Name", "Description", LocalDate.of(1990, Month.APRIL, 13), Duration.ofMinutes(100), null, List.of(new Genre(1, null)), new Rating(1, null), new ArrayList<>());
        filmService.saveNew(film);
        User user = new User("abc@acb.ru", "login", "name", LocalDate.of(1986, Month.APRIL, 13), 1);
        userService.saveNew(user);
        FilmNotFoundException exception = Assertions.assertThrows(FilmNotFoundException.class, () -> filmService.makeLike(99, 1));
        Assertions.assertEquals(exception.getMessage(), "Фильм c ID 99 не найден.",
                "Ошибка при добавлении лайка к фильму с ID99.");
    }

    @Test
    public void addLikeWithWrongUserDataId0() {
        Film film = new Film("Name", "Description", LocalDate.of(1990, Month.APRIL, 13), Duration.ofMinutes(100), null, List.of(new Genre(1, null)), new Rating(1, null), new ArrayList<>());
        filmService.saveNew(film);
        User user = new User("abc@acb.ru", "login", "name", LocalDate.of(1986, Month.APRIL, 13), 1);
        userService.saveNew(user);
        UserNotFoundException exception = Assertions.assertThrows(UserNotFoundException.class, () -> filmService.makeLike(1, 0));
        Assertions.assertEquals(exception.getMessage(), "Пользователь c ID 0 не найден.",
                "Ошибка при добавлении лайка к фильму с юзером ID0.");
    }

    @Test
    public void addLikeWithWrongUserDataNegativeId() {
        Film film = new Film("Name", "Description", LocalDate.of(1990, Month.APRIL, 13), Duration.ofMinutes(100), null, List.of(new Genre(1, null)), new Rating(1, null), new ArrayList<>());
        filmService.saveNew(film);
        User user = new User("abc@acb.ru", "login", "name", LocalDate.of(1986, Month.APRIL, 13), 1);
        userService.saveNew(user);
        UserNotFoundException exception = Assertions.assertThrows(UserNotFoundException.class, () -> filmService.makeLike(1, -1));
        Assertions.assertEquals(exception.getMessage(), "Пользователь c ID -1 не найден.",
                "Ошибка при добавлении лайка к фильму с юзером ID-1.");
    }

    @Test
    public void addLikeWithWrongUserDataNoSuchFilm() {
        Film film = new Film("Name", "Description", LocalDate.of(1990, Month.APRIL, 13), Duration.ofMinutes(100), null, List.of(new Genre(1, null)), new Rating(1, null), new ArrayList<>());
        filmService.saveNew(film);
        User user = new User("abc@acb.ru", "login", "name", LocalDate.of(1986, Month.APRIL, 13), 1);
        userService.saveNew(user);
        UserNotFoundException exception = Assertions.assertThrows(UserNotFoundException.class, () -> filmService.makeLike(1, 99));
        Assertions.assertEquals(exception.getMessage(), "Пользователь c ID 99 не найден.",
                "Ошибка при добавлении лайка к фильму с юзером ID99.");
    }

    @Test
    public void removeLikeNormal() {
        Film film = new Film("Name", "Description", LocalDate.of(1990, Month.APRIL, 13), Duration.ofMinutes(100), null, List.of(new Genre(1, null)), new Rating(1, null), new ArrayList<>());
        filmService.saveNew(film);
        User user = new User("abc@acb.ru", "login", "name", LocalDate.of(1986, Month.APRIL, 13), 1);
        userService.saveNew(user);
        filmService.makeLike(1, 1);
        filmService.removeLike(1, 1);
        Assertions.assertTrue(filmService.findLikes(1).isEmpty(), "Ошибка при нормальном удалении лайка.");
    }

    @Test
    public void removeLikeErrorWithWrongFilmDataId0() {
        Film film = new Film("Name", "Description", LocalDate.of(1990, Month.APRIL, 13), Duration.ofMinutes(100), null, List.of(new Genre(1, null)), new Rating(1, null), new ArrayList<>());
        filmService.saveNew(film);
        User user = new User("abc@acb.ru", "login", "name", LocalDate.of(1986, Month.APRIL, 13), 1);
        userService.saveNew(user);
        filmService.makeLike(1, 1);
        FilmNotFoundException exception = Assertions.assertThrows(FilmNotFoundException.class, () -> filmService.removeLike(0, 1));
        Assertions.assertEquals(exception.getMessage(), "Фильм c ID 0 не найден.",
                "Ошибка при удалении лайка к фильму с ID0.");
    }

    @Test
    public void removeLikeErrorWithWrongFilmDataIdNegative() {
        Film film = new Film("Name", "Description", LocalDate.of(1990, Month.APRIL, 13), Duration.ofMinutes(100), null, List.of(new Genre(1, null)), new Rating(1, null), new ArrayList<>());
        filmService.saveNew(film);
        User user = new User("abc@acb.ru", "login", "name", LocalDate.of(1986, Month.APRIL, 13), 1);
        userService.saveNew(user);
        filmService.makeLike(1, 1);
        FilmNotFoundException exception = Assertions.assertThrows(FilmNotFoundException.class, () -> filmService.removeLike(-1, 1));
        Assertions.assertEquals(exception.getMessage(), "Фильм c ID -1 не найден.",
                "Ошибка при удалении лайка к фильму с ID-1.");
    }

    @Test
    public void removeLikeErrorWithWrongFilmDataWrongId() {
        Film film = new Film("Name", "Description", LocalDate.of(1990, Month.APRIL, 13), Duration.ofMinutes(100), null, List.of(new Genre(1, null)), new Rating(1, null), new ArrayList<>());
        filmService.saveNew(film);
        User user = new User("abc@acb.ru", "login", "name", LocalDate.of(1986, Month.APRIL, 13), 1);
        userService.saveNew(user);
        filmService.makeLike(1, 1);
        FilmNotFoundException exception = Assertions.assertThrows(FilmNotFoundException.class, () -> filmService.removeLike(99, 1));
        Assertions.assertEquals(exception.getMessage(), "Фильм c ID 99 не найден.",
                "Ошибка при удалении лайка к фильму с ID99.");
    }

    @Test
    public void removeLikeErrorWithWrongUserDataId0() {
        Film film = new Film("Name", "Description", LocalDate.of(1990, Month.APRIL, 13), Duration.ofMinutes(100), null, List.of(new Genre(1, null)), new Rating(1, null), new ArrayList<>());
        filmService.saveNew(film);
        User user = new User("abc@acb.ru", "login", "name", LocalDate.of(1986, Month.APRIL, 13), 1);
        userService.saveNew(user);
        filmService.makeLike(1, 1);
        UserNotFoundException exception = Assertions.assertThrows(UserNotFoundException.class, () -> filmService.removeLike(1, 0));
        Assertions.assertEquals(exception.getMessage(), "Пользователь c ID 0 не найден.",
                "Ошибка при удалении лайка к фильму с юзером ID0.");
    }

    @Test
    public void removeLikeErrorWithWrongUserDataIdNegative() {
        Film film = new Film("Name", "Description", LocalDate.of(1990, Month.APRIL, 13), Duration.ofMinutes(100), null, List.of(new Genre(1, null)), new Rating(1, null), new ArrayList<>());
        filmService.saveNew(film);
        User user = new User("abc@acb.ru", "login", "name", LocalDate.of(1986, Month.APRIL, 13), 1);
        userService.saveNew(user);
        filmService.makeLike(1, 1);
        UserNotFoundException exception = Assertions.assertThrows(UserNotFoundException.class, () -> filmService.removeLike(1, -1));
        Assertions.assertEquals(exception.getMessage(), "Пользователь c ID -1 не найден.",
                "Ошибка при удалении лайка к фильму с юзером ID-1.");
    }

    @Test
    public void removeLikeErrorWithWrongUserDataWrongId() {
        Film film = new Film("Name", "Description", LocalDate.of(1990, Month.APRIL, 13), Duration.ofMinutes(100), null, List.of(new Genre(1, null)), new Rating(1, null), new ArrayList<>());
        filmService.saveNew(film);
        User user = new User("abc@acb.ru", "login", "name", LocalDate.of(1986, Month.APRIL, 13), null);
        userService.saveNew(user);
        filmService.makeLike(1, 1);
        UserNotFoundException exception = Assertions.assertThrows(UserNotFoundException.class, () -> filmService.removeLike(1, 99));
        Assertions.assertEquals(exception.getMessage(), "Пользователь c ID 99 не найден.",
                "Ошибка при удалении лайка к фильму с юзером ID99.");
    }

    @Test
    public void findPopularNormalWithCount() {
        Film film = new Film("Name", "Description", LocalDate.of(1990, Month.APRIL, 13), Duration.ofMinutes(100), 1, List.of(new Genre(1, "Комедия")), new Rating(1, "G"), new ArrayList<>());
        filmService.saveNew(film);
        Film film2 = new Film("Name2", "Description2", LocalDate.of(1990, Month.APRIL, 13), Duration.ofMinutes(100), null, List.of(new Genre(1, null)), new Rating(1, null), new ArrayList<>());
        filmService.saveNew(film2);
        User user = new User("abc@acb.ru", "login", "name", LocalDate.of(1986, Month.APRIL, 13), 1);
        userService.saveNew(user);
        filmService.makeLike(1, 1);
        Assertions.assertEquals(filmService.findPopular(1, Optional.empty(), Optional.empty()), new ArrayList<>(List.of(film)),
                "Ошибка при получении списка из 1 популярных фильмов.");
    }

    @Test
    public void findPopularNormalWithNoCount() {
        Film film = new Film("Name", "Description", LocalDate.of(1990, Month.APRIL, 13), Duration.ofMinutes(100), 1, List.of(new Genre(1, "Комедия")), new Rating(1, "G"), new ArrayList<>());
        filmService.saveNew(film);
        Film film2 = new Film("Name2", "Description2", LocalDate.of(1990, Month.APRIL, 13), Duration.ofMinutes(100), null, List.of(new Genre(1, "Комедия")), new Rating(1, "G"), new ArrayList<>());
        filmService.saveNew(film2);
        User user = new User("abc@acb.ru", "login", "name", LocalDate.of(1986, Month.APRIL, 13), null);
        userService.saveNew(user);
        filmService.makeLike(1, 1);
        Assertions.assertEquals(filmService.findPopular(10, Optional.empty(), Optional.empty()), new ArrayList<>(List.of(film, film2)),
                "Ошибка при нормальном получении списка из 10 пополурных фильмов.");
    }

    @Test
    public void findPopularErrorCount0() {
        Film film = new Film("Name", "Description", LocalDate.of(1990, Month.APRIL, 13), Duration.ofMinutes(100), null, List.of(new Genre(1, null)), new Rating(1, null), new ArrayList<>());
        filmService.saveNew(film);
        Film film2 = new Film("Name2", "Description2", LocalDate.of(1990, Month.APRIL, 13), Duration.ofMinutes(100), null, List.of(new Genre(1, null)), new Rating(1, null), new ArrayList<>());
        filmService.saveNew(film2);
        User user = new User("abc@acb.ru", "login", "name", LocalDate.of(1986, Month.APRIL, 13), 1);
        userService.saveNew(user);
        filmService.makeLike(1, 1);
        ValidationException exception = Assertions.assertThrows(ValidationException.class, () -> filmService.findPopular(0, Optional.empty(), Optional.empty()));
        Assertions.assertEquals(exception.getMessage(), "Значение выводимых фильмов не может быть меньше или равно нулю.",
                "Ошибка при получении ошибки получения популярных 0 фильмов");
    }

    @Test
    public void findPopularErrorCountNegative() {
        Film film = new Film("Name", "Description", LocalDate.of(1990, Month.APRIL, 13), Duration.ofMinutes(100), null, List.of(new Genre(1, null)), new Rating(1, null), new ArrayList<>());
        filmService.saveNew(film);
        Film film2 = new Film("Name2", "Description2", LocalDate.of(1990, Month.APRIL, 13), Duration.ofMinutes(100), null, List.of(new Genre(1, null)), new Rating(1, null), new ArrayList<>());
        filmService.saveNew(film2);
        User user = new User("abc@acb.ru", "login", "name", LocalDate.of(1986, Month.APRIL, 13), 1);
        userService.saveNew(user);
        filmService.makeLike(1, 1);
        ValidationException exception = Assertions.assertThrows(ValidationException.class, () -> filmService.findPopular(-1, Optional.empty(), Optional.empty()));
        Assertions.assertEquals(exception.getMessage(), "Значение выводимых фильмов не может быть меньше или равно нулю.",
                "Ошибка при получении ошибки получения популярных -1 фильмов");
    }
}