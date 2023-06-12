package ru.yandex.practicum.filmorate.exceptions.exceptions;

import org.springframework.dao.EmptyResultDataAccessException;

public class NoResultDataAccessException extends EmptyResultDataAccessException {

    public NoResultDataAccessException(String msg, int expectedSize) {
        super(msg, expectedSize);
    }
}
