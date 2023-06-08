package ru.yandex.practicum.filmorate.exceptions.exceptions;

public class RatingNotFoundException extends RuntimeException {

    public RatingNotFoundException(String msg) {
        super(msg);
    }
}
