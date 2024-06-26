package ru.yandex.practicum.filmorate.validator;

import ru.yandex.practicum.filmorate.exception.DuplicatedException;
import ru.yandex.practicum.filmorate.exception.IllegalInitializationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.Map;

public class FilmValidator {
    public void validate(Film film, Map<Long, Film> films, boolean duplicationCheck) {
        if (film.getId() == null) {
            throw new IllegalInitializationException("Film must have an id");
        }
        if (film.getName().isBlank()) {
            throw new IllegalInitializationException("Film name cannot be blank");
        }
        if (film.getDuration() != null && film.getDuration() < 0) {
            throw new IllegalInitializationException("Duration cannot be negative");
        }
        if (films.containsKey(film.getId()) && duplicationCheck) {
            throw new DuplicatedException("Film " + film.getName() + "has already beed added");
        }
        if (film.getDescription().length() > 100) {
            throw new IllegalInitializationException("Film description must contain less than 100 characters");
        }
    }
}
