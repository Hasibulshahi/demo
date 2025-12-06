package com.example.demo.service;

import com.example.demo.entity.LoanEntity;
import com.example.demo.generated.model.LoanRequest;
import com.example.demo.generated.model.LoanResponse;

import java.util.List;

public interface LoanService {

    /**
     * Apply a new loan for a given user.
     *
     * @param request       ID of the user applying for the loan
     * @return Saved LoanEntity
     */
    LoanResponse applyLoan(LoanRequest request);

    /**
     * Get all loans.
     *
     * @return List of LoanEntity
     */
    List<LoanResponse> getAllLoans();

    /**
     * Get a loan by its ID.
     *
     * @param loanId ID of the loan
     * @return LoanEntity
     */
    LoanResponse getLoanById(Long loanId);

    /**
     * Get all loans applied by a specific user with pagination.
     *
     * @param userId ID of the user
     * @param page zero-based page index
     * @param size page size
     * @return List of LoanEntity for the page
     */
    java.util.List<com.example.demo.generated.model.LoanResponse> getLoansByUserId(Long userId, int page, int size);

    /**
     * Update loan approval status for a given loan and user.
     *
     * @param loanId ID of the loan
     * @param userId ID of the user
     * @param loanApproval The new approval status (PENDING, APPROVED, REJECTED)
     * @return Number of rows updated
     */
    int updateLoanApproval(Long loanId, Long userId, String loanApproval);
}
