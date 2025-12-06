package com.example.demo.service;

import com.example.demo.generated.model.BackofficeRequest;
import com.example.demo.generated.model.BackofficeResponse;

import java.util.List;

public interface BackOfficeService {
/**
     * Get all backoffice records
     * 
     * @return List of all backoffice records
     */
    /**
     * Get backoffice records with pagination
     *
     * @param page zero-based page index
     * @param size page size
     * @return List of backoffice records for the page
     */
    List<BackofficeResponse> getAllBackOfficeLoans(int page, int size);

    /**
     * Create a new backoffice record
     * 
     * @param request The backoffice request with userId, loanId, appliedDate
     * @return Created BackofficeResponse
     */
    BackofficeResponse createBackOffice(BackofficeRequest request);

    /**
     * Get backoffice record by user ID
     * 
     * @param userId The user ID
     * @return BackofficeResponse for that user
     */
    BackofficeResponse getBackOfficeByUser(Integer userId);

    /**
     * Update backoffice record
     * 
     * @param userId The user ID
     * @param request The updated backoffice request
     * @return Updated BackofficeResponse
     */
    BackofficeResponse updateBackOffice(Integer userId, BackofficeRequest request);
}
