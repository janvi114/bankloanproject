package com.bank.loan.controller;

import com.bank.loan.service.ReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/reports")
public class ReportController {
    
    @Autowired
    private ReportService reportService;
    
    @GetMapping("/loans")
    public ResponseEntity<Map<String, Object>> generateLoanReport() {
        Map<String, Object> report = reportService.generateLoanReport();
        return ResponseEntity.ok(report);
    }
    
    @GetMapping("/repayments")
    public ResponseEntity<Map<String, Object>> generateRepaymentReport() {
        Map<String, Object> report = reportService.generateRepaymentReport();
        return ResponseEntity.ok(report);
    }
    
    @GetMapping("/outstanding")
    public ResponseEntity<Map<String, Object>> generateOutstandingLoansReport() {
        Map<String, Object> report = reportService.generateOutstandingLoansReport();
        return ResponseEntity.ok(report);
    }
    
    @GetMapping("/customer-portfolio/{customerId}")
    public ResponseEntity<Map<String, Object>> generateCustomerPortfolioReport(@PathVariable Long customerId) {
        Map<String, Object> report = reportService.generateCustomerPortfolioReport(customerId);
        return ResponseEntity.ok(report);
    }
}
