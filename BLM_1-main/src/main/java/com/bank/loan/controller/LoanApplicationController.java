package com.bank.loan.controller;

import com.bank.loan.dto.LoanApplicationRequest;
import com.bank.loan.dto.LoanApplicationResponse;
import com.bank.loan.model.LoanApplication;
import com.bank.loan.service.LoanApplicationService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/loan-applications")
public class LoanApplicationController {
    
    @Autowired
    private LoanApplicationService loanApplicationService;
    
    @PostMapping
    public ResponseEntity<LoanApplicationResponse> applyForLoan(@Valid @RequestBody LoanApplicationRequest request) {
        LoanApplicationResponse response = loanApplicationService.applyForLoan(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }
    
    @GetMapping("/{applicationId}")
    public ResponseEntity<LoanApplicationResponse> getApplicationStatus(@PathVariable Long applicationId) {
        LoanApplicationResponse response = loanApplicationService.getApplicationStatus(applicationId);
        return ResponseEntity.ok(response);
    }
    
    @PutMapping("/{applicationId}/process")
    public ResponseEntity<LoanApplicationResponse> processLoanApplication(
            @PathVariable Long applicationId, 
            @RequestParam LoanApplication.ApprovalStatus status) {
        LoanApplicationResponse response = loanApplicationService.processLoanApplication(applicationId, status);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/customer/{customerId}")
    public ResponseEntity<List<LoanApplicationResponse>> getApplicationsByCustomer(@PathVariable Long customerId) {
        List<LoanApplicationResponse> responses = loanApplicationService.getApplicationsByCustomer(customerId);
        return ResponseEntity.ok(responses);
    }
    
    @GetMapping("/status/{status}")
    public ResponseEntity<List<LoanApplicationResponse>> getApplicationsByStatus(
            @PathVariable LoanApplication.ApprovalStatus status) {
        List<LoanApplicationResponse> responses = loanApplicationService.getApplicationsByStatus(status);
        return ResponseEntity.ok(responses);
    }
}
