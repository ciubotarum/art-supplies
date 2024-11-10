package com.onlinestore.art_supplies.users;

import ch.qos.logback.core.util.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {
    private final UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User register(User user) {
        if (StringUtil.isNullOrEmpty(user.getUsername()) || StringUtil.isNullOrEmpty(user.getPassword())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Empty username or password");
        }
        if (userRepository.existsByUsername(user.getUsername())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "This username already exists");
        }
        if (user.getIsAdmin() == null) {
            user.setIsAdmin(false);
        }
        return userRepository.save(user);
    }

    public User login(String username, String password) {
        User user = userRepository.getByUsername(username);
        if (user == null || !password.matches(user.getPassword())) {
            throw new RuntimeException("Invalid username or password");
        }
        return user;
    }

    public Optional<User> getUserById(Long userId) {
        return userRepository.findById(userId);
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }
}
