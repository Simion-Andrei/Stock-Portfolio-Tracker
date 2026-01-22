package com.andrei.stockportfoliobackend.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.util.List;

/**
 * Comprehensive DTO containing the entire portfolio summary.
 * Includes aggregate statistics (Total Value, Total Return) and the list of individual holdings.
 */
@Data
public class PortfolioOverviewDTO {
    private BigDecimal totalPortfolioValue;
    private BigDecimal totalProfitLoss;
    private Double totalReturnPercent;

    private List<HoldingDTO> holdings;
}