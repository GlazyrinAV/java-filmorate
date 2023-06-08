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
import ru.yandex.practicum.filmorate.exceptions.exceptions.FilmNotFoundException;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.service.DirectorsService;
import ru.yandex.practicum.filmorate.service.FilmService;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Sql(value = {"/data.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
public class DirectorStorageTests {

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
        Assertions.assertEquals(exception.getMessage(), "Режиссер не найден.",
                "Ошибка при обновлении режиссера c указанием несуществующего ид.");
    }

    @Test
    @Sql(value = {"/dataForDirectorTests.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    public void updateDirectorWithNegativeId() {
        Director director = new Director(-1, "Director update");
        DirectorNotFoundException exception = Assertions.assertThrows(DirectorNotFoundException.class, () ->
                directorsService.update(director));
        Assertions.assertEquals(exception.getMessage(), "Режиссер не найден.",
                "Ошибка при обновлении режиссера c указанием отрицательного ид.");
}

    @Test
    @Sql(value = {"/dataForDirectorTests.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    public void updateDirectorWithZeroId() {
        Director director = new Director(0, "Director update");
        DirectorNotFoundException exception = Assertions.assertThrows(DirectorNotFoundException.class, () ->
                directorsService.update(director));
        Assertions.assertEquals(exception.getMessage(), "Режиссер не найден.",
                "Ошибка при обновлении режиссера c указанием нулевого ид.");
    }

    @Test
    public void findAllWithNoDirectors() {
        Assertions.assertTrue(directorsService.findAll().isEmpty(),
                "Ошибка при получении всех режиссеров при пустой базе.");
    }

    @Test
    @Sql(value = {"/dataForDirectorTests.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    public void findAllWithDirectors()  {
        Assertions.assertTrue(directorsService.findAll().size() == 2,
                "Ошибка при получении всех режиссеров при заполненной базе.");
    }

    @Test
    @Sql(value = {"/dataForDirectorTests.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    public void findByIdNormal() {
        Assertions.assertTrue(directorsService.findById(2).equals(new Director(2, "Other Director")),
                "Ошибка при нормальном поиске по ид.");
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
        directorsService.removeByFilmId(1);
        Assertions.assertTrue(filmService.findById(1).getDirectors().isEmpty(),
                "Ошибка при нормальном удалении режиссеров из фильма.");
    }

    @Test
    @Sql(value = {"/dataForDirectorTests.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    public void removeByFilmIdWithNegativeId() {
        FilmNotFoundException exception = Assertions.assertThrows(FilmNotFoundException.class, () ->
                directorsService.removeByFilmId(-1));
        Assertions.assertEquals(exception.getMessage(), "Фильм c ID -1 не найден.",
                "Ошибка при нормальном удалении режиссеров из фильма с -1 ид.");
    }

    @Test
    @Sql(value = {"/dataForDirectorTests.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    public void removeByFilmIdWithZeroId() {
        FilmNotFoundException exception = Assertions.assertThrows(FilmNotFoundException.class, () ->
                directorsService.removeByFilmId(0));
        Assertions.assertEquals(exception.getMessage(), "Фильм c ID 0 не найден.",
                "Ошибка при нормальном удалении режиссеров из фильма с 0 ид.");
    }

    @Test
    @Sql(value = {"/dataForDirectorTests.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    public void removeByFilmIdWithWrongId() {
        FilmNotFoundException exception = Assertions.assertThrows(FilmNotFoundException.class, () ->
                directorsService.removeByFilmId(99));
        Assertions.assertEquals(exception.getMessage(), "Фильм c ID 99 не найден.",
                "Ошибка при нормальном удалении режиссеров из фильма с 99 ид.");
    }

    @Test
    @Sql(value = {"/dataForDirectorTests.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    public void isExistsTrue() {
        Assertions.assertTrue(directorsService.isExists(1),
                "Ошибка при проверке существующего режиссера.");
    }

    @Test
    @Sql(value = {"/dataForDirectorTests.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    public void isExistsFalse() {
        Assertions.assertFalse(directorsService.isExists(99),
                "Ошибка при проверке несущесвующего режиссера.");
    }

    @Test
    @Sql(value = {"/dataForDirectorTests.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    public void placeDirectorsToFilmFromDBNormal() {

    }

    @Test
    @Sql(value = {"/dataForDirectorTests.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    public void placeDirectorsToFilmFromDBWithNegativeId() {

    }

    @Test
    @Sql(value = {"/dataForDirectorTests.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    public void placeDirectorsToFilmFromDBWithZeroId() {

    }

    @Test
    @Sql(value = {"/dataForDirectorTests.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    public void placeDirectorsToFilmFromDBWrongId() {

    }


    @Test
    @Sql(value = {"/dataForDirectorTests.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    public void saveFilmDirectorsToDBNormal() {

    }

    @Test
    @Sql(value = {"/dataForDirectorTests.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    public void saveFilmDirectorsToDBWithNegativeId() {

    }

    @Test
    @Sql(value = {"/dataForDirectorTests.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    public void saveFilmDirectorsToDBWithZeroId() {

    }

    @Test
    @Sql(value = {"/dataForDirectorTests.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    public void saveFilmDirectorsToDBWrongId() {

    }
}