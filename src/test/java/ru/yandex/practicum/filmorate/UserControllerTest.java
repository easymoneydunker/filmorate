package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.controller.UserController;
import ru.yandex.practicum.filmorate.exception.DuplicatedException;
import ru.yandex.practicum.filmorate.exception.IllegalInitializationException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;

public class UserControllerTest {

    private UserController userController;

    @BeforeEach
    public void setUp() {
        userController = new UserController();
    }

    @Test
    public void testGetUsers() {
        Collection<User> users = userController.getUsers();
        assertNotNull(users);
        assertTrue(users.isEmpty());
    }

    @Test
    public void testCreateValidUser() {
        User user = new User();
        user.setEmail("test@example.com");
        user.setLogin("testuser");
        user.setBirthday(LocalDate.of(2000, 8, 20));

        User createdUser = userController.create(user);
        assertNotNull(createdUser);
        assertEquals(user.getEmail(), createdUser.getEmail());
        assertEquals(user.getLogin(), createdUser.getLogin());
    }

    @Test
    public void testCreateUserWithFutureBirthday() {
        User user = new User();
        user.setEmail("future@example.com");
        user.setLogin("futureuser");
        user.setBirthday(LocalDate.now().plusDays(1));

        assertThrows(IllegalInitializationException.class, () -> {
            userController.create(user);
        });
    }

    @Test
    public void testCreateDuplicateUser() {
        User user1 = new User();
        user1.setEmail("duplicate@example.com");
        user1.setLogin("duplicateuser");
        user1.setBirthday(LocalDate.of(2000, 8, 20));
        userController.create(user1);

        User user2 = new User();
        user2.setEmail("duplicate@example.com");
        user2.setLogin("duplicateuser2");
        user2.setBirthday(LocalDate.of(2000, 8, 20));

        assertThrows(DuplicatedException.class, () -> {
            userController.create(user2);
        });
    }

    @Test
    public void testUpdateValidUser() {
        User user = new User();
        user.setEmail("testupdate@example.com");
        user.setLogin("testupdateuser");
        user.setBirthday(LocalDate.of(2000, 8, 20));
        User createdUser = userController.create(user);

        User updatedUser = new User();
        updatedUser.setId(createdUser.getId());
        updatedUser.setEmail("updated@example.com");
        updatedUser.setLogin("updateduser");

        User resultUser = userController.update(updatedUser);
        assertNotNull(resultUser);
        assertEquals(updatedUser.getEmail(), resultUser.getEmail());
        assertEquals(updatedUser.getLogin(), resultUser.getLogin());
    }

    @Test
    public void testUpdateUserWithNullId() {
        User user = new User();
        user.setEmail("nullid@example.com");
        user.setLogin("nulliduser");

        assertThrows(IllegalInitializationException.class, () -> {
            userController.update(user);
        });
    }

    @Test
    public void testUpdateNonExistentUser() {
        User user = new User();
        user.setId(999L);
        user.setEmail("nonexistent@example.com");
        user.setLogin("nonexistentuser");

        assertThrows(NotFoundException.class, () -> {
            userController.update(user);
        });
    }

    @Test
    public void testUpdateUserWithDuplicateEmail() {
        User user1 = new User();
        user1.setEmail("dupemail@example.com");
        user1.setLogin("dupemailuser");
        user1.setBirthday(LocalDate.of(2000, 8, 20));
        userController.create(user1);

        User user2 = new User();
        user2.setEmail("uniqueemail@example.com");
        user2.setLogin("uniqueuser");
        user2.setBirthday(LocalDate.of(2000, 8, 20));
        User createdUser2 = userController.create(user2);

        User updatedUser2 = new User();
        updatedUser2.setId(createdUser2.getId());
        updatedUser2.setEmail("dupemail@example.com");
        updatedUser2.setLogin("uniqueuser");

        assertThrows(DuplicatedException.class, () -> {
            userController.update(updatedUser2);
        });
    }
}
