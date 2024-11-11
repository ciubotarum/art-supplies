package com.onlinestore.art_supplies.users;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {
    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public ResponseEntity<User> register(@RequestBody User user) {
        return new ResponseEntity<>(userService.register(user), HttpStatus.CREATED);
    }
    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestParam String username, @RequestParam String password) {
        User user = userService.login(username, password);
        if (user != null) {
            return ResponseEntity.ok("Login successful! Welcome, " + user.getFullName());
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid username or password.");
        }
    }

    @GetMapping("/{userId}")
    public ResponseEntity<?> getAllUsers(@PathVariable Long userId, @RequestParam Long adminId) {
        User adminUser = userService.getUserById(adminId).orElse(null);
        if (adminUser != null && Boolean.TRUE.equals(adminUser.getIsAdmin())) {
            User user = userService.getUserById(userId).orElse(null);
            return ResponseEntity.ok(user);
        }  else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Access denied: Only admins can view all users.");
        }
    }

    @GetMapping("/all")
    public ResponseEntity<?> getAllUsers(@RequestParam Long adminId) {
        User adminUser = userService.getUserById(adminId).orElse(null);

        if (adminUser != null && Boolean.TRUE.equals(adminUser.getIsAdmin())) {
            List<User> users = userService.getAllUsers();
            return ResponseEntity.ok(users);
        }  else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Access denied: Only admins can view all users.");
        }
    }
}
