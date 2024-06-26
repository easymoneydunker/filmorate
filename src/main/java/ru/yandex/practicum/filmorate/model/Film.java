package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
@EqualsAndHashCode(of = {"name", "description"})
@NoArgsConstructor
@AllArgsConstructor
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
    private final Set<Long> likes = new HashSet<>();

    public void addLikedUserId(long userId) {
        likes.add(userId);
    }

    public void removeLikedUserId(long userId) {
        likes.remove(userId);
    }
}
