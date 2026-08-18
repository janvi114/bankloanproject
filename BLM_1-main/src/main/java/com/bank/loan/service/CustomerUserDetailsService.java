package com.bank.loan.service;

import com.bank.loan.model.Customer;
import com.bank.loan.repository.CustomerRepository;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;

@Service
public class CustomerUserDetailsService implements UserDetailsService {
	
	@Autowired
    private CustomerRepository customerRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Customer customer = customerRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Customer not found"));
        
        if (customer.getPassword() == null) {
            throw new UsernameNotFoundException("Customer account not activated");
        }
        
        return User.builder()
                .username(customer.getEmail())
                .password(customer.getPassword())
                .roles("CUSTOMER")
                .build();
    }
}
