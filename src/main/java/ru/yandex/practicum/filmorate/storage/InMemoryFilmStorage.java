package ru.yandex.practicum.filmorate.storage;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.DuplicatedException;
import ru.yandex.practicum.filmorate.exception.IllegalInitializationException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.validator.FilmValidator;

import java.time.LocalDate;
import java.time.Month;
import java.util.*;

public class InMemoryFilmStorage implements FilmStorage {
    private final Map<Long, Film> films = new HashMap<>();
    private final FilmValidator filmValidator = new FilmValidator();

    @Override
    public Film create(Film film) {
        if (Objects.nonNull(film.getDuration()) && film.getDuration() < 0) {
            throw new IllegalInitializationException("Duration cannot be negative");
        }
        if (Objects.nonNull(film.getReleaseDate()) && film.getReleaseDate().isBefore(LocalDate.of(1895, Month.DECEMBER, 28))) {
            throw new IllegalInitializationException("Illegal release date");
        }
        if (films.values().stream().anyMatch(film1 -> film1.equals(film))) {
            throw new DuplicatedException("Film " + film.getName() + "has already beed added");
        }
        film.setId(getNextId());
        filmValidator.validate(film, films, true);
        films.put(film.getId(), film);
        return film;
    }

    @Override
    public Film update(Film film) {
        if (!films.containsKey(film.getId())) {
            throw new NotFoundException("No film with id = " + film.getId());
        }
        Film oldFilm = films.get(film.getId());
        if (Objects.isNull(film.getName())) {
            film.setName(oldFilm.getName());
        }
        if (Objects.isNull(film.getDescription())) {
            film.setDescription(oldFilm.getDescription());
        }
        if (Objects.isNull(film.getReleaseDate())) {
            film.setReleaseDate(oldFilm.getReleaseDate());
        }
        if (Objects.isNull(film.getDuration())) {
            film.setDuration(oldFilm.getDuration());
        }
        filmValidator.validate(film, films, false);
        films.put(film.getId(), film);
        return film;
    }

    @Override
    public Collection<Film> getAllFilms() {
        return films.values();
    }

    @Override
    public Optional<Film> getFilmById(long id) {
        return Optional.ofNullable(films.get(id));
    }

    private long getNextId() {
        long currentMaxId = films.keySet().stream().mapToLong(id -> id).max().orElse(0);
        return ++currentMaxId;
    }
}
