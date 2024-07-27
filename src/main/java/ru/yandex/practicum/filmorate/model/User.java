package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@EqualsAndHashCode(of = {"email", "login"})
@NoArgsConstructor
@AllArgsConstructor
public class User {
    private Long id;
    @Email(message = "Email should be valid")
    @NotEmpty(message = "Email cannot be null")
    private String email;
    @Pattern(regexp = "^[a-zA-Z0-9]{6,12}$", message = "Login should be valid")
    private String login;
    private String name;
    @PastOrPresent(message = "Birthday must be in the past or present")
    @NotNull
    private LocalDate birthday;
}
