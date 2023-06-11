package ru.yandex.practicum.filmorate.exceptions;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
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
        return sendErrorResponse(ErrorType.ERROR, exception.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse dataIntegrityViolationException(DataIntegrityViolationException exception) {
        return sendErrorResponse(ErrorType.DATA_ACCESS_ERROR, exception.getMessage());
    }

    @ExceptionHandler({UserAlreadyExistsException.class, FilmAlreadyExistsException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse userAlreadyExistsException(RuntimeException exception) {
        return sendErrorResponse(ErrorType.ERROR, exception.getMessage());
    }

    @ExceptionHandler({UserNotFoundException.class, FilmNotFoundException.class, ReviewNotFoundException.class,
            RatingNotFoundException.class, GenreNotFoundException.class, DirectorNotFoundException.class})
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse entityNotFoundException(RuntimeException exception) {
        return sendErrorResponse(ErrorType.ERROR, exception.getMessage());
    }

    @ExceptionHandler({ValidationException.class})
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse validationException(RuntimeException exception) {
        return sendErrorResponse(ErrorType.ERROR, exception.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse illegalArgumentException(IllegalArgumentException e) {
        return sendErrorResponse(ErrorType.ERROR, "Получен неподходящий аргумент или аргумент неправильного типа");
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse otherException(RuntimeException e) {
        return sendErrorResponse(ErrorType.ERROR, "В работе сервера возникла ошибка.");
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse methodArgumentNotValidException(MethodArgumentNotValidException exception) {
        return sendErrorResponse(ErrorType.ERROR, exception.getMessage());
    }

    @ExceptionHandler({LikeAlreadyExistsException.class, ReviewAlreadyExistsException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse entityAlreadyExistsException(RuntimeException exception) {
        return sendErrorResponse(ErrorType.ERROR, exception.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse friendAlreadyExistException(FriendAlreadyExistException exception) {
        return sendErrorResponse(ErrorType.ERROR, exception.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse likeNotFoundException(LikeNotFoundException exception) {
        return sendErrorResponse(ErrorType.ERROR, exception.getMessage());
    }

    private ErrorResponse sendErrorResponse(ErrorType errorType, String description) {
        log.info(description);
        return new ErrorResponse(errorType, description);
    }

    @Data
    public static class ErrorResponse {
        private final ErrorType error;
        private final String description;
    }
}