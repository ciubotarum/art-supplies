package com.onlinestore.art_supplies.users;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {
    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    private User user;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setUsername("username");
        user.setPassword("password");
        user.setPhone("1234567890");
        user.setFullName("User FullName");
        user.setUserId(1L);
    }

    @Test
    void register_ShouldSaveUser_WhenValidUser() {

        when(userRepository.existsByUsername("username")).thenReturn(false);

        when(userRepository.save(any(User.class))).thenReturn(user);

        User savedUser = userService.register(user);
        assertNotNull(savedUser);
        assertEquals("username", savedUser.getUsername());
        verify(userRepository, times(1)).save(user);
    }


    @Test
    void register_ShouldThrowException_WhenUsernameAlreadyExists() {
        user.setUsername("existingUsername");

        when(userRepository.existsByUsername("existingUsername")).thenReturn(true);

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class, () -> userService.register(user));
        assertEquals("400 BAD_REQUEST \"This username already exists\"", exception.getMessage());
    }

    @Test
    void login_ShouldReturnUser_WhenValidCredentials() {

        when(userRepository.getByUsername("username")).thenReturn(user);

        Optional<User> loggedInUser = userService.login("username", "password");
        assertNotNull(loggedInUser);
        assertEquals("username", loggedInUser.get().getUsername());
    }

    @Test
    void login_ShouldReturnEmpty_WhenUsernameIsNull() {
        Optional<User> result = userService.login(null, "password");
        assertTrue(result.isEmpty());
    }

    @Test
    void login_ShouldReturnEmpty_WhenPasswordDoesNotMatch() {

        when(userRepository.getByUsername("username")).thenReturn(user);

        Optional<User> result = userService.login("username", "wrongPassword");
        assertTrue(result.isEmpty());
    }

    @Test
    void isLoggedIn_ShouldReturnTrue_WhenUserIsLoggedIn() {

        when(userRepository.getByUsername("username")).thenReturn(user);

        userService.login("username", "password");
        assertTrue(userService.isLoggedIn(1L));
    }

    @Test
    void isLoggedIn_ShouldReturnFalse_WhenUserIsNotLoggedIn() {
        assertFalse(userService.isLoggedIn(1L));
    }


    @Test
    void getUserById_ShouldReturnUser_WhenUserExists() {

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        Optional<User> result = userService.getUserById(1L);
        assertTrue(result.isPresent());
        assertEquals(1L, result.get().getUserId());
    }

    @Test
    void getUserById_ShouldReturnEmpty_WhenUserDoesNotExist() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        Optional<User> result = userService.getUserById(1L);
        assertTrue(result.isEmpty());
    }
}