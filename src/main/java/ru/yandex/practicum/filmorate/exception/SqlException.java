package ru.yandex.practicum.filmorate.exception;

public class SqlException extends RuntimeException {
    public SqlException(String message) {
        super(message);
    }
}