package com.bank.loan.controller;

import com.bank.loan.dto.CustomerRequest;
import com.bank.loan.dto.CustomerResponse;
import com.bank.loan.model.Customer;
import com.bank.loan.service.CustomerService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/customers")
public class CustomerController {
    
    @Autowired
    private CustomerService customerService;
    
    @PostMapping
    public ResponseEntity<CustomerResponse> registerCustomer(@Valid @RequestBody CustomerRequest request) {
        CustomerResponse response = customerService.registerCustomer(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }
    
    @GetMapping("/{customerId}")
    public ResponseEntity<CustomerResponse> getCustomerDetails(@PathVariable Long customerId) {
        CustomerResponse response = customerService.getCustomerDetails(customerId);
        return ResponseEntity.ok(response);
    }
    
    @PutMapping("/{customerId}")
    public ResponseEntity<CustomerResponse> updateCustomerProfile(
            @PathVariable Long customerId, 
            @Valid @RequestBody CustomerRequest request) {
        CustomerResponse response = customerService.updateCustomerProfile(customerId, request);
        return ResponseEntity.ok(response);
    }
    
    @PutMapping("/{customerId}/kyc-status")
    public ResponseEntity<CustomerResponse> updateKycStatus(
            @PathVariable Long customerId, 
            @RequestParam Customer.KycStatus kycStatus) {
        CustomerResponse response = customerService.updateKycStatus(customerId, kycStatus);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping
    public ResponseEntity<List<CustomerResponse>> getAllCustomers() {
        List<CustomerResponse> responses = customerService.getAllCustomers();
        return ResponseEntity.ok(responses);
    }
    
    @GetMapping("/kyc-status/{kycStatus}")
    public ResponseEntity<List<CustomerResponse>> getCustomersByKycStatus(
            @PathVariable Customer.KycStatus kycStatus) {
        List<CustomerResponse> responses = customerService.getCustomersByKycStatus(kycStatus);
        return ResponseEntity.ok(responses);
    }
}
