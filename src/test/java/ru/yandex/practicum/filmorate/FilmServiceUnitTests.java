package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import ru.yandex.practicum.filmorate.exceptions.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exceptions.UserNotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.InMemoryFilmStorage;
import ru.yandex.practicum.filmorate.storage.InMemoryUserStorage;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.time.Duration;
import java.time.LocalDate;
import java.time.Month;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

@SpringBootTest
public class FilmServiceUnitTests {

    private static final ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
    private static final Validator validator = factory.getValidator();
    @Autowired
    private FilmService filmService;
    @Autowired
    private UserService userService;
    @Autowired
    private InMemoryFilmStorage storage;
    @Autowired
    private InMemoryUserStorage userStorage;

    static Stream<Film> filmWithWrongParameters() {
        return Stream.of(
                new Film("", "adipisicing", LocalDate.of(1967, Month.APRIL, 25), Duration.ofMinutes(100), 1),
                new Film("name", "adipisicing", LocalDate.of(1800, Month.APRIL, 25), Duration.ofMinutes(100), 1),
                new Film("name", "adipisicing", LocalDate.of(1967, Month.APRIL, 25), Duration.ofMinutes(-100), 1),
                new Film("name", "nnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnn" +
                        "nnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnn",
                        LocalDate.of(1967, Month.APRIL, 25), Duration.ofMinutes(100), 1)
        );
    }

    @BeforeEach
    public void start() {
        storage.findAllFilms().clear();
        storage.resetCounter();
        userStorage.findAllUsers().clear();
        userStorage.resetCounter();
    }

    @Test
    public void createFilmNormal() {
        Film film = new Film("Name", "Description", LocalDate.now(), Duration.ofMinutes(100), 1);
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
        Assertions.assertTrue(filmService.findAllFilms().isEmpty(), "Ошибка при получении пустого хранилища фильмов.");
    }

    @Test
    public void getFilmsNormal() {
        Film film = new Film("Name", "Description", LocalDate.of(1990, Month.APRIL, 13), Duration.ofMinutes(100), 1);
        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        filmService.addNewFilm(film);
        Assertions.assertEquals("[Film(liked=[], name=Name, description=Description, releaseDate=1990-04-13, duration=PT1H40M, id=1)]", filmService.findAllFilms().toString(),
                "Ошибка при получении из хранилища существующего фильма.");
    }

    @Test
    public void findFilmNormal() {
        Film film = new Film("Name", "Description", LocalDate.of(1990, Month.APRIL, 13), Duration.ofMinutes(100), 1);
        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        filmService.addNewFilm(film);
        Assertions.assertEquals("Film(liked=[], name=Name, description=Description, releaseDate=1990-04-13, duration=PT1H40M, id=1)", filmService.findFilm(1).toString(),
                "Ошибка при получении из хранилища существующего фильма.");
    }

    @Test
    public void getFilmWithWrongId() {
        Film film = new Film("Name", "Description", LocalDate.of(1990, Month.APRIL, 13), Duration.ofMinutes(100), 1);
        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        filmService.addNewFilm(film);
        FilmNotFoundException exception = Assertions.assertThrows(FilmNotFoundException.class, () -> filmService.findFilm(99));
        Assertions.assertEquals("Фильм c ID 99 не найден.", exception.getMessage(),
                "Ошибка при получении из хранилища фильма с неправильным ID.");
    }

    @Test
    public void addNewFilmNormal() {
        Film film = new Film("Name", "Description", LocalDate.of(1990, Month.APRIL, 13), Duration.ofMinutes(100), 1);
        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        Assertions.assertEquals(filmService.addNewFilm(film), film, "Ошибка при добавлении в хранилище нормального фильма.");
    }

    @Test
    public void addLikeNormal() {
        Film film = new Film("Name", "Description", LocalDate.of(1990, Month.APRIL, 13), Duration.ofMinutes(100), 1);
        filmService.addNewFilm(film);
        User user = new User("abc@acb.ru", "login", "name", LocalDate.of(1986, Month.APRIL, 13), 1);
        userService.addNewUser(user);
        filmService.addLike(1, 1);
        Assertions.assertEquals(filmService.findFilm(1).getLiked().toString(), "[1]", "Ошибка при нормальном добавлении лайка.");
    }

    @Test
    public void addLikeWithWrongFilmDataId0() {
        Film film = new Film("Name", "Description", LocalDate.of(1990, Month.APRIL, 13), Duration.ofMinutes(100), 1);
        filmService.addNewFilm(film);
        User user = new User("abc@acb.ru", "login", "name", LocalDate.of(1986, Month.APRIL, 13), 1);
        userService.addNewUser(user);
        ValidationException exception = Assertions.assertThrows(ValidationException.class, () -> filmService.addLike(0, 1));
        Assertions.assertEquals(exception.getMessage(), "ID фильма не может быть меньше или равно нулю.",
                "Ошибка при добавлении лайка к фильму с ID0.");
    }

    @Test
    public void addLikeWithWrongFilmDataNegativeId() {
        Film film = new Film("Name", "Description", LocalDate.of(1990, Month.APRIL, 13), Duration.ofMinutes(100), 1);
        filmService.addNewFilm(film);
        User user = new User("abc@acb.ru", "login", "name", LocalDate.of(1986, Month.APRIL, 13), 1);
        userService.addNewUser(user);
        ValidationException exception = Assertions.assertThrows(ValidationException.class, () -> filmService.addLike(-1, 1));
        Assertions.assertEquals(exception.getMessage(), "ID фильма не может быть меньше или равно нулю.",
                "Ошибка при добавлении лайка к фильму с ID-1.");
    }

    @Test
    public void addLikeWithWrongFilmDataNoSuchFilm() {
        Film film = new Film("Name", "Description", LocalDate.of(1990, Month.APRIL, 13), Duration.ofMinutes(100), 1);
        filmService.addNewFilm(film);
        User user = new User("abc@acb.ru", "login", "name", LocalDate.of(1986, Month.APRIL, 13), 1);
        userService.addNewUser(user);
        FilmNotFoundException exception = Assertions.assertThrows(FilmNotFoundException.class, () -> filmService.addLike(99, 1));
        Assertions.assertEquals(exception.getMessage(), "Фильм c ID 99 не найден.",
                "Ошибка при добавлении лайка к фильму с ID99.");
    }

    @Test
    public void addLikeWithWrongUserDataId0() {
        Film film = new Film("Name", "Description", LocalDate.of(1990, Month.APRIL, 13), Duration.ofMinutes(100), 1);
        filmService.addNewFilm(film);
        User user = new User("abc@acb.ru", "login", "name", LocalDate.of(1986, Month.APRIL, 13), 1);
        userService.addNewUser(user);
        ValidationException exception = Assertions.assertThrows(ValidationException.class, () -> filmService.addLike(1, 0));
        Assertions.assertEquals(exception.getMessage(), "ID юзера не может быть меньше или равно нулю.",
                "Ошибка при добавлении лайка к фильму с юзером ID0.");
    }

    @Test
    public void addLikeWithWrongUserDataNegativeId() {
        Film film = new Film("Name", "Description", LocalDate.of(1990, Month.APRIL, 13), Duration.ofMinutes(100), 1);
        filmService.addNewFilm(film);
        User user = new User("abc@acb.ru", "login", "name", LocalDate.of(1986, Month.APRIL, 13), 1);
        userService.addNewUser(user);
        ValidationException exception = Assertions.assertThrows(ValidationException.class, () -> filmService.addLike(1, -1));
        Assertions.assertEquals(exception.getMessage(), "ID юзера не может быть меньше или равно нулю.",
                "Ошибка при добавлении лайка к фильму с юзером ID-1.");
    }

    @Test
    public void addLikeWithWrongUserDataNoSuchFilm() {
        Film film = new Film("Name", "Description", LocalDate.of(1990, Month.APRIL, 13), Duration.ofMinutes(100), 1);
        filmService.addNewFilm(film);
        User user = new User("abc@acb.ru", "login", "name", LocalDate.of(1986, Month.APRIL, 13), 1);
        userService.addNewUser(user);
        UserNotFoundException exception = Assertions.assertThrows(UserNotFoundException.class, () -> filmService.addLike(1, 99));
        Assertions.assertEquals(exception.getMessage(), "Пользователь c ID 99 не найден.",
                "Ошибка при добавлении лайка к фильму с юзером ID99.");
    }

    @Test
    public void removeLikeNormal() {
        Film film = new Film("Name", "Description", LocalDate.of(1990, Month.APRIL, 13), Duration.ofMinutes(100), 1);
        filmService.addNewFilm(film);
        User user = new User("abc@acb.ru", "login", "name", LocalDate.of(1986, Month.APRIL, 13), 1);
        userService.addNewUser(user);
        filmService.addLike(1, 1);
        filmService.removeLike(1, 1);
        Assertions.assertTrue(filmService.findFilm(1).getLiked().isEmpty(), "Ошибка при нормальном удалении лайка.");
    }

    @Test
    public void removeLikeErrorWithWrongFilmDataId0() {
        Film film = new Film("Name", "Description", LocalDate.of(1990, Month.APRIL, 13), Duration.ofMinutes(100), 1);
        filmService.addNewFilm(film);
        User user = new User("abc@acb.ru", "login", "name", LocalDate.of(1986, Month.APRIL, 13), 1);
        userService.addNewUser(user);
        filmService.addLike(1, 1);
        ValidationException exception = Assertions.assertThrows(ValidationException.class, () -> filmService.removeLike(0, 1));
        Assertions.assertEquals(exception.getMessage(), "ID фильма не может быть меньше или равно нулю.",
                "Ошибка при удалении лайка к фильму с ID0.");
    }

    @Test
    public void removeLikeErrorWithWrongFilmDataIdNegative() {
        Film film = new Film("Name", "Description", LocalDate.of(1990, Month.APRIL, 13), Duration.ofMinutes(100), 1);
        filmService.addNewFilm(film);
        User user = new User("abc@acb.ru", "login", "name", LocalDate.of(1986, Month.APRIL, 13), 1);
        userService.addNewUser(user);
        filmService.addLike(1, 1);
        ValidationException exception = Assertions.assertThrows(ValidationException.class, () -> filmService.removeLike(-1, 1));
        Assertions.assertEquals(exception.getMessage(), "ID фильма не может быть меньше или равно нулю.",
                "Ошибка при удалении лайка к фильму с ID-1.");
    }

    @Test
    public void removeLikeErrorWithWrongFilmDataWrongId() {
        Film film = new Film("Name", "Description", LocalDate.of(1990, Month.APRIL, 13), Duration.ofMinutes(100), 1);
        filmService.addNewFilm(film);
        User user = new User("abc@acb.ru", "login", "name", LocalDate.of(1986, Month.APRIL, 13), 1);
        userService.addNewUser(user);
        filmService.addLike(1, 1);
        FilmNotFoundException exception = Assertions.assertThrows(FilmNotFoundException.class, () -> filmService.removeLike(99, 1));
        Assertions.assertEquals(exception.getMessage(), "Фильм c ID 99 не найден.",
                "Ошибка при удалении лайка к фильму с ID99.");
    }


    @Test
    public void removeLikeErrorWithWrongUserDataId0() {
        Film film = new Film("Name", "Description", LocalDate.of(1990, Month.APRIL, 13), Duration.ofMinutes(100), 1);
        filmService.addNewFilm(film);
        User user = new User("abc@acb.ru", "login", "name", LocalDate.of(1986, Month.APRIL, 13), 1);
        userService.addNewUser(user);
        filmService.addLike(1, 1);
        ValidationException exception = Assertions.assertThrows(ValidationException.class, () -> filmService.removeLike(1, 0));
        Assertions.assertEquals(exception.getMessage(), "ID юзера не может быть меньше или равно нулю.",
                "Ошибка при удалении лайка к фильму с юзером ID0.");
    }

    @Test
    public void removeLikeErrorWithWrongUserDataIdNegative() {
        Film film = new Film("Name", "Description", LocalDate.of(1990, Month.APRIL, 13), Duration.ofMinutes(100), 1);
        filmService.addNewFilm(film);
        User user = new User("abc@acb.ru", "login", "name", LocalDate.of(1986, Month.APRIL, 13), 1);
        userService.addNewUser(user);
        filmService.addLike(1, 1);
        ValidationException exception = Assertions.assertThrows(ValidationException.class, () -> filmService.removeLike(1, -1));
        Assertions.assertEquals(exception.getMessage(), "ID юзера не может быть меньше или равно нулю.",
                "Ошибка при удалении лайка к фильму с юзером ID-1.");
    }

    @Test
    public void removeLikeErrorWithWrongUserDataWrongId() {
        Film film = new Film("Name", "Description", LocalDate.of(1990, Month.APRIL, 13), Duration.ofMinutes(100), null);
        filmService.addNewFilm(film);
        User user = new User("abc@acb.ru", "login", "name", LocalDate.of(1986, Month.APRIL, 13), null);
        userService.addNewUser(user);
        filmService.addLike(1, 1);
        UserNotFoundException exception = Assertions.assertThrows(UserNotFoundException.class, () -> filmService.removeLike(1, 99));
        Assertions.assertEquals(exception.getMessage(), "Пользователь c ID 99 не найден.",
                "Ошибка при удалении лайка к фильму с юзером ID99.");
    }

    @Test
    public void findPopularNormalWithCount() {
        Film film = new Film("Name", "Description", LocalDate.of(1990, Month.APRIL, 13), Duration.ofMinutes(100), null);
        filmService.addNewFilm(film);
        Film film2 = new Film("Name2", "Description2", LocalDate.of(1990, Month.APRIL, 13), Duration.ofMinutes(100), null);
        filmService.addNewFilm(film2);
        User user = new User("abc@acb.ru", "login", "name", LocalDate.of(1986, Month.APRIL, 13), 1);
        userService.addNewUser(user);
        filmService.addLike(1, 1);
        Assertions.assertEquals(filmService.getPopularFilms(1), new ArrayList<>(List.of(film)),
                "Ошибка при получении списка из 1 популярных фильмов.");
    }

    @Test
    public void findPopularNormalWithNoCount() {
        Film film = new Film("Name", "Description", LocalDate.of(1990, Month.APRIL, 13), Duration.ofMinutes(100), null);
        filmService.addNewFilm(film);
        Film film2 = new Film("Name2", "Description2", LocalDate.of(1990, Month.APRIL, 13), Duration.ofMinutes(100), null);
        filmService.addNewFilm(film2);
        User user = new User("abc@acb.ru", "login", "name", LocalDate.of(1986, Month.APRIL, 13), null);
        userService.addNewUser(user);
        filmService.addLike(1, 1);
        Assertions.assertEquals(filmService.getPopularFilms(10), new ArrayList<>(List.of(film, film2)),
                "Ошибка при нормальном получении списка из 10 пополурных фильмов.");
    }

    @Test
    public void findPopularErrorCount0() {
        Film film = new Film("Name", "Description", LocalDate.of(1990, Month.APRIL, 13), Duration.ofMinutes(100), null);
        filmService.addNewFilm(film);
        Film film2 = new Film("Name2", "Description2", LocalDate.of(1990, Month.APRIL, 13), Duration.ofMinutes(100), null);
        filmService.addNewFilm(film2);
        User user = new User("abc@acb.ru", "login", "name", LocalDate.of(1986, Month.APRIL, 13), 1);
        userService.addNewUser(user);
        filmService.addLike(1, 1);
        ValidationException exception = Assertions.assertThrows(ValidationException.class, () -> filmService.getPopularFilms(0));
        Assertions.assertEquals(exception.getMessage(), "Значение выводимых фильмов не может быть меньше или равно нулю.",
                "Ошибка при получении ошибки получения популярных 0 фильмов");
    }

    @Test
    public void findPopularErrorCountNegative() {
        Film film = new Film("Name", "Description", LocalDate.of(1990, Month.APRIL, 13), Duration.ofMinutes(100), null);
        filmService.addNewFilm(film);
        Film film2 = new Film("Name2", "Description2", LocalDate.of(1990, Month.APRIL, 13), Duration.ofMinutes(100), null);
        filmService.addNewFilm(film2);
        User user = new User("abc@acb.ru", "login", "name", LocalDate.of(1986, Month.APRIL, 13), 1);
        userService.addNewUser(user);
        filmService.addLike(1, 1);
        ValidationException exception = Assertions.assertThrows(ValidationException.class, () -> filmService.getPopularFilms(-1));
        Assertions.assertEquals(exception.getMessage(), "Значение выводимых фильмов не может быть меньше или равно нулю.",
                "Ошибка при получении ошибки получения популярных -1 фильмов");
    }

    @TestConfiguration
    static class EmployeeServiceImplTestContextConfiguration {
        @Bean
        public FilmService filmServiceInt() {
            return new FilmService();
        }
    }
}