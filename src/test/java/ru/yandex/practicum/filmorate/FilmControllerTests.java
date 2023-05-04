//package ru.yandex.practicum.filmorate;
//
//import org.junit.jupiter.api.Assertions;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.params.ParameterizedTest;
//import org.junit.jupiter.params.provider.MethodSource;
//import org.springframework.boot.test.context.SpringBootTest;
//import ru.yandex.practicum.filmorate.model.Film;
//import ru.yandex.practicum.filmorate.storage.InMemoryFilmStorage;
//
//import javax.validation.ConstraintViolation;
//import javax.validation.Validation;
//import javax.validation.Validator;
//import javax.validation.ValidatorFactory;
//import java.time.Duration;
//import java.time.LocalDate;
//import java.time.Month;
//import java.util.Set;
//import java.util.stream.Stream;
//
//@SpringBootTest
//public class FilmControllerTests {
//
//    private InMemoryFilmStorage filmStorage;
//
//    private Validator validator;
//
//    static Stream<Film> filmWithWrongParameters() {
//        return Stream.of(
//                new Film("", "adipisicing", LocalDate.of(1967, Month.APRIL, 25), Duration.ofMinutes(100), 1),
//                new Film("name", "adipisicing", LocalDate.of(1800, Month.APRIL, 25), Duration.ofMinutes(100), 1),
//                new Film("name", "adipisicing", LocalDate.of(1967, Month.APRIL, 25), Duration.ofMinutes(-100), 1),
//                new Film("name", "nnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnn" +
//                        "nnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnn",
//                        LocalDate.of(1967, Month.APRIL, 25), Duration.ofMinutes(100), 1)
//        );
//    }
//
//    @BeforeEach
//    public void start() {
//        filmStorage = new InMemoryFilmStorage();
//        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
//        validator = factory.getValidator();
//    }
//
//    @Test
//    public void createFilmNormal() {
//        Film film = new Film("Name", "Description", LocalDate.now(), Duration.ofMinutes(100), 1);
//        Set<ConstraintViolation<Film>> violations = validator.validate(film);
//        violations.stream().map(ConstraintViolation::getMessage)
//                .forEach(System.out::println);
//        Assertions.assertSame(0, violations.size(), "Ошибка при создании нормального фильма.");
//    }
//
//    @ParameterizedTest
//    @MethodSource("filmWithWrongParameters")
//    public void postFilmsWithErrorData(Film film) {
//        Set<ConstraintViolation<Film>> violations = validator.validate(film);
//        violations.stream().map(ConstraintViolation::getMessage)
//                .forEach(System.out::println);
//        Assertions.assertSame(1, violations.size(), "Ошибка при выявлении ошибок в данных фильма.");
//    }
//
//    @Test
//    public void getFilmsEmpty() {
//        Assertions.assertTrue(filmStorage.findAllFilms().isEmpty(), "Ошибка при получении пустого хранилища фильмов.");
//    }
//
//    @Test
//    public void getFilmsNormal() {
//        Film film = new Film("Name", "Description", LocalDate.of(1990, Month.APRIL, 13), Duration.ofMinutes(100), 1);
//        Set<ConstraintViolation<Film>> violations = validator.validate(film);
//        filmStorage.addNewFilm(film);
//        Assertions.assertEquals("[Film(liked=[], name=Name, description=Description, releaseDate=1990-04-13, duration=PT1H40M, id=1)]", filmStorage.findAllFilms().toString(),
//                "Ошибка при получении из хранилища существующего фильма.");
//    }
//
//    @Test
//    public void addNewFilmNormal() {
//        Film film = new Film("Name", "Description", LocalDate.of(1990, Month.APRIL, 13), Duration.ofMinutes(100), 1);
//        Set<ConstraintViolation<Film>> violations = validator.validate(film);
//        Assertions.assertEquals(filmStorage.addNewFilm(film), film, "Ошибка при добавлении в хранилище нормального фильма.");
//    }
//}