package ru.yandex.practicum.filmorate;


import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.jdbc.Sql;
import ru.yandex.practicum.filmorate.exceptions.exceptions.DirectorNotFoundException;
import ru.yandex.practicum.filmorate.exceptions.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.service.DirectorsService;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.util.List;
import java.util.Optional;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Sql(value = {"/data.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
public class DirectorServiceTests {

    private final DirectorsService directorsService;

    private final FilmService filmService;

    @Test
    public void saveNewDirectorNormal() {
        Director director = new Director(1, "Director");
        directorsService.saveNew(new Director(null, "Director"));
        Assertions.assertEquals(directorsService.findById(1), director,
                "Ошибка при нормальном добавлении нового режиссера.");
    }

    @Test
    public void saveNewDirectorWithNullName() {
        Director director = new Director(null, null);
        DataIntegrityViolationException exception = Assertions.assertThrows(DataIntegrityViolationException.class, () ->
                directorsService.saveNew(director));
        Assertions.assertNotNull(exception.getMessage(),
                "Ошибка при добавлении режиссера без имени.");
    }

    @Test
    public void saveNewDirectorWithWrongId() {
        Director director = new Director(1, "Director");
        directorsService.saveNew(new Director(99, "Director"));
        Assertions.assertEquals(directorsService.findById(1), director,
                "Ошибка при добавлении нового режиссера c указанием несуществующего ид.");
    }

    @Test
    public void saveNewDirectorWithNegativeId() {
        Director director = new Director(1, "Director");
        directorsService.saveNew(new Director(-1, "Director"));
        Assertions.assertEquals(directorsService.findById(1), director,
                "Ошибка при добавлении нового режиссера c указанием отрицательного ид.");
    }

    @Test
    public void saveNewDirectorWithZeroId() {
        Director director = new Director(1, "Director");
        directorsService.saveNew(new Director(0, "Director"));
        Assertions.assertEquals(directorsService.findById(1), director,
                "Ошибка при добавлении нового режиссера c указанием нулевого ид.");
    }

    @Test
    @Sql(value = {"/dataForDirectorTests.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    public void updateDirectorNormal() {
        Director director = new Director(1, "Director update");
        directorsService.update(new Director(1, "Director update"));
        Assertions.assertEquals(directorsService.findById(1), director,
                "Ошибка при нормальном обновлении нового режиссера.");
    }

    @Test
    @Sql(value = {"/dataForDirectorTests.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    public void updateDirectorWithNullName() {
        Director director = new Director(1, null);
        DataIntegrityViolationException exception = Assertions.assertThrows(DataIntegrityViolationException.class, () ->
                directorsService.update(director));
        Assertions.assertNotNull(exception.getMessage(),
                "Ошибка при обновлении режиссера без имени.");
    }

    @Test
    @Sql(value = {"/dataForDirectorTests.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    public void updateDirectorWithWrongId() {
        Director director = new Director(99, "Director update");
        DirectorNotFoundException exception = Assertions.assertThrows(DirectorNotFoundException.class, () ->
                directorsService.update(director));
        Assertions.assertEquals(exception.getMessage(), "Режиссер c ID 99 не найден.",
                "Ошибка при обновлении режиссера c указанием несуществующего ид.");
    }

    @Test
    @Sql(value = {"/dataForDirectorTests.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    public void updateDirectorWithNegativeId() {
        Director director = new Director(-1, "Director update");
        DirectorNotFoundException exception = Assertions.assertThrows(DirectorNotFoundException.class, () ->
                directorsService.update(director));
        Assertions.assertEquals(exception.getMessage(), "Режиссер c ID -1 не найден.",
                "Ошибка при обновлении режиссера c указанием отрицательного ид.");
    }

    @Test
    @Sql(value = {"/dataForDirectorTests.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    public void updateDirectorWithZeroId() {
        Director director = new Director(0, "Director update");
        DirectorNotFoundException exception = Assertions.assertThrows(DirectorNotFoundException.class, () ->
                directorsService.update(director));
        Assertions.assertEquals(exception.getMessage(), "Режиссер c ID 0 не найден.",
                "Ошибка при обновлении режиссера c указанием нулевого ид.");
    }

    @Test
    public void findAllWithNoDirectors() {
        Assertions.assertTrue(directorsService.findAll().isEmpty(),
                "Ошибка при получении всех режиссеров при пустой базе.");
    }

    @Test
    @Sql(value = {"/dataForDirectorTests.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    public void findAllWithDirectors() {
        Assertions.assertEquals(2, directorsService.findAll().size(), "Ошибка при получении всех режиссеров при заполненной базе.");
    }

    @Test
    @Sql(value = {"/dataForDirectorTests.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    public void findByIdNormal() {
        Assertions.assertEquals(directorsService.findById(2), new Director(2, "Other Director"), "Ошибка при нормальном поиске по ид.");
    }

    @Test
    @Sql(value = {"/dataForDirectorTests.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    public void findByIdWithNegativeId() {
        DirectorNotFoundException exception = Assertions.assertThrows(DirectorNotFoundException.class, () ->
                directorsService.findById(-1));
        Assertions.assertEquals(exception.getMessage(), "Режиссер c ID -1 не найден.",
                "Ошибка при поиске по -1 ид.");
    }

    @Test
    @Sql(value = {"/dataForDirectorTests.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    public void findByIdWithZeroId() {
        DirectorNotFoundException exception = Assertions.assertThrows(DirectorNotFoundException.class, () ->
                directorsService.findById(0));
        Assertions.assertEquals(exception.getMessage(), "Режиссер c ID 0 не найден.",
                "Ошибка при поиске по 0 ид.");
    }

    @Test
    @Sql(value = {"/dataForDirectorTests.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    public void findByIdWithWrongId() {
        DirectorNotFoundException exception = Assertions.assertThrows(DirectorNotFoundException.class, () ->
                directorsService.findById(99));
        Assertions.assertEquals(exception.getMessage(), "Режиссер c ID 99 не найден.",
                "Ошибка при поиске по 99 ид.");
    }

    @Test
    @Sql(value = {"/dataForDirectorTests.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    public void removeByIdNormal() {
        Director director = directorsService.findById(2);
        directorsService.removeById(2);
        Assertions.assertFalse(directorsService.findAll().contains(director),
                "Ошибка при нормальном удалении режиссера.");
    }

    @Test
    @Sql(value = {"/dataForDirectorTests.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    public void removeByIdWithNegativeId() {
        DirectorNotFoundException exception = Assertions.assertThrows(DirectorNotFoundException.class, () ->
                directorsService.removeById(-1));
        Assertions.assertEquals(exception.getMessage(), "Режиссер c ID -1 не найден.",
                "Ошибка при удалении режиссера с -1 ид.");
    }

    @Test
    @Sql(value = {"/dataForDirectorTests.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    public void removeByIdWithZeroId() {
        DirectorNotFoundException exception = Assertions.assertThrows(DirectorNotFoundException.class, () ->
                directorsService.removeById(0));
        Assertions.assertEquals(exception.getMessage(), "Режиссер c ID 0 не найден.",
                "Ошибка при удалении режиссера с 0 ид.");
    }

    @Test
    @Sql(value = {"/dataForDirectorTests.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    public void removeByIdWithWrongId() {
        DirectorNotFoundException exception = Assertions.assertThrows(DirectorNotFoundException.class, () ->
                directorsService.removeById(99));
        Assertions.assertEquals(exception.getMessage(), "Режиссер c ID 99 не найден.",
                "Ошибка при удалении режиссера с 99 ид.");
    }

    @Test
    @Sql(value = {"/dataForDirectorTests.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    public void removeByFilmIdNormal() {
        directorsService.removeFromFilmByFilmId(1);
        Assertions.assertTrue(filmService.findById(1).getDirectors().isEmpty(),
                "Ошибка при нормальном удалении режиссеров из фильма.");
    }

    @Test
    @Sql(value = {"/dataForDirectorTests.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    public void placeOneDirectorToFilmFromDBNormal() {
        directorsService.saveDirectorsToFilmFromDB(1);
        Assertions.assertTrue(filmService.findById(1).getDirectors().contains(new Director(1, "Director")),
                "Ошибка при нормальном добавлении режиссеров в film.");
    }

    @Test
    @Sql(value = {"/dataForDirectorTests.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    public void placeDirectorsToFilmFromDBWithNegativeId() {
        Assertions.assertEquals(0, directorsService.saveDirectorsToFilmFromDB(-1).size(),
                "Ошибка при добавлении режиссеров в film с ид -1.");
    }

    @Test
    @Sql(value = {"/dataForDirectorTests.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    public void placeDirectorsToFilmFromDBWithZeroId() {
        Assertions.assertEquals(0, directorsService.saveDirectorsToFilmFromDB(0).size(),
                "Ошибка при добавлении режиссеров в film с ид 0.");
    }

    @Test
    @Sql(value = {"/dataForDirectorTests.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    public void placeDirectorsToFilmFromDBWrongId() {
        Assertions.assertEquals(0, directorsService.saveDirectorsToFilmFromDB(99).size(),
                "Ошибка при добавлении режиссеров в film с ид 99.");
    }


    @Test
    @Sql(value = {"/dataForDirectorTests.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    public void saveFilmDirectorsToDBNormal() {
        directorsService.saveDirectorsToDBFromFilm(Optional.of(List.of(new Director(1, null))), 2);
        Assertions.assertEquals(2, filmService.findByDirectorId(Optional.of(1), Optional.of("year")).size(),
                "Ошибка при нормальном добавлении режиссера в БД.");
    }

    @Test
    @Sql(value = {"/dataForDirectorTests.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    public void saveFilmDirectorsToDBWithNegativeId() {
        DataIntegrityViolationException exception = Assertions.assertThrows(DataIntegrityViolationException.class, () ->
                directorsService.saveDirectorsToDBFromFilm(Optional.of(List.of(new Director(-1, null))), 2));
        Assertions.assertEquals(exception.getMessage(), "В запросе неправильно указаны данные о фильме.",
                "Ошибка при добавлении режиссера в БД с режиссером с ид -1.");
    }

    @Test
    @Sql(value = {"/dataForDirectorTests.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    public void saveFilmDirectorsToDBWithZeroId() {
        DataIntegrityViolationException exception = Assertions.assertThrows(DataIntegrityViolationException.class, () ->
                directorsService.saveDirectorsToDBFromFilm(Optional.of(List.of(new Director(0, null))), 2));
        Assertions.assertEquals(exception.getMessage(), "В запросе неправильно указаны данные о фильме.",
                "Ошибка при добавлении режиссера в БД с режиссером с ид 0.");
    }

    @Test
    @Sql(value = {"/dataForDirectorTests.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    public void saveFilmDirectorsToDBWrongId() {
        DataIntegrityViolationException exception = Assertions.assertThrows(DataIntegrityViolationException.class, () ->
                directorsService.saveDirectorsToDBFromFilm(Optional.of(List.of(new Director(99, null))), 2));
        Assertions.assertEquals(exception.getMessage(), "В запросе неправильно указаны данные о фильме.",
                "Ошибка при добавлении режиссера в БД с режиссером с ид 99.");
    }

    @Test
    @Sql(value = {"/dataForDirectorTests.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    public void saveFilmDirectorsToDBWithNegativeFilmId() {
        DataIntegrityViolationException exception = Assertions.assertThrows(DataIntegrityViolationException.class, () ->
                directorsService.saveDirectorsToDBFromFilm(Optional.of(List.of(new Director(1, null))), -1));
        Assertions.assertEquals(exception.getMessage(), "В запросе неправильно указаны данные о фильме.",
                "Ошибка при добавлении режиссера в БД с фильмом с ид -1.");
    }


    @Test
    @Sql(value = {"/dataForDirectorTests.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    public void saveFilmDirectorsToDBWithZeroFilmId() {
        DataIntegrityViolationException exception = Assertions.assertThrows(DataIntegrityViolationException.class, () ->
                directorsService.saveDirectorsToDBFromFilm(Optional.of(List.of(new Director(1, null))), 0));
        Assertions.assertEquals(exception.getMessage(), "В запросе неправильно указаны данные о фильме.",
                "Ошибка при добавлении режиссера в БД с фильмом с ид 0.");
    }

    @Test
    @Sql(value = {"/dataForDirectorTests.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    public void saveFilmDirectorsToDBWithWrongFilmId() {
        DataIntegrityViolationException exception = Assertions.assertThrows(DataIntegrityViolationException.class, () ->
                directorsService.saveDirectorsToDBFromFilm(Optional.of(List.of(new Director(1, null))), 99));
        Assertions.assertEquals(exception.getMessage(), "В запросе неправильно указаны данные о фильме.",
                "Ошибка при добавлении режиссера в БД с фильмом с ид 99.");
    }

    @Test
    @Sql(value = {"/dataForDirectorTests.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    public void findByDirectorIdNormal() {
        Assertions.assertEquals(filmService.findByDirectorId(Optional.of(1), Optional.of("year")).toString(),
                "[Film(name=film, description=description, releaseDate=1999-04-30, duration=PT0.1S, id=1, genres=[], mpa=Rating(id=1, name=G), directors=[Director(id=1, name=Director)])]",
                "Ошибка при нормальном поиске фильмов по режиссеру.");
    }

    @Test
    @Sql(value = {"/dataForDirectorTests.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    public void findByDirectorIdNegativeId() {
        DirectorNotFoundException exception = Assertions.assertThrows(DirectorNotFoundException.class, () ->
                filmService.findByDirectorId(Optional.of(-1), Optional.of("year")));
        Assertions.assertEquals(exception.getMessage(), "Режиссер c ID -1 не найден.",
                "Ошибка при поиске фильмов по режиссеру c bl -1.");
    }


    @Test
    @Sql(value = {"/dataForDirectorTests.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    public void findByDirectorIdZeroId() {
        DirectorNotFoundException exception = Assertions.assertThrows(DirectorNotFoundException.class, () ->
                filmService.findByDirectorId(Optional.of(0), Optional.of("year")));
        Assertions.assertEquals(exception.getMessage(), "Режиссер c ID 0 не найден.",
                "Ошибка при поиске фильмов по режиссеру c bl 0.");
    }

    @Test
    @Sql(value = {"/dataForDirectorTests.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    public void findByDirectorIdWrongId() {
        DirectorNotFoundException exception = Assertions.assertThrows(DirectorNotFoundException.class, () ->
                filmService.findByDirectorId(Optional.of(99), Optional.of("year")));
        Assertions.assertEquals(exception.getMessage(), "Режиссер c ID 99 не найден.",
                "Ошибка при поиске фильмов по режиссеру c bl 99.");
    }

    @Test
    @Sql(value = {"/dataForDirectorTests.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    public void findByDirectorIdWithoutSort() {
        ValidationException exception = Assertions.assertThrows(ValidationException.class, () ->
                filmService.findByDirectorId(Optional.of(1), Optional.ofNullable(null)));
        Assertions.assertEquals(exception.getMessage(), "Не указан параметр сортировки.",
                "Ошибка при поиске фильмов по режисеру без указания сортировки.");
    }

    @Test
    @Sql(value = {"/dataForDirectorTests.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    public void findByDirectorIdWithRWongSort() {
        ValidationException exception = Assertions.assertThrows(ValidationException.class, () ->
                filmService.findByDirectorId(Optional.of(1), Optional.of("nothing")));
        Assertions.assertEquals(exception.getMessage(), "Недопустимый параметр сортировки.",
                "Ошибка при поиске фильмов по режисеру неправильному указанию типа сортировки.");
    }

    @Test
    @Sql(value = {"/dataForDirectorTests.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    public void findByDirectorIdWithoutId() {
        ValidationException exception = Assertions.assertThrows(ValidationException.class, () ->
                filmService.findByDirectorId(Optional.ofNullable(null), Optional.of("year")));
        Assertions.assertEquals(exception.getMessage(), "Не указан ид режиссера.",
                "Ошибка при поиске фильмов по режисеру без указания ид режиссера.");
    }
}