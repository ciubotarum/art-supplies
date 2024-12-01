package com.onlinestore.art_supplies.users;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class UserService {
    private final UserRepository userRepository;

    private static final Map<Long, Boolean> loggedInUsers = new HashMap<>();

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User register(User user) {
        if (userRepository.existsByUsername(user.getUsername())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "This username already exists");
        }
        if (user.getIsAdmin() == null) {
            user.setIsAdmin(false);
        }
        return userRepository.save(user);
    }

    public Optional<User> login(String username, String password) {
        User user = userRepository.getByUsername(username);
        if (user == null || !user.getPassword().equals(password)) {
            return Optional.empty();
        }
        loggedInUsers.put(user.getUserId(), true);
        return Optional.of(user);
    }

    public boolean isLoggedIn(Long userId) {
        return loggedInUsers.getOrDefault(userId, false);
    }

    public void checkAdminAndLoggedIn(Long adminId) {
        User adminUser = getUserById(adminId).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Admin user not found"));
        if (!Boolean.TRUE.equals(adminUser.getIsAdmin()) || !isLoggedIn(adminId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access denied: Only logged-in admins can perform this action.");
        }
    }

    public Optional<User> getUserById(Long userId) {
        return userRepository.findById(userId);
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }
}
