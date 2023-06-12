package ru.yandex.practicum.filmorate.exceptions.exceptions;

public class FilmAlreadyExistsException extends RuntimeException {

    public FilmAlreadyExistsException(String message) {
        super(message);
    }

}
