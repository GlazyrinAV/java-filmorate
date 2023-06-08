package ru.yandex.practicum.filmorate.exceptions.exceptions;

public class LikeAlreadyExistsException extends RuntimeException {

    public LikeAlreadyExistsException(String msg) {
        super(msg);
    }
}
