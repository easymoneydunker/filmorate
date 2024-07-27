package ru.yandex.practicum.filmorate.storage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.IllegalInitializationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;

@Repository
public class FilmDbStorage implements FilmStorage {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public FilmDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Film create(Film film) {
        validateRatingId(Long.valueOf(film.getMpa().getId()));
        validateGenres(film.getGenres());
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
            throw new RuntimeException("Failed to retrieve generated film ID.");
        }
        insertFilmGenres(film.getId(), film.getGenres());
        return film;
    }

    @Override
    public Film update(Film film) {
        validateFilmId(film.getId());
        validateRatingId(Long.valueOf(film.getMpa().getId()));
        validateGenres(film.getGenres());
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
            String query = "SELECT f.film_id, f.name, f.description, f.release_date, f.duration, f.rating_id, r.rating_name " +
                    "FROM films f " +
                    "JOIN ratings r ON f.rating_id = r.rating_id";
            List<Film> films = jdbcTemplate.query(query, new FilmRowMapper());

            for (Film film : films) {
                String likesQuery = "SELECT user_id FROM likes WHERE film_id = ?";
                List<Long> likes = jdbcTemplate.query(likesQuery, (rs, rowNum) -> rs.getLong("user_id"), film.getId());
                film.getLikes().addAll(likes);
            }

            return films;
        } catch (Exception e) {
            throw new RuntimeException("Error executing query: SELECT * FROM films", e);
        }
    }


    @Override
    public Optional<Film> getFilmById(long id) {
        try {
            String filmQuery = "SELECT f.film_id, f.name, f.description, f.release_date, f.duration, f.rating_id, r.rating_name " +
                    "FROM films f " +
                    "JOIN ratings r ON f.rating_id = r.rating_id " +
                    "WHERE f.film_id = ?";
            List<Film> films = jdbcTemplate.query(filmQuery, new FilmRowMapper(), id);

            if (films.isEmpty()) {
                return Optional.empty();
            }
            Film film = films.get(0);
            String genreQuery = "SELECT g.genre_id, g.genre_name FROM genre_line gl " +
                    "JOIN genres g ON gl.genre_id = g.genre_id " +
                    "WHERE gl.film_id = ?";
            List<Genre> genres = jdbcTemplate.query(genreQuery, new GenreRowMapper(), id);
            film.getGenres().addAll(genres);
            String likesQuery = "SELECT user_id FROM likes WHERE film_id = ?";
            List<Long> likes = jdbcTemplate.query(likesQuery, (rs, rowNum) -> rs.getLong("user_id"), id);
            film.getLikes().addAll(likes);

            return Optional.of(film);
        } catch (Exception e) {
            throw new RuntimeException("Error executing query: SELECT * FROM films WHERE film_id = ?", e);
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

    private void validateRatingId(Long ratingId) {
        String query = "SELECT COUNT(*) FROM ratings WHERE rating_id = ?";
        Integer count = jdbcTemplate.queryForObject(query, Integer.class, ratingId);
        if (count == null || count == 0) {
            throw new IllegalInitializationException("Invalid rating_id: " + ratingId);
        }
    }

    private void validateFilmId(Long filmId) {
        String query = "SELECT COUNT(*) FROM films WHERE film_id = ?";
        Integer count = jdbcTemplate.queryForObject(query, Integer.class, filmId);
        if (count == null || count == 0) {
            throw new IllegalArgumentException("Invalid film_id: " + filmId);
        }
    }

    private void validateGenres(Set<Genre> genres) {
        for (Genre genre : genres) {
            String query = "SELECT COUNT(*) FROM genres WHERE genre_id = ?";
            Integer count = jdbcTemplate.queryForObject(query, Integer.class, genre.getId());
            if (count == null || count == 0) {
                throw new IllegalInitializationException("Invalid genre_id: " + genre.getId());
            }
        }
    }

    private void insertFilmGenres(Long filmId, Set<Genre> genres) {
        String insertGenresQuery = "INSERT INTO genre_line (film_id, genre_id) VALUES (?, ?)";
        for (Genre genre : genres) {
            jdbcTemplate.update(insertGenresQuery, filmId, genre.getId());
        }
    }

    private static class FilmRowMapper implements RowMapper<Film> {
        @Override
        public Film mapRow(ResultSet rs, int rowNum) throws SQLException {
            Mpa mpa = new Mpa((int) rs.getLong("rating_id"), rs.getString("rating_name"));
            return new Film(rs.getLong("film_id"),
                    rs.getString("name"),
                    rs.getString("description"),
                    rs.getDate("release_date").toLocalDate(),
                    rs.getInt("duration"), mpa, new HashSet<>(), new HashSet<>());
        }
    }

    private static class GenreRowMapper implements RowMapper<Genre> {
        @Override
        public Genre mapRow(ResultSet rs, int rowNum) throws SQLException {
            return new Genre((int) rs.getLong("genre_id"), rs.getString("genre_name"));
        }
    }
}
