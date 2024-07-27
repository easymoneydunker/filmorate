package ru.yandex.practicum.filmorate.storage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.IllegalInitializationException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.Collection;

@Repository
public class GenreRepository{

    @Autowired
    private JdbcTemplate jdbcTemplate;


    public Genre getById(long id) {
        String query = "SELECT * FROM genres WHERE genre_id = ?";
        try {
            return jdbcTemplate.queryForObject(query, (rs, rowNum) -> {
                Genre genre = new Genre();
                genre.setId((int) rs.getLong("genre_id"));
                genre.setName(rs.getString("genre_name"));
                return genre;
            }, id);
        } catch (RuntimeException e) {
            //кидаю IllegalInitializationException, тк постман хочет код 400,
            //а NotFound у меня 404
            throw new IllegalInitializationException("Genre with id=" + id + " not found");
        }
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
