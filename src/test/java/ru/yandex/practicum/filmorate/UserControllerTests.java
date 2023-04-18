package ru.yandex.practicum.filmorate;


import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
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
                new User("@acb.ru", "login", "name", LocalDate.of(1986, Month.APRIL, 13)),
                new User("abc@", "login", "name", LocalDate.of(1986, Month.APRIL, 13)),
                new User("a bc@acb.ru", "login", "name", LocalDate.of(1986, Month.APRIL, 13)),
                new User("майл@acb.ru", "login", "name", LocalDate.of(1986, Month.APRIL, 13)),
                new User("abc@acb", "login", "name", LocalDate.of(1986, Month.APRIL, 13)),
                new User("abc@acb.ru", "", "name", LocalDate.of(1986, Month.APRIL, 13)),
                new User("abc@acb.ru", "log in", "name", LocalDate.of(1986, Month.APRIL, 13)),
                new User("abc@acb.ru", "login", "name", LocalDate.of(3000, Month.APRIL, 13))
        );
    }

    @Test
    public void newUserNormal() {
        User user = new User("abc@acb.ru", "login", "name", LocalDate.of(1986, Month.APRIL, 13));
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        violations.stream().map(ConstraintViolation::getMessage)
                .forEach(System.out::println);
        Assertions.assertSame(0, violations.size());
    }

    @ParameterizedTest
    @MethodSource("userWithWrongParameters")
    public void postFilmsWithErrorData(User user) {
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        violations.stream().map(ConstraintViolation::getMessage)
                .forEach(System.out::println);
        Assertions.assertSame(1, violations.size());
    }


    @Test
    public void getFilmsEmpty() {
        Assertions.assertTrue(storage.findAllUsers().isEmpty());
    }

    @Test
    public void getFilmsNormal() {
        User user = new User("abc@acb.ru", "login", "name", LocalDate.of(1986, Month.APRIL, 13));
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        storage.addNewUser(user);
        Assertions.assertEquals("[User(friends=[], email=abc@acb.ru, login=login, name=name, birthday=1986-04-13, id=1)]", storage.findAllUsers().toString());
    }

    @BeforeEach
    public void start() {
        storage = new InMemoryUserStorage();
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }
}