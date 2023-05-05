package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserDbStorage;

import java.time.LocalDate;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class UserStorageTests {

    @Autowired
    UserDbStorage userStorage;

    @Test
    public void createUserNormal() {
        User user = new User("aa@aa.com", "abc", "bca", LocalDate.of(1986,4,4),null);
        userStorage.addNewUser(user);
        Assertions.assertEquals(user, userStorage.findUser(1));
    }

}
