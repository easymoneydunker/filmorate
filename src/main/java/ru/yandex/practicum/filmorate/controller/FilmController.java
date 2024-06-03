package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.DuplicatedException;
import ru.yandex.practicum.filmorate.exception.IllegalInitializationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.time.Month;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@RestController
@RequestMapping("/films")
public class FilmController {
    private final Map<Long, Film> films = new HashMap<>();

    @GetMapping
    public Collection<Film> getFilms() {
        return films.values();
    }

    @PostMapping
    public Film create(@Valid @RequestBody Film newFilm) {
        if (Objects.nonNull(newFilm.getDuration()) && newFilm.getDuration() < 0) {
            throw new IllegalInitializationException("Duration cannot be negative");
        }
        if (newFilm.getReleaseDate() != null && newFilm.getReleaseDate().isBefore(LocalDate.of(1895, Month.DECEMBER, 28))) {
            throw new IllegalInitializationException("Illegal release date");
        }
        if (films.values().stream().anyMatch(film -> film.equals(newFilm))) {
            throw new DuplicatedException("Film " + newFilm.getName() + "has already beed added");
        }
        newFilm.setId(getNextId());
        films.put(newFilm.getId(), newFilm);
        return newFilm;
    }

    @PutMapping
    public Film update(@RequestBody Film newFilm) {
        validate(newFilm);
        Film oldFilm = films.get(newFilm.getId());
        if (newFilm.getName() == null) {
            newFilm.setName(oldFilm.getName());
        }
        if (newFilm.getDescription() == null) {
            newFilm.setDescription(oldFilm.getDescription());
        }
        if (newFilm.getReleaseDate() == null) {
            newFilm.setReleaseDate(oldFilm.getReleaseDate());
        }
        if (newFilm.getDuration() == null) {
            newFilm.setDuration(oldFilm.getDuration());
        }
        films.put(newFilm.getId(), newFilm);
        return newFilm;
    }

    private long getNextId() {
        long currentMaxId = films.keySet().stream().mapToLong(id -> id).max().orElse(0);
        return ++currentMaxId;
    }

    private void validate(Film film) {
        if (film.getId() == null) {
            throw new IllegalInitializationException("Film must have an id");
        }
        if (film.getDuration() != null && film.getDuration() < 0) {
            throw new IllegalInitializationException("Duration cannot be negative");
        }
        if (!films.containsKey(film.getId())) {
            throw new DuplicatedException("Film " + film.getName() + "has already beed added");
        }
    }
}
