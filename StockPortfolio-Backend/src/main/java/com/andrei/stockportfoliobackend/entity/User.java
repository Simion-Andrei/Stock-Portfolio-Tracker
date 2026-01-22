package com.andrei.stockportfoliobackend.entity;

import jakarta.persistence.*;
import lombok.Data;

/**
 * Entity representing a registered user of the application.
 * Maps to the "users" table.
 * Stores authentication details (username and hashed password).
 */
@Entity
@Table(name = "users")
@Data
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String username;

    private String passwordHash;
}
