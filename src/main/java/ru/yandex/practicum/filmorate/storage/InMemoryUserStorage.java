package ru.yandex.practicum.filmorate.storage;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.DuplicatedException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.validator.UserValidator;

import java.util.*;


public class InMemoryUserStorage implements UserStorage {
    private final Map<Long, User> users = new HashMap<>();
    private final UserValidator userValidator = new UserValidator();

    @Override
    public User create(User user) {
        if (users.values().stream().anyMatch(user1 -> user1.getEmail().equals(user.getEmail()) || user1.getLogin().equals(user.getLogin()))) {
            throw new DuplicatedException("This email and/or username have already been registered");
        }
        if (user.getName() == null) {
            user.setName(user.getLogin());
        }
        user.setId(getNextId());
        users.put(user.getId(), user);
        userValidator.validate(user, users);
        return user;
    }

    @Override
    public User update(User user) {
        User oldUser = users.get(user.getId());
        if (Objects.isNull(user.getName())) {
            user.setName(oldUser.getName());
        }
        if (Objects.isNull(user.getLogin())) {
            user.setLogin(oldUser.getLogin());
        }
        if (Objects.isNull(user.getBirthday())) {
            user.setBirthday(oldUser.getBirthday());
        }
        if (Objects.isNull(user.getEmail())) {
            user.setEmail(oldUser.getEmail());
        }
        userValidator.validate(user, users);
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public Collection<User> getAllUsers() {
        return users.values();
    }

    @Override
    public Optional<User> getUserById(long id) {
        return Optional.ofNullable(users.get(id));
    }

    private long getNextId() {
        long currentMaxId = users.keySet().stream().mapToLong(id -> id).max().orElse(0);
        return ++currentMaxId;
    }
}
