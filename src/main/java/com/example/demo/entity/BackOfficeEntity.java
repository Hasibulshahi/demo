package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;

@Data
@Entity
public class BackOfficeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Reference to UserEntity
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    // Reference to LoanEntity
    @ManyToOne
    @JoinColumn(name = "loan_id", nullable = false)
    private LoanEntity loan;

    // Date when the loan was applied for backoffice processing
    private LocalDate appliedDate;
}
