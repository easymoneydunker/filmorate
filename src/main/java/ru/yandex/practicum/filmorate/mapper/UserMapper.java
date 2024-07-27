package ru.yandex.practicum.filmorate.mapper;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class UserMapper {
    public static User mapRowToUser(ResultSet rs, int rowNum) throws SQLException {
        return new User(rs.getLong("user_id"), rs.getString("name"), rs.getString("email"), rs.getString("login"), rs.getDate("birthday").toLocalDate());
    }
}
