package com.bank.loan.config;

import com.bank.loan.model.Admin;
import com.bank.loan.model.Customer;
import com.bank.loan.repository.AdminRepository;
import com.bank.loan.repository.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UnifiedUserDetailsService implements UserDetailsService {

    @Autowired
    private AdminRepository adminRepository;
    
    @Autowired
    private CustomerRepository customerRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // Try admin first
        Optional<Admin> admin = adminRepository.findByUsername(username);
        if (admin.isPresent()) {
            return User.builder()
                    .username(admin.get().getUsername())
                    .password(admin.get().getPassword())
                    .roles("ADMIN")
                    .build();
        }
        
        // Try customer
        Optional<Customer> customer = customerRepository.findByEmail(username);
        if (customer.isPresent()) {
            Customer c = customer.get();
            if (c.getPassword() != null && !c.getPassword().isEmpty()) {
                return User.builder()
                        .username(c.getEmail())
                        .password(c.getPassword())
                        .roles("CUSTOMER")
                        .build();
            }
        }
        
        System.out.println("Login failed for: " + username);
        throw new UsernameNotFoundException("User not found: " + username);
    }
}
