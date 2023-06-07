package ru.yandex.practicum.filmorate.exceptions;

import lombok.Data;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.yandex.practicum.filmorate.exceptions.exceptions.*;

@RestControllerAdvice
public class ErrorHandler {

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse noResultDataAccessException(NoResultDataAccessException exception) {
        return new ErrorResponse("no data returned", exception.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse dataIntegrityViolationException(DataIntegrityViolationException exception) {
        return new ErrorResponse("data access error", exception.getMessage());
    }

    @ExceptionHandler({UserAlreadyExistsException.class, FilmAlreadyExistsException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse userAlreadyExistsException(RuntimeException exception) {
        return new ErrorResponse("error", exception.getMessage());
    }

    @ExceptionHandler({UserNotFoundException.class, FilmNotFoundException.class, ReviewNotFoundException.class})
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse entityNotFoundException(RuntimeException exception) {
        return new ErrorResponse("error", exception.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse validationException(ValidationException exception) {
        return new ErrorResponse("error", exception.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse illegalArgumentException(IllegalArgumentException exception) {
        return new ErrorResponse("error", "Получен неподходящий аргумент или аргумент неправильного типа");
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse otherException(RuntimeException exception) {
        return new ErrorResponse("error", "В работе сервера возникла ошибка.");
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public  ErrorResponse methodArgumentNotValidException(MethodArgumentNotValidException exception) {
        return new ErrorResponse("error", exception.getFieldErrors().toString());
    }

    @ExceptionHandler({LikeAlreadyExistsException.class, ReviewAlreadyExistsException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse entityAlreadyExistsException(RuntimeException exception) {
        return new ErrorResponse("error", exception.getMessage());
    }

    @Data
    public static class ErrorResponse {
        private final String error;
        private final String description;
    }
}
