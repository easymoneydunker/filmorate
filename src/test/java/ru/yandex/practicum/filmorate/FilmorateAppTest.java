package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserDbStorage;
import ru.yandex.practicum.filmorate.storage.UserStorageConfig;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest
@AutoConfigureTestDatabase
@Import(UserStorageConfig.class)
public class FilmorateAppTest {

    @Autowired
    private UserDbStorage userStorage;

    @Test
    public void testFindUserById() {
        User user = new User();
        user.setName("user");
        user.setEmail("useremail@gmail.com");
        user.setLogin("userlogin");
        user.setBirthday(LocalDate.now().minusYears(20));

        User createdUser = userStorage.create(user);
        long userId = createdUser.getId();
        User retrievedUser = userStorage.getUserById(userId);

        assertThat(retrievedUser).hasFieldOrPropertyWithValue("id", 1L);
    }
}
