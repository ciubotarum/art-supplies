package com.onlinestore.art_supplies.users;

import com.onlinestore.art_supplies.config.security.CustomAuthenticationFilter;
import com.onlinestore.art_supplies.config.security.JwtUtils;
import com.onlinestore.art_supplies.dto.LoginRequest;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authManager;
    private final JwtUtils jwtUtils;
    private final CustomAuthenticationFilter customAuthenticationFilter;

    public User register(User user) {
        if (userRepository.existsByUsername(user.getUsername())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "This username already exists");
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        if (user.getIsAdmin() == null) {
            user.setIsAdmin(false);
        }
        return userRepository.save(user);
    }

    public String verify(LoginRequest loginRequest) {
        try {
            Authentication authentication = authManager.authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

            if (authentication.isAuthenticated()) {
                User user = userRepository.findByUsername(loginRequest.getUsername())
                        .orElseThrow(() -> new UsernameNotFoundException("User not found"));
                return jwtUtils.generateToken(user);
            }
        } catch (BadCredentialsException e) {
            System.out.println("Authentication failed: " + e.getMessage());
        }

        return "fails";
    }

    public User getAuthenticatedUser(HttpServletRequest request) {
        String token = customAuthenticationFilter.extractTokenFromCookie(request);
        if (token == null || token.isBlank()) {
            return null;
        }
        String username = jwtUtils.getClaim(token, "sub", String.class);
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }
}
