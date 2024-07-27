package ru.yandex.practicum.filmorate.storage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

@Configuration
public class UserStorageConfig {
    @Bean
    @Autowired
    public UserDbStorage userDbStorage(JdbcTemplate jdbcTemplate) {
        return new UserDbStorage(jdbcTemplate);
    }
}

