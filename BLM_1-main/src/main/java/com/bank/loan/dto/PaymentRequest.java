package com.bank.loan.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;

public class PaymentRequest {
    
    @NotNull(message = "Repayment ID is required")
    private Long repaymentId;
    
    @NotNull(message = "Payment amount is required")
    @DecimalMin(value = "0.0", message = "Payment amount must be non-negative")
    private BigDecimal paymentAmount;
    
    @NotNull(message = "Payment date is required")
    private LocalDate paymentDate;

    public PaymentRequest() {}

    public Long getRepaymentId() {
        return repaymentId;
    }

    public void setRepaymentId(Long repaymentId) {
        this.repaymentId = repaymentId;
    }

    public BigDecimal getPaymentAmount() {
        return paymentAmount;
    }

    public void setPaymentAmount(BigDecimal paymentAmount) {
        this.paymentAmount = paymentAmount;
    }

    public LocalDate getPaymentDate() {
        return paymentDate;
    }

    public void setPaymentDate(LocalDate paymentDate) {
        this.paymentDate = paymentDate;
    }
}
