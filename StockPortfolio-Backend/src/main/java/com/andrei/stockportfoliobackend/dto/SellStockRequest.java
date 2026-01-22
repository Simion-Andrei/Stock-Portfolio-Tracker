package com.andrei.stockportfoliobackend.dto;

import lombok.Data;

/**
 * DTO used when a user wants to sell shares.
 * Specifies which stock and how many units to sell.
 */
@Data
public class SellStockRequest {
    private String ticker;
    private Integer quantity;
}