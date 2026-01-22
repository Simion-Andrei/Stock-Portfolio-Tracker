package com.andrei.stockportfoliobackend.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;

/**
 * Entity representing a publicly traded company/stock.
 * Maps to the "stocks" table in the database.
 * Acts as a catalog of available assets to buy.
 */
@Entity
@Table(name = "stocks")
@Data
public class Stock {
    @Id
    private String ticker;

    private String name;
    private BigDecimal currentPrice;
    private Double dayChangePercent;
}