package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.RatingRepository;

import java.util.Collection;

@Service
@RequiredArgsConstructor
public class RatingService {
    @Autowired
    private final RatingRepository ratingRepository;


    public Mpa getRatingById(long id) {
        return ratingRepository.getById(id);
    }

    public Collection<Mpa> getAllRatings() {
        return ratingRepository.getAll();
    }
}
