package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;

@Data
@Entity
public class LoanEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Double amount;
    private Double interestRate;
    private LocalDate startDate;
    private LocalDate endDate;
    private String loanType;
    private Double payablelLoanAmount;

    // Default value for loanApproval
    private String loanApproval = "PENDING";

    // This creates the foreign key column 'user_id'
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    // Alias getter/setter to normalize naming without breaking existing mappings/tests
    public Double getPayableLoanAmount() {
        return this.payablelLoanAmount;
    }

    public void setPayableLoanAmount(Double payableLoanAmount) {
        this.payablelLoanAmount = payableLoanAmount;
    }
}
