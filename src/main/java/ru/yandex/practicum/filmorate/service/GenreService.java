package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.GenreRepository;

import java.util.Collection;
import java.util.Set;

@Service
public class GenreService {
    private final GenreRepository genreRepository;

    @Autowired
    public GenreService(GenreRepository genreRepository) {
        this.genreRepository = genreRepository;
    }

    public Genre getGenreById(long id) {
        return genreRepository.getById(id);
    }

    public Collection<Genre> getAllGenres() {
        return genreRepository.getAll();
    }

    public Set<Genre> getMultipleGenres(String ids) {
        return genreRepository.getMultipleGenresByIds(ids);
    }
}
