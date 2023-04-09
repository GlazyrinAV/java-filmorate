package ru.yandex.practicum.filmorate.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;

import javax.validation.Valid;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static org.springframework.http.HttpStatus.*;

@RestController
@Validated
@Slf4j
public class UserController {

    private int idUserSequence = 1;
    private final Map<Integer, User> users = new ConcurrentHashMap<>();

    @PostMapping("/users")
    public ResponseEntity<User> addNewUser(@Valid @RequestBody User user) {
        log.info("Получен запрос на создание пользователя.");
        if (!users.containsValue(user)) {
            if (user.getName() == null) {
                user.setName(user.getLogin());
            }
            user.setId(setNewId());
            users.put(user.getId(), user);
            return new ResponseEntity<>(user, CREATED);
        } else {
            log.info("Такой пользователь уже существует.");
            return new ResponseEntity<>(user, BAD_REQUEST);
        }
    }

    @PutMapping("/users")
    public ResponseEntity<User> updateUser(@Valid @RequestBody User user) {
        log.info("Получен запрос на обновление пользователя.");
        if (!users.containsKey(user.getId())) {
            return new ResponseEntity<>(user, NOT_FOUND);
        } else {
            users.replace(user.getId(), user);
            return new ResponseEntity<>(user, OK);
        }
    }

    @GetMapping("/users")
    public ResponseEntity<Collection<User>> getAllUsers() {
        log.info("Получен запрос на получение списка пользователей.");
        return new ResponseEntity<>(users.values(), OK);
    }

    @ExceptionHandler(RuntimeException.class)
    public final ResponseEntity<Exception> handleAllExceptions(RuntimeException ex) {
        return new ResponseEntity<Exception>(ex, INTERNAL_SERVER_ERROR);
    }

    private int setNewId() {
        return idUserSequence++;
    }

    @DeleteMapping("/resetUsers")
    public void resetForTests() {
        idUserSequence = 1;
    }
}