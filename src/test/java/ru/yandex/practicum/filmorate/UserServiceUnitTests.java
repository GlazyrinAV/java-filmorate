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
class UserServiceUnitTests {

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
    void newUserNormal() {
        User user = new User("abc@acb.ru", "login", "name", LocalDate.of(1986, Month.APRIL, 13), 1);
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        violations.stream().map(ConstraintViolation::getMessage)
                .forEach(System.out::println);
        Assertions.assertSame(0, violations.size(), "Ошибка при нормальном создании нового юзера.");
    }

    @ParameterizedTest
    @MethodSource("userWithWrongParameters")
    void postFilmsWithErrorData(User user) {
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        violations.stream().map(ConstraintViolation::getMessage)
                .forEach(System.out::println);
        Assertions.assertSame(1, violations.size(), "Ошибка при выявлении ошибок в данных новых юзеров.");
    }

    @Test
    void getUsersEmpty() {
        Assertions.assertTrue(userService.findAll().isEmpty(), "Ошибка при получении пустого хранилища юзеров.");
    }

    @Test
    void getUsersNormal() {
        User user = new User("abc@acb.ru", "login", "name", LocalDate.of(1986, Month.APRIL, 13), 1);
        validator.validate(user);
        userService.saveNew(user);
        Assertions.assertEquals("User(email=abc@acb.ru, login=login, name=name, birthday=1986-04-13, id=1)", userService.findById(1).toString(),
                "Ошибка при получении из хранилища существующего юзера.");
    }

    @Test
    void addNewUserNormal() {
        User user = new User("abc@acb.ru", "login", "name", LocalDate.of(1986, Month.APRIL, 13), 1);
        validator.validate(user);
        Assertions.assertEquals(userService.saveNew(user), user, "Ошибка при добавлении нового юзера в хранилище");
    }

    @Test
    void updateUserNormal() {
        User user = new User("abc@acb.ru", "login", "name", LocalDate.of(1986, Month.APRIL, 13), 1);
        validator.validate(user);
        userService.saveNew(user);
        User userUpdate = new User("zxc@acb.ru", "log", "name", LocalDate.of(1986, Month.APRIL, 13), 1);
        validator.validate(userUpdate);
        Assertions.assertEquals(userService.update(userUpdate), userUpdate, "Ошибка при нормальном обновлении юзера.");
    }

    @Test
    void updateUserWithWrongId() {
        User user = new User("abc@acb.ru", "login", "name", LocalDate.of(1986, Month.APRIL, 13), 1);
        validator.validate(user);
        userService.saveNew(user);
        User userUpdate = new User("zxc@acb.ru", "log", "name", LocalDate.of(1986, Month.APRIL, 13), 99);
        validator.validate(userUpdate);
        UserNotFoundException exception = Assertions.assertThrows(UserNotFoundException.class, () -> userService.update(userUpdate));
        Assertions.assertEquals("Пользователь c ID 99 не найден.", exception.getMessage(),
                "Ошибка при обновлении юзера с ошибочным ID.");
    }

    @Test
    void findAllUsersEmpty() {
        Assertions.assertTrue(userService.findAll().isEmpty(),
                "Ошибка в получении пустого списка юзеров.");
    }

    @Test
    void findAllUsersNormal() {
        User user = new User("abc@acb.ru", "login", "name", LocalDate.of(1986, Month.APRIL, 13), null);
        userService.saveNew(user);
        Assertions.assertEquals("[User(email=abc@acb.ru, login=login, name=name, birthday=1986-04-13, id=1)]", userService.findAll().toString(),
                "Ошибка при нормальном получении списка юзеров.");
    }

    @Test
    void findUserByIdNormal() {
        User user = new User("abc@acb.ru", "login", "name", LocalDate.of(1986, Month.APRIL, 13), null);
        userService.saveNew(user);
        Assertions.assertEquals("User(email=abc@acb.ru, login=login, name=name, birthday=1986-04-13, id=1)", userService.findById(1).toString(),
                "Ошибка при нормальном поиске юзера.");
    }

    @Test
    void findUserErrorWrongId0() {
        User user = new User("abc@acb.ru", "login", "name", LocalDate.of(1986, Month.APRIL, 13), null);
        userService.saveNew(user);
        UserNotFoundException exception = Assertions.assertThrows(UserNotFoundException.class, () -> userService.findById(0));
        Assertions.assertEquals("Пользователь c ID 0 не найден.", exception.getMessage(),
                "Ошибка в получении ошибки при поиске юзера с ID 0");
    }

    @Test
    void findUserErrorWrongIdNegative() {
        User user = new User("abc@acb.ru", "login", "name", LocalDate.of(1986, Month.APRIL, 13), null);
        userService.saveNew(user);
        UserNotFoundException exception = Assertions.assertThrows(UserNotFoundException.class, () -> userService.findById(-1));
        Assertions.assertEquals("Пользователь c ID -1 не найден.", exception.getMessage(),
                "Ошибка в получении ошибки при поиске юзера с ID -1");
    }

    @Test
    void findUserErrorWrongUser() {
        User user = new User("abc@acb.ru", "login", "name", LocalDate.of(1986, Month.APRIL, 13), null);
        userService.saveNew(user);
        UserNotFoundException exception = Assertions.assertThrows(UserNotFoundException.class, () -> userService.findById(99));
        Assertions.assertEquals("Пользователь c ID 99 не найден.", exception.getMessage(),
                "Ошибка в получении ошибки при поиске юзера с ID 99");
    }

    @Test
    void addFriendNormalOneSide() {
        User user = new User("abc@acb.ru", "login", "name", LocalDate.of(1986, Month.APRIL, 13), null);
        userService.saveNew(user);
        User user2 = new User("vcx@acb.ru", "afr", "hh", LocalDate.of(1986, Month.APRIL, 14), null);
        userService.saveNew(user2);
        userService.saveFriend(1, 2);
        Assertions.assertTrue(userService.findFriends(1).size() == 1 &&
                        userService.findFriends(2).isEmpty(),
                "Ошибка при нормальном добавлении друга.");

        List<Feed> feeds = new ArrayList<>(feedStorage.findFeed(1));
        Assertions.assertEquals(1, feeds.size());
        Feed feed = feeds.get(0);
        Assertions.assertEquals(1, feed.getEventId());
        Assertions.assertEquals(1, feed.getUserId());
        Assertions.assertEquals(2, feed.getEntityId());
        Assertions.assertEquals(3, feed.getEventType().getEventTypeId());
        Assertions.assertEquals(2, feed.getOperation().getOperationId());
    }

    @Test
    void addFriendNormalTwoSides() {
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
    void addFriendErrorUserId0() {
        User user = new User("abc@acb.ru", "login", "name", LocalDate.of(1986, Month.APRIL, 13), null);
        userService.saveNew(user);
        User user2 = new User("vcx@acb.ru", "afr", "hh", LocalDate.of(1986, Month.APRIL, 14), null);
        userService.saveNew(user2);
        UserNotFoundException exception = Assertions.assertThrows(UserNotFoundException.class, () -> userService.saveFriend(0, 2));
        Assertions.assertEquals("Пользователь c ID 0 не найден.", exception.getMessage(),
                "Ошибка в получении ошибки при добавлении друга юзеру с ID 0");
    }

    @Test
    void addFriendErrorUserIdNegative() {
        User user = new User("abc@acb.ru", "login", "name", LocalDate.of(1986, Month.APRIL, 13), null);
        userService.saveNew(user);
        User user2 = new User("vcx@acb.ru", "afr", "hh", LocalDate.of(1986, Month.APRIL, 14), null);
        userService.saveNew(user2);
        UserNotFoundException exception = Assertions.assertThrows(UserNotFoundException.class, () -> userService.saveFriend(-1, 2));
        Assertions.assertEquals("Пользователь c ID -1 не найден.", exception.getMessage(),
                "Ошибка в получении ошибки при добавлении друга юзеру с ID -1");
    }

    @Test
    void addFriendErrorUserWrongId() {
        User user = new User("abc@acb.ru", "login", "name", LocalDate.of(1986, Month.APRIL, 13), null);
        userService.saveNew(user);
        User user2 = new User("vcx@acb.ru", "afr", "hh", LocalDate.of(1986, Month.APRIL, 14), null);
        userService.saveNew(user2);
        UserNotFoundException exception = Assertions.assertThrows(UserNotFoundException.class, () -> userService.saveFriend(99, 2));
        Assertions.assertEquals("Пользователь c ID 99 не найден.", exception.getMessage(),
                "Ошибка в получении ошибки при добавлении друга юзеру с ID -1");
    }

    @Test
    void addFriendErrorFriendId0() {
        User user = new User("abc@acb.ru", "login", "name", LocalDate.of(1986, Month.APRIL, 13), null);
        userService.saveNew(user);
        User user2 = new User("vcx@acb.ru", "afr", "hh", LocalDate.of(1986, Month.APRIL, 14), null);
        userService.saveNew(user2);
        UserNotFoundException exception = Assertions.assertThrows(UserNotFoundException.class, () -> userService.saveFriend(1, 0));
        Assertions.assertEquals("Пользователь c ID 0 не найден.", exception.getMessage(),
                "Ошибка в получении ошибки при добавлении друга юзеру с ID 0");
    }

    @Test
    void addFriendErrorFriendIdNegative() {
        User user = new User("abc@acb.ru", "login", "name", LocalDate.of(1986, Month.APRIL, 13), null);
        userService.saveNew(user);
        User user2 = new User("vcx@acb.ru", "afr", "hh", LocalDate.of(1986, Month.APRIL, 14), null);
        userService.saveNew(user2);
        UserNotFoundException exception = Assertions.assertThrows(UserNotFoundException.class, () -> userService.saveFriend(1, -1));
        Assertions.assertEquals("Пользователь c ID -1 не найден.", exception.getMessage(),
                "Ошибка в получении ошибки при добавлении друга юзеру с ID -1");
    }

    @Test
    void addFriendErrorFriendWrongId() {
        User user = new User("abc@acb.ru", "login", "name", LocalDate.of(1986, Month.APRIL, 13), null);
        userService.saveNew(user);
        User user2 = new User("vcx@acb.ru", "afr", "hh", LocalDate.of(1986, Month.APRIL, 14), null);
        userService.saveNew(user2);
        UserNotFoundException exception = Assertions.assertThrows(UserNotFoundException.class, () -> userService.saveFriend(1, 99));
        Assertions.assertEquals("Пользователь c ID 99 не найден.", exception.getMessage(),
                "Ошибка в получении ошибки при добавлении друга юзеру с ID 99");
    }

    @Test
    void removeFriendNormal() {
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
        Assertions.assertEquals(2, feeds.size());
        Feed firstFeed = feeds.get(0);
        Assertions.assertEquals(1, firstFeed.getEventId());
        Assertions.assertEquals(1, firstFeed.getUserId());
        Assertions.assertEquals(2, firstFeed.getEntityId());
        Assertions.assertEquals(3, firstFeed.getEventType().getEventTypeId());
        Assertions.assertEquals(2, firstFeed.getOperation().getOperationId());
        Feed secondFeed = feeds.get(1);
        Assertions.assertEquals(2, secondFeed.getEventId());
        Assertions.assertEquals(1, secondFeed.getUserId());
        Assertions.assertEquals(2, secondFeed.getEntityId());
        Assertions.assertEquals(3, secondFeed.getEventType().getEventTypeId());
        Assertions.assertEquals(1, secondFeed.getOperation().getOperationId());
    }

    @Test
    void removeFriendErrorUserId0() {
        User user = new User("abc@acb.ru", "login", "name", LocalDate.of(1986, Month.APRIL, 13), null);
        userService.saveNew(user);
        User user2 = new User("vcx@acb.ru", "afr", "hh", LocalDate.of(1986, Month.APRIL, 14), null);
        userService.saveNew(user2);
        UserNotFoundException exception = Assertions.assertThrows(UserNotFoundException.class, () -> userService.removeFriend(0, 2));
        Assertions.assertEquals("Пользователь c ID 0 не найден.", exception.getMessage(),
                "Ошибка в получении ошибки при удалении друга у юзера с ID 0");
    }

    @Test
    void removeFriendErrorUserIdNegative() {
        User user = new User("abc@acb.ru", "login", "name", LocalDate.of(1986, Month.APRIL, 13), null);
        userService.saveNew(user);
        User user2 = new User("vcx@acb.ru", "afr", "hh", LocalDate.of(1986, Month.APRIL, 14), null);
        userService.saveNew(user2);
        UserNotFoundException exception = Assertions.assertThrows(UserNotFoundException.class, () -> userService.removeFriend(-1, 2));
        Assertions.assertEquals("Пользователь c ID -1 не найден.", exception.getMessage(),
                "Ошибка в получении ошибки  при удалении друга у юзера с ID -1");
    }

    @Test
    void removeFriendErrorUserWrongId() {
        User user = new User("abc@acb.ru", "login", "name", LocalDate.of(1986, Month.APRIL, 13), null);
        userService.saveNew(user);
        User user2 = new User("vcx@acb.ru", "afr", "hh", LocalDate.of(1986, Month.APRIL, 14), null);
        userService.saveNew(user2);
        UserNotFoundException exception = Assertions.assertThrows(UserNotFoundException.class, () -> userService.removeFriend(99, 2));
        Assertions.assertEquals("Пользователь c ID 99 не найден.", exception.getMessage(),
                "Ошибка в получении ошибки  при удалении друга у юзера с ID -1");
    }

    @Test
    void removeFriendErrorFriendId0() {
        User user = new User("abc@acb.ru", "login", "name", LocalDate.of(1986, Month.APRIL, 13), null);
        userService.saveNew(user);
        User user2 = new User("vcx@acb.ru", "afr", "hh", LocalDate.of(1986, Month.APRIL, 14), null);
        userService.saveNew(user2);
        UserNotFoundException exception = Assertions.assertThrows(UserNotFoundException.class, () -> userService.removeFriend(1, 0));
        Assertions.assertEquals("Пользователь c ID 0 не найден.", exception.getMessage(),
                "Ошибка в получении ошибки  при удалении друга у юзера с ID 0");
    }

    @Test
    void removeFriendErrorFriendIdNegative() {
        User user = new User("abc@acb.ru", "login", "name", LocalDate.of(1986, Month.APRIL, 13), null);
        userService.saveNew(user);
        User user2 = new User("vcx@acb.ru", "afr", "hh", LocalDate.of(1986, Month.APRIL, 14), null);
        userService.saveNew(user2);
        UserNotFoundException exception = Assertions.assertThrows(UserNotFoundException.class, () -> userService.removeFriend(1, -1));
        Assertions.assertEquals("Пользователь c ID -1 не найден.", exception.getMessage(),
                "Ошибка в получении ошибки  при удалении друга у юзера с ID -1");
    }

    @Test
    void removeFriendErrorFriendWrongId() {
        User user = new User("abc@acb.ru", "login", "name", LocalDate.of(1986, Month.APRIL, 13), null);
        userService.saveNew(user);
        User user2 = new User("vcx@acb.ru", "afr", "hh", LocalDate.of(1986, Month.APRIL, 14), null);
        userService.saveNew(user2);
        UserNotFoundException exception = Assertions.assertThrows(UserNotFoundException.class, () -> userService.removeFriend(1, 99));
        Assertions.assertEquals("Пользователь c ID 99 не найден.", exception.getMessage(),
                "Ошибка в получении ошибки  при удалении друга у юзера с ID 99");
    }

    @Test
    void findFriendsNormalOneSide() {
        User user = new User("abc@acb.ru", "login", "name", LocalDate.of(1986, Month.APRIL, 13), 1);
        userService.saveNew(user);
        User user2 = new User("vcx@acb.ru", "afr", "hh", LocalDate.of(1986, Month.APRIL, 14), 2);
        userService.saveNew(user2);
        userService.saveFriend(1, 2);
        Assertions.assertTrue(userService.findFriends(1).equals(new ArrayList<>(List.of(user2))) &&
                        userService.findFriends(2).isEmpty(),
                "Ошибка при нормально получении списка друзей.");
    }

    @Test
    void findFriendsNormalTwoSides() {
        User user = new User("abc@acb.ru", "login", "name", LocalDate.of(1986, Month.APRIL, 13), 1);
        userService.saveNew(user);
        User user2 = new User("vcx@acb.ru", "afr", "hh", LocalDate.of(1986, Month.APRIL, 14), 2);
        userService.saveNew(user2);
        userService.saveFriend(1, 2);
        userService.saveFriend(2, 1);
        Assertions.assertTrue(userService.findFriends(1).equals(new ArrayList<>(List.of(user2))) &&
                        userService.findFriends(2).equals(new ArrayList<>(List.of(user))),
                "Ошибка при нормально получении списка друзей.");
    }

    @Test
    void findFriendsErrorUserId0() {
        User user = new User("abc@acb.ru", "login", "name", LocalDate.of(1986, Month.APRIL, 13), null);
        userService.saveNew(user);
        User user2 = new User("vcx@acb.ru", "afr", "hh", LocalDate.of(1986, Month.APRIL, 14), null);
        userService.saveNew(user2);
        UserNotFoundException exception = Assertions.assertThrows(UserNotFoundException.class, () -> userService.findFriends(0));
        Assertions.assertEquals("Пользователь c ID 0 не найден.", exception.getMessage(),
                "Ошибка в получении ошибки при получении списка друзей у юзера с ID 0");
    }

    @Test
    void findFriendsErrorUserIdNegative() {
        User user = new User("abc@acb.ru", "login", "name", LocalDate.of(1986, Month.APRIL, 13), null);
        userService.saveNew(user);
        User user2 = new User("vcx@acb.ru", "afr", "hh", LocalDate.of(1986, Month.APRIL, 14), null);
        userService.saveNew(user2);
        UserNotFoundException exception = Assertions.assertThrows(UserNotFoundException.class, () -> userService.findFriends(-1));
        Assertions.assertEquals("Пользователь c ID -1 не найден.", exception.getMessage(),
                "Ошибка в получении ошибки при получении списка друзей у юзера с ID -1");
    }

    @Test
    void findFriendsErrorUserWrongId() {
        User user = new User("abc@acb.ru", "login", "name", LocalDate.of(1986, Month.APRIL, 13), null);
        userService.saveNew(user);
        User user2 = new User("vcx@acb.ru", "afr", "hh", LocalDate.of(1986, Month.APRIL, 14), null);
        userService.saveNew(user2);
        UserNotFoundException exception = Assertions.assertThrows(UserNotFoundException.class, () -> userService.findFriends(99));
        Assertions.assertEquals("Пользователь c ID 99 не найден.", exception.getMessage(),
                "Ошибка в получении ошибки при получении списка друзей у юзера с ID -1");
    }

    @Test
    void findCommonFriendsNormal() {
        User user = new User("abc@acb.ru", "login", "name", LocalDate.of(1986, Month.APRIL, 13), null);
        userService.saveNew(user);
        User user2 = new User("vcx@acb.ru", "afr", "hh", LocalDate.of(1986, Month.APRIL, 14), null);
        userService.saveNew(user2);
        User user3 = new User("jkf@acb.ru", "dsa", "bb", LocalDate.of(1986, Month.APRIL, 14), 3);
        userService.saveNew(user3);
        userService.saveFriend(1, 3);
        userService.saveFriend(2, 3);
        Assertions.assertEquals(userService.findCommonFriends(1, 2), new ArrayList<>(List.of(user3)), "Ошибка в нормальном получении общих друзей.");
    }

    @Test
    void findCommonFriendsErrorUserId0() {
        User user = new User("abc@acb.ru", "login", "name", LocalDate.of(1986, Month.APRIL, 13), null);
        userService.saveNew(user);
        User user2 = new User("vcx@acb.ru", "afr", "hh", LocalDate.of(1986, Month.APRIL, 14), null);
        userService.saveNew(user2);
        User user3 = new User("jkf@acb.ru", "dsa", "bb", LocalDate.of(1986, Month.APRIL, 14), null);
        userService.saveNew(user3);
        userService.saveFriend(1, 3);
        userService.saveFriend(2, 3);
        UserNotFoundException exception = Assertions.assertThrows(UserNotFoundException.class, () -> userService.findCommonFriends(0, 2));
        Assertions.assertEquals("Пользователь c ID 0 не найден.", exception.getMessage(),
                "Ошибка в получении ошибка при поиске общих друзей юзера с ID 0.");
    }

    @Test
    void findCommonFriendsErrorUserIdNegative() {
        User user = new User("abc@acb.ru", "login", "name", LocalDate.of(1986, Month.APRIL, 13), null);
        userService.saveNew(user);
        User user2 = new User("vcx@acb.ru", "afr", "hh", LocalDate.of(1986, Month.APRIL, 14), null);
        userService.saveNew(user2);
        User user3 = new User("jkf@acb.ru", "dsa", "bb", LocalDate.of(1986, Month.APRIL, 14), null);
        userService.saveNew(user3);
        userService.saveFriend(1, 3);
        userService.saveFriend(2, 3);
        UserNotFoundException exception = Assertions.assertThrows(UserNotFoundException.class, () -> userService.findCommonFriends(-1, 2));
        Assertions.assertEquals("Пользователь c ID -1 не найден.", exception.getMessage(),
                "Ошибка в получении ошибка при поиске общих друзей юзера с ID -1.");
    }

    @Test
    void findCommonFriendsErrorUserWrongId() {
        User user = new User("abc@acb.ru", "login", "name", LocalDate.of(1986, Month.APRIL, 13), null);
        userService.saveNew(user);
        User user2 = new User("vcx@acb.ru", "afr", "hh", LocalDate.of(1986, Month.APRIL, 14), null);
        userService.saveNew(user2);
        User user3 = new User("jkf@acb.ru", "dsa", "bb", LocalDate.of(1986, Month.APRIL, 14), null);
        userService.saveNew(user3);
        userService.saveFriend(1, 3);
        userService.saveFriend(2, 3);
        UserNotFoundException exception = Assertions.assertThrows(UserNotFoundException.class, () -> userService.findCommonFriends(99, 2));
        Assertions.assertEquals("Пользователь c ID 99 не найден.", exception.getMessage(),
                "Ошибка в получении ошибка при поиске общих друзей юзера с ID 99.");
    }

    @Test
    void findCommonFriendsErrorOtherUserId0() {
        User user = new User("abc@acb.ru", "login", "name", LocalDate.of(1986, Month.APRIL, 13), null);
        userService.saveNew(user);
        User user2 = new User("vcx@acb.ru", "afr", "hh", LocalDate.of(1986, Month.APRIL, 14), null);
        userService.saveNew(user2);
        User user3 = new User("jkf@acb.ru", "dsa", "bb", LocalDate.of(1986, Month.APRIL, 14), null);
        userService.saveNew(user3);
        userService.saveFriend(1, 3);
        userService.saveFriend(2, 3);
        UserNotFoundException exception = Assertions.assertThrows(UserNotFoundException.class, () -> userService.findCommonFriends(1, 0));
        Assertions.assertEquals("Пользователь c ID 0 не найден.", exception.getMessage(),
                "Ошибка в получении ошибка при поиске общих друзей юзера с ID 0.");
    }

    @Test
    void findCommonFriendsErrorOtherUserIdNegative() {
        User user = new User("abc@acb.ru", "login", "name", LocalDate.of(1986, Month.APRIL, 13), null);
        userService.saveNew(user);
        User user2 = new User("vcx@acb.ru", "afr", "hh", LocalDate.of(1986, Month.APRIL, 14), null);
        userService.saveNew(user2);
        User user3 = new User("jkf@acb.ru", "dsa", "bb", LocalDate.of(1986, Month.APRIL, 14), null);
        userService.saveNew(user3);
        userService.saveFriend(1, 3);
        userService.saveFriend(2, 3);
        UserNotFoundException exception = Assertions.assertThrows(UserNotFoundException.class, () -> userService.findCommonFriends(1, -1));
        Assertions.assertEquals("Пользователь c ID -1 не найден.", exception.getMessage(),
                "Ошибка в получении ошибка при поиске общих друзей юзера с ID -1.");
    }

    @Test
    void findCommonFriendsErrorOtherUserWrongId() {
        User user = new User("abc@acb.ru", "login", "name", LocalDate.of(1986, Month.APRIL, 13), null);
        userService.saveNew(user);
        User user2 = new User("vcx@acb.ru", "afr", "hh", LocalDate.of(1986, Month.APRIL, 14), null);
        userService.saveNew(user2);
        User user3 = new User("jkf@acb.ru", "dsa", "bb", LocalDate.of(1986, Month.APRIL, 14), null);
        userService.saveNew(user3);
        userService.saveFriend(1, 3);
        userService.saveFriend(2, 3);
        UserNotFoundException exception = Assertions.assertThrows(UserNotFoundException.class, () -> userService.findCommonFriends(1, 99));
        Assertions.assertEquals("Пользователь c ID 99 не найден.", exception.getMessage(),
                "Ошибка в получении ошибка при поиске общих друзей юзера с ID 99.");
    }
}