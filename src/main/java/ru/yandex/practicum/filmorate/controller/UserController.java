package ru.yandex.practicum.filmorate.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import java.util.Collection;
import java.util.Map;

@RestController
@RequestMapping("/users")
public class UserController {
    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public Collection<User> getAllUsers() {
        return userService.getAllUsers();
    }

    @GetMapping("/{userId}")
    public User getUserById(@PathVariable long id) {
        return userService.getUserById(id);
    }

    @GetMapping("/{userId}/friends")
    public Collection<User> getFriends(@PathVariable long userId) {
        return userService.getUserFriendsByUserId(userId);
    }

    @GetMapping("{userId}/friends/common/{otherId}")
    public Collection<User> getCommonFriends(@PathVariable long userId, @PathVariable long otherId) {
        return userService.getCommonFriends(userId, otherId);
    }

    @PostMapping
    public User create(@RequestBody User newUser) {
        return userService.create(newUser);
    }

    @PutMapping
    public User update(@RequestBody User newUser) {
        return userService.update(newUser);
    }

    @PutMapping("/{userId}/friends/{friendId}")
    public Map<String, Long> addFriend(@PathVariable long userId, @PathVariable long friendId) {
        return userService.addFriend(userId, friendId);
    }

    @DeleteMapping("/{userId}/friends/{friendId}")
    public Map<String, Long> removeFriend(@PathVariable long userId, @PathVariable long friendId) {
        return userService.removeFriend(userId, friendId);
    }
}
