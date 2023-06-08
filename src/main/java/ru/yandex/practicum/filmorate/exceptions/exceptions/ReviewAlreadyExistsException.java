package ru.yandex.practicum.filmorate.exceptions.exceptions;

public class ReviewAlreadyExistsException extends RuntimeException {

    public ReviewAlreadyExistsException(String msg) {
        super(msg);
    }
}
