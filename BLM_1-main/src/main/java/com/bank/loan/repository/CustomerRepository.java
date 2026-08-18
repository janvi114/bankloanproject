package com.bank.loan.repository;

import com.bank.loan.model.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {
    
    Optional<Customer> findByEmail(String email);
    
    List<Customer> findByKycStatus(Customer.KycStatus kycStatus);
    
    @Query("SELECT c FROM Customer c WHERE c.name LIKE %:name%")
    List<Customer> findByNameContaining(String name);
    
    boolean existsByEmail(String email);
}
