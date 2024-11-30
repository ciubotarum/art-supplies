package com.onlinestore.art_supplies.users;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {
    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

//    @Test
//    void register_ShouldThrowException_WhenUsernameOrPasswordIsEmpty() {
//        User user = new User();
//        user.setUsername("");
//        user.setPassword("");
//
//        ResponseStatusException exception = assertThrows(
//                ResponseStatusException.class, () -> userService.register(user));
//        assertEquals("400 BAD_REQUEST \"Empty username or password\"", exception.getMessage());
//    }

    @Test
    void register_ShouldThrowException_WhenUsernameAlreadyExists() {
        User user = new User();
        user.setUsername("existingUsername");
        user.setPassword("password");

        when(userRepository.existsByUsername("existingUsername")).thenReturn(true);

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class, () -> userService.register(user));
        assertEquals("400 BAD_REQUEST \"This username already exists\"", exception.getMessage());
    }

    @Test
    void register_ShouldSaveUser_WhenValidUser() {
        User user = new User();
        user.setUsername("newUsername");
        user.setPassword("password");

        when(userRepository.existsByUsername(user.getUsername())).thenReturn(false);
        when(userRepository.save(user)).thenReturn(user);

        User savedUser = userService.register(user);
        assertNotNull(savedUser);
        assertEquals("newUsername", savedUser.getUsername());
        verify(userRepository, times(1)).save(user);
    }

    @Test
    void login_ShouldThrowException_WhenInvalidCredentials() {
        when(userRepository.getByUsername("username")).thenReturn(null);

        RuntimeException exception = assertThrows(
                RuntimeException.class, () -> userService.login("username", "password"));
        assertEquals("401 UNAUTHORIZED \"Invalid username or password\"", exception.getMessage());
    }

    @Test
    void login_ShouldReturnUser_WhenValidCredentials() {
        User user = new User();
        user.setUsername("username");
        user.setPassword("password");

        when(userRepository.getByUsername("username")).thenReturn(user);

        User loggedInUser = userService.login("username", "password");
        assertNotNull(loggedInUser);
        assertEquals("username", loggedInUser.getUsername());
    }

}