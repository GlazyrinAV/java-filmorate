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
import ru.yandex.practicum.filmorate.exceptions.exceptions.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.Feed;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.dao.FeedStorage;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import java.time.LocalDate;
import java.time.Month;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Sql(value = {"/schema.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(value = {"/data.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
public class UserServiceUnitTests {
    private final Validator validator;
    private final UserService userService;
    private final FeedStorage feedStorage;

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
        Assertions.assertTrue(userService.findAll().isEmpty(), "Ошибка при получении пустого хранилища юзеров.");
    }

    @Test
    public void getUsersNormal() {
        User user = new User("abc@acb.ru", "login", "name", LocalDate.of(1986, Month.APRIL, 13), 1);
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        userService.saveNew(user);
        Assertions.assertEquals("User(email=abc@acb.ru, login=login, name=name, birthday=1986-04-13, id=1)", userService.findById(1).toString(),
                "Ошибка при получении из хранилища существующего юзера.");
    }

    @Test
    public void addNewUserNormal() {
        User user = new User("abc@acb.ru", "login", "name", LocalDate.of(1986, Month.APRIL, 13), 1);
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        Assertions.assertEquals(userService.saveNew(user), user, "Ошибка при добавлении нового юзера в хранилище");
    }

    @Test
    public void updateUserNormal() {
        User user = new User("abc@acb.ru", "login", "name", LocalDate.of(1986, Month.APRIL, 13), 1);
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        userService.saveNew(user);
        User userUpdate = new User("zxc@acb.ru", "nigol", "name", LocalDate.of(1986, Month.APRIL, 13), 1);
        Set<ConstraintViolation<User>> violations2 = validator.validate(userUpdate);
        Assertions.assertEquals(userService.update(userUpdate), userUpdate, "Ошибка при нормальном обновлении юзера.");
    }

    @Test
    public void updateUserWithWrongId() {
        User user = new User("abc@acb.ru", "login", "name", LocalDate.of(1986, Month.APRIL, 13), 1);
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        userService.saveNew(user);
        User userUpdate = new User("zxc@acb.ru", "nigol", "name", LocalDate.of(1986, Month.APRIL, 13), 99);
        Set<ConstraintViolation<User>> violations2 = validator.validate(userUpdate);
        UserNotFoundException exception = Assertions.assertThrows(UserNotFoundException.class, () -> userService.update(userUpdate));
        Assertions.assertEquals(exception.getMessage(), "Пользователь c ID 99 не найден.",
                "Ошибка при обновлении юзера с ошибочным ID.");
    }

    @Test
    public void findAllUsersEmpty() {
        Assertions.assertTrue(userService.findAll().isEmpty(),
                "Ошибка в получении пустого списка юзеров.");
    }

    @Test
    public void findAllUsersNormal() {
        User user = new User("abc@acb.ru", "login", "name", LocalDate.of(1986, Month.APRIL, 13), null);
        userService.saveNew(user);
        Assertions.assertEquals(userService.findAll().toString(), "[User(email=abc@acb.ru, login=login, name=name, birthday=1986-04-13, id=1)]",
                "Ошибка при нормальном получении списка юзеров.");
    }

    @Test
    public void findUserByIdNormal() {
        User user = new User("abc@acb.ru", "login", "name", LocalDate.of(1986, Month.APRIL, 13), null);
        userService.saveNew(user);
        Assertions.assertEquals(userService.findById(1).toString(), "User(email=abc@acb.ru, login=login, name=name, birthday=1986-04-13, id=1)",
                "Ошибка при нормальном поиске юзера.");
    }

    @Test
    public void findUserErrorWrongId0() {
        User user = new User("abc@acb.ru", "login", "name", LocalDate.of(1986, Month.APRIL, 13), null);
        userService.saveNew(user);
        UserNotFoundException exception = Assertions.assertThrows(UserNotFoundException.class, () -> userService.findById(0));
        Assertions.assertEquals(exception.getMessage(), "Пользователь c ID 0 не найден.",
                "Ошибка в получении ошибки при поиске юзера с ID 0");
    }

    @Test
    public void findUserErrorWrongIdNegative() {
        User user = new User("abc@acb.ru", "login", "name", LocalDate.of(1986, Month.APRIL, 13), null);
        userService.saveNew(user);
        UserNotFoundException exception = Assertions.assertThrows(UserNotFoundException.class, () -> userService.findById(-1));
        Assertions.assertEquals(exception.getMessage(), "Пользователь c ID -1 не найден.",
                "Ошибка в получении ошибки при поиске юзера с ID -1");
    }

    @Test
    public void findUserErrorWrongUser() {
        User user = new User("abc@acb.ru", "login", "name", LocalDate.of(1986, Month.APRIL, 13), null);
        userService.saveNew(user);
        UserNotFoundException exception = Assertions.assertThrows(UserNotFoundException.class, () -> userService.findById(99));
        Assertions.assertEquals(exception.getMessage(), "Пользователь c ID 99 не найден.",
                "Ошибка в получении ошибки при поиске юзера с ID 99");
    }

    @Test
    public void addFriendNormalOneSide() {
        User user = new User("abc@acb.ru", "login", "name", LocalDate.of(1986, Month.APRIL, 13), null);
        userService.saveNew(user);
        User user2 = new User("vcx@acb.ru", "afr", "hh", LocalDate.of(1986, Month.APRIL, 14), null);
        userService.saveNew(user2);
        userService.saveFriend(1, 2);
        Assertions.assertTrue(userService.findFriends(1).size() == 1 &&
                        userService.findFriends(2).isEmpty(),
                "Ошибка при нормальном добавлении друга.");

        List<Feed> feeds = new ArrayList<>(feedStorage.findFeed(1));
        Assertions.assertEquals(feeds.size(), 1);
        Feed feed = feeds.get(0);
        Assertions.assertEquals(feed.getEventId(), 1);
        Assertions.assertEquals(feed.getUserId(), 1);
        Assertions.assertEquals(feed.getEntityId(), 2);
        Assertions.assertEquals(feed.getEventType().getEventTypeId(), 3);
        Assertions.assertEquals(feed.getOperation().getOperationId(), 2);
    }

    @Test
    public void addFriendNormalTwoSides() {
        User user = new User("abc@acb.ru", "login", "name", LocalDate.of(1986, Month.APRIL, 13), null);
        userService.saveNew(user);
        User user2 = new User("vcx@acb.ru", "afr", "hh", LocalDate.of(1986, Month.APRIL, 14), null);
        userService.saveNew(user2);
        userService.saveFriend(1, 2);
        userService.saveFriend(2, 1);
        Assertions.assertTrue(userService.findFriends(1).size() == 1 &&
                        userService.findFriends(2).size() == 1,
                "Ошибка при нормальном добавлении друга.");
    }

    @Test
    public void addFriendErrorUserId0() {
        User user = new User("abc@acb.ru", "login", "name", LocalDate.of(1986, Month.APRIL, 13), null);
        userService.saveNew(user);
        User user2 = new User("vcx@acb.ru", "afr", "hh", LocalDate.of(1986, Month.APRIL, 14), null);
        userService.saveNew(user2);
        UserNotFoundException exception = Assertions.assertThrows(UserNotFoundException.class, () -> userService.saveFriend(0, 2));
        Assertions.assertEquals(exception.getMessage(), "Пользователь c ID 0 не найден.",
                "Ошибка в получении ошибки при добавлении друга юзеру с ID 0");
    }

    @Test
    public void addFriendErrorUserIdNegative() {
        User user = new User("abc@acb.ru", "login", "name", LocalDate.of(1986, Month.APRIL, 13), null);
        userService.saveNew(user);
        User user2 = new User("vcx@acb.ru", "afr", "hh", LocalDate.of(1986, Month.APRIL, 14), null);
        userService.saveNew(user2);
        UserNotFoundException exception = Assertions.assertThrows(UserNotFoundException.class, () -> userService.saveFriend(-1, 2));
        Assertions.assertEquals(exception.getMessage(), "Пользователь c ID -1 не найден.",
                "Ошибка в получении ошибки при добавлении друга юзеру с ID -1");
    }

    @Test
    public void addFriendErrorUserWrongId() {
        User user = new User("abc@acb.ru", "login", "name", LocalDate.of(1986, Month.APRIL, 13), null);
        userService.saveNew(user);
        User user2 = new User("vcx@acb.ru", "afr", "hh", LocalDate.of(1986, Month.APRIL, 14), null);
        userService.saveNew(user2);
        UserNotFoundException exception = Assertions.assertThrows(UserNotFoundException.class, () -> userService.saveFriend(99, 2));
        Assertions.assertEquals(exception.getMessage(), "Пользователь c ID 99 не найден.",
                "Ошибка в получении ошибки при добавлении друга юзеру с ID -1");
    }

    @Test
    public void addFriendErrorFriendId0() {
        User user = new User("abc@acb.ru", "login", "name", LocalDate.of(1986, Month.APRIL, 13), null);
        userService.saveNew(user);
        User user2 = new User("vcx@acb.ru", "afr", "hh", LocalDate.of(1986, Month.APRIL, 14), null);
        userService.saveNew(user2);
        UserNotFoundException exception = Assertions.assertThrows(UserNotFoundException.class, () -> userService.saveFriend(1, 0));
        Assertions.assertEquals(exception.getMessage(), "Пользователь c ID 0 не найден.",
                "Ошибка в получении ошибки при добавлении друга юзеру с ID 0");
    }

    @Test
    public void addFriendErrorFriendIdNegative() {
        User user = new User("abc@acb.ru", "login", "name", LocalDate.of(1986, Month.APRIL, 13), null);
        userService.saveNew(user);
        User user2 = new User("vcx@acb.ru", "afr", "hh", LocalDate.of(1986, Month.APRIL, 14), null);
        userService.saveNew(user2);
        UserNotFoundException exception = Assertions.assertThrows(UserNotFoundException.class, () -> userService.saveFriend(1, -1));
        Assertions.assertEquals(exception.getMessage(), "Пользователь c ID -1 не найден.",
                "Ошибка в получении ошибки при добавлении друга юзеру с ID -1");
    }

    @Test
    public void addFriendErrorFriendWrongId() {
        User user = new User("abc@acb.ru", "login", "name", LocalDate.of(1986, Month.APRIL, 13), null);
        userService.saveNew(user);
        User user2 = new User("vcx@acb.ru", "afr", "hh", LocalDate.of(1986, Month.APRIL, 14), null);
        userService.saveNew(user2);
        UserNotFoundException exception = Assertions.assertThrows(UserNotFoundException.class, () -> userService.saveFriend(1, 99));
        Assertions.assertEquals(exception.getMessage(), "Пользователь c ID 99 не найден.",
                "Ошибка в получении ошибки при добавлении друга юзеру с ID 99");
    }

    @Test
    public void removeFriendNormal() {
        User user = new User("abc@acb.ru", "login", "name", LocalDate.of(1986, Month.APRIL, 13), null);
        userService.saveNew(user);
        User user2 = new User("vcx@acb.ru", "afr", "hh", LocalDate.of(1986, Month.APRIL, 14), null);
        userService.saveNew(user2);
        userService.saveFriend(1, 2);
        userService.removeFriend(1, 2);
        Assertions.assertTrue(userService.findFriends(1).isEmpty() &&
                        userService.findFriends(2).isEmpty(),
                "Ошибка при нормальном удалении друга.");

        List<Feed> feeds = new ArrayList<>(feedStorage.findFeed(1));
        Assertions.assertEquals(feeds.size(), 2);
        Feed firstFeed = feeds.get(0);
        Assertions.assertEquals(firstFeed.getEventId(), 1);
        Assertions.assertEquals(firstFeed.getUserId(), 1);
        Assertions.assertEquals(firstFeed.getEntityId(), 2);
        Assertions.assertEquals(firstFeed.getEventType().getEventTypeId(), 3);
        Assertions.assertEquals(firstFeed.getOperation().getOperationId(), 2);
        Feed secondFeed = feeds.get(1);
        Assertions.assertEquals(secondFeed.getEventId(), 2);
        Assertions.assertEquals(secondFeed.getUserId(), 1);
        Assertions.assertEquals(secondFeed.getEntityId(), 2);
        Assertions.assertEquals(secondFeed.getEventType().getEventTypeId(), 3);
        Assertions.assertEquals(secondFeed.getOperation().getOperationId(), 1);
    }

    @Test
    public void removeFriendErrorUserId0() {
        User user = new User("abc@acb.ru", "login", "name", LocalDate.of(1986, Month.APRIL, 13), null);
        userService.saveNew(user);
        User user2 = new User("vcx@acb.ru", "afr", "hh", LocalDate.of(1986, Month.APRIL, 14), null);
        userService.saveNew(user2);
        UserNotFoundException exception = Assertions.assertThrows(UserNotFoundException.class, () -> userService.removeFriend(0, 2));
        Assertions.assertEquals(exception.getMessage(), "Пользователь c ID 0 не найден.",
                "Ошибка в получении ошибки при удалении друга у юзера с ID 0");
    }

    @Test
    public void removeFriendErrorUserIdNegative() {
        User user = new User("abc@acb.ru", "login", "name", LocalDate.of(1986, Month.APRIL, 13), null);
        userService.saveNew(user);
        User user2 = new User("vcx@acb.ru", "afr", "hh", LocalDate.of(1986, Month.APRIL, 14), null);
        userService.saveNew(user2);
        UserNotFoundException exception = Assertions.assertThrows(UserNotFoundException.class, () -> userService.removeFriend(-1, 2));
        Assertions.assertEquals(exception.getMessage(), "Пользователь c ID -1 не найден.",
                "Ошибка в получении ошибки  при удалении друга у юзера с ID -1");
    }

    @Test
    public void removeFriendErrorUserWrongId() {
        User user = new User("abc@acb.ru", "login", "name", LocalDate.of(1986, Month.APRIL, 13), null);
        userService.saveNew(user);
        User user2 = new User("vcx@acb.ru", "afr", "hh", LocalDate.of(1986, Month.APRIL, 14), null);
        userService.saveNew(user2);
        UserNotFoundException exception = Assertions.assertThrows(UserNotFoundException.class, () -> userService.removeFriend(99, 2));
        Assertions.assertEquals(exception.getMessage(), "Пользователь c ID 99 не найден.",
                "Ошибка в получении ошибки  при удалении друга у юзера с ID -1");
    }

    @Test
    public void removeFriendErrorFriendId0() {
        User user = new User("abc@acb.ru", "login", "name", LocalDate.of(1986, Month.APRIL, 13), null);
        userService.saveNew(user);
        User user2 = new User("vcx@acb.ru", "afr", "hh", LocalDate.of(1986, Month.APRIL, 14), null);
        userService.saveNew(user2);
        UserNotFoundException exception = Assertions.assertThrows(UserNotFoundException.class, () -> userService.removeFriend(1, 0));
        Assertions.assertEquals(exception.getMessage(), "Пользователь c ID 0 не найден.",
                "Ошибка в получении ошибки  при удалении друга у юзера с ID 0");
    }

    @Test
    public void removeFriendErrorFriendIdNegative() {
        User user = new User("abc@acb.ru", "login", "name", LocalDate.of(1986, Month.APRIL, 13), null);
        userService.saveNew(user);
        User user2 = new User("vcx@acb.ru", "afr", "hh", LocalDate.of(1986, Month.APRIL, 14), null);
        userService.saveNew(user2);
        UserNotFoundException exception = Assertions.assertThrows(UserNotFoundException.class, () -> userService.removeFriend(1, -1));
        Assertions.assertEquals(exception.getMessage(), "Пользователь c ID -1 не найден.",
                "Ошибка в получении ошибки  при удалении друга у юзера с ID -1");
    }

    @Test
    public void removeFriendErrorFriendWrongId() {
        User user = new User("abc@acb.ru", "login", "name", LocalDate.of(1986, Month.APRIL, 13), null);
        userService.saveNew(user);
        User user2 = new User("vcx@acb.ru", "afr", "hh", LocalDate.of(1986, Month.APRIL, 14), null);
        userService.saveNew(user2);
        UserNotFoundException exception = Assertions.assertThrows(UserNotFoundException.class, () -> userService.removeFriend(1, 99));
        Assertions.assertEquals(exception.getMessage(), "Пользователь c ID 99 не найден.",
                "Ошибка в получении ошибки  при удалении друга у юзера с ID 99");
    }

    @Test
    public void findFriendsNormalOneSide() {
        User user = new User("abc@acb.ru", "login", "name", LocalDate.of(1986, Month.APRIL, 13), null);
        userService.saveNew(user);
        User user2 = new User("vcx@acb.ru", "afr", "hh", LocalDate.of(1986, Month.APRIL, 14), null);
        userService.saveNew(user2);
        userService.saveFriend(1, 2);
        Assertions.assertTrue(userService.findFriends(1).equals(new ArrayList<>(List.of(user2))) &&
                        userService.findFriends(2).isEmpty(),
                "Ошибка при нормально получении списка друзей.");
    }

    @Test
    public void findFriendsNormalTwoSides() {
        User user = new User("abc@acb.ru", "login", "name", LocalDate.of(1986, Month.APRIL, 13), null);
        userService.saveNew(user);
        User user2 = new User("vcx@acb.ru", "afr", "hh", LocalDate.of(1986, Month.APRIL, 14), null);
        userService.saveNew(user2);
        userService.saveFriend(1, 2);
        userService.saveFriend(2, 1);
        Assertions.assertTrue(userService.findFriends(1).equals(new ArrayList<>(List.of(user2))) &&
                        userService.findFriends(2).equals(new ArrayList<>(List.of(user))),
                "Ошибка при нормально получении списка друзей.");
    }

    @Test
    public void findFriendsErrorUserId0() {
        User user = new User("abc@acb.ru", "login", "name", LocalDate.of(1986, Month.APRIL, 13), null);
        userService.saveNew(user);
        User user2 = new User("vcx@acb.ru", "afr", "hh", LocalDate.of(1986, Month.APRIL, 14), null);
        userService.saveNew(user2);
        UserNotFoundException exception = Assertions.assertThrows(UserNotFoundException.class, () -> userService.findFriends(0));
        Assertions.assertEquals(exception.getMessage(), "Пользователь c ID 0 не найден.",
                "Ошибка в получении ошибки при получении списка друзей у юзера с ID 0");
    }

    @Test
    public void findFriendsErrorUserIdNegative() {
        User user = new User("abc@acb.ru", "login", "name", LocalDate.of(1986, Month.APRIL, 13), null);
        userService.saveNew(user);
        User user2 = new User("vcx@acb.ru", "afr", "hh", LocalDate.of(1986, Month.APRIL, 14), null);
        userService.saveNew(user2);
        UserNotFoundException exception = Assertions.assertThrows(UserNotFoundException.class, () -> userService.findFriends(-1));
        Assertions.assertEquals(exception.getMessage(), "Пользователь c ID -1 не найден.",
                "Ошибка в получении ошибки при получении списка друзей у юзера с ID -1");
    }

    @Test
    public void findFriendsErrorUserWrongId() {
        User user = new User("abc@acb.ru", "login", "name", LocalDate.of(1986, Month.APRIL, 13), null);
        userService.saveNew(user);
        User user2 = new User("vcx@acb.ru", "afr", "hh", LocalDate.of(1986, Month.APRIL, 14), null);
        userService.saveNew(user2);
        UserNotFoundException exception = Assertions.assertThrows(UserNotFoundException.class, () -> userService.findFriends(99));
        Assertions.assertEquals(exception.getMessage(), "Пользователь c ID 99 не найден.",
                "Ошибка в получении ошибки при получении списка друзей у юзера с ID -1");
    }

    @Test
    public void findCommonFriendsNormal() {
        User user = new User("abc@acb.ru", "login", "name", LocalDate.of(1986, Month.APRIL, 13), null);
        userService.saveNew(user);
        User user2 = new User("vcx@acb.ru", "afr", "hh", LocalDate.of(1986, Month.APRIL, 14), null);
        userService.saveNew(user2);
        User user3 = new User("jkf@acb.ru", "dsa", "bb", LocalDate.of(1986, Month.APRIL, 14), null);
        userService.saveNew(user3);
        userService.saveFriend(1, 3);
        userService.saveFriend(2, 3);
        Assertions.assertEquals(userService.findCommonFriends(1, 2), new ArrayList<>(List.of(user3)), "Ошибка в нормальном получении общих друзей.");
    }

    @Test
    public void findCommonFriendsErrorUserId0() {
        User user = new User("abc@acb.ru", "login", "name", LocalDate.of(1986, Month.APRIL, 13), null);
        userService.saveNew(user);
        User user2 = new User("vcx@acb.ru", "afr", "hh", LocalDate.of(1986, Month.APRIL, 14), null);
        userService.saveNew(user2);
        User user3 = new User("jkf@acb.ru", "dsa", "bb", LocalDate.of(1986, Month.APRIL, 14), null);
        userService.saveNew(user3);
        userService.saveFriend(1, 3);
        userService.saveFriend(2, 3);
        UserNotFoundException exception = Assertions.assertThrows(UserNotFoundException.class, () -> userService.findCommonFriends(0, 2));
        Assertions.assertEquals(exception.getMessage(), "Пользователь c ID 0 не найден.",
                "Ошибка в получении ошибка при поиске общих друзей юзера с ID 0.");
    }

    @Test
    public void findCommonFriendsErrorUserIdNegative() {
        User user = new User("abc@acb.ru", "login", "name", LocalDate.of(1986, Month.APRIL, 13), null);
        userService.saveNew(user);
        User user2 = new User("vcx@acb.ru", "afr", "hh", LocalDate.of(1986, Month.APRIL, 14), null);
        userService.saveNew(user2);
        User user3 = new User("jkf@acb.ru", "dsa", "bb", LocalDate.of(1986, Month.APRIL, 14), null);
        userService.saveNew(user3);
        userService.saveFriend(1, 3);
        userService.saveFriend(2, 3);
        UserNotFoundException exception = Assertions.assertThrows(UserNotFoundException.class, () -> userService.findCommonFriends(-1, 2));
        Assertions.assertEquals(exception.getMessage(), "Пользователь c ID -1 не найден.",
                "Ошибка в получении ошибка при поиске общих друзей юзера с ID -1.");
    }

    @Test
    public void findCommonFriendsErrorUserWrongId() {
        User user = new User("abc@acb.ru", "login", "name", LocalDate.of(1986, Month.APRIL, 13), null);
        userService.saveNew(user);
        User user2 = new User("vcx@acb.ru", "afr", "hh", LocalDate.of(1986, Month.APRIL, 14), null);
        userService.saveNew(user2);
        User user3 = new User("jkf@acb.ru", "dsa", "bb", LocalDate.of(1986, Month.APRIL, 14), null);
        userService.saveNew(user3);
        userService.saveFriend(1, 3);
        userService.saveFriend(2, 3);
        UserNotFoundException exception = Assertions.assertThrows(UserNotFoundException.class, () -> userService.findCommonFriends(99, 2));
        Assertions.assertEquals(exception.getMessage(), "Пользователь c ID 99 не найден.",
                "Ошибка в получении ошибка при поиске общих друзей юзера с ID 99.");
    }

    @Test
    public void findCommonFriendsErrorOtherUserId0() {
        User user = new User("abc@acb.ru", "login", "name", LocalDate.of(1986, Month.APRIL, 13), null);
        userService.saveNew(user);
        User user2 = new User("vcx@acb.ru", "afr", "hh", LocalDate.of(1986, Month.APRIL, 14), null);
        userService.saveNew(user2);
        User user3 = new User("jkf@acb.ru", "dsa", "bb", LocalDate.of(1986, Month.APRIL, 14), null);
        userService.saveNew(user3);
        userService.saveFriend(1, 3);
        userService.saveFriend(2, 3);
        UserNotFoundException exception = Assertions.assertThrows(UserNotFoundException.class, () -> userService.findCommonFriends(1, 0));
        Assertions.assertEquals(exception.getMessage(), "Пользователь c ID 0 не найден.",
                "Ошибка в получении ошибка при поиске общих друзей юзера с ID 0.");
    }

    @Test
    public void findCommonFriendsErrorOtherUserIdNegative() {
        User user = new User("abc@acb.ru", "login", "name", LocalDate.of(1986, Month.APRIL, 13), null);
        userService.saveNew(user);
        User user2 = new User("vcx@acb.ru", "afr", "hh", LocalDate.of(1986, Month.APRIL, 14), null);
        userService.saveNew(user2);
        User user3 = new User("jkf@acb.ru", "dsa", "bb", LocalDate.of(1986, Month.APRIL, 14), null);
        userService.saveNew(user3);
        userService.saveFriend(1, 3);
        userService.saveFriend(2, 3);
        UserNotFoundException exception = Assertions.assertThrows(UserNotFoundException.class, () -> userService.findCommonFriends(1, -1));
        Assertions.assertEquals(exception.getMessage(), "Пользователь c ID -1 не найден.",
                "Ошибка в получении ошибка при поиске общих друзей юзера с ID -1.");
    }

    @Test
    public void findCommonFriendsErrorOtherUserWrongId() {
        User user = new User("abc@acb.ru", "login", "name", LocalDate.of(1986, Month.APRIL, 13), null);
        userService.saveNew(user);
        User user2 = new User("vcx@acb.ru", "afr", "hh", LocalDate.of(1986, Month.APRIL, 14), null);
        userService.saveNew(user2);
        User user3 = new User("jkf@acb.ru", "dsa", "bb", LocalDate.of(1986, Month.APRIL, 14), null);
        userService.saveNew(user3);
        userService.saveFriend(1, 3);
        userService.saveFriend(2, 3);
        UserNotFoundException exception = Assertions.assertThrows(UserNotFoundException.class, () -> userService.findCommonFriends(1, 99));
        Assertions.assertEquals(exception.getMessage(), "Пользователь c ID 99 не найден.",
                "Ошибка в получении ошибка при поиске общих друзей юзера с ID 99.");
    }
}