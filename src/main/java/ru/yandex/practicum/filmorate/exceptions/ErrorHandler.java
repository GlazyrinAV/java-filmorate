package ru.yandex.practicum.filmorate.exceptions;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.yandex.practicum.filmorate.exceptions.exceptions.*;

@RestControllerAdvice
@Slf4j
public class ErrorHandler {

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse noResultDataAccessException(NoResultDataAccessException exception) {
        log.info(exception.getMessage());
        return new ErrorResponse("no data returned", exception.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse dataIntegrityViolationException(DataIntegrityViolationException exception) {
        log.info(exception.getMessage());
        return new ErrorResponse("data access error", exception.getMessage());
    }

    @ExceptionHandler({UserAlreadyExistsException.class, FilmAlreadyExistsException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse userAlreadyExistsException(RuntimeException exception) {
        log.info(exception.getMessage());
        return new ErrorResponse("error", exception.getMessage());
    }

    @ExceptionHandler({UserNotFoundException.class, FilmNotFoundException.class, ReviewNotFoundException.class})
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse entityNotFoundException(RuntimeException exception) {
        log.info(exception.getMessage());
        return new ErrorResponse("error", exception.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse validationException(ValidationException exception) {
        log.info(exception.getMessage());
        return new ErrorResponse("error", exception.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse illegalArgumentException(IllegalArgumentException exception) {
        log.info("Получен неподходящий аргумент или аргумент неправильного типа");
        return new ErrorResponse("error", "Получен неподходящий аргумент или аргумент неправильного типа");
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse otherException(RuntimeException exception) {
        log.info("В работе сервера возникла ошибка.");
        return new ErrorResponse("error", "В работе сервера возникла ошибка.");
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public  ErrorResponse methodArgumentNotValidException(MethodArgumentNotValidException exception) {
        log.info(exception.getMessage());
        return new ErrorResponse("error", exception.getFieldErrors().toString());
    }

    @ExceptionHandler({LikeAlreadyExistsException.class, ReviewAlreadyExistsException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse entityAlreadyExistsException(RuntimeException exception) {
        log.info(exception.getMessage());
        return new ErrorResponse("error", exception.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse friendAlreadyExistException(FriendAlreadyExistException exception) {
        log.info(exception.getMessage());
        return new ErrorResponse("error", exception.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse likeNotFoundException(LikeNotFoundException exception) {
        log.info(exception.getMessage());
        return new ErrorResponse("error", exception.getMessage());
    }

    @Data
    public static class ErrorResponse {
        private final String error;
        private final String description;
    }
}