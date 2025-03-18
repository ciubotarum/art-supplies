package com.onlinestore.art_supplies.dto;

import lombok.Data;

@Data
public class LoginRequest {
    private String username;
    private String password;
}
