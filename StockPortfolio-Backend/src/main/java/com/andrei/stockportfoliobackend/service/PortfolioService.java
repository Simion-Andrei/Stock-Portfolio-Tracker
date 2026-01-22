package com.andrei.stockportfoliobackend.service;

import com.andrei.stockportfoliobackend.dto.AddHoldingRequest;
import com.andrei.stockportfoliobackend.dto.HoldingDTO;
import com.andrei.stockportfoliobackend.dto.PortfolioOverviewDTO;
import com.andrei.stockportfoliobackend.dto.StockDTO;
import com.andrei.stockportfoliobackend.entity.Holding;
import com.andrei.stockportfoliobackend.entity.Stock;
import com.andrei.stockportfoliobackend.entity.User;
import com.andrei.stockportfoliobackend.repository.HoldingRepository;
import com.andrei.stockportfoliobackend.repository.StockRepository;
import com.andrei.stockportfoliobackend.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Service class responsible for managing user portfolios.
 * Handles the business logic for buying (adding) and selling stocks,
 * as well as calculating performance metrics like Total Value, Profit/Loss, and ROI.
 */
@Service
public class PortfolioService {

    private final HoldingRepository holdingRepository;
    private final StockRepository stockRepository;
    private final UserRepository userRepository;

    public PortfolioService(HoldingRepository holdingRepository, StockRepository stockRepository, UserRepository userRepository) {
        this.holdingRepository = holdingRepository;
        this.stockRepository = stockRepository;
        this.userRepository = userRepository;
    }

    /**
     * Retrieves all available stocks from the database.
     * Used to populate the dropdown menu in the frontend.
     *
     * @return List of StockDTOs containing ticker, name, and current price.
     */
    public List<StockDTO> getAllAvailableStocks() {
        return stockRepository.findAll().stream().map(stock -> {
            StockDTO dto = new StockDTO();
            dto.setTicker(stock.getTicker());
            dto.setName(stock.getName());
            dto.setCurrentPrice(stock.getCurrentPrice());
            dto.setDayChangePercent(stock.getDayChangePercent());
            return dto;
        }).collect(Collectors.toList());
    }

    /**
     * Calculates the full portfolio overview for a specific user.
     * Iterates through all holdings to calculate individual and total performance.
     *
     * @param username The username of the authenticated user.
     * @return PortfolioOverviewDTO containing total stats and a list of holdings.
     * @throws RuntimeException if the user is not found.
     */
    public PortfolioOverviewDTO getUserPortfolio(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<Holding> holdings = holdingRepository.findByUserId(user.getId());

        BigDecimal totalValue = BigDecimal.ZERO;
        BigDecimal totalInvestment = BigDecimal.ZERO;
        List<HoldingDTO> holdingDTOs = new ArrayList<>();

        for (Holding h : holdings) {
            HoldingDTO dto = new HoldingDTO();
            Stock stock = h.getStock();

            dto.setId(h.getId());
            dto.setTicker(stock.getTicker());
            dto.setCompanyName(stock.getName());
            dto.setQuantity(h.getQuantity());
            dto.setBuyPrice(h.getBuyPrice());
            dto.setCurrentPrice(stock.getCurrentPrice());

            // Value = Current Price * Quantity
            BigDecimal currentValue = stock.getCurrentPrice().multiply(BigDecimal.valueOf(h.getQuantity()));

            // Cost = Average Buy Price * Quantity
            BigDecimal investmentCost = h.getBuyPrice().multiply(BigDecimal.valueOf(h.getQuantity()));

            // Profit = Value - Cost
            BigDecimal profit = currentValue.subtract(investmentCost);

            dto.setTotalValue(currentValue);
            dto.setProfitLoss(profit);

            // ROI % Calculation
            if (investmentCost.compareTo(BigDecimal.ZERO) > 0) {
                double percent = profit.divide(investmentCost, 4, RoundingMode.HALF_UP).doubleValue() * 100;
                dto.setProfitLossPercent(percent);
            } else {
                dto.setProfitLossPercent(0.0);
            }

            holdingDTOs.add(dto);

            // Add to totals
            totalValue = totalValue.add(currentValue);
            totalInvestment = totalInvestment.add(investmentCost);
        }

        PortfolioOverviewDTO overview = new PortfolioOverviewDTO();
        overview.setHoldings(holdingDTOs);
        overview.setTotalPortfolioValue(totalValue);

        BigDecimal totalProfit = totalValue.subtract(totalInvestment);
        overview.setTotalProfitLoss(totalProfit);

        if (totalInvestment.compareTo(BigDecimal.ZERO) > 0) {
            double totalPercent = totalProfit.divide(totalInvestment, 4, RoundingMode.HALF_UP).doubleValue() * 100;
            overview.setTotalReturnPercent(totalPercent);
        } else {
            overview.setTotalReturnPercent(0.0);
        }

        return overview;
    }

    /**
     * Adds a stock to the user's portfolio.
     * Implements the **Weighted Average Cost** basis method.
     * <p>
     * Logic:
     * 1. If the user already owns the stock, the new buy price is averaged with the existing position.
     * Formula: ((OldQty * OldPrice) + (NewQty * NewPrice)) / TotalQty
     * 2. If the user does not own the stock, a new holding is created.
     * </p>
     *
     * @param username The username of the buyer.
     * @param request  DTO containing ticker, quantity, and optional buy price.
     */
    public void addHolding(String username, AddHoldingRequest request) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Stock stock = stockRepository.findById(request.getTicker())
                .orElseThrow(() -> new RuntimeException("Stock not found"));

        Optional<Holding> existingHoldingOpt = holdingRepository.findByUserUsernameAndStockTicker(username, request.getTicker());

        if (existingHoldingOpt.isPresent()) {
            // CASE 1: UPDATE EXISTING (Weighted Average)
            Holding existing = existingHoldingOpt.get();

            BigDecimal pricePerShare = request.getBuyPrice();
            if (pricePerShare == null) {
                pricePerShare = stock.getCurrentPrice();
            }

            BigDecimal currentTotalValue = existing.getBuyPrice().multiply(BigDecimal.valueOf(existing.getQuantity()));
            BigDecimal newPurchaseValue = pricePerShare.multiply(BigDecimal.valueOf(request.getQuantity()));

            int newTotalQuantity = existing.getQuantity() + request.getQuantity();

            BigDecimal totalValue = currentTotalValue.add(newPurchaseValue);
            BigDecimal newAveragePrice = totalValue.divide(BigDecimal.valueOf(newTotalQuantity), 4, RoundingMode.HALF_UP);

            existing.setQuantity(newTotalQuantity);
            existing.setBuyPrice(newAveragePrice);
            existing.setPurchaseDate(java.time.LocalDate.now());

            holdingRepository.save(existing);

        } else {
            // CASE 2: NEW HOLDING
            Holding holding = new Holding();
            holding.setUser(user);
            holding.setStock(stock);
            holding.setQuantity(request.getQuantity());

            if (request.getBuyPrice() != null) {
                holding.setBuyPrice(request.getBuyPrice());
            } else {
                holding.setBuyPrice(stock.getCurrentPrice());
            }

            holding.setPurchaseDate(java.time.LocalDate.now());
            holdingRepository.save(holding);
        }
    }

    /**
     * Sells a specified quantity of a stock.
     * Reduces the quantity held. If the remaining quantity reaches zero, the holding is removed.
     * <br>
     * <b>Note:</b> Selling does NOT change the Average Buy Price of the remaining shares.
     *
     * @param username       The username of the seller.
     * @param ticker         The stock symbol to sell.
     * @param quantityToSell The amount of shares to sell.
     * @throws RuntimeException if the user doesn't own the stock or tries to sell more than they own.
     */
    public void sellStock(String username, String ticker, Integer quantityToSell) {
        Holding holding = holdingRepository.findByUserUsernameAndStockTicker(username, ticker)
                .orElseThrow(() -> new RuntimeException("You do not own this stock: " + ticker));

        if (holding.getQuantity() < quantityToSell) {
            throw new RuntimeException("Insufficient shares! You have " + holding.getQuantity());
        }

        int remainingQuantity = holding.getQuantity() - quantityToSell;

        if (remainingQuantity == 0) {
            holdingRepository.delete(holding);
        } else {
            holding.setQuantity(remainingQuantity);
            // Average Buy Price remains unchanged on sell
            holdingRepository.save(holding);
        }
    }
}