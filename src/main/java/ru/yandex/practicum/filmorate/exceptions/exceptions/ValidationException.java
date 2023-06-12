package ru.yandex.practicum.filmorate.exceptions.exceptions;

import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus()
public class ValidationException extends RuntimeException {

    public ValidationException(String message) {
        super(message);
    }

}