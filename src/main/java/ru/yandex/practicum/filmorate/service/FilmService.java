package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.IllegalInitializationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

@Service
public class FilmService {
    private final UserStorage userStorage;
    private final FilmStorage filmStorage;
    private final GenreService genreService;
    private final RatingService ratingService;

    @Autowired
    public FilmService(UserStorage userStorage, FilmStorage filmStorage, GenreService genreService, RatingService ratingService) {
        this.userStorage = userStorage;
        this.filmStorage = filmStorage;
        this.genreService = genreService;
        this.ratingService = ratingService;
    }

    public Film create(Film film) {
        validateGenres(film.getGenres());
        validateMpa(Long.valueOf(film.getMpa().getId()));
        return filmStorage.create(film);
    }

    public Film update(Film film) {
        validateFilmId(film.getId());
        validateGenres(film.getGenres());
        validateMpa(Long.valueOf(film.getMpa().getId()));
        return filmStorage.update(film);
    }

    public Collection<Film> getAllFilms() {
        return filmStorage.getAllFilms();
    }

    public Film getFilmById(long id) {
        return filmStorage.getFilmById(id);
    }

    public Map<String, Long> likeFilm(long userId, long filmId) {
        User user = userStorage.getUserById(userId);
        Film film = filmStorage.getFilmById(filmId);
        filmStorage.addLike(filmId, userId);
        return Map.of("userId", userId, "filmId", filmId);
    }

    public Map<String, Long> removeLikeFromFilm(long userId, long filmId) {
        User user = userStorage.getUserById(userId);
        Film film = filmStorage.getFilmById(filmId);

        filmStorage.removeLike(filmId, userId);
        return Map.of("userId", userId, "filmId", filmId);
    }

    public Collection<Film> getPopularFilmList(int count) {
        return filmStorage.getPopularFilmList(count);
    }

    private void validateGenres(Set<Genre> genres) {
        String genreIdLine = generateGenreIdLine(genres);
        Set<Genre> genreSet = genreService.getMultipleGenres(genreIdLine);
        if (genreSet.size() != genres.size()) {
            throw new IllegalInitializationException("Invalid genre_id: ");
        }
    }

    private void validateFilmId(Long filmId) {
        filmStorage.getFilmById(filmId);
    }

    private void validateMpa(Long ratingId) {
        ratingService.getRatingById(ratingId);
    }

    private String generateGenreIdLine(Set<Genre> genres) {
        if (genres == null || genres.isEmpty()) {
            return "";
        }
        StringBuilder stringBuilder = new StringBuilder();
        for (Genre genre : genres) {
            stringBuilder.append(genre.getId()).append(",");
        }
        return stringBuilder.substring(0, stringBuilder.length() - 1);
    }

}
