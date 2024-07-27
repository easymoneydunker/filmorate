package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.RatingRepository;

import java.util.Collection;

@Service
public class RatingService {
    @Autowired
    private final RatingRepository ratingRepository;

    public RatingService(RatingRepository ratingRepository) {
        this.ratingRepository = ratingRepository;
    }

    public Mpa getRatingById(long id) {
        return ratingRepository.getById(id);
    }

    public Collection<Mpa> getAllRatings() {
        return ratingRepository.getAll();
    }
}
