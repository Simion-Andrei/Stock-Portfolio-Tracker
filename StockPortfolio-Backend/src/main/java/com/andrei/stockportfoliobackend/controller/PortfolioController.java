package com.andrei.stockportfoliobackend.controller;

import com.andrei.stockportfoliobackend.dto.AddHoldingRequest;
import com.andrei.stockportfoliobackend.dto.PortfolioOverviewDTO;
import com.andrei.stockportfoliobackend.dto.SellStockRequest;
import com.andrei.stockportfoliobackend.dto.StockDTO;
import com.andrei.stockportfoliobackend.service.PortfolioService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST Controller for managing user portfolios.
 * Exposes endpoints to view portfolio, get stock list, buy, and sell assets.
 * All endpoints (except auth) are secured and require a valid JWT token.
 */
@RestController
@RequestMapping("/api/portfolio")
public class PortfolioController {

    private final PortfolioService portfolioService;

    public PortfolioController(PortfolioService portfolioService) {
        this.portfolioService = portfolioService;
    }

    /**
     * GET /api/portfolio
     * Retrieves the authenticated user's portfolio overview.
     *
     * @param authentication Auto-injected by Spring Security (contains the user Principal).
     * @return Portfolio stats and list of holdings.
     */
    @GetMapping
    public ResponseEntity<PortfolioOverviewDTO> getMyPortfolio(Authentication authentication) {
        String username = authentication.getName();
        PortfolioOverviewDTO portfolio = portfolioService.getUserPortfolio(username);
        return ResponseEntity.ok(portfolio);
    }

    /**
     * GET /api/portfolio/stocks
     * Retrieves a list of all stocks available in the system.
     * Used for the frontend dropdown/search.
     *
     * @return List of available stocks.
     */
    @GetMapping("/stocks")
    public ResponseEntity<List<StockDTO>> getAllStocks() {
        return ResponseEntity.ok(portfolioService.getAllAvailableStocks());
    }

    /**
     * POST /api/portfolio
     * Buys a stock (Adds a holding).
     * If the stock is already owned, it recalculates the average price.
     *
     * @param request JSON body containing ticker, quantity, and optional price.
     * @param authentication The current user.
     */
    @PostMapping
    public ResponseEntity<String> addHolding(@RequestBody AddHoldingRequest request, Authentication authentication) {
        portfolioService.addHolding(authentication.getName(), request);
        return ResponseEntity.ok("Transaction successful!");
    }

    /**
     * POST /api/portfolio/sell
     * Sells a specific quantity of a stock.
     *
     * @param request JSON body containing ticker and quantity.
     * @param authentication The current user.
     */
    @PostMapping("/sell")
    public ResponseEntity<String> sellStock(@RequestBody SellStockRequest request, Authentication authentication) {
        portfolioService.sellStock(authentication.getName(), request.getTicker(), request.getQuantity());
        return ResponseEntity.ok("Sale recorded successfully!");
    }
}