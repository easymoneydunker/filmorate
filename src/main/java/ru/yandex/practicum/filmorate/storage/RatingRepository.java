package ru.yandex.practicum.filmorate.storage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.IllegalInitializationException;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.Collection;


@Repository
public class RatingRepository {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public RatingRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Mpa getById(long id) {
        String query = "SELECT * FROM ratings WHERE rating_id = ?";
        try {
            return jdbcTemplate.queryForObject(query, (rs, rowNum) -> {
                Mpa rating = new Mpa();
                rating.setId((int) rs.getLong("rating_id"));
                rating.setName(rs.getString("rating_name"));
                return rating;
            }, id);
        } catch (DataAccessException e) {
            throw new IllegalInitializationException("Rating with id=" + id + " not found");
        }
    }

    public Collection<Mpa> getAll() {
        String query = "SELECT * FROM ratings";
        return jdbcTemplate.query(query, (rs, rowNum) -> {
            Mpa rating = new Mpa();
            rating.setId((int) rs.getLong("rating_id"));
            rating.setName(rs.getString("rating_name"));
            return rating;
        });
    }
}