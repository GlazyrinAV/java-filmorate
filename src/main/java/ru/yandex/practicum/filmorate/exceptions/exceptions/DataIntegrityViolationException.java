package ru.yandex.practicum.filmorate.exceptions.exceptions;

import org.springframework.dao.DataAccessException;

public class DataIntegrityViolationException extends DataAccessException {
    public DataIntegrityViolationException(String msg) {
        super(msg);
    }
}