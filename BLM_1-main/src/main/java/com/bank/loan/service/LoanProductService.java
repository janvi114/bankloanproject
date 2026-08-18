package com.bank.loan.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bank.loan.dto.LoanProductRequest;
import com.bank.loan.dto.LoanProductResponse;
import com.bank.loan.exception.LoanProductNotFoundException;
import com.bank.loan.model.LoanProduct;
import com.bank.loan.repository.LoanProductRepository;
@Service
@Transactional
public class LoanProductService {
    
	@Autowired
    private LoanProductRepository loanProductRepository;
    
    public LoanProductResponse addLoanProduct(LoanProductRequest request) {
        // Validate that max amount is greater than min amount
        if (request.getMaxAmount().compareTo(request.getMinAmount()) <= 0) {
            throw new IllegalArgumentException("Maximum amount must be greater than minimum amount");
        }
        
        LoanProduct product = new LoanProduct();
        product.setProductName(request.getProductName());
        product.setInterestRate(request.getInterestRate());
        product.setMinAmount(request.getMinAmount());
        product.setMaxAmount(request.getMaxAmount());
        product.setTenure(request.getTenure());
        
        LoanProduct savedProduct = loanProductRepository.save(product);
        return LoanProductResponse.fromEntity(savedProduct);
    }
    
    public LoanProductResponse updateLoanProduct(Long productId, LoanProductRequest request) {
        LoanProduct product = loanProductRepository.findById(productId)
                .orElseThrow(() -> new LoanProductNotFoundException("Loan product not found with ID: " + productId));
        
        // Validate that max amount is greater than min amount
        if (request.getMaxAmount().compareTo(request.getMinAmount()) <= 0) {
            throw new IllegalArgumentException("Maximum amount must be greater than minimum amount");
        }
        
        product.setProductName(request.getProductName());
        product.setInterestRate(request.getInterestRate());
        product.setMinAmount(request.getMinAmount());
        product.setMaxAmount(request.getMaxAmount());
        product.setTenure(request.getTenure());
        
        LoanProduct updatedProduct = loanProductRepository.save(product);
        return LoanProductResponse.fromEntity(updatedProduct);
    }
    
    @Transactional(readOnly = true)
    public LoanProductResponse getLoanProductDetails(Long productId) {
        LoanProduct product = loanProductRepository.findById(productId)
                .orElseThrow(() -> new LoanProductNotFoundException("Loan product not found with ID: " + productId));
        return LoanProductResponse.fromEntity(product);
    }
    
    @Transactional(readOnly = true)
    public List<LoanProductResponse> getAllLoanProducts() {
        return loanProductRepository.findAll().stream()
                .map(LoanProductResponse::fromEntity)
                .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public List<LoanProductResponse> getEligibleProducts(BigDecimal loanAmount) {
        return loanProductRepository.findEligibleProducts(loanAmount).stream()
                .map(LoanProductResponse::fromEntity)
                .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public LoanProduct getLoanProductEntity(Long productId) {
        return loanProductRepository.findById(productId)
                .orElseThrow(() -> new LoanProductNotFoundException("Loan product not found with ID: " + productId));
    }
}
