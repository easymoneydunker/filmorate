package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.DuplicatedException;
import ru.yandex.practicum.filmorate.exception.IllegalInitializationException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/users")
public class UserController {
    private final Map<Long, User> users = new HashMap<>();

    @GetMapping
    public Collection<User> getUsers() {
        return users.values();
    }

    @PostMapping
    public User create(@Valid @RequestBody User newUser) {
        if (newUser.getBirthday() != null && newUser.getBirthday().isAfter(LocalDate.now())) {
            throw new IllegalInitializationException("Illegal birthday date");
        }
        if (users.values().stream().anyMatch(user ->
            user.getEmail().equals(newUser.getEmail()) || user.getLogin().equals(newUser.getLogin())
        )) {
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
        if (newUser.getId() == null) {
            throw new IllegalInitializationException("User must have an id");
        }
        if (!users.containsKey(newUser.getId())) {
            throw new NotFoundException("User not found");
        }
        if (users.values().stream().anyMatch(user -> user.getEmail()
                .equals(newUser.getEmail()) && !user.equals(newUser))) {
            throw new DuplicatedException("This email is already in use");
        }
        User oldUser = users.get(newUser.getId());
        if (newUser.getName() == null) {
            newUser.setName(oldUser.getName());
        }
        if (newUser.getLogin() == null) {
            newUser.setLogin(oldUser.getLogin());
        }
        if (newUser.getBirthday() == null) {
            newUser.setBirthday(oldUser.getBirthday());
        }
        if (newUser.getEmail() == null) {
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
