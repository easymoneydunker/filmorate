package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.DuplicatedException;
import ru.yandex.practicum.filmorate.exception.IllegalInitializationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.validator.UserValidator;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@RestController
@RequestMapping("/users")
public class UserController {
    private final Map<Long, User> users = new HashMap<>();
    private final UserValidator userValidator = new UserValidator();

    @GetMapping
    public Collection<User> getUsers() {
        return users.values();
    }

    @PostMapping
    public User create(@Valid @RequestBody User newUser) {
        if (Objects.nonNull(newUser.getBirthday()) && newUser.getBirthday().isAfter(LocalDate.now())) {
            throw new IllegalInitializationException("Illegal birthday date");
        }
        if (users.values().stream().anyMatch(user -> user.getEmail().equals(newUser.getEmail()) || user.getLogin().equals(newUser.getLogin()))) {
            throw new DuplicatedException("This email and/or username have already been registered");
        }
        if (newUser.getName() == null) {
            newUser.setName(newUser.getLogin());
        }
        newUser.setId(getNextId());
        users.put(newUser.getId(), newUser);
        return newUser;
    }

    @PutMapping
    public User update(@RequestBody User newUser) {
        userValidator.validate(newUser, users);
        User oldUser = users.get(newUser.getId());
        if (Objects.isNull(newUser.getName())) {
            newUser.setName(oldUser.getName());
        }
        if (Objects.isNull(newUser.getLogin())) {
            newUser.setLogin(oldUser.getLogin());
        }
        if (Objects.isNull(newUser.getBirthday())) {
            newUser.setBirthday(oldUser.getBirthday());
        }
        if (Objects.isNull(newUser.getEmail())) {
            newUser.setEmail(oldUser.getEmail());
        }
        users.put(newUser.getId(), newUser);
        return newUser;
    }

    private long getNextId() {
        long currentMaxId = users.keySet().stream().mapToLong(id -> id).max().orElse(0);
        return ++currentMaxId;
    }
}
