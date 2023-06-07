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
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.service.DirectorsService;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Sql(value = {"/data.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
public class DirectorStorageTests {

    private final DirectorsService directorsService;

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

    }

    @Test
    @Sql(value = {"/dataForDirectorTests.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    public void findByIdWithNegativeId() {

    }

    @Test
    @Sql(value = {"/dataForDirectorTests.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    public void findByIdWithZeroId() {

    }

    @Test
    @Sql(value = {"/dataForDirectorTests.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    public void findByIdWithWrongId() {

    }

    @Test
    @Sql(value = {"/dataForDirectorTests.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    public void removeByIdNormal() {

    }

    @Test
    @Sql(value = {"/dataForDirectorTests.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    public void removeByIdWithNegativeId() {

    }

    @Test
    @Sql(value = {"/dataForDirectorTests.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    public void removeByIdWithZeroId() {

    }

    @Test
    @Sql(value = {"/dataForDirectorTests.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    public void removeByIdWithWrongId() {

    }

    @Test
    @Sql(value = {"/dataForDirectorTests.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    public void removeByFilmIdNormal() {

    }

    @Test
    @Sql(value = {"/dataForDirectorTests.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    public void removeByFilmIdWithNegativeId() {

    }

    @Test
    @Sql(value = {"/dataForDirectorTests.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    public void removeByFilmIdWithZeroId() {

    }

    @Test
    @Sql(value = {"/dataForDirectorTests.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    public void removeByFilmIdWithWrongId() {

    }

    @Test
    @Sql(value = {"/dataForDirectorTests.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    public void isExistsTrue() {

    }

    @Test
    @Sql(value = {"/dataForDirectorTests.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    public void isExistsFalse() {

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