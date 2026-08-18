package com.bank.loan.repository;

import com.bank.loan.model.LoanProduct;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface LoanProductRepository extends JpaRepository<LoanProduct, Long> {
    
    List<LoanProduct> findByProductNameContaining(String productName);
    
    @Query("SELECT lp FROM LoanProduct lp WHERE lp.minAmount <= :amount AND lp.maxAmount >= :amount")
    List<LoanProduct> findEligibleProducts(BigDecimal amount);
    
    @Query("SELECT lp FROM LoanProduct lp WHERE lp.interestRate <= :maxRate")
    List<LoanProduct> findByInterestRateLessThanEqual(BigDecimal maxRate);
}
