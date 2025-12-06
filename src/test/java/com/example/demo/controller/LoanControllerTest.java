package com.example.demo.controller;

import com.example.demo.generated.model.LoanRequest;
import com.example.demo.generated.model.LoanResponse;
import com.example.demo.service.LoanService;
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
@DisplayName("LoanController Tests")
class LoanControllerTest {

    @Mock
    private LoanService loanService;

    @InjectMocks
    private LoanController loanController;

    private LoanRequest loanRequest;
    private LoanResponse loanResponse;

    @BeforeEach
    void setUp() {
        // Create test loan request
        loanRequest = new LoanRequest();
        loanRequest.setUserId(1L);
        loanRequest.setAmount(5000.0);
        loanRequest.setInterestRate(7.5);
        loanRequest.setEndDate(LocalDate.of(2026, 12, 31));
        loanRequest.setLoanType("Home Loan");

        // Create test loan response
        loanResponse = new LoanResponse();
        loanResponse.setId(10);
        loanResponse.setUserId(1L);
        loanResponse.setAmount(5000.0);
        loanResponse.setInterestRate(7.5);
        loanResponse.setPayableLoanAmount(5375.0);
        loanResponse.setStartDate(LocalDate.now());
        loanResponse.setEndDate(LocalDate.of(2026, 12, 31));
        loanResponse.setLoanType("Home Loan");
        loanResponse.setLoanApproval(LoanResponse.LoanApprovalEnum.PENDING);
    }

    @Test
    @DisplayName("Should apply loan successfully")
    void testApplyLoanSuccess() {
        // Arrange
        when(loanService.applyLoan(any(LoanRequest.class)))
                .thenReturn(loanResponse);

        // Act
        ResponseEntity<LoanResponse> response = loanController.applyLoan(loanRequest);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(10, response.getBody().getId());
        assertEquals(1L, response.getBody().getUserId());
        assertEquals(5000.0, response.getBody().getAmount());
        assertEquals(7.5, response.getBody().getInterestRate());
        assertEquals("Home Loan", response.getBody().getLoanType());
        assertEquals(LoanResponse.LoanApprovalEnum.PENDING, response.getBody().getLoanApproval());

        // Verify
        verify(loanService, times(1)).applyLoan(any(LoanRequest.class));
        verifyNoMoreInteractions(loanService);
    }

    @Test
    @DisplayName("Should apply loan with correct payable amount")
    void testApplyLoanWithPayableAmount() {
        // Arrange
        LoanResponse responseWithPayable = new LoanResponse();
        responseWithPayable.setId(11);
        responseWithPayable.setAmount(10000.0);
        responseWithPayable.setInterestRate(5.0);
        responseWithPayable.setPayableLoanAmount(10500.0); // 10000 + (5% of 10000)
        
        when(loanService.applyLoan(any(LoanRequest.class)))
                .thenReturn(responseWithPayable);

        // Act
        ResponseEntity<LoanResponse> response = loanController.applyLoan(loanRequest);

        // Assert
        assertNotNull(response.getBody());
        assertEquals(10500.0, response.getBody().getPayableLoanAmount());

        // Verify
        verify(loanService, times(1)).applyLoan(any(LoanRequest.class));
    }

    @Test
    @DisplayName("Should get loans by user ID with default pagination")
    void testGetLoansByUserIdDefaultPagination() {
        // Arrange
        Long userId = 1L;
        List<LoanResponse> loanList = Arrays.asList(loanResponse);
        when(loanService.getLoansByUserId(userId, 0, 20))
                .thenReturn(loanList);

        // Act
        ResponseEntity<List<LoanResponse>> response = loanController.getLoansByUserId(userId, null, null);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        assertEquals(10, response.getBody().get(0).getId());

        // Verify - called with default page=0, size=20
        verify(loanService, times(1)).getLoansByUserId(1L, 0, 20);
        verifyNoMoreInteractions(loanService);
    }

    @Test
    @DisplayName("Should get loans by user ID with custom pagination")
    void testGetLoansByUserIdCustomPagination() {
        // Arrange
        Long userId = 1L;
        Integer page = 1;
        Integer size = 10;
        List<LoanResponse> loanList = Arrays.asList(
                loanResponse,
                new LoanResponse() {{
                    setId(11);
                    setUserId(1L);
                    setLoanType("Car Loan");
                }}
        );
        when(loanService.getLoansByUserId(userId, page, size))
                .thenReturn(loanList);

        // Act
        ResponseEntity<List<LoanResponse>> response = loanController.getLoansByUserId(userId, page, size);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(2, response.getBody().size());

        // Verify
        verify(loanService, times(1)).getLoansByUserId(1L, 1, 10);
        verifyNoMoreInteractions(loanService);
    }

    @Test
    @DisplayName("Should get loans with page parameter only")
    void testGetLoansByUserIdWithPageOnly() {
        // Arrange
        Long userId = 2L;
        Integer page = 2;
        List<LoanResponse> loanList = Arrays.asList(loanResponse);
        when(loanService.getLoansByUserId(userId, page, 20))
                .thenReturn(loanList);

        // Act
        ResponseEntity<List<LoanResponse>> response = loanController.getLoansByUserId(userId, page, null);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());

        // Verify - size defaults to 20
        verify(loanService, times(1)).getLoansByUserId(2L, 2, 20);
    }

    @Test
    @DisplayName("Should get loans with size parameter only")
    void testGetLoansByUserIdWithSizeOnly() {
        // Arrange
        Long userId = 3L;
        Integer size = 50;
        List<LoanResponse> loanList = Arrays.asList(loanResponse);
        when(loanService.getLoansByUserId(userId, 0, size))
                .thenReturn(loanList);

        // Act
        ResponseEntity<List<LoanResponse>> response = loanController.getLoansByUserId(userId, null, size);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());

        // Verify - page defaults to 0
        verify(loanService, times(1)).getLoansByUserId(3L, 0, 50);
    }

    @Test
    @DisplayName("Should return empty list when user has no loans")
    void testGetLoansByUserIdEmptyResult() {
        // Arrange
        Long userId = 999L;
        when(loanService.getLoansByUserId(userId, 0, 20))
                .thenReturn(Arrays.asList());

        // Act
        ResponseEntity<List<LoanResponse>> response = loanController.getLoansByUserId(userId, null, null);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isEmpty());

        // Verify
        verify(loanService, times(1)).getLoansByUserId(999L, 0, 20);
    }

    @Test
    @DisplayName("Should handle multiple loans in paginated response")
    void testGetLoansByUserIdMultipleLoans() {
        // Arrange
        Long userId = 1L;
        List<LoanResponse> loanList = Arrays.asList(
                createLoanResponse(10, "Home Loan"),
                createLoanResponse(11, "Car Loan"),
                createLoanResponse(12, "Personal Loan")
        );
        when(loanService.getLoansByUserId(userId, 0, 20))
                .thenReturn(loanList);

        // Act
        ResponseEntity<List<LoanResponse>> response = loanController.getLoansByUserId(userId, null, null);

        // Assert
        assertNotNull(response.getBody());
        assertEquals(3, response.getBody().size());
        assertEquals("Home Loan", response.getBody().get(0).getLoanType());
        assertEquals("Car Loan", response.getBody().get(1).getLoanType());
        assertEquals("Personal Loan", response.getBody().get(2).getLoanType());

        // Verify
        verify(loanService, times(1)).getLoansByUserId(userId, 0, 20);
    }

    @Test
    @DisplayName("Should apply loan with various interest rates")
    void testApplyLoanWithDifferentInterestRates() {
        // Arrange
        double[] interestRates = {5.0, 7.5, 10.0, 15.0};
        
        for (double rate : interestRates) {
            LoanRequest request = new LoanRequest();
            request.setUserId(1L);
            request.setAmount(1000.0);
            request.setInterestRate(rate);
            request.setEndDate(LocalDate.of(2026, 12, 31));
            request.setLoanType("Test Loan");

            LoanResponse response = new LoanResponse();
            response.setId((int) (100 + rate));
            response.setInterestRate(rate);
            response.setAmount(1000.0);
            
            when(loanService.applyLoan(any(LoanRequest.class)))
                    .thenReturn(response);

            // Act
            ResponseEntity<LoanResponse> result = loanController.applyLoan(request);

            // Assert
            assertNotNull(result.getBody());
            assertEquals(rate, result.getBody().getInterestRate());
        }
    }

    @Test
    @DisplayName("Should apply loan with different loan types")
    void testApplyLoanWithDifferentLoanTypes() {
        // Arrange
        String[] loanTypes = {"Home Loan", "Car Loan", "Personal Loan", "Student Loan"};
        
        for (String loanType : loanTypes) {
            LoanRequest request = new LoanRequest();
            request.setUserId(1L);
            request.setAmount(5000.0);
            request.setInterestRate(7.5);
            request.setEndDate(LocalDate.of(2026, 12, 31));
            request.setLoanType(loanType);

            LoanResponse response = new LoanResponse();
            response.setId(100);
            response.setLoanType(loanType);
            
            when(loanService.applyLoan(any(LoanRequest.class)))
                    .thenReturn(response);

            // Act
            ResponseEntity<LoanResponse> result = loanController.applyLoan(request);

            // Assert
            assertNotNull(result.getBody());
            assertEquals(loanType, result.getBody().getLoanType());
        }
    }

    @Test
    @DisplayName("Should handle service exception during apply loan")
    void testApplyLoanServiceException() {
        // Arrange
        when(loanService.applyLoan(any(LoanRequest.class)))
                .thenThrow(new RuntimeException("User not found"));

        // Act & Assert
        assertThrows(RuntimeException.class, () -> loanController.applyLoan(loanRequest));

        // Verify
        verify(loanService, times(1)).applyLoan(any(LoanRequest.class));
    }

    @Test
    @DisplayName("Should handle service exception during get loans by user")
    void testGetLoansByUserIdServiceException() {
        // Arrange
        Long userId = 1L;
        when(loanService.getLoansByUserId(userId, 0, 20))
                .thenThrow(new RuntimeException("Database error"));

        // Act & Assert
        assertThrows(RuntimeException.class, () -> loanController.getLoansByUserId(userId, null, null));

        // Verify
        verify(loanService, times(1)).getLoansByUserId(userId, 0, 20);
    }

    /**
     * Helper method to create LoanResponse for tests
     */
    private LoanResponse createLoanResponse(int id, String loanType) {
        LoanResponse response = new LoanResponse();
        response.setId(id);
        response.setUserId(1L);
        response.setAmount(5000.0);
        response.setInterestRate(7.5);
        response.setPayableLoanAmount(5375.0);
        response.setStartDate(LocalDate.now());
        response.setEndDate(LocalDate.of(2026, 12, 31));
        response.setLoanType(loanType);
        response.setLoanApproval(LoanResponse.LoanApprovalEnum.PENDING);
        return response;
    }
}
