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
import java.util.Optional;

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
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Put your username, password, fullName, email and phone"
            ),
            responses = {
                    @ApiResponse(responseCode = "201", description = "User registered"),
                    @ApiResponse(responseCode = "400", description = "Invalid input")
            })
    public ResponseEntity<User> register(@RequestBody @Valid User user) {
        return new ResponseEntity<>(userService.register(user), HttpStatus.CREATED);
    }

    @PostMapping("/login")
    @Operation(summary = "Login",
            description = "Login with your username and password",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Login successful"),
                    @ApiResponse(responseCode = "401", description = "Invalid username or password")
            })
    public ResponseEntity<String> login(@RequestParam String username, @RequestParam String password) {
        Optional<User> user = userService.login(username, password);

        if (user.isPresent()) {
            return ResponseEntity.ok("Login successful! Welcome, " + user.get().getFullName());
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid username or password");
        }
    }

    @GetMapping("/{userId}")
    @Operation(summary = "Get user by ID",
            description = "Get a user by its ID",
            responses = {
                    @ApiResponse(responseCode = "200", description = "User found"),
                    @ApiResponse(responseCode = "403", description = "Forbidden: Not an logged-in admin"),
                    @ApiResponse(responseCode = "404", description = "User not found or admin user not found")
            })
    public ResponseEntity<?> getUserById(@PathVariable Long userId, @RequestParam Long adminId) {
        userService.checkAdminAndLoggedIn(adminId);
        User user = userService.getUserById(userId).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
        return ResponseEntity.ok(user);
    }

    @GetMapping("/all")
    @Operation(summary = "Get all users",
            description = "Get all users",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Users found"),
                    @ApiResponse(responseCode = "403", description = "Forbidden: Not an logged-in admin")
            })
    public ResponseEntity<?> getAllUsers(@RequestParam Long adminId) {
        userService.checkAdminAndLoggedIn(adminId);
        List<User> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }
}
