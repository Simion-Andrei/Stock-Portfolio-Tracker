package com.andrei.stockportfoliobackend;

import com.andrei.stockportfoliobackend.entity.Holding;
import com.andrei.stockportfoliobackend.entity.Stock;
import com.andrei.stockportfoliobackend.entity.User;
import com.andrei.stockportfoliobackend.repository.HoldingRepository;
import com.andrei.stockportfoliobackend.repository.StockRepository;
import com.andrei.stockportfoliobackend.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Component that runs automatically on application startup.
 * Checks if the database is empty and populates it with initial seed data (Stocks, Test User, Holdings).
 * Useful for development and testing environments.
 */
@Component
public class DataInitializer implements CommandLineRunner {
    private final StockRepository stockRepository;
    private final UserRepository userRepository;
    private final HoldingRepository holdingRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(StockRepository stockRepository,
                           UserRepository userRepository,
                           HoldingRepository holdingRepository,
                           PasswordEncoder passwordEncoder) {
        this.stockRepository = stockRepository;
        this.userRepository = userRepository;
        this.holdingRepository = holdingRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) throws Exception {
        if (stockRepository.count() == 0) {
            Stock aapl = createStock("AAPL", "Apple Inc.", 185.50, 1.25);
            Stock tsla = createStock("TSLA", "Tesla, Inc.", 240.00, -2.50);
            createStock("GOOGL", "Alphabet Inc.", 140.20, 0.50);

            User user = new User();
            user.setUsername("student");
            user.setPasswordHash(passwordEncoder.encode("password123"));
            userRepository.save(user);

            createHolding(user, aapl, 10, 150.00);
            createHolding(user, tsla, 5, 260.00);

            System.out.println("--- TEST DATA INITIALIZED SUCCESSFULLY ---");
        }
    }

    private Stock createStock(String ticker, String name, double price, double dailyChange) {
        Stock stock = new Stock();
        stock.setTicker(ticker);
        stock.setName(name);
        stock.setCurrentPrice(BigDecimal.valueOf(price));
        stock.setDayChangePercent(dailyChange);
        return stockRepository.save(stock);
    }

    private void createHolding(User user, Stock stock, int quantity, double buyPrice) {
        Holding holding = new Holding();
        holding.setUser(user);
        holding.setStock(stock);
        holding.setQuantity(quantity);
        holding.setBuyPrice(BigDecimal.valueOf(buyPrice));
        holding.setPurchaseDate(LocalDate.now().minusMonths(2));
        holdingRepository.save(holding);
    }
}