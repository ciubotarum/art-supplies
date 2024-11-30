package com.onlinestore.art_supplies.users;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

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
    @Operation(summary = "Register a new user",
            description = "Take personal details and register a new user",
            responses = {
                    @ApiResponse(responseCode = "201", description = "User registered"),
                    @ApiResponse(responseCode = "400", description = "Invalid input")
            })
    public ResponseEntity<User> register(@RequestBody @Valid User user) {
        return new ResponseEntity<>(userService.register(user), HttpStatus.CREATED);
    }

    @PostMapping("/login")
    @Operation(summary = "Login",
            description = "Login",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Login successful"),
                    @ApiResponse(responseCode = "401", description = "Invalid username or password")
            })
    public ResponseEntity<String> login(@RequestParam String username, @RequestParam String password) {
        if (username == null || username.isEmpty() || password == null || password.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid username or password.");
        }
        try {
            User user = userService.login(username, password);
            return ResponseEntity.ok("Login successful! Welcome, " + user.getFullName());
        } catch (ResponseStatusException e) {
            if (e.getStatusCode() == HttpStatus.UNAUTHORIZED) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid username or password.");
            }
            throw e;
        }
    }

    @GetMapping("/{userId}")
    @Operation(summary = "Get user by ID",
            description = "Get a user by its ID",
            responses = {
                    @ApiResponse(responseCode = "200", description = "User found"),
                    @ApiResponse(responseCode = "403", description = "Forbidden: Not an admin"),
                    @ApiResponse(responseCode = "404", description = "User not found")
            })
    public ResponseEntity<?> getAllUsers(@PathVariable Long userId, @RequestParam Long adminId) {
        User adminUser = userService.getUserById(adminId).orElse(null);
        if (adminUser != null && Boolean.TRUE.equals(adminUser.getIsAdmin())) {
            User user = userService.getUserById(userId).orElseThrow(
                    () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
            return ResponseEntity.ok(user);
        }  else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Access denied: Only admins can view users.");
        }
    }

    @GetMapping("/all")
    @Operation(summary = "Get all users",
            description = "Get all users",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Users found"),
                    @ApiResponse(responseCode = "403", description = "Forbidden: Not an admin")
            })
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
