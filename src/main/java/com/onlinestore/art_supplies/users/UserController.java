package com.onlinestore.art_supplies.users;

import com.onlinestore.art_supplies.dto.LoginRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "User Controller", description = "Operations related to users")
@RestController
@RequestMapping("/users")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    @Operation(summary = "Register a new user",
            description = "Registers a new user by providing personal details including username, password, fullName," +
                    " email, and phone number.",
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
            responses = {
                    @ApiResponse(responseCode = "200", description = "Login successful"),
                    @ApiResponse(responseCode = "401", description = "Invalid username or password")
            })
    public ResponseEntity<String> login(@RequestBody LoginRequest loginRequest) {
        String token = userService.verify(loginRequest);
        if (!"fails".equals(token)) {
            return ResponseEntity.ok(token);
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid username or password");
    }
}
