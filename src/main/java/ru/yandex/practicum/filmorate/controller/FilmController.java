package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.DuplicatedException;
import ru.yandex.practicum.filmorate.exception.IllegalInitializationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.validator.FilmValidator;

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
    private final FilmValidator filmValidator = new FilmValidator();

    @GetMapping
    public Collection<Film> getFilms() {
        return films.values();
    }

    @PostMapping
    public Film create(@Valid @RequestBody Film newFilm) {
        if (Objects.nonNull(newFilm.getDuration()) && newFilm.getDuration() < 0) {
            throw new IllegalInitializationException("Duration cannot be negative");
        }
        if (Objects.nonNull(newFilm.getReleaseDate()) && newFilm.getReleaseDate().isBefore(LocalDate.of(1895, Month.DECEMBER, 28))) {
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
        filmValidator.validate(newFilm, films);
        Film oldFilm = films.get(newFilm.getId());
        if (Objects.isNull(newFilm.getName())) {
            newFilm.setName(oldFilm.getName());
        }
        if (Objects.isNull(newFilm.getDescription())) {
            newFilm.setDescription(oldFilm.getDescription());
        }
        if (Objects.isNull(newFilm.getReleaseDate())) {
            newFilm.setReleaseDate(oldFilm.getReleaseDate());
        }
        if (Objects.isNull(newFilm.getDuration())) {
            newFilm.setDuration(oldFilm.getDuration());
        }
        films.put(newFilm.getId(), newFilm);
        return newFilm;
    }

    private long getNextId() {
        long currentMaxId = films.keySet().stream().mapToLong(id -> id).max().orElse(0);
        return ++currentMaxId;
    }
}
