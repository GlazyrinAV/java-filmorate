package ru.yandex.practicum.filmorate.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.InMemoryUserStorage;

import javax.validation.Valid;
import java.util.Collection;

@RestController
@Validated
@Slf4j
public class UserController {

    @Autowired
    UserService userService;

    @Autowired
    InMemoryUserStorage storage;

    @PostMapping("/users")
    @ResponseStatus(HttpStatus.CREATED)
    public User addNewUser(@Valid @RequestBody User user) {
        log.info("Получен запрос на создание пользователя.");
        return storage.addNewUser(user);
    }

    @PutMapping("/users")
    @ResponseStatus(HttpStatus.OK)
    public User updateUser(@Valid @RequestBody User user) {
        log.info("Получен запрос на обновление пользователя.");
        return storage.updateUser(user);
    }

    @GetMapping("/users")
    @ResponseStatus(HttpStatus.OK)
    public Collection<User> getAllUsers() {
        log.info("Получен запрос на получение списка пользователей.");
        return storage.findAllUsers();
    }

    @DeleteMapping("/resetUsers")
    @ResponseStatus(HttpStatus.OK)
    public void resetForTests() {
        storage.resetUsersForTest();
    }
}