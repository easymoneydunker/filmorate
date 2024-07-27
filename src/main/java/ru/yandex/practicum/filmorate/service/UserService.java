package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.Collection;

@Service
public class UserService {
    private final UserStorage userDbStorage;

    @Autowired
    public UserService(UserStorage userDbStorage) {
        this.userDbStorage = userDbStorage;
    }

    public User addUser(User user) {
        return userDbStorage.create(user);
    }

    public User updateUser(User user) {
        getUserById(user.getId());
        return userDbStorage.update(user);
    }

    public Collection<User> getAllUsers() {
        return userDbStorage.getAllUsers();
    }

    public User getUserById(long id) {
        return userDbStorage.getUserById(id);
    }

    public void addFriend(long userId, long friendId) {
        userDbStorage.addFriend(userId, friendId);
    }

    public void removeFriend(long userId, long friendId) {
        userDbStorage.removeFriend(userId, friendId);
    }

    public Collection<User> getUserFriends(long userId) {
        getUserById(userId);
        return userDbStorage.getUserFriendsByUserId(userId);
    }

    public Collection<User> getCommonFriends(long userId, long friendId) {
        return userDbStorage.getCommonFriends(userId, friendId);
    }
}
