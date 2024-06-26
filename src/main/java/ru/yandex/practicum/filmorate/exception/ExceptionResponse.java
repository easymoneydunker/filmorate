package ru.yandex.practicum.filmorate.exception;

import lombok.Getter;

@Getter
public class ExceptionResponse {
    private final String error;
    private final String cause;

    public ExceptionResponse(String error, String cause) {
        this.error = error;
        this.cause = cause;
    }
}
