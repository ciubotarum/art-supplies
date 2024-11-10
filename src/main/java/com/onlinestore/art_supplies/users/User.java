package com.onlinestore.art_supplies.users;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Component
@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long userId;

    private String username;
    private String password;

    @Column(name = "full_name")
    private String fullName;

    private String email;
    private String phone;

    private Boolean isAdmin = false;

    public User(String username, String password) {
        this.username = username;
        this.password = password;
    }
}
