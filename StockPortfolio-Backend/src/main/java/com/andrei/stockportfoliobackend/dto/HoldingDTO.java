package com.andrei.stockportfoliobackend.dto;

import lombok.Data;
import java.math.BigDecimal;

/**
 * Data Transfer Object for displaying a holding in the frontend.
 * Enriches the raw database entity with calculated fields like
 * total current value, profit/loss, and percentage return.
 */
@Data
public class HoldingDTO {
    private Long id;
    private String ticker;
    private String companyName;
    private Integer quantity;
    private BigDecimal buyPrice;
    private BigDecimal currentPrice;

    private BigDecimal totalValue;
    private BigDecimal profitLoss;
    private Double profitLossPercent;
}