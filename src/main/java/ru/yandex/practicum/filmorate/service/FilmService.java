package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.*;

@Service
public class FilmService {
    private final UserStorage userStorage;
    private final FilmStorage filmStorage;

    @Autowired
    public FilmService(UserStorage userStorage, FilmStorage filmStorage) {
        this.userStorage = userStorage;
        this.filmStorage = filmStorage;
    }

    public Film create(Film film) {
        return filmStorage.create(film);
    }

    public Film update(Film film) {
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
        return Map.of("userId", userId, "filmId", filmId);
    }

    public Map<String, Long> removeLikeFromFilm(long userId, long filmId) {
        User user = userStorage.getUserById(userId)
                .orElseThrow(() -> new NotFoundException("No user with id = " + userId));
        Film film = filmStorage.getFilmById(filmId)
                .orElseThrow(() -> new NotFoundException("No film with id = " + filmId));
        user.removeLikedFilm(filmId);
        film.removeLikedUserId(userId);
        return Map.of("userId", userId, "filmId", filmId);
    }

    public Collection<Film> getPopularFilmList(int count) {
        Set<Film> sortedFilms = new TreeSet<>(Comparator.comparingInt((Film f) -> f.getLikes().size()).reversed());
        sortedFilms.addAll(filmStorage.getAllFilms());
        List<Film> sortedFilmList = new ArrayList<>(sortedFilms);
        count = Math.min(count, sortedFilmList.size());
        return sortedFilmList.subList(0, count);
    }
}
