package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.SqlException;
import ru.yandex.practicum.filmorate.mapper.FilmMapper;
import ru.yandex.practicum.filmorate.mapper.GenreMapper;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.Collection;
import java.util.List;
import java.util.Set;

@Repository
@RequiredArgsConstructor
public class FilmDbStorage implements FilmStorage {
    @Autowired
    private final JdbcTemplate jdbcTemplate;

    @Override
    public Film create(Film film) {
        String query = "INSERT INTO films (name, description, release_date, duration, rating_id) VALUES (?, ?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, film.getName());
            ps.setString(2, film.getDescription());
            ps.setDate(3, java.sql.Date.valueOf(film.getReleaseDate()));
            ps.setInt(4, film.getDuration());
            ps.setLong(5, film.getMpa().getId());
            return ps;
        }, keyHolder);
        Number generatedId = keyHolder.getKey();
        if (generatedId != null) {
            film.setId(generatedId.longValue());
        } else {
            throw new NotFoundException("Failed to retrieve generated film ID.");
        }
        insertFilmGenres(film.getId(), film.getGenres());
        return film;
    }

    @Override
    public Film update(Film film) {
        String query = "UPDATE films SET name = ?, description = ?, release_date = ?, duration = ?, rating_id = ? WHERE film_id = ?";
        jdbcTemplate.update(query, film.getName(), film.getDescription(), java.sql.Date.valueOf(film.getReleaseDate()), film.getDuration(), film.getMpa().getId(), film.getId());
        String deleteGenresQuery = "DELETE FROM genre_line WHERE film_id = ?";
        jdbcTemplate.update(deleteGenresQuery, film.getId());
        insertFilmGenres(film.getId(), film.getGenres());
        return film;
    }

    @Override
    public Collection<Film> getAllFilms() {
        try {
            String query = "SELECT f.film_id, f.name, f.description, f.release_date, f.duration, f.rating_id, r.rating_name " + "FROM films f " + "JOIN ratings r ON f.rating_id = r.rating_id";
            return jdbcTemplate.query(query, new FilmMapper());
        } catch (DataAccessException e) {
            throw new SqlException("Error executing query: SELECT * FROM films");
        }
    }


    @Override
    public Film getFilmById(long id) {
        try {
            String filmQuery = "SELECT f.film_id, f.name, f.description, f.release_date, f.duration, f.rating_id, r.rating_name " + "FROM films f " + "JOIN ratings r ON f.rating_id = r.rating_id " + "WHERE f.film_id = ?";
            List<Film> films = jdbcTemplate.query(filmQuery, new FilmMapper(), id);

            if (films.isEmpty()) {
                throw new NotFoundException("No films found");
            }

            Film film = films.get(0);
            String genreQuery = "SELECT g.genre_id, g.genre_name FROM genre_line gl " + "JOIN genres g ON gl.genre_id = g.genre_id " + "WHERE gl.film_id = ?";
            List<Genre> genres = jdbcTemplate.query(genreQuery, new GenreMapper(), id);
            film.getGenres().addAll(genres);
            return film;
        } catch (DataAccessException e) {
            throw new SqlException("Error executing query: " + e.getMessage());
        }
    }

    public void addLike(long filmId, long userId) {
        String query = "INSERT INTO likes (film_id, user_id) VALUES (?, ?)";
        jdbcTemplate.update(query, filmId, userId);
    }

    public void removeLike(long filmId, long userId) {
        String query = "DELETE FROM likes WHERE film_id = ? AND user_id = ?";
        jdbcTemplate.update(query, filmId, userId);
    }

    private void insertFilmGenres(Long filmId, Set<Genre> genres) {
        if (genres == null || genres.isEmpty()) {
            return;
        }
        StringBuilder insertGenresQuery = new StringBuilder("INSERT INTO genre_line (film_id, genre_id) VALUES ");
        for (Genre genre : genres) {
            insertGenresQuery.append("(").append(filmId).append(", ").append(genre.getId()).append("),");
        }
        insertGenresQuery.setLength(insertGenresQuery.length() - 1);
        try {
            jdbcTemplate.update(insertGenresQuery.toString());
        } catch (DataAccessException e) {
            throw new SqlException("Error executing query: " + insertGenresQuery);
        }
    }


    @Override
    public Collection<Film> getPopularFilmList(int count) {
        String query = "SELECT f.film_id, f.name, f.description, f.release_date, f.duration, f.rating_id, r.rating_name, COUNT(l.user_id) AS like_count " + "FROM films f " + "LEFT JOIN likes l ON f.film_id = l.film_id " + "JOIN ratings r ON f.rating_id = r.rating_id " + "GROUP BY f.film_id, r.rating_id " + "ORDER BY like_count DESC " + "LIMIT ?";
        return jdbcTemplate.query(query, new FilmMapper(), count);
    }
}
