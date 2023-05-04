package ru.yandex.practicum.filmorate;


import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.exceptions.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.InMemoryUserStorage;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.time.LocalDate;
import java.time.Month;
import java.util.Set;
import java.util.stream.Stream;

@SpringBootTest
@AutoConfigureMockMvc
public class UserControllerTests {

    private InMemoryUserStorage storage;
    private Validator validator;

    static Stream<User> userWithWrongParameters() {
        return Stream.of(
                new User("@acb.ru", "login", "name", LocalDate.of(1986, Month.APRIL, 13), 1),
                new User("abc@", "login", "name", LocalDate.of(1986, Month.APRIL, 13), 1),
                new User("a bc@acb.ru", "login", "name", LocalDate.of(1986, Month.APRIL, 13), 1),
                new User("майл@acb.ru", "login", "name", LocalDate.of(1986, Month.APRIL, 13), 1),
                new User("abc@acb", "login", "name", LocalDate.of(1986, Month.APRIL, 13), 1),
                new User("abc@acb.ru", "", "name", LocalDate.of(1986, Month.APRIL, 13), 1),
                new User("abc@acb.ru", "log in", "name", LocalDate.of(1986, Month.APRIL, 13), 1),
                new User("abc@acb.ru", "login", "name", LocalDate.of(3000, Month.APRIL, 13), 1)
        );
    }

    @BeforeEach
    public void start() {
        storage = new InMemoryUserStorage();
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    public void newUserNormal() {
        User user = new User("abc@acb.ru", "login", "name", LocalDate.of(1986, Month.APRIL, 13), 1);
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        violations.stream().map(ConstraintViolation::getMessage)
                .forEach(System.out::println);
        Assertions.assertSame(0, violations.size(), "Ошибка при нормальном создании нового юзера.");
    }

    @ParameterizedTest
    @MethodSource("userWithWrongParameters")
    public void postFilmsWithErrorData(User user) {
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        violations.stream().map(ConstraintViolation::getMessage)
                .forEach(System.out::println);
        Assertions.assertSame(1, violations.size(), "Ошибка при выявлении ошибок в данных новых юзеров.");
    }

    @Test
    public void getUsersEmpty() {
        Assertions.assertTrue(storage.findAllUsers().isEmpty(), "Ошибка при получении пустого хранилища юзеров.");
    }

    @Test
    public void getUsersNormal() {
        User user = new User("abc@acb.ru", "login", "name", LocalDate.of(1986, Month.APRIL, 13), 1);
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        storage.addNewUser(user);
        Assertions.assertEquals("[User(friends=[], email=abc@acb.ru, login=login, name=name, birthday=1986-04-13, id=1)]", storage.findAllUsers().toString(),
                "Ошибка при получении из хранилища существующего юзера.");
    }

    @Test
    public void addNewUserNormal() {
        User user = new User("abc@acb.ru", "login", "name", LocalDate.of(1986, Month.APRIL, 13), 1);
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        Assertions.assertEquals(storage.addNewUser(user), user, "Ошибка при добавлении нового юзера в хранилище");
    }

    @Test
    public void updateUserNormal() {
        User user = new User("abc@acb.ru", "login", "name", LocalDate.of(1986, Month.APRIL, 13), 1);
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        storage.addNewUser(user);
        User userUpdate = new User("zxc@acb.ru", "nigol", "name", LocalDate.of(1986, Month.APRIL, 13), 1);
        Set<ConstraintViolation<User>> violations2 = validator.validate(userUpdate);
        Assertions.assertSame(storage.updateUser(userUpdate), userUpdate, "Ошибка при нормальном обновлении юзера.");
    }

    @Test
    public void updateUserWithWrongId() {
        User user = new User("abc@acb.ru", "login", "name", LocalDate.of(1986, Month.APRIL, 13), 1);
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        storage.addNewUser(user);
        User userUpdate = new User("zxc@acb.ru", "nigol", "name", LocalDate.of(1986, Month.APRIL, 13), 99);
        Set<ConstraintViolation<User>> violations2 = validator.validate(userUpdate);
        UserNotFoundException exception = Assertions.assertThrows(UserNotFoundException.class, () -> storage.updateUser(userUpdate));
        Assertions.assertEquals(exception.getMessage(), "Пользователь c ID 99 не найден.",
                "Ошибка при обновлении юзера с ошибочным ID.");
    }
}