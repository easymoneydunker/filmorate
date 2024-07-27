package ru.yandex.practicum.filmorate.storage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.IllegalInitializationException;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

@Repository
public class GenreRepository {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public GenreRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Genre getById(long id) {
        String query = "SELECT * FROM genres WHERE genre_id = ?";
        try {
            return jdbcTemplate.queryForObject(query, (rs, rowNum) -> {
                Genre genre = new Genre();
                genre.setId((int) rs.getLong("genre_id"));
                genre.setName(rs.getString("genre_name"));
                return genre;
            }, id);
        } catch (DataAccessException e) {
            throw new IllegalInitializationException("Genre with id=" + id + " not found");
        }
    }

    public Set<Genre> getMultipleGenresByIds(String ids) {
        String query = "SELECT * FROM genres WHERE genre_id IN (" + ids + ")";
        return new HashSet<>(jdbcTemplate.query(query, (rs, rowNum) -> {
            Genre genre = new Genre();
            genre.setId((int) rs.getLong("genre_id"));
            genre.setName(rs.getString("genre_name"));
            return genre;
        }));
    }


    public Collection<Genre> getAll() {
        String query = "SELECT * FROM genres";
        return jdbcTemplate.query(query, (rs, rowNum) -> {
            Genre genre = new Genre();
            genre.setId((int) rs.getLong("genre_id"));
            genre.setName(rs.getString("genre_name"));
            return genre;
        });
    }
}
