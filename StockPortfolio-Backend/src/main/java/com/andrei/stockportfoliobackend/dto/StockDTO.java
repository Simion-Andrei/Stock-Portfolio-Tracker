package com.andrei.stockportfoliobackend.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class StockDTO {
    private String ticker;
    private String name;
    private BigDecimal currentPrice;
    private Double dayChangePercent;
}