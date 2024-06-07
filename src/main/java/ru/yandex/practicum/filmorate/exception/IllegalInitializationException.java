package ru.yandex.practicum.filmorate.exception;

public class IllegalInitializationException extends RuntimeException {
    public IllegalInitializationException(String message) {
        super(message);
    }
}