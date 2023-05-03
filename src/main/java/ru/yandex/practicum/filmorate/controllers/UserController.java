package ru.yandex.practicum.filmorate.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import javax.validation.Valid;
import java.util.Collection;

@RestController
@Slf4j
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/users")
    @ResponseStatus(HttpStatus.CREATED)
    public User addNewUser(@Valid @RequestBody User user) {
        log.info("Получен запрос на создание пользователя.");
        return userService.addNewUser(user);
    }

    @PutMapping("/users")
    @ResponseStatus(HttpStatus.OK)
    public User updateUser(@Valid @RequestBody User user) {
        log.info("Получен запрос на обновление пользователя.");
        return userService.updateUser(user);
    }

    @GetMapping("/users")
    @ResponseStatus(HttpStatus.OK)
    public Collection<User> getAllUsers() {
        log.info("Получен запрос на получение списка пользователей.");
        return userService.findAllUsers();
    }

    @GetMapping("/users/{id}")
    @ResponseStatus(HttpStatus.OK)
    public User findUserById(@PathVariable int id) {
        log.info("Получен запрос на поиск пользователя с ID" + id + ".");
        return userService.findUserById(id);
    }

    @PutMapping("/users/{id}/friends/{friendId}")
    @ResponseStatus(HttpStatus.OK)
    public void addNewFriend(@PathVariable int id, @PathVariable int friendId) {
        log.info("Получен запрос на добавление друга юзеру ID" + id);
        userService.addFriend(id, friendId);
    }

    @DeleteMapping("/users/{id}/friends/{friendId}")
    @ResponseStatus(HttpStatus.OK)
    public void removeFriend(@PathVariable int id, @PathVariable int friendId) {
        log.info("Получен запрос на удаление друга у юзера ID" + id);
        userService.removeFriend(id, friendId);
    }

    @GetMapping("/users/{id}/friends")
    @ResponseStatus(HttpStatus.OK)
    public Collection<User> findFriendsOfUser(@PathVariable int id) {
        log.info("Получен запрос на получение друзей у юзера ID" + id);
        return userService.findFriends(id);
    }

    @GetMapping("/users/{id}/friends/common/{otherId}")
    @ResponseStatus(HttpStatus.OK)
    public Collection<User> findCommonFriends(@PathVariable int id, @PathVariable int otherId) {
        log.info("Получен запрос на получение общих друзей у юзера ID" + id + " и юзера ID" + otherId);
        return userService.findCommonFriends(id, otherId);
    }
}