package ru.yandex.practicum.filmorate.exceptions.exceptions;

import org.springframework.dao.DataIntegrityViolationException;

public class DataIntegrityException extends DataIntegrityViolationException {
    public DataIntegrityException(String msg) {
        super(msg);
    }
}
