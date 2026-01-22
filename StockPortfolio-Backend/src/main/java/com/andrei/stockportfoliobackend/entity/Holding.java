package com.andrei.stockportfoliobackend.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Entity representing a stock holding in a user's portfolio.
 * Maps to the "holding" table in the database.
 * Tracks how many shares of a specific stock a user owns and the average buy price.
 */
@Entity
@Table(name = "holding")
@Data
public class Holding {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "stock_ticker", nullable = false)
    private Stock stock;

    private Integer quantity;
    private BigDecimal buyPrice;
    private LocalDate purchaseDate;
}
