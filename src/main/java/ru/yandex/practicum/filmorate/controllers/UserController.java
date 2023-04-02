package ru.yandex.practicum.filmorate.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;

import static org.springframework.http.HttpStatus.*;


@RestController
@Slf4j
public class UserController {

    private final HashMap<Integer, User> users = new HashMap<>();
    private static int idUserSequence = 1;

    @PostMapping("/users")
    public ResponseEntity<User> addNewUser(@RequestBody User user) {
        log.info("Получен запрос на создание пользователя.");
        try {
            if (validateUser(user) && !users.containsValue(user)) {
                if (user.getName() == null) {
                    user.setName(user.getLogin());
                }
                user.setId(setId());
                users.put(user.getId(), user);
                return new ResponseEntity<>(user, CREATED);
            } else if (validateUser(user) && users.containsValue(user)) {
                log.info("Такой пользователь уже существует.");
                return new ResponseEntity<>(user, BAD_REQUEST);
            }
        } catch (ValidationException e) {
            log.info(e.getMessage());
            return new ResponseEntity<>(user, BAD_REQUEST);
        }
        return new ResponseEntity<>(user, BAD_REQUEST);
    }

    @PutMapping("/users")
    public ResponseEntity<User> updateUser(@RequestBody User user) {
        log.info("Получен запрос на обновление пользователя.");
        try {
            if (validateUser(user) && !users.containsKey(user.getId())) {
                return new ResponseEntity<>(user, NOT_FOUND);
            } else if (validateUser(user) && users.containsKey(user.getId())) {
                users.replace(user.getId(), user);
                return new ResponseEntity<>(user, OK);
            }
        } catch (ValidationException e) {
            log.info(e.getMessage());
            return new ResponseEntity<>(user, BAD_REQUEST);
        }
        return new ResponseEntity<>(user, BAD_REQUEST);
    }

    @GetMapping("/users")
    public ResponseEntity<Collection<User>> getAllUsers() {
        log.info("Получен запрос на получение списка пользователей.");
        return new ResponseEntity<>(users.values(), OK);
    }

    private boolean validateUser(User user) throws ValidationException {
        if (user.getEmail().isEmpty() || !user.getEmail().contains("@")) {
            throw new ValidationException("Неправильно указана электронная почта.");
        } else if (user.getLogin().isEmpty() || user.getLogin().contains(" ")) {
            throw new ValidationException("Неверно указан логин.");
        } else if (user.getBirthday().isAfter(LocalDate.now())) {
            throw new ValidationException("Неверно указана дата рождения.");
        }
        return true;
    }

    private int setId() {
        return idUserSequence++;
    }
}