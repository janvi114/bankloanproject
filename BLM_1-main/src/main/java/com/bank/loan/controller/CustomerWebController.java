package com.bank.loan.controller;

import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.bank.loan.dto.CustomerRequest;
import com.bank.loan.dto.CustomerResponse;
import com.bank.loan.dto.LoanApplicationRequest;
import com.bank.loan.model.Customer;
import com.bank.loan.service.CustomerService;
import com.bank.loan.service.LoanApplicationService;
import com.bank.loan.service.LoanProductService;
import com.bank.loan.service.RepaymentService;

import org.springframework.beans.factory.annotation.Autowired;
import java.math.BigDecimal;

@Controller
@RequestMapping("/customer")
public class CustomerWebController {

    @Autowired
    private CustomerService customerService;
    
    @Autowired
    private LoanProductService loanProductService;
    
    @Autowired
    private LoanApplicationService loanApplicationService;
    
    @Autowired
    private RepaymentService repaymentService;
    
    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostMapping("/register")
    public String register(@ModelAttribute CustomerRequest request) {
        try {
            request.setPassword(passwordEncoder.encode(request.getPassword()));
            customerService.registerCustomer(request);
            return "redirect:/login";
        } catch (Exception e) {
            return "redirect:/customer-register?error";
        }
    }

    @GetMapping("/dashboard")
    public String dashboard(Authentication auth, Model model) {
        String email = auth.getName();
        CustomerResponse customerResponse = customerService.getAllCustomers().stream()
            .filter(c -> c.getEmail().equals(email))
            .findFirst()
            .orElseThrow(() -> new RuntimeException("Customer not found"));
        
        Customer customer = customerService.getCustomerEntity(customerResponse.getCustomerId());
        model.addAttribute("customer", customer);
        model.addAttribute("applications", loanApplicationService.getApplicationsByCustomer(customer.getCustomerId()));
        return "customer-dashboard";
    }

    @GetMapping("/profile")
    public String viewProfile(Authentication auth, Model model) {
        String email = auth.getName();
        CustomerResponse customerResponse = customerService.getAllCustomers().stream()
            .filter(c -> c.getEmail().equals(email))
            .findFirst()
            .orElseThrow(() -> new RuntimeException("Customer not found"));

        Customer customer = customerService.getCustomerEntity(customerResponse.getCustomerId());
        model.addAttribute("customer", customer);
        return "customer-view-profile";
    }

    @GetMapping("/apply-loan")
    public String applyLoanPage(Authentication auth, Model model) {
        String email = auth.getName();
        CustomerResponse customerResponse = customerService.getAllCustomers().stream()
            .filter(c -> c.getEmail().equals(email))
            .findFirst()
            .orElseThrow(() -> new RuntimeException("Customer not found"));
        
        Customer customer = customerService.getCustomerEntity(customerResponse.getCustomerId());
        model.addAttribute("customer", customer);
        model.addAttribute("products", loanProductService.getAllLoanProducts());
        model.addAttribute("application", new LoanApplicationRequest());
        return "customer-apply-loan";
    }

    @PostMapping("/apply-loan")
    public String applyLoan(Authentication auth, @ModelAttribute LoanApplicationRequest request) {
        try {
            String email = auth.getName();
            CustomerResponse customerResponse = customerService.getAllCustomers().stream()
                .filter(c -> c.getEmail().equals(email))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Customer not found"));
            
            request.setCustomerId(customerResponse.getCustomerId());
            loanApplicationService.applyForLoan(request);
            return "redirect:/customer/apply-loan?applied";
        } catch (Exception e) {
            return "redirect:/customer/apply-loan?error";
        }
    }

    @GetMapping("/my-loans")
    public String myLoans(Authentication auth, Model model) {
        String email = auth.getName();
        CustomerResponse customerResponse = customerService.getAllCustomers().stream()
            .filter(c -> c.getEmail().equals(email))
            .findFirst()
            .orElseThrow(() -> new RuntimeException("Customer not found"));
        
        Customer customer = customerService.getCustomerEntity(customerResponse.getCustomerId());
        model.addAttribute("applications", customer.getLoanApplications());
        return "customer-loans";
    }

    @GetMapping("/repayments/{applicationId}")
    public String viewRepayments(@PathVariable Long applicationId, Model model) {
        var repayments = repaymentService.getRepaymentSchedule(applicationId);
        BigDecimal outstanding = repaymentService.getOutstandingBalance(applicationId);
        
        long completedCount = repayments.stream().filter(r -> r.getPaymentStatus().name().equals("COMPLETED")).count();
        double progressPercentage = repayments.isEmpty() ? 0 : (completedCount * 100.0 / repayments.size());
        
        model.addAttribute("repayments", repayments);
        model.addAttribute("outstandingBalance", String.format("%.2f", outstanding));
        model.addAttribute("progressPercentage", String.format("%.0f", progressPercentage));
        model.addAttribute("applicationId", applicationId);
        return "customer-repayments-page";
    }

    @GetMapping("/repayments")
    public String allRepayments(Authentication auth, Model model) {
        String email = auth.getName();
        CustomerResponse customerResponse = customerService.getAllCustomers().stream()
            .filter(c -> c.getEmail().equals(email))
            .findFirst()
            .orElseThrow(() -> new RuntimeException("Customer not found"));
        
        Customer customer = customerService.getCustomerEntity(customerResponse.getCustomerId());
        var approvedLoans = customer.getLoanApplications().stream()
            .filter(app -> app.getApprovalStatus() == com.bank.loan.model.LoanApplication.ApprovalStatus.APPROVED)
            .findFirst();
        
        if (approvedLoans.isPresent()) {
            Long applicationId = approvedLoans.get().getApplicationId();
            try {
                var repayments = repaymentService.getRepaymentSchedule(applicationId);
                BigDecimal outstanding = repaymentService.getOutstandingBalance(applicationId);
                
                long completedCount = repayments.stream().filter(r -> "COMPLETED".equals(r.getPaymentStatus())).count();
                double progressPercentage = repayments.isEmpty() ? 0 : (completedCount * 100.0 / repayments.size());
                
                model.addAttribute("repayments", repayments);
                model.addAttribute("outstandingBalance", String.format("%.2f", outstanding));
                model.addAttribute("progressPercentage", String.format("%.0f", progressPercentage));
                model.addAttribute("hasApprovedLoan", true);
            } catch (Exception e) {
                // If repayment schedule doesn't exist, generate it
                repaymentService.generateRepaymentSchedule(approvedLoans.get());
                var repayments = repaymentService.getRepaymentSchedule(applicationId);
                BigDecimal outstanding = repaymentService.getOutstandingBalance(applicationId);
                
                model.addAttribute("repayments", repayments);
                model.addAttribute("outstandingBalance", String.format("%.2f", outstanding));
                model.addAttribute("progressPercentage", "0");
                model.addAttribute("hasApprovedLoan", true);
            }
        } else {
            model.addAttribute("repayments", java.util.Collections.emptyList());
            model.addAttribute("outstandingBalance", "0.00");
            model.addAttribute("progressPercentage", "0");
            model.addAttribute("hasApprovedLoan", false);
        }
        
        return "customer-repayments-page";
    }

    @GetMapping("/report")
    public String report(Authentication auth, Model model) {
        String email = auth.getName();
        CustomerResponse customerResponse = customerService.getAllCustomers().stream()
            .filter(c -> c.getEmail().equals(email))
            .findFirst()
            .orElseThrow(() -> new RuntimeException("Customer not found"));
        
        Customer customer = customerService.getCustomerEntity(customerResponse.getCustomerId());
        model.addAttribute("customer", customer);
        
        var approvedLoan = customer.getLoanApplications().stream()
            .filter(app -> "APPROVED".equals(app.getApprovalStatus()))
            .findFirst();
        
        if (approvedLoan.isPresent()) {
            var loan = approvedLoan.get();
            model.addAttribute("loanApplication", loan);
            
            BigDecimal principal = loan.getLoanAmount();
            BigDecimal rate = loan.getLoanProduct().getInterestRate().divide(BigDecimal.valueOf(100), 6, java.math.RoundingMode.HALF_UP).divide(BigDecimal.valueOf(12), 6, java.math.RoundingMode.HALF_UP);
            int tenure = loan.getLoanProduct().getTenure();
            BigDecimal emi = principal.multiply(rate).multiply(BigDecimal.ONE.add(rate).pow(tenure)).divide(BigDecimal.ONE.add(rate).pow(tenure).subtract(BigDecimal.ONE), 2, java.math.RoundingMode.HALF_UP);
            
            model.addAttribute("emiAmount", emi);
        } else {
            model.addAttribute("loanApplication", null);
        }
        
        return "customer-report-page";
    }

    @GetMapping("/edit-profile")
    public String editProfilePage(Authentication auth, Model model) {
        try {
            String email = auth.getName();
            CustomerResponse customerResponse = customerService.getAllCustomers().stream()
                .filter(c -> c.getEmail().equals(email))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Customer not found"));
            
            CustomerRequest customerRequest = new CustomerRequest();
            customerRequest.setName(customerResponse.getName());
            customerRequest.setEmail(customerResponse.getEmail());
            customerRequest.setPhone(customerResponse.getPhone());
            customerRequest.setAddress(customerResponse.getAddress());
            
            model.addAttribute("customerRequest", customerRequest);
            return "customer-edit-profile";
        } catch (Exception e) {
            return "redirect:/customer/dashboard?error";
        }
    }

    @PostMapping("/edit-profile")
    public String updateProfile(
            Authentication auth,
            @ModelAttribute CustomerRequest request,
            @RequestParam(value = "redirectTo", required = false, defaultValue = "/customer/edit-profile") String redirectTo) {
        try {
            String email = auth.getName();
            CustomerResponse customerResponse = customerService.getAllCustomers().stream()
                .filter(c -> c.getEmail().equals(email))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Customer not found"));

            customerService.updateCustomerProfile(customerResponse.getCustomerId(), request);
            // If email was changed, sign out to re-login with the new email
            if (request.getEmail() != null && !request.getEmail().equalsIgnoreCase(email)) {
                return "redirect:/logout";
            }
            String sep = redirectTo.contains("?") ? "&" : "?";
            return "redirect:" + redirectTo + sep + "success";
        } catch (Exception e) {
            String sep = redirectTo != null && redirectTo.contains("?") ? "&" : "?";
            return "redirect:" + (redirectTo == null ? "/customer/edit-profile" : redirectTo) + sep + "error";
        }
    }

    @PostMapping("/change-password")
    public String changePassword(
            Authentication auth,
            @RequestParam String currentPassword,
            @RequestParam String newPassword,
            @RequestParam String confirmPassword) {
        try {
            if (!newPassword.equals(confirmPassword)) {
                return "redirect:/customer/profile?error=passwords_dont_match";
            }
            
            String email = auth.getName();
            CustomerResponse customerResponse = customerService.getAllCustomers().stream()
                .filter(c -> c.getEmail().equals(email))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Customer not found"));

            // Verify current password
            Customer customer = customerService.getCustomerEntity(customerResponse.getCustomerId());
            if (!passwordEncoder.matches(currentPassword, customer.getPassword())) {
                return "redirect:/customer/profile?error=invalid_current_password";
            }

            // Update password
            customerService.updateCustomerPassword(customerResponse.getCustomerId(), passwordEncoder.encode(newPassword));
            return "redirect:/customer/profile?success=password_changed";
        } catch (Exception e) {
            return "redirect:/customer/profile?error=password_change_failed";
        }
    }
}
