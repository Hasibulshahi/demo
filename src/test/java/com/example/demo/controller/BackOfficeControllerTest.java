package com.example.demo.controller;

import com.example.demo.generated.model.BackofficeRequest;
import com.example.demo.generated.model.BackofficeResponse;
import com.example.demo.generated.model.LoanResponse;
import com.example.demo.service.BackOfficeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("BackOfficeController Tests")
class BackOfficeControllerTest {

    @Mock
    private BackOfficeService backOfficeService;

    @InjectMocks
    private BackOfficeController backOfficeController;

    private BackofficeRequest backofficeRequest;
    private BackofficeResponse backofficeResponse;

    @BeforeEach
    void setUp() {
        // Create test backoffice request
        backofficeRequest = new BackofficeRequest();
        backofficeRequest.setUserId(1);
        backofficeRequest.setLoanId(10);
        backofficeRequest.setAppliedDate(LocalDate.of(2025, 12, 6));
        backofficeRequest.setLoanApproval("PENDING");

        // Create test backoffice response with loan details
        LoanResponse loanDetails = new LoanResponse();
        loanDetails.setId(10);
        loanDetails.setLoanType("Home Loan");
        loanDetails.setUserId(1L);
        loanDetails.setAmount(5000.0);
        loanDetails.setInterestRate(7.5);
        loanDetails.setPayableLoanAmount(5375.0);
        loanDetails.setStartDate(LocalDate.of(2025, 12, 6));
        loanDetails.setEndDate(LocalDate.of(2026, 12, 31));
        loanDetails.setLoanApproval(LoanResponse.LoanApprovalEnum.PENDING);

        backofficeResponse = new BackofficeResponse();
        backofficeResponse.setId(1);
        backofficeResponse.setAppliedDate(LocalDate.of(2025, 12, 6));
        backofficeResponse.setLoanDetails(loanDetails);
    }

    @Test
    @DisplayName("Should get all backoffice loans with default pagination")
    void testGetAllBackOfficeLoansDefaultPagination() {
        // Arrange
        List<BackofficeResponse> backofficeList = Arrays.asList(backofficeResponse);
        when(backOfficeService.getAllBackOfficeLoans(0, 20))
                .thenReturn(backofficeList);

        // Act
        ResponseEntity<List<BackofficeResponse>> response = 
                backOfficeController.getAllBackOfficeLoans(null, null);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        assertEquals(1, response.getBody().get(0).getId());

        // Verify - called with default page=0, size=20
        verify(backOfficeService, times(1)).getAllBackOfficeLoans(0, 20);
        verifyNoMoreInteractions(backOfficeService);
    }

    @Test
    @DisplayName("Should get all backoffice loans with custom pagination")
    void testGetAllBackOfficeLoansCustomPagination() {
        // Arrange
        List<BackofficeResponse> backofficeList = Arrays.asList(
                backofficeResponse,
                createBackofficeResponse(2)
        );
        when(backOfficeService.getAllBackOfficeLoans(1, 10))
                .thenReturn(backofficeList);

        // Act
        ResponseEntity<List<BackofficeResponse>> response = 
                backOfficeController.getAllBackOfficeLoans(1, 10);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(2, response.getBody().size());

        // Verify
        verify(backOfficeService, times(1)).getAllBackOfficeLoans(1, 10);
        verifyNoMoreInteractions(backOfficeService);
    }

    @Test
    @DisplayName("Should get all backoffice loans with page parameter only")
    void testGetAllBackOfficeLoansWithPageOnly() {
        // Arrange
        List<BackofficeResponse> backofficeList = Arrays.asList(backofficeResponse);
        when(backOfficeService.getAllBackOfficeLoans(2, 20))
                .thenReturn(backofficeList);

        // Act
        ResponseEntity<List<BackofficeResponse>> response = 
                backOfficeController.getAllBackOfficeLoans(2, null);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());

        // Verify - size defaults to 20
        verify(backOfficeService, times(1)).getAllBackOfficeLoans(2, 20);
    }

    @Test
    @DisplayName("Should get all backoffice loans with size parameter only")
    void testGetAllBackOfficeLoansWithSizeOnly() {
        // Arrange
        List<BackofficeResponse> backofficeList = Arrays.asList(backofficeResponse);
        when(backOfficeService.getAllBackOfficeLoans(0, 50))
                .thenReturn(backofficeList);

        // Act
        ResponseEntity<List<BackofficeResponse>> response = 
                backOfficeController.getAllBackOfficeLoans(null, 50);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());

        // Verify - page defaults to 0
        verify(backOfficeService, times(1)).getAllBackOfficeLoans(0, 50);
    }

    @Test
    @DisplayName("Should return empty list when no backoffice records exist")
    void testGetAllBackOfficeLoansEmptyResult() {
        // Arrange
        when(backOfficeService.getAllBackOfficeLoans(0, 20))
                .thenReturn(Arrays.asList());

        // Act
        ResponseEntity<List<BackofficeResponse>> response = 
                backOfficeController.getAllBackOfficeLoans(null, null);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isEmpty());

        // Verify
        verify(backOfficeService, times(1)).getAllBackOfficeLoans(0, 20);
    }

    @Test
    @DisplayName("Should get all backoffice loans with maximum size parameter")
    void testGetAllBackOfficeLoansMaxSize() {
        // Arrange
        List<BackofficeResponse> backofficeList = Arrays.asList(backofficeResponse);
        when(backOfficeService.getAllBackOfficeLoans(0, 100))
                .thenReturn(backofficeList);

        // Act
        ResponseEntity<List<BackofficeResponse>> response = 
                backOfficeController.getAllBackOfficeLoans(null, 100);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());

        // Verify
        verify(backOfficeService, times(1)).getAllBackOfficeLoans(0, 100);
    }

    @Test
    @DisplayName("Should create backoffice successfully")
    void testCreateBackOfficeSuccess() {
        // Arrange
        when(backOfficeService.createBackOffice(any(BackofficeRequest.class)))
                .thenReturn(backofficeResponse);

        // Act
        ResponseEntity<BackofficeResponse> response = 
                backOfficeController.createBackOffice(backofficeRequest);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().getId());
        assertNotNull(response.getBody().getLoanDetails());
        assertEquals("Home Loan", response.getBody().getLoanDetails().getLoanType());

        // Verify
        verify(backOfficeService, times(1)).createBackOffice(any(BackofficeRequest.class));
        verifyNoMoreInteractions(backOfficeService);
    }

    @Test
    @DisplayName("Should create backoffice with correct loan details")
    void testCreateBackOfficeWithLoanDetails() {
        // Arrange
        when(backOfficeService.createBackOffice(any(BackofficeRequest.class)))
                .thenReturn(backofficeResponse);

        // Act
        ResponseEntity<BackofficeResponse> response = 
                backOfficeController.createBackOffice(backofficeRequest);

        // Assert
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().getId());
        assertEquals(LocalDate.of(2025, 12, 6), response.getBody().getAppliedDate());
        assertEquals(10, response.getBody().getLoanDetails().getId());
        assertEquals(5000.0, response.getBody().getLoanDetails().getAmount());
        assertEquals(7.5, response.getBody().getLoanDetails().getInterestRate());

        // Verify
        verify(backOfficeService, times(1)).createBackOffice(any(BackofficeRequest.class));
    }

    @Test
    @DisplayName("Should get backoffice by user successfully")
    void testGetBackOfficeByUserSuccess() {
        // Arrange
        Integer userId = 1;
        when(backOfficeService.getBackOfficeByUser(userId))
                .thenReturn(backofficeResponse);

        // Act
        ResponseEntity<BackofficeResponse> response = 
                backOfficeController.getBackOfficeByUser(userId);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().getId());

        // Verify
        verify(backOfficeService, times(1)).getBackOfficeByUser(userId);
        verifyNoMoreInteractions(backOfficeService);
    }

    @Test
    @DisplayName("Should get backoffice by user with loan details")
    void testGetBackOfficeByUserWithLoanDetails() {
        // Arrange
        Integer userId = 1;
        when(backOfficeService.getBackOfficeByUser(userId))
                .thenReturn(backofficeResponse);

        // Act
        ResponseEntity<BackofficeResponse> response = 
                backOfficeController.getBackOfficeByUser(userId);

        // Assert
        assertNotNull(response.getBody());
        assertNotNull(response.getBody().getLoanDetails());
        assertEquals("Home Loan", response.getBody().getLoanDetails().getLoanType());
        assertEquals(1L, response.getBody().getLoanDetails().getUserId());

        // Verify
        verify(backOfficeService, times(1)).getBackOfficeByUser(userId);
    }

    @Test
    @DisplayName("Should handle exception when getting backoffice by user")
    void testGetBackOfficeByUserException() {
        // Arrange
        Integer userId = 999;
        when(backOfficeService.getBackOfficeByUser(userId))
                .thenThrow(new RuntimeException("BackOffice record not found"));

        // Act & Assert
        assertThrows(RuntimeException.class, () -> 
                backOfficeController.getBackOfficeByUser(userId));

        // Verify
        verify(backOfficeService, times(1)).getBackOfficeByUser(userId);
    }

    @Test
    @DisplayName("Should update backoffice successfully")
    void testUpdateBackOfficeSuccess() {
        // Arrange
        Integer id = 1;
        BackofficeResponse updatedResponse = new BackofficeResponse();
        updatedResponse.setId(1);
        updatedResponse.setAppliedDate(LocalDate.of(2025, 12, 7));
        
        LoanResponse updatedLoanDetails = new LoanResponse();
        updatedLoanDetails.setId(11);
        updatedLoanDetails.setLoanType("Car Loan");
        updatedResponse.setLoanDetails(updatedLoanDetails);

        when(backOfficeService.updateBackOffice(id, backofficeRequest))
                .thenReturn(updatedResponse);

        // Act
        ResponseEntity<BackofficeResponse> response = 
                backOfficeController.updateBackOffice(id, backofficeRequest);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().getId());
        assertEquals("Car Loan", response.getBody().getLoanDetails().getLoanType());

        // Verify
        verify(backOfficeService, times(1)).updateBackOffice(id, backofficeRequest);
        verifyNoMoreInteractions(backOfficeService);
    }

    @Test
    @DisplayName("Should update backoffice with new applied date")
    void testUpdateBackOfficeWithNewDate() {
        // Arrange
        Integer id = 1;
        BackofficeResponse updatedResponse = new BackofficeResponse();
        updatedResponse.setId(1);
        updatedResponse.setAppliedDate(LocalDate.of(2025, 12, 10));
        
        when(backOfficeService.updateBackOffice(id, backofficeRequest))
                .thenReturn(updatedResponse);

        // Act
        ResponseEntity<BackofficeResponse> response = 
                backOfficeController.updateBackOffice(id, backofficeRequest);

        // Assert
        assertNotNull(response.getBody());
        assertEquals(LocalDate.of(2025, 12, 10), response.getBody().getAppliedDate());

        // Verify
        verify(backOfficeService, times(1)).updateBackOffice(id, backofficeRequest);
    }

    @Test
    @DisplayName("Should handle exception when updating backoffice")
    void testUpdateBackOfficeException() {
        // Arrange
        Integer id = 999;
        when(backOfficeService.updateBackOffice(id, backofficeRequest))
                .thenThrow(new RuntimeException("BackOffice record not found"));

        // Act & Assert
        assertThrows(RuntimeException.class, () -> 
                backOfficeController.updateBackOffice(id, backofficeRequest));

        // Verify
        verify(backOfficeService, times(1)).updateBackOffice(id, backofficeRequest);
    }

    @Test
    @DisplayName("Should handle create backoffice with exception")
    void testCreateBackOfficeException() {
        // Arrange
        when(backOfficeService.createBackOffice(any(BackofficeRequest.class)))
                .thenThrow(new RuntimeException("Failed to create BackOffice record"));

        // Act & Assert
        assertThrows(RuntimeException.class, () -> 
                backOfficeController.createBackOffice(backofficeRequest));

        // Verify
        verify(backOfficeService, times(1)).createBackOffice(any(BackofficeRequest.class));
    }

    @Test
    @DisplayName("Should handle get all backoffice loans with service exception")
    void testGetAllBackOfficeLoansException() {
        // Arrange
        when(backOfficeService.getAllBackOfficeLoans(0, 20))
                .thenThrow(new RuntimeException("Database error"));

        // Act & Assert
        assertThrows(RuntimeException.class, () -> 
                backOfficeController.getAllBackOfficeLoans(null, null));

        // Verify
        verify(backOfficeService, times(1)).getAllBackOfficeLoans(0, 20);
    }

    @Test
    @DisplayName("Should get multiple backoffice records with pagination")
    void testGetAllBackOfficeLoansMultipleRecords() {
        // Arrange
        List<BackofficeResponse> backofficeList = Arrays.asList(
                createBackofficeResponse(1),
                createBackofficeResponse(2),
                createBackofficeResponse(3)
        );
        when(backOfficeService.getAllBackOfficeLoans(0, 20))
                .thenReturn(backofficeList);

        // Act
        ResponseEntity<List<BackofficeResponse>> response = 
                backOfficeController.getAllBackOfficeLoans(null, null);

        // Assert
        assertNotNull(response.getBody());
        assertEquals(3, response.getBody().size());
        assertEquals(1, response.getBody().get(0).getId());
        assertEquals(2, response.getBody().get(1).getId());
        assertEquals(3, response.getBody().get(2).getId());

        // Verify
        verify(backOfficeService, times(1)).getAllBackOfficeLoans(0, 20);
    }

    /**
     * Helper method to create BackofficeResponse for tests
     */
    private BackofficeResponse createBackofficeResponse(int id) {
        LoanResponse loanDetails = new LoanResponse();
        loanDetails.setId(10 + id);
        loanDetails.setLoanType("Loan Type " + id);
        loanDetails.setUserId((long) id);
        loanDetails.setAmount(5000.0 + (id * 1000));
        loanDetails.setInterestRate(7.5);

        BackofficeResponse response = new BackofficeResponse();
        response.setId(id);
        response.setAppliedDate(LocalDate.of(2025, 12, 6));
        response.setLoanDetails(loanDetails);
        return response;
    }
}
