package ru.yandex.practicum.filmorate.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.util.Collection;
import java.util.Map;

@RestController
@RequestMapping("/films")
public class FilmController {
    private final FilmService filmService;

    @Autowired
    public FilmController(FilmService filmService) {
        this.filmService = filmService;
    }

    @GetMapping
    public Collection<Film> getAllFilms() {
        return filmService.getAllFilms();
    }

    @GetMapping("/{filmId}")
    public Film getFilmById(@PathVariable long filmId) {
        return filmService.getFilmById(filmId);
    }

    @GetMapping("/popular")
    public Collection<Film> getPopularFilms(@RequestParam(defaultValue = "10") int count) {
        return filmService.getPopularFilmList(count);
    }

    @PostMapping
    public Film create(@RequestBody Film newFilm) {
        return filmService.create(newFilm);
    }

    @PutMapping
    public Film update(@RequestBody Film newFilm) {
        return filmService.update(newFilm);
    }

    @PutMapping("{filmId}/like/{userId}")
    public Map<String, Long> likeFilm(@PathVariable long filmId, @PathVariable long userId) {
        return filmService.likeFilm(userId, filmId);
    }

    @DeleteMapping("{filmId}/like/{userId}")
    public Map<String, Long> removeLikeFilm(@PathVariable long filmId, @PathVariable long userId) {
        return filmService.removeLikeFromFilm(userId, filmId);
    }
}
