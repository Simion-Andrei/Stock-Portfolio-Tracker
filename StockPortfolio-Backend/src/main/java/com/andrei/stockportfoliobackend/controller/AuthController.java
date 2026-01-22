package com.andrei.stockportfoliobackend.controller;

import com.andrei.stockportfoliobackend.dto.LoginRequest;
import com.andrei.stockportfoliobackend.entity.User;
import com.andrei.stockportfoliobackend.repository.UserRepository;
import com.andrei.stockportfoliobackend.security.JwtUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * REST Controller responsible for handling public authentication endpoints.
 * Allows new users to register and existing users to login and receive a JWT token.
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;

    public AuthController(AuthenticationManager authenticationManager, UserRepository userRepository,
                          PasswordEncoder passwordEncoder, JwtUtils jwtUtils) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtils = jwtUtils;
    }

    /**
     * Endpoint for user registration.
     * Checks if username exists, encodes the password, and saves the new user.
     *
     * @param signUpRequest The DTO containing username and raw password.
     * @return A success message or an error if username is taken.
     */
    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody LoginRequest signUpRequest) {
        if (userRepository.findByUsername(signUpRequest.getUsername()).isPresent()) {
            return ResponseEntity.badRequest().body("Error: Username is already taken!");
        }

        User user = new User();
        user.setUsername(signUpRequest.getUsername());
        user.setPasswordHash(passwordEncoder.encode(signUpRequest.getPassword()));

        userRepository.save(user);

        return ResponseEntity.ok("User registered successfully!");
    }

    /**
     * Endpoint for user login.
     * Authenticates credentials using Spring Security and generates a JWT token.
     *
     * @param loginRequest The DTO containing username and password.
     * @return A JSON object containing the JWT token.
     */
    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@RequestBody LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

        String jwt = jwtUtils.generateToken(loginRequest.getUsername());

        return ResponseEntity.ok(Map.of("token", jwt));
    }
}