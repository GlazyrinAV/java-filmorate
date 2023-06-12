package ru.yandex.practicum.filmorate.exceptions.exceptions;

public class FriendAlreadyExistException extends RuntimeException {
    public FriendAlreadyExistException(String msg) {
        super(msg);
    }
}
