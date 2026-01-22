package com.andrei.stockportfoliobackend.dto;

import lombok.Data;
import java.math.BigDecimal;

/**
 * DTO used when a user wants to buy a new stock.
 * Transfers the ticker, quantity, and optional custom price from client to server.
 */
@Data
public class AddHoldingRequest {
    private String ticker;
    private Integer quantity;
    private BigDecimal buyPrice;
}