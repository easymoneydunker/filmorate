package ru.yandex.practicum.filmorate.service;

import jakarta.validation.ConstraintViolationException;
import jakarta.validation.ValidationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.yandex.practicum.filmorate.exception.*;

@RestControllerAdvice
@ControllerAdvice
public class ErrorHandler {

    @ExceptionHandler(DuplicatedException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ExceptionResponse handleDuplicated(final DuplicatedException e) {
        return new ExceptionResponse("Duplicated error", e.getMessage());
    }

    @ExceptionHandler(IllegalInitializationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ExceptionResponse handleIllegalInitialization(final IllegalInitializationException e) {
        return new ExceptionResponse("Invalid initialization error", e.getMessage());
    }

    @ExceptionHandler(NotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ExceptionResponse handleNotFound(final NotFoundException e) {
        return new ExceptionResponse("Not found error", e.getMessage());
    }

    @ExceptionHandler({ValidationException.class, ConstraintViolationException.class, MethodArgumentNotValidException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ExceptionResponse handleValidationException(final ValidationException e) {
        return new ExceptionResponse("Validation error", e.getMessage());
    }

    @ExceptionHandler(SqlException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ExceptionResponse handleSqlException(final SqlException e) {
        return new ExceptionResponse("Sql execution exception", e.getMessage());
    }

    @ExceptionHandler(Throwable.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ExceptionResponse handleUnknown(final Throwable e) {
        return new ExceptionResponse("Internal error", e.getMessage());
    }
}
