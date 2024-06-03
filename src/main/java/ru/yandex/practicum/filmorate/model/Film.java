package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;

@Data
@EqualsAndHashCode(of = {"name", "description"})
public class Film {
    private Long id;
    @NotNull
    @NotBlank
    private String name;
    @NotNull
    @Size(min = 0, max = 100)
    private String description;
    private LocalDate releaseDate;
    private Integer duration;
}