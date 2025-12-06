package com.example.demo.service.impl;

import com.example.demo.entity.LoanEntity;
import com.example.demo.entity.UserEntity;
import com.example.demo.generated.model.LoanRequest;
import com.example.demo.generated.model.LoanResponse;
import com.example.demo.mapper.LoanMapper;
import com.example.demo.repository.LoanRepository;
import com.example.demo.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("LoanServiceImpl Tests")
class LoanServiceImplTest {

    @Mock
    private LoanRepository loanRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private LoanMapper loanMapper;

    @InjectMocks
    private LoanServiceImpl loanService;

    private LoanRequest loanRequest;
    private LoanEntity loanEntity;
    private LoanResponse loanResponse;
    private UserEntity userEntity;

    @BeforeEach
    void setUp() {
        // Create test user entity
        userEntity = new UserEntity();
        userEntity.setId(1L);
        userEntity.setName("John Doe");
        userEntity.setEmail("john@example.com");
        userEntity.setPhone("+60123456789");
        userEntity.setAddress("123 Main St, City, Country");

        // Create test loan request
        loanRequest = new LoanRequest();
        loanRequest.setUserId(1L);
        loanRequest.setAmount(100000.0);
        loanRequest.setInterestRate(5.0);
        loanRequest.setEndDate(LocalDate.now().plusMonths(60));
        loanRequest.setLoanType("Personal");

        // Create test loan entity
        loanEntity = new LoanEntity();
        loanEntity.setId(1L);
        loanEntity.setUser(userEntity);
        loanEntity.setAmount(100000.0);
        loanEntity.setInterestRate(5.0);
        loanEntity.setEndDate(LocalDate.now().plusMonths(60));
        loanEntity.setLoanType("Personal");
        loanEntity.setPayablelLoanAmount(105000.0); // 100000 + (5% of 100000)
        loanEntity.setLoanApproval("PENDING");

        // Create test loan response
        loanResponse = new LoanResponse();
        loanResponse.setId(1);
        loanResponse.setUserId(1L);
        loanResponse.setAmount(100000.0);
        loanResponse.setInterestRate(5.0);
        loanResponse.setEndDate(LocalDate.now().plusMonths(60));
        loanResponse.setLoanType("Personal");
        loanResponse.setPayableLoanAmount(105000.0);
        loanResponse.setLoanApproval(LoanResponse.LoanApprovalEnum.PENDING);
    }

    @Test
    @DisplayName("Should apply loan successfully for existing user")
    void testApplyLoanSuccess() {
        // Arrange
        when(userRepository.findById(1L))
                .thenReturn(Optional.of(userEntity));
        when(loanMapper.toEntity(any(LoanRequest.class)))
                .thenReturn(loanEntity);
        when(loanRepository.save(any(LoanEntity.class)))
                .thenReturn(loanEntity);
        when(loanMapper.toResponse(any(LoanEntity.class)))
                .thenReturn(loanResponse);

        // Act
        LoanResponse response = loanService.applyLoan(loanRequest);

        // Assert
        assertNotNull(response);
        assertEquals(1, response.getId());
        assertEquals(100000.0, response.getAmount());
        assertEquals(105000.0, response.getPayableLoanAmount());
        assertEquals(LoanResponse.LoanApprovalEnum.PENDING, response.getLoanApproval());

        // Verify
        verify(userRepository, times(1)).findById(1L);
        verify(loanMapper, times(1)).toEntity(any(LoanRequest.class));
        verify(loanRepository, times(1)).save(any(LoanEntity.class));
        verify(loanMapper, times(1)).toResponse(any(LoanEntity.class));
        verifyNoMoreInteractions(userRepository, loanRepository, loanMapper);
    }

    @Test
    @DisplayName("Should throw exception when user not found for loan application")
    void testApplyLoanUserNotFound() {
        // Arrange
        when(userRepository.findById(999L))
                .thenReturn(Optional.empty());

        LoanRequest invalidRequest = new LoanRequest();
        invalidRequest.setUserId(999L);
        invalidRequest.setAmount(100000.0);
        invalidRequest.setInterestRate(5.0);
        invalidRequest.setEndDate(LocalDate.now().plusMonths(60));
        invalidRequest.setLoanType("Personal");

        // Act & Assert
        assertThrows(RuntimeException.class, () -> 
                loanService.applyLoan(invalidRequest),
                "Should throw exception when user not found");

        // Verify
        verify(userRepository, times(1)).findById(999L);
        verify(loanRepository, never()).save(any(LoanEntity.class));
    }

    @Test
    @DisplayName("Should calculate payable loan amount correctly with interest")
    void testApplyLoanWithInterestCalculation() {
        // Arrange
        LoanRequest requestWithHighInterest = new LoanRequest();
        requestWithHighInterest.setUserId(1L);
        requestWithHighInterest.setAmount(50000.0);
        requestWithHighInterest.setInterestRate(10.0);
        requestWithHighInterest.setEndDate(LocalDate.now().plusMonths(36));
        requestWithHighInterest.setLoanType("Home");

        LoanEntity entityWithHighInterest = new LoanEntity();
        entityWithHighInterest.setId(2L);
        entityWithHighInterest.setUser(userEntity);
        entityWithHighInterest.setAmount(50000.0);
        entityWithHighInterest.setInterestRate(10.0);
        entityWithHighInterest.setPayablelLoanAmount(55000.0); // 50000 + 10%

        LoanResponse responseWithHighInterest = new LoanResponse();
        responseWithHighInterest.setId(2);
        responseWithHighInterest.setUserId(1L);
        responseWithHighInterest.setAmount(50000.0);
        responseWithHighInterest.setInterestRate(10.0);
        responseWithHighInterest.setPayableLoanAmount(55000.0);

        when(userRepository.findById(1L)).thenReturn(Optional.of(userEntity));
        when(loanMapper.toEntity(any(LoanRequest.class))).thenReturn(entityWithHighInterest);
        when(loanRepository.save(any(LoanEntity.class))).thenReturn(entityWithHighInterest);
        when(loanMapper.toResponse(any(LoanEntity.class))).thenReturn(responseWithHighInterest);

        // Act
        LoanResponse response = loanService.applyLoan(requestWithHighInterest);

        // Assert
        assertEquals(55000.0, response.getPayableLoanAmount());
        assertEquals(50000.0, response.getAmount());

        // Verify
        verify(loanRepository, times(1)).save(any(LoanEntity.class));
    }

    @Test
    @DisplayName("Should retrieve all loans successfully")
    void testGetAllLoansSuccess() {
        // Arrange
        LoanEntity loan2 = new LoanEntity();
        loan2.setId(2L);
        loan2.setAmount(50000.0);
        loan2.setLoanApproval("APPROVED");

        List<LoanEntity> loanList = Arrays.asList(loanEntity, loan2);

        LoanResponse response2 = new LoanResponse();
        response2.setId(2);
        response2.setUserId(1L);
        response2.setAmount(50000.0);
        response2.setLoanApproval(LoanResponse.LoanApprovalEnum.APPROVED);

        List<LoanResponse> responseList = Arrays.asList(loanResponse, response2);

        when(loanRepository.findAll()).thenReturn(loanList);
        when(loanMapper.toLoansResponse(loanList)).thenReturn(responseList);

        // Act
        List<LoanResponse> responses = loanService.getAllLoans();

        // Assert
        assertNotNull(responses);
        assertEquals(2, responses.size());
        assertEquals(LoanResponse.LoanApprovalEnum.PENDING, responses.get(0).getLoanApproval());
        assertEquals(LoanResponse.LoanApprovalEnum.APPROVED, responses.get(1).getLoanApproval());

        // Verify
        verify(loanRepository, times(1)).findAll();
        verify(loanMapper, times(1)).toLoansResponse(loanList);
    }

    @Test
    @DisplayName("Should retrieve all loans with empty list")
    void testGetAllLoansEmptyList() {
        // Arrange
        when(loanRepository.findAll()).thenReturn(Arrays.asList());
        when(loanMapper.toLoansResponse(Arrays.asList())).thenReturn(Arrays.asList());

        // Act
        List<LoanResponse> responses = loanService.getAllLoans();

        // Assert
        assertNotNull(responses);
        assertTrue(responses.isEmpty());

        // Verify
        verify(loanRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Should get loan by ID successfully")
    void testGetLoanByIdSuccess() {
        // Arrange
        Long loanId = 1L;
        when(loanRepository.findLoanById(loanId)).thenReturn(loanEntity);
        when(loanMapper.toResponse(loanEntity)).thenReturn(loanResponse);

        // Act
        LoanResponse response = loanService.getLoanById(loanId);

        // Assert
        assertNotNull(response);
        assertEquals(1, response.getId());
        assertEquals(100000.0, response.getAmount());

        // Verify
        verify(loanRepository, times(1)).findLoanById(loanId);
        verify(loanMapper, times(1)).toResponse(loanEntity);
    }

    @Test
    @DisplayName("Should return null when loan not found by ID")
    void testGetLoanByIdNotFound() {
        // Arrange
        Long loanId = 999L;
        when(loanRepository.findLoanById(loanId)).thenReturn(null);
        when(loanMapper.toResponse(null)).thenReturn(null);

        // Act
        LoanResponse response = loanService.getLoanById(loanId);

        // Assert
        assertNull(response);

        // Verify
        verify(loanRepository, times(1)).findLoanById(loanId);
    }

    @Test
    @DisplayName("Should get loans by user ID with pagination")
    void testGetLoansByUserIdSuccess() {
        // Arrange
        Long userId = 1L;
        int page = 0;
        int size = 20;
        Pageable pageable = PageRequest.of(page, size);

        LoanEntity loan2 = new LoanEntity();
        loan2.setId(2L);
        loan2.setAmount(50000.0);
        loan2.setLoanApproval("APPROVED");

        List<LoanEntity> loanList = Arrays.asList(loanEntity, loan2);
        Page<LoanEntity> pageResult = new PageImpl<>(loanList, pageable, 2);

        LoanResponse response2 = new LoanResponse();
        response2.setId(2);
        response2.setUserId(1L);
        response2.setAmount(50000.0);
        response2.setLoanApproval(LoanResponse.LoanApprovalEnum.APPROVED);

        List<LoanResponse> responseList = Arrays.asList(loanResponse, response2);

        when(loanRepository.findAllByUser_Id(userId, pageable)).thenReturn(pageResult);
        when(loanMapper.toLoansResponse(loanList)).thenReturn(responseList);

        // Act
        List<LoanResponse> responses = loanService.getLoansByUserId(userId, page, size);

        // Assert
        assertNotNull(responses);
        assertEquals(2, responses.size());
        assertEquals(1, responses.get(0).getId());
        assertEquals(2, responses.get(1).getId());

        // Verify
        verify(loanRepository, times(1)).findAllByUser_Id(userId, pageable);
        verify(loanMapper, times(1)).toLoansResponse(loanList);
    }

    @Test
    @DisplayName("Should get loans by user ID with custom pagination values")
    void testGetLoansByUserIdCustomPagination() {
        // Arrange
        Long userId = 1L;
        int page = 1;
        int size = 10;
        Pageable pageable = PageRequest.of(page, size);

        List<LoanEntity> loanList = Arrays.asList(loanEntity);
        Page<LoanEntity> pageResult = new PageImpl<>(loanList, pageable, 1);

        List<LoanResponse> responseList = Arrays.asList(loanResponse);

        when(loanRepository.findAllByUser_Id(userId, pageable)).thenReturn(pageResult);
        when(loanMapper.toLoansResponse(loanList)).thenReturn(responseList);

        // Act
        List<LoanResponse> responses = loanService.getLoansByUserId(userId, page, size);

        // Assert
        assertEquals(1, responses.size());

        // Verify - verify pageable was created with correct page and size
        verify(loanRepository, times(1)).findAllByUser_Id(eq(userId), any(Pageable.class));
    }

    @Test
    @DisplayName("Should return empty list when user has no loans")
    void testGetLoansByUserIdEmptyList() {
        // Arrange
        Long userId = 999L;
        int page = 0;
        int size = 20;
        Pageable pageable = PageRequest.of(page, size);

        Page<LoanEntity> emptyPage = new PageImpl<>(Arrays.asList(), pageable, 0);

        when(loanRepository.findAllByUser_Id(userId, pageable)).thenReturn(emptyPage);
        when(loanMapper.toLoansResponse(Arrays.asList())).thenReturn(Arrays.asList());

        // Act
        List<LoanResponse> responses = loanService.getLoansByUserId(userId, page, size);

        // Assert
        assertNotNull(responses);
        assertTrue(responses.isEmpty());

        // Verify
        verify(loanRepository, times(1)).findAllByUser_Id(userId, pageable);
    }

    @Test
    @DisplayName("Should handle negative pagination values gracefully")
    void testGetLoansByUserIdNegativePagination() {
        // Arrange
        Long userId = 1L;
        int negativePage = -5;
        int negativeSize = -10;
        
        // Service should convert negative values to positive
        Pageable pageable = PageRequest.of(Math.max(0, negativePage), Math.max(1, negativeSize));
        Page<LoanEntity> pageResult = new PageImpl<>(Arrays.asList(loanEntity), pageable, 1);

        List<LoanResponse> responseList = Arrays.asList(loanResponse);

        when(loanRepository.findAllByUser_Id(userId, pageable)).thenReturn(pageResult);
        when(loanMapper.toLoansResponse(any(List.class))).thenReturn(responseList);

        // Act
        List<LoanResponse> responses = loanService.getLoansByUserId(userId, negativePage, negativeSize);

        // Assert
        assertEquals(1, responses.size());

        // Verify that PageRequest was created with page=0 and size=1 (corrected values)
        verify(loanRepository, times(1)).findAllByUser_Id(eq(userId), any(Pageable.class));
    }

    @Test
    @DisplayName("Should update loan approval status successfully")
    void testUpdateLoanApprovalSuccess() {
        // Arrange
        Long loanId = 1L;
        Long userId = 1L;
        String approvalStatus = "APPROVED";
        int updatedRows = 1;

        when(loanRepository.updateLoanApproval(loanId, userId, approvalStatus))
                .thenReturn(updatedRows);

        // Act
        int result = loanService.updateLoanApproval(loanId, userId, approvalStatus);

        // Assert
        assertEquals(1, result);

        // Verify
        verify(loanRepository, times(1)).updateLoanApproval(loanId, userId, approvalStatus);
        verifyNoMoreInteractions(loanRepository);
    }

    @Test
    @DisplayName("Should return 0 when loan approval update finds no matching records")
    void testUpdateLoanApprovalNotFound() {
        // Arrange
        Long loanId = 999L;
        Long userId = 1L;
        String approvalStatus = "APPROVED";
        int updatedRows = 0;

        when(loanRepository.updateLoanApproval(loanId, userId, approvalStatus))
                .thenReturn(updatedRows);

        // Act
        int result = loanService.updateLoanApproval(loanId, userId, approvalStatus);

        // Assert
        assertEquals(0, result);

        // Verify
        verify(loanRepository, times(1)).updateLoanApproval(loanId, userId, approvalStatus);
    }

    @Test
    @DisplayName("Should update loan approval to REJECTED")
    void testUpdateLoanApprovalRejected() {
        // Arrange
        Long loanId = 1L;
        Long userId = 1L;
        String approvalStatus = "REJECTED";
        int updatedRows = 1;

        when(loanRepository.updateLoanApproval(loanId, userId, approvalStatus))
                .thenReturn(updatedRows);

        // Act
        int result = loanService.updateLoanApproval(loanId, userId, approvalStatus);

        // Assert
        assertEquals(1, result);
        assertTrue(approvalStatus.equals("REJECTED"));

        // Verify
        verify(loanRepository, times(1)).updateLoanApproval(loanId, userId, approvalStatus);
    }

    @Test
    @DisplayName("Should update loan approval to PENDING")
    void testUpdateLoanApprovalPending() {
        // Arrange
        Long loanId = 1L;
        Long userId = 1L;
        String approvalStatus = "PENDING";
        int updatedRows = 1;

        when(loanRepository.updateLoanApproval(loanId, userId, approvalStatus))
                .thenReturn(updatedRows);

        // Act
        int result = loanService.updateLoanApproval(loanId, userId, approvalStatus);

        // Assert
        assertEquals(1, result);
        assertTrue(approvalStatus.equals("PENDING"));

        // Verify
        verify(loanRepository, times(1)).updateLoanApproval(loanId, userId, approvalStatus);
    }

    @Test
    @DisplayName("Should apply loan with zero interest rate")
    void testApplyLoanZeroInterest() {
        // Arrange
        LoanRequest zeroInterestRequest = new LoanRequest();
        zeroInterestRequest.setUserId(1L);
        zeroInterestRequest.setAmount(100000.0);
        zeroInterestRequest.setInterestRate(0.0);
        zeroInterestRequest.setEndDate(LocalDate.now().plusMonths(60));
        zeroInterestRequest.setLoanType("Promotional");

        LoanEntity zeroInterestEntity = new LoanEntity();
        zeroInterestEntity.setId(3L);
        zeroInterestEntity.setUser(userEntity);
        zeroInterestEntity.setAmount(100000.0);
        zeroInterestEntity.setInterestRate(0.0);
        zeroInterestEntity.setPayablelLoanAmount(100000.0); // No interest added

        LoanResponse zeroInterestResponse = new LoanResponse();
        zeroInterestResponse.setId(3);
        zeroInterestResponse.setUserId(1L);
        zeroInterestResponse.setAmount(100000.0);
        zeroInterestResponse.setPayableLoanAmount(100000.0);

        when(userRepository.findById(1L)).thenReturn(Optional.of(userEntity));
        when(loanMapper.toEntity(any(LoanRequest.class))).thenReturn(zeroInterestEntity);
        when(loanRepository.save(any(LoanEntity.class))).thenReturn(zeroInterestEntity);
        when(loanMapper.toResponse(any(LoanEntity.class))).thenReturn(zeroInterestResponse);

        // Act
        LoanResponse response = loanService.applyLoan(zeroInterestRequest);

        // Assert
        assertEquals(100000.0, response.getPayableLoanAmount());
        assertNotNull(response);

        // Verify
        verify(loanRepository, times(1)).save(any(LoanEntity.class));
    }

    @Test
    @DisplayName("Should retrieve multiple loans for same user")
    void testGetLoansByUserIdMultipleLoans() {
        // Arrange
        Long userId = 1L;
        int page = 0;
        int size = 20;
        Pageable pageable = PageRequest.of(page, size);

        LoanEntity loan2 = new LoanEntity();
        loan2.setId(2L);
        loan2.setAmount(50000.0);

        LoanEntity loan3 = new LoanEntity();
        loan3.setId(3L);
        loan3.setAmount(75000.0);

        List<LoanEntity> loanList = Arrays.asList(loanEntity, loan2, loan3);
        Page<LoanEntity> pageResult = new PageImpl<>(loanList, pageable, 3);

        LoanResponse response2 = new LoanResponse();
        response2.setId(2);
        response2.setUserId(1L);
        response2.setAmount(50000.0);

        LoanResponse response3 = new LoanResponse();
        response3.setId(3);
        response3.setUserId(1L);
        response3.setAmount(75000.0);

        List<LoanResponse> responseList = Arrays.asList(loanResponse, response2, response3);

        when(loanRepository.findAllByUser_Id(userId, pageable)).thenReturn(pageResult);
        when(loanMapper.toLoansResponse(loanList)).thenReturn(responseList);

        // Act
        List<LoanResponse> responses = loanService.getLoansByUserId(userId, page, size);

        // Assert
        assertEquals(3, responses.size());
        assertEquals(100000.0, responses.get(0).getAmount());
        assertEquals(50000.0, responses.get(1).getAmount());
        assertEquals(75000.0, responses.get(2).getAmount());

        // Verify
        verify(loanRepository, times(1)).findAllByUser_Id(userId, pageable);
    }

    @Test
    @DisplayName("Should handle mapper exception during loan application")
    void testApplyLoanMapperException() {
        // Arrange
        when(userRepository.findById(1L)).thenReturn(Optional.of(userEntity));
        when(loanMapper.toEntity(any(LoanRequest.class)))
                .thenThrow(new RuntimeException("Mapping error"));

        // Act & Assert
        assertThrows(RuntimeException.class, () -> 
                loanService.applyLoan(loanRequest));

        // Verify
        verify(userRepository, times(1)).findById(1L);
        verify(loanRepository, never()).save(any(LoanEntity.class));
    }

    @Test
    @DisplayName("Should handle repository exception during loan save")
    void testApplyLoanRepositoryException() {
        // Arrange
        when(userRepository.findById(1L)).thenReturn(Optional.of(userEntity));
        when(loanMapper.toEntity(any(LoanRequest.class))).thenReturn(loanEntity);
        when(loanRepository.save(any(LoanEntity.class)))
                .thenThrow(new RuntimeException("Database error"));

        // Act & Assert
        assertThrows(RuntimeException.class, () -> 
                loanService.applyLoan(loanRequest));

        // Verify
        verify(loanRepository, times(1)).save(any(LoanEntity.class));
    }

    @Test
    @DisplayName("Should search loans by username only")
    void testSearchLoansByUsernameOnly() {
        // Arrange
        String username = "John Doe";
        String loanType = null;
        int page = 0;
        int size = 20;
        Pageable pageable = PageRequest.of(page, size);

        LoanEntity loan2 = new LoanEntity();
        loan2.setId(2L);
        loan2.setUser(userEntity);
        loan2.setAmount(50000.0);
        loan2.setLoanType("Auto");

        List<LoanEntity> loanList = Arrays.asList(loanEntity, loan2);
        Page<LoanEntity> pageResult = new PageImpl<>(loanList, pageable, 2);

        LoanResponse response2 = new LoanResponse();
        response2.setId(2);
        response2.setUserId(1L);
        response2.setAmount(50000.0);
        response2.setLoanType("Auto");

        List<LoanResponse> responseList = Arrays.asList(loanResponse, response2);

        when(loanRepository.searchLoans(username, loanType, pageable)).thenReturn(pageResult);
        when(loanMapper.toLoansResponse(loanList)).thenReturn(responseList);

        // Act
        List<LoanResponse> responses = loanService.searchLoans(username, loanType, page, size);

        // Assert
        assertNotNull(responses);
        assertEquals(2, responses.size());
        assertEquals(1, responses.get(0).getId());
        assertEquals(2, responses.get(1).getId());

        // Verify
        verify(loanRepository, times(1)).searchLoans(username, loanType, pageable);
        verify(loanMapper, times(1)).toLoansResponse(loanList);
    }

    @Test
    @DisplayName("Should search loans by loan type only")
    void testSearchLoansByLoanTypeOnly() {
        // Arrange
        String username = null;
        String loanType = "Home Loan";
        int page = 0;
        int size = 20;
        Pageable pageable = PageRequest.of(page, size);

        List<LoanEntity> loanList = Arrays.asList(loanEntity);
        Page<LoanEntity> pageResult = new PageImpl<>(loanList, pageable, 1);

        List<LoanResponse> responseList = Arrays.asList(loanResponse);

        when(loanRepository.searchLoans(username, loanType, pageable)).thenReturn(pageResult);
        when(loanMapper.toLoansResponse(loanList)).thenReturn(responseList);

        // Act
        List<LoanResponse> responses = loanService.searchLoans(username, loanType, page, size);

        // Assert
        assertNotNull(responses);
        assertEquals(1, responses.size());
        assertEquals("Personal", responses.get(0).getLoanType());

        // Verify
        verify(loanRepository, times(1)).searchLoans(username, loanType, pageable);
    }

    @Test
    @DisplayName("Should search loans by both username and loan type")
    void testSearchLoansByUsernameAndLoanType() {
        // Arrange
        String username = "John Doe";
        String loanType = "Personal";
        int page = 0;
        int size = 20;
        Pageable pageable = PageRequest.of(page, size);

        List<LoanEntity> loanList = Arrays.asList(loanEntity);
        Page<LoanEntity> pageResult = new PageImpl<>(loanList, pageable, 1);

        List<LoanResponse> responseList = Arrays.asList(loanResponse);

        when(loanRepository.searchLoans(username, loanType, pageable)).thenReturn(pageResult);
        when(loanMapper.toLoansResponse(loanList)).thenReturn(responseList);

        // Act
        List<LoanResponse> responses = loanService.searchLoans(username, loanType, page, size);

        // Assert
        assertNotNull(responses);
        assertEquals(1, responses.size());
        assertEquals(1, responses.get(0).getId());
        assertEquals("Personal", responses.get(0).getLoanType());

        // Verify
        verify(loanRepository, times(1)).searchLoans(username, loanType, pageable);
    }

    @Test
    @DisplayName("Should return empty list when no loans match search criteria")
    void testSearchLoansNoMatches() {
        // Arrange
        String username = "Unknown User";
        String loanType = "Business Loan";
        int page = 0;
        int size = 20;
        Pageable pageable = PageRequest.of(page, size);

        Page<LoanEntity> emptyPage = new PageImpl<>(Arrays.asList(), pageable, 0);

        when(loanRepository.searchLoans(username, loanType, pageable)).thenReturn(emptyPage);
        when(loanMapper.toLoansResponse(Arrays.asList())).thenReturn(Arrays.asList());

        // Act
        List<LoanResponse> responses = loanService.searchLoans(username, loanType, page, size);

        // Assert
        assertNotNull(responses);
        assertTrue(responses.isEmpty());

        // Verify
        verify(loanRepository, times(1)).searchLoans(username, loanType, pageable);
    }

    @Test
    @DisplayName("Should search loans with pagination")
    void testSearchLoansWithPagination() {
        // Arrange
        String username = "John Doe";
        String loanType = null;
        int page = 1;
        int size = 10;
        Pageable pageable = PageRequest.of(page, size);

        List<LoanEntity> loanList = Arrays.asList(loanEntity);
        Page<LoanEntity> pageResult = new PageImpl<>(loanList, pageable, 1);

        List<LoanResponse> responseList = Arrays.asList(loanResponse);

        when(loanRepository.searchLoans(username, loanType, pageable)).thenReturn(pageResult);
        when(loanMapper.toLoansResponse(loanList)).thenReturn(responseList);

        // Act
        List<LoanResponse> responses = loanService.searchLoans(username, loanType, page, size);

        // Assert
        assertEquals(1, responses.size());

        // Verify
        verify(loanRepository, times(1)).searchLoans(eq(username), eq(loanType), any(Pageable.class));
    }

    @Test
    @DisplayName("Should handle null username and loan type in search")
    void testSearchLoansWithBothNull() {
        // Arrange
        String username = null;
        String loanType = null;
        int page = 0;
        int size = 20;
        Pageable pageable = PageRequest.of(page, size);

        LoanEntity loan2 = new LoanEntity();
        loan2.setId(2L);
        loan2.setAmount(50000.0);

        List<LoanEntity> loanList = Arrays.asList(loanEntity, loan2);
        Page<LoanEntity> pageResult = new PageImpl<>(loanList, pageable, 2);

        LoanResponse response2 = new LoanResponse();
        response2.setId(2);
        response2.setAmount(50000.0);

        List<LoanResponse> responseList = Arrays.asList(loanResponse, response2);

        when(loanRepository.searchLoans(username, loanType, pageable)).thenReturn(pageResult);
        when(loanMapper.toLoansResponse(loanList)).thenReturn(responseList);

        // Act
        List<LoanResponse> responses = loanService.searchLoans(username, loanType, page, size);

        // Assert
        assertNotNull(responses);
        assertEquals(2, responses.size());

        // Verify
        verify(loanRepository, times(1)).searchLoans(null, null, pageable);
    }

    @Test
    @DisplayName("Should search loans with negative pagination values corrected")
    void testSearchLoansNegativePagination() {
        // Arrange
        String username = "John Doe";
        String loanType = "Personal";
        int negativePage = -5;
        int negativeSize = -10;
        
        // Service should convert negative values to positive
        Pageable pageable = PageRequest.of(Math.max(0, negativePage), Math.max(1, negativeSize));
        Page<LoanEntity> pageResult = new PageImpl<>(Arrays.asList(loanEntity), pageable, 1);

        List<LoanResponse> responseList = Arrays.asList(loanResponse);

        when(loanRepository.searchLoans(username, loanType, pageable)).thenReturn(pageResult);
        when(loanMapper.toLoansResponse(any(List.class))).thenReturn(responseList);

        // Act
        List<LoanResponse> responses = loanService.searchLoans(username, loanType, negativePage, negativeSize);

        // Assert
        assertEquals(1, responses.size());

        // Verify that PageRequest was created with page=0 and size=1
        verify(loanRepository, times(1)).searchLoans(eq(username), eq(loanType), any(Pageable.class));
    }
}
