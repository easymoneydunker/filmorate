package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.Map;

public interface UserStorage {
    User create(User user);

    User update(User user);

    Collection<User> getAllUsers();

    User getUserById(long id);

    Map<String, Long> addFriend(long user1Id, long user2Id);

    Map<String, Long> removeFriend(long user1Id, long user2Id);

    Collection<User> getUserFriendsByUserId(long userId);

    Collection<User> getCommonFriends(long user1Id, long user2Id);

}
