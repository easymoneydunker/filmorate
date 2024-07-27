package ru.yandex.practicum.filmorate.storage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.mapper.UserMapper;
import ru.yandex.practicum.filmorate.model.User;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;

@Repository
@Qualifier("UserDbStorage")
public class UserDbStorage implements UserStorage {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public User create(User user) {
        String query = "INSERT INTO users (name, email, login, birthday) VALUES (?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, user.getName());
            ps.setString(2, user.getEmail());
            ps.setString(3, user.getLogin());
            ps.setDate(4, java.sql.Date.valueOf(user.getBirthday()));
            return ps;
        }, keyHolder);

        Number generatedId = keyHolder.getKey();
        if (generatedId != null) {
            user.setId(generatedId.longValue());
        } else {
            throw new RuntimeException("Failed to retrieve generated user ID.");
        }

        return user;
    }

    public User update(User user) {
        String query = "UPDATE users SET name = ?, email = ?, login = ?, birthday = ? WHERE user_id = ?";
        int rowsAffected = jdbcTemplate.update(query, user.getName(), user.getEmail(), user.getLogin(), user.getBirthday(), user.getId());

        if (rowsAffected == 0) {
            throw new NotFoundException("No user with id = " + user.getId());
        }
        return user;
    }

    public Collection<User> getAllUsers() {
        String query = "SELECT * FROM users";
        return jdbcTemplate.query(query, UserMapper::mapRowToUser);
    }

    public Optional<User> getUserById(long id) {
        String query = "SELECT * FROM users WHERE user_id = ?";
        try {
            return Optional.ofNullable(jdbcTemplate.queryForObject(query, UserMapper::mapRowToUser, id));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Transactional
    public Map<String, Long> addFriend(long user1Id, long user2Id) {
        if (user1Id == user2Id) {
            throw new IllegalArgumentException("A user cannot add themselves as a friend.");
        }
        validateUsersExist(user1Id, user2Id);

        Long existingFriendshipId = getFriendshipId(user1Id, user2Id);
        if (existingFriendshipId != null) {
            throw new RuntimeException("Friendship already exists.");
        }

        String insertFriendshipQuery = "INSERT INTO friendships (status) VALUES (?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(insertFriendshipQuery, Statement.RETURN_GENERATED_KEYS);
            ps.setInt(1, 0);
            return ps;
        }, keyHolder);
        Number friendshipId = keyHolder.getKey();
        if (friendshipId == null) {
            throw new RuntimeException("Failed to retrieve generated friendship ID.");
        }

        String insertFriendshipLineQuery = "INSERT INTO friendship_line (user1_id, user2_id, friendship_id) VALUES (?, ?, ?)";
        jdbcTemplate.update(insertFriendshipLineQuery, user1Id, user2Id, friendshipId);

        return Map.of("user1Id", user1Id, "user2Id", user2Id, "friendshipId", friendshipId.longValue());
    }

    @Transactional
    public Map<String, Long> removeFriend(long user1Id, long user2Id) {
        validateUsersExist(user1Id, user2Id);

        Long friendshipId = getFriendshipId(user1Id, user2Id);
        if (friendshipId == null) {
            return Map.of("user1Id", user1Id, "user2Id", user2Id);
        }

        String deleteFriendshipLineQuery = "DELETE FROM friendship_line WHERE (user1_id = ? AND user2_id = ?)";
        jdbcTemplate.update(deleteFriendshipLineQuery, user1Id, user2Id);

        String deleteFriendshipQuery = "DELETE FROM friendships WHERE friendship_id = ? AND NOT EXISTS (SELECT 1 FROM friendship_line WHERE friendship_id = ?)";
        jdbcTemplate.update(deleteFriendshipQuery, friendshipId, friendshipId);

        return Map.of("user1Id", user1Id, "user2Id", user2Id);
    }

    public Collection<User> getUserFriendsByUserId(long userId) {
        String query = "SELECT u.* FROM users u JOIN friendship_line fl ON u.user_id = fl.user2_id WHERE fl.user1_id = ?";
        return jdbcTemplate.query(query, UserMapper::mapRowToUser, userId);
    }

    public Collection<User> getCommonFriends(long user1Id, long user2Id) {
        String query = "SELECT u.* FROM users u " + "JOIN friendship_line fl1 ON u.user_id = fl1.user2_id " + "JOIN friendship_line fl2 ON u.user_id = fl2.user2_id " + "WHERE fl1.user1_id = ? AND fl2.user1_id = ?";
        return jdbcTemplate.query(query, UserMapper::mapRowToUser, user1Id, user2Id);
    }

    private Long getFriendshipId(long user1Id, long user2Id) {
        String query = "SELECT friendship_id FROM friendship_line WHERE (user1_id = ? AND user2_id = ?) OR (user1_id = ? AND user2_id = ?)";
        try {
            return jdbcTemplate.queryForObject(query, Long.class, user1Id, user2Id, user2Id, user1Id);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    private void validateUsersExist(long user1Id, long user2Id) {
        String userCheckQuery = "SELECT COUNT(*) FROM users WHERE user_id = ? OR user_id = ?";
        Integer userCount = jdbcTemplate.queryForObject(userCheckQuery, Integer.class, user1Id, user2Id);
        if (userCount == null || userCount < 2) {
            throw new NotFoundException("One or both users do not exist.");
        }
    }
}
