package com.andrei.stockportfoliobackend.repository;

import com.andrei.stockportfoliobackend.entity.Holding;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface HoldingRepository extends JpaRepository<Holding, Long> {
    List<Holding> findByUserId(Long userId);
    Optional<Holding> findByUserUsernameAndStockTicker(String username, String ticker);
}
