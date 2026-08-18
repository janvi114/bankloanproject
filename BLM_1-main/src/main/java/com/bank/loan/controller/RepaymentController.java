package com.bank.loan.controller;

import com.bank.loan.dto.PaymentRequest;
import com.bank.loan.dto.RepaymentResponse;
import com.bank.loan.service.RepaymentService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/repayments")
public class RepaymentController {
    
    @Autowired
    private RepaymentService repaymentService;
    
    @GetMapping("/schedule/{applicationId}")
    public ResponseEntity<List<RepaymentResponse>> getRepaymentSchedule(@PathVariable Long applicationId) {
        List<RepaymentResponse> responses = repaymentService.getRepaymentSchedule(applicationId);
        return ResponseEntity.ok(responses);
    }
    
    @PostMapping("/payment")
    public ResponseEntity<RepaymentResponse> makePayment(@Valid @RequestBody PaymentRequest request) {
        RepaymentResponse response = repaymentService.makePayment(request);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/outstanding/{applicationId}")
    public ResponseEntity<BigDecimal> getOutstandingBalance(@PathVariable Long applicationId) {
        BigDecimal outstandingBalance = repaymentService.getOutstandingBalance(applicationId);
        return ResponseEntity.ok(outstandingBalance);
    }
    
    @GetMapping("/pending/{applicationId}")
    public ResponseEntity<List<RepaymentResponse>> getPendingRepayments(@PathVariable Long applicationId) {
        List<RepaymentResponse> responses = repaymentService.getPendingRepayments(applicationId);
        return ResponseEntity.ok(responses);
    }
    
    @GetMapping("/completed/{applicationId}")
    public ResponseEntity<List<RepaymentResponse>> getCompletedRepayments(@PathVariable Long applicationId) {
        List<RepaymentResponse> responses = repaymentService.getCompletedRepayments(applicationId);
        return ResponseEntity.ok(responses);
    }
}
