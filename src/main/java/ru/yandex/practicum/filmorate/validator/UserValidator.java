package ru.yandex.practicum.filmorate.validator;

import ru.yandex.practicum.filmorate.exception.DuplicatedException;
import ru.yandex.practicum.filmorate.exception.IllegalInitializationException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.Map;

public class UserValidator {
    public void validate(User user, Map<Long, User> users) {
        if (user.getId() == null) {
            throw new IllegalInitializationException("User must have an id");
        }
        if (user.getLogin().contains(" ")) {
            throw new IllegalInitializationException("Invalid login");
        }
        if (user.getBirthday().isAfter(LocalDate.now().plusDays(1))) {
            throw new IllegalInitializationException("Invalid birthday");
        }
        if (!users.containsKey(user.getId())) {
            throw new NotFoundException("User not found");
        }
        if (users.values().stream().anyMatch(user1 -> user1.getEmail().equals(user.getEmail()) && !user1.equals(user))) {
            throw new DuplicatedException("This email is already in use");
        }
    }
}
