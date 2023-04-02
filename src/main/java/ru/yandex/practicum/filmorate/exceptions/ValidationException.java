package ru.yandex.practicum.filmorate.exceptions;

import org.springframework.web.bind.annotation.ResponseStatus;

import java.io.IOException;

@ResponseStatus()
public class ValidationException extends IOException {


    public ValidationException(String message) {
        super(message);
    }
}