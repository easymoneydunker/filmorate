package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import ru.yandex.practicum.filmorate.validator.FilmReleaseDate;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
@EqualsAndHashCode(of = {"name", "description"})
@NoArgsConstructor
@AllArgsConstructor
public class Film {
    private Long id;
    @NotBlank
    private String name;
    @Size(min = 10, max = 100)
    private String description;
    @FilmReleaseDate
    private LocalDate releaseDate;
    @Positive
    @Min(1)
    private Integer duration;
    @NotNull
    private Mpa mpa;
    private Set<Genre> genres = new HashSet<>();

    public void addGenre(Genre genre) {
        genres.add(genre);
    }

    public void removeGenre(Genre genre) {
        genres.remove(genre);
    }
}
