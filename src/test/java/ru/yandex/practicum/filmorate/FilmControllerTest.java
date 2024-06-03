package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.exception.DuplicatedException;
import ru.yandex.practicum.filmorate.exception.IllegalInitializationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.Duration;
import java.time.LocalDate;
import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;

public class FilmControllerTest {

    private FilmController filmController;

    @BeforeEach
    public void beforeEach() {
        filmController = new FilmController();
    }

    @Test
    public void testGetFilms() {
        Collection<Film> films = filmController.getFilms();
        assertNotNull(films);
        assertTrue(films.isEmpty());
    }

    @Test
    public void testCreateValidFilm() {
        Film film = new Film();
        film.setName("Valid Film");
        film.setDescription("A valid film description");
        film.setReleaseDate(LocalDate.parse("2000-08-20"));
        film.setDuration(90);

        Film createdFilm = filmController.create(film);
        assertNotNull(createdFilm);
        assertEquals(film.getName(), createdFilm.getName());
    }

    @Test
    public void testCreateFilmWithNullName() {
        Film film = new Film();
        film.setDescription("A film description");
        film.setReleaseDate(LocalDate.parse("2000-08-20"));
        film.setDuration(90);

        assertEquals(0, filmController.getFilms().size());
    }

    @Test
    public void testCreateFilmWithNegativeDuration() {
        Film film = new Film();
        film.setName("Invalid Film");
        film.setDescription("A film description");
        film.setReleaseDate(LocalDate.parse("2000-08-20"));
        film.setDuration(-90);

        assertThrows(IllegalInitializationException.class, () -> {
            filmController.create(film);
        });
    }

    @Test
    public void testUpdateValidFilm() {
        Film film = new Film();
        film.setName("Valid Film");
        film.setDescription("A valid film description");
        film.setReleaseDate(LocalDate.parse("2000-08-20"));
        film.setDuration(90);

        Film createdFilm = filmController.create(film);

        createdFilm.setDescription("Updated description");
        Film updatedFilm = filmController.update(createdFilm);

        assertNotNull(updatedFilm);
        assertEquals(createdFilm.getDescription(), updatedFilm.getDescription());
    }

    @Test
    public void testUpdateFilmWithNullId() {
        Film film = new Film();
        film.setName("Invalid Film");
        film.setDescription("A film description");
        film.setReleaseDate(LocalDate.parse("2000-08-20"));
        film.setDuration(90);

        assertThrows(IllegalInitializationException.class, () -> {
            filmController.update(film);
        });
    }

    @Test
    public void testUpdateNonExistentFilm() {
        Film film = new Film();
        film.setId(999L);
        film.setName("Invalid Film");
        film.setDescription("A film description");
        film.setReleaseDate(LocalDate.parse("2000-08-20"));
        film.setDuration(90);

        assertThrows(DuplicatedException.class, () -> {
            filmController.update(film);
        });
    }
}
