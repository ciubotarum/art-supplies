package com.onlinestore.art_supplies.config.security;

import com.onlinestore.art_supplies.users.User;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;

@Service
public class JwtUtils {
    private static final String SECRET_KEY = "your-very-secret-key-must-be-at-least-32-characters";
    private static final long EXPIRATION_TIME = 1000 * 60 * 60; // 1 hour
    private static SecretKey key = Keys.hmacShaKeyFor(SECRET_KEY.getBytes());

    public String generateToken(User user) {
        return Jwts.builder()
                .setSubject(user.getUsername())
                .claim("isAdmin", user.getIsAdmin())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(key)
                .compact();
    }

    public <T> T getClaim(String token, String claim, Class<T> requiredType) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .get(claim, requiredType);
    }

    public boolean validateToken(String token, UserDetails userDetails) {
        String username = getClaim(token, "sub", String.class);
        return username.equals(userDetails.getUsername()) && !isTokenExpired(token);
    }

    private boolean isTokenExpired(String token) {
        Date expiration = getClaim(token, "exp", Date.class);
        return expiration.before(new Date());
    }
}
