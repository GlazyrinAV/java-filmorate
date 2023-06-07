package ru.yandex.practicum.filmorate.exceptions.exceptions;

public class LikeNotFoundException extends RuntimeException {
    public LikeNotFoundException(String msg) {
        super(msg);
    }
}
