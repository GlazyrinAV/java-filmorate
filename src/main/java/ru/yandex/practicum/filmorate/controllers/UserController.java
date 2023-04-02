package ru.yandex.practicum.filmorate.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.HashMap;


@RestController
@Slf4j
public class UserController {

    private int idUserSequence = 1;
    private final HashMap<Integer, User> users = new HashMap<>();

    @PostMapping("/users")
    public User addNewUser(@RequestBody User user) {
        user.setId(setId());
        try {
            if (validateUser(user) && !users.containsValue(user)) {
                users.put(user.getId(), user);
                return user;
            } else if (validateUser(user) && users.containsValue(user)) {
                log.info("Такой пользователь уже существует.");
            }
        } catch (ValidationException e) {
            log.info(e.getMessage());
        }
        return user;
    }

    @PutMapping("/users")
    public User updateUser(@RequestBody User user) {
        try {
            if (validateUser(user) && !users.containsKey(user.getId())) {
                user.setId(setId());
                users.put(user.getId(), user);
                return user;
            } else if (validateUser(user) && users.containsKey(user.getId())) {
                users.replace(user.getId(), user);
                return user;
            }
        } catch (ValidationException e) {
            log.info(e.getMessage());
        }
        return user;
    }

    @GetMapping("/users")
    public HashMap<Integer, User> getAllUsers() {
        return users;
    }

    private boolean validateUser(User user) throws ValidationException {
        if (user.getEmail().isEmpty() || !user.getEmail().contains("@")) {
            throw new ValidationException("Неправильно указана электронная почта.");
        } else if (user.getLogin().isEmpty() || !user.getLogin().contains(" ")) {
            throw new ValidationException("Неверное указан логин.");
        } else if (user.getBirthday().isAfter(LocalDate.now())) {
            throw new ValidationException("Неверно указана дата рождения.");
        } else if (user.getName().isEmpty()) {
            user.setName(user.getLogin());
        }
        return true;
    }

    private int setId() {
        return idUserSequence++;
    }
}