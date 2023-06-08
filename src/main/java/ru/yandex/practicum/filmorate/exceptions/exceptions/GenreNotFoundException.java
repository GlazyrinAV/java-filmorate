package ru.yandex.practicum.filmorate.exceptions.exceptions;

public class GenreNotFoundException extends RuntimeException {

    public GenreNotFoundException(String msg) {
        super(msg);
    }
}
