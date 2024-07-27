package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.IllegalInitializationException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FilmDbStorage;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Qualifier("DbFilmStorage")
public class FilmService {

    private final UserStorage userStorage;
    private final FilmStorage filmStorage;
    private final FilmDbStorage filmDbStorage;
    private final GenreService genreService;
    private final RatingService ratingService;

    @Autowired
    public FilmService(
            @Qualifier("userDbStorage") UserStorage userStorage,
            @Qualifier("filmDbStorage") FilmStorage filmStorage,
            GenreService genreService,
            RatingService ratingService) {
        this.userStorage = userStorage;
        this.filmStorage = filmStorage;
        this.filmDbStorage = (FilmDbStorage) filmStorage;
        this.genreService = genreService;
        this.ratingService = ratingService;
    }

    public Film create(Film film) {
        validateFilm(film);
        validateGenres(film.getGenres());
        validateRating(film.getMpa());

        return filmStorage.create(film);
    }

    public Film update(Film film) {
        validateFilm(film);
        validateFilmId(film.getId());
        validateGenres(film.getGenres());
        validateRating(film.getMpa());

        return filmStorage.update(film);
    }

    public Collection<Film> getAllFilms() {
        return filmStorage.getAllFilms();
    }

    public Film getFilmById(long id) {
        return filmStorage.getFilmById(id)
                .orElseThrow(() -> new NotFoundException("No film with id = " + id));
    }

    public Map<String, Long> likeFilm(long userId, long filmId) {
        User user = userStorage.getUserById(userId)
                .orElseThrow(() -> new NotFoundException("No user with id = " + userId));
        Film film = filmStorage.getFilmById(filmId)
                .orElseThrow(() -> new NotFoundException("No film with id = " + filmId));

        user.addLikedFilm(filmId);
        film.addLikedUserId(userId);
        filmDbStorage.addLike(filmId, userId);
        return Map.of("userId", userId, "filmId", filmId);
    }

    public Map<String, Long> removeLikeFromFilm(long userId, long filmId) {
        User user = userStorage.getUserById(userId)
                .orElseThrow(() -> new NotFoundException("No user with id = " + userId));
        Film film = filmStorage.getFilmById(filmId)
                .orElseThrow(() -> new NotFoundException("No film with id = " + filmId));

        user.removeLikedFilm(filmId);
        film.removeLikedUserId(userId);
        filmDbStorage.removeLike(filmId, userId);
        return Map.of("userId", userId, "filmId", filmId);
    }

    public Collection<Film> getPopularFilmList(int count) {
        return filmStorage.getAllFilms().stream()
                .sorted(Comparator.comparingInt((Film f) -> f.getLikes().size()).reversed())
                .limit(count)
                .collect(Collectors.toList());
    }

    private void validateFilm(Film film) {
        if (film.getName() == null || film.getName().isBlank()) {
            throw new IllegalInitializationException("Film name cannot be null or blank");
        }
        if (film.getDescription() == null || film.getDescription().length() > 100) {
            throw new IllegalInitializationException("Film description cannot be null and must be less than 100 characters");
        }
        if (film.getReleaseDate() != null && (film.getReleaseDate().isAfter(LocalDate.now()) || film.getReleaseDate().isBefore(LocalDate.of(1890, 3, 26)))) {
            throw new IllegalInitializationException("Illegal film release date");
        }
        if (film.getDuration() != null && film.getDuration() <= 0) {
            throw new IllegalInitializationException("Film duration must be a positive integer");
        }
        if (film.getMpa() != null && film.getMpa().getId() <= 0) {
            throw new IllegalInitializationException("Film MPA rating cannot be null or less than or equal to zero");
        }
    }

    private void validateGenres(Set<Genre> genres) {
        for (Genre genre : genres) {
            if (genreService.getGenreById(genre.getId()) == null) {
                throw new IllegalInitializationException("Invalid genre_id: " + genre.getId());
            }
        }
    }

    private void validateRating(Mpa rating) {
        if (ratingService.getRatingById(rating.getId()) == null) {
            throw new IllegalInitializationException("Invalid rating_id: " + rating.getId());
        }
    }

    private void validateFilmId(Long filmId) {
        Optional<Film> film = filmStorage.getFilmById(filmId);
        if (film.isEmpty()) {
            throw new NotFoundException("No film with id = " + filmId);
        }
    }
}
