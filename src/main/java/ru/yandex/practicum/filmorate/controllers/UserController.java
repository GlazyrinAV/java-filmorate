package ru.yandex.practicum.filmorate.controllers;

import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.HashMap;


@RestController
public class UserController {

    private final HashMap<Integer, User> users = new HashMap<>();

    @PostMapping("/user")
    public User addNewUser(@RequestBody User user) {
        try {
            if (validateUser(user) && !users.containsKey(user.getId())) {
                users.put(user.getId(), user);
                return user;
            } else if (validateUser(user) && users.containsKey(user.getId())) {
                System.out.println("Такой пользователь уже существует.");
            }
        } catch (ValidationException e) {
            System.out.println(e.getMessage());
        }
        return null;
    }

    @PutMapping("/user")
    public User updateUser(@RequestBody User user) {
        try {
            if (validateUser(user) && !users.containsKey(user.getId())) {
                users.put(user.getId(), user);
                return user;
            } else if (validateUser(user) && users.containsKey(user.getId())) {
                users.replace(user.getId(), user);
                return user;
            }
        } catch (ValidationException e) {
            System.out.println(e.getMessage());
        }
        return null;
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
}