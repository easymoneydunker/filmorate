package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
@EqualsAndHashCode(of = {"email", "login"})
@NoArgsConstructor
@AllArgsConstructor
public class User {
    private Long id;
    @Email(message = "Email should be valid")
    @NotNull(message = "Email cannot be null")
    private String email;
    @NotNull(message = "Login cannot be null")
    @NotBlank(message = "Login cannot be blank")
    private String login;
    private String name;
    @PastOrPresent(message = "Birthday must be in the past or present")
    private LocalDate birthday;

    private final Set<Long> friends = new HashSet<>();
    private final Set<Long> likedFilms = new HashSet<>();

    public void addFriend(long friendId) {
        friends.add(friendId);
    }

    public void removeFriend(long friendId) {
        friends.remove(friendId);
    }

    public void addLikedFilm(long filmId) {
        likedFilms.add(filmId);
    }

    public void removeLikedFilm(long filmId) {
        likedFilms.remove(filmId);
    }
}
