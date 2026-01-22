package com.andrei.stockportfoliobackend.dto;

import lombok.Data;

/**
 * Simple DTO for capturing user credentials during login and registration.
 */
@Data
public class LoginRequest {
    private String username;
    private String password;
}