package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class UserService {
    private final UserStorage userStorage;

    @Autowired
    public UserService(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public User create(User user) {
        return userStorage.create(user);
    }

    public User update(User user) {
        return userStorage.update(user);
    }

    public Collection<User> getAllUsers() {
        return userStorage.getAllUsers();
    }

    public User getUserById(long id) {
        return userStorage.getUserById(id)
                .orElseThrow(() -> new NotFoundException("No user with id = " + id));
    }

    public Collection<User> getUserFriendsByUserId(long id) {
        User user = getUserById(id);

        Collection<Long> friendIds = user.getFriends();
        Collection<User> friends = new HashSet<>();

        for (Long friendId : friendIds) {
            friends.add(getUserById(friendId));
        }

        return friends;
    }

    public Map<String, Long> addFriend(long user1Id, long user2Id) {
        if (user1Id == user2Id) {
            throw new IllegalArgumentException("A user cannot add themselves as a friend.");
        }

        User user1 = userStorage.getUserById(user1Id)
                .orElseThrow(() -> new NotFoundException("No user with id = " + user1Id));
        User user2 = userStorage.getUserById(user2Id)
                .orElseThrow(() -> new NotFoundException("No user with id = " + user2Id));

        if (!user1.getFriends().contains(user2Id)) {
            user1.addFriend(user2Id);
            userStorage.update(user1);
        }

        if (!user2.getFriends().contains(user1Id)) {
            user2.addFriend(user1Id);
            userStorage.update(user2);
        }

        return Map.of("id", user1Id, "friend_id", user2Id);
    }

    public Map<String, Long> removeFriend(long user1Id, long user2Id) {
        User user1 = userStorage.getUserById(user1Id)
                .orElseThrow(() -> new NotFoundException("No user with id = " + user1Id));
        User user2 = userStorage.getUserById(user2Id)
                .orElseThrow(() -> new NotFoundException("No user with id = " + user2Id));

        user1.removeFriend(user2Id);
        user2.removeFriend(user1Id);

        return Map.of("id", user1Id, "friend_id", user2Id);
    }

    public Collection<User> getCommonFriends(long user1Id, long user2Id) {
        User user1 = userStorage.getUserById(user1Id)
                .orElseThrow(() -> new NotFoundException("No user with id = " + user1Id));
        User user2 = userStorage.getUserById(user2Id)
                .orElseThrow(() -> new NotFoundException("No user with id = " + user2Id));

        Set<Long> user1FriendIds = new HashSet<>(user1.getFriends());
        Set<Long> user2FriendIds = new HashSet<>(user2.getFriends());

        user1FriendIds.retainAll(user2FriendIds);

        return user1FriendIds.stream().map(id -> userStorage.getUserById(id)
                .orElseThrow(() -> new NotFoundException("No user with id = " + id))).collect(Collectors.toSet());
    }
}
