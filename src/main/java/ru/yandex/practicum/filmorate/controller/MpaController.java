package ru.yandex.practicum.filmorate.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.service.RatingService;

import java.util.Collection;

@RestController
@RequestMapping("/mpa")
public class MpaController {

    private final RatingService ratingService;

    @Autowired
    public MpaController(RatingService ratingService) {
        this.ratingService = ratingService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<Mpa> getMpaById(@PathVariable long id) {
        try {
            Mpa mpa = ratingService.getRatingById(id);
            return ResponseEntity.ok(mpa);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    @GetMapping
    public ResponseEntity<Collection<Mpa>> getAllMpas() {
        Collection<Mpa> mpas = ratingService.getAllRatings();
        return ResponseEntity.ok(mpas);
    }
}
