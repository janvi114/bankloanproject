package com.bank.loan.controller;

import com.bank.loan.dto.LoanProductRequest;
import com.bank.loan.dto.LoanProductResponse;
import com.bank.loan.service.LoanProductService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/loan-products")
public class LoanProductController {
    
    @Autowired
    private LoanProductService loanProductService;
    
    @PostMapping
    public ResponseEntity<LoanProductResponse> addLoanProduct(@Valid @RequestBody LoanProductRequest request) {
        LoanProductResponse response = loanProductService.addLoanProduct(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }
    
    @PutMapping("/{productId}")
    public ResponseEntity<LoanProductResponse> updateLoanProduct(
            @PathVariable Long productId, 
            @Valid @RequestBody LoanProductRequest request) {
        LoanProductResponse response = loanProductService.updateLoanProduct(productId, request);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/{productId}")
    public ResponseEntity<LoanProductResponse> getLoanProductDetails(@PathVariable Long productId) {
        LoanProductResponse response = loanProductService.getLoanProductDetails(productId);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping
    public ResponseEntity<List<LoanProductResponse>> getAllLoanProducts() {
        List<LoanProductResponse> responses = loanProductService.getAllLoanProducts();
        return ResponseEntity.ok(responses);
    }
    
    @GetMapping("/eligible")
    public ResponseEntity<List<LoanProductResponse>> getEligibleProducts(@RequestParam BigDecimal loanAmount) {
        List<LoanProductResponse> responses = loanProductService.getEligibleProducts(loanAmount);
        return ResponseEntity.ok(responses);
    }
}
