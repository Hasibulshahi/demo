package com.example.demo.service.impl;

import com.example.demo.entity.BackOfficeEntity;
import com.example.demo.entity.LoanEntity;
import com.example.demo.entity.UserEntity;
import com.example.demo.generated.model.BackofficeRequest;
import com.example.demo.generated.model.BackofficeResponse;
import com.example.demo.generated.model.LoanResponse;
import com.example.demo.mapper.BackOfficeMapper;
import com.example.demo.repository.BackOfficeRepository;
import com.example.demo.repository.LoanRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.LoanService;
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
@DisplayName("BackOfficeServiceImpl Tests")
class BackOfficeServiceImplTest {

    @Mock
    private BackOfficeRepository backOfficeRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private LoanRepository loanRepository;

    @Mock
    private BackOfficeMapper backOfficeMapper;

    @Mock
    private LoanService loanService;

    @InjectMocks
    private BackOfficeServiceImpl backOfficeService;

    private BackofficeRequest backofficeRequest;
    private BackOfficeEntity backOfficeEntity;
    private BackofficeResponse backofficeResponse;
    private UserEntity userEntity;
    private LoanEntity loanEntity;
    private LoanResponse loanResponse;

    @BeforeEach
    void setUp() {
        // Create test user entity
        userEntity = new UserEntity();
        userEntity.setId(1L);
        userEntity.setName("John Doe");
        userEntity.setEmail("john@example.com");

        // Create test loan entity
        loanEntity = new LoanEntity();
        loanEntity.setId(1L);
        loanEntity.setUser(userEntity);
        loanEntity.setAmount(100000.0);
        loanEntity.setInterestRate(5.0);
        loanEntity.setLoanType("Personal");
        loanEntity.setLoanApproval("PENDING");

        // Create test loan response
        loanResponse = new LoanResponse();
        loanResponse.setId(1);
        loanResponse.setUserId(1L);
        loanResponse.setAmount(100000.0);
        loanResponse.setInterestRate(5.0);
        loanResponse.setLoanType("Personal");
        loanResponse.setLoanApproval(LoanResponse.LoanApprovalEnum.PENDING);

        // Create test backoffice entity
        backOfficeEntity = new BackOfficeEntity();
        backOfficeEntity.setId(1L);
        backOfficeEntity.setUser(userEntity);
        backOfficeEntity.setLoan(loanEntity);
        backOfficeEntity.setAppliedDate(LocalDate.now());

        // Create test backoffice request
        backofficeRequest = new BackofficeRequest();
        backofficeRequest.setUserId(1);
        backofficeRequest.setLoanId(1);
        backofficeRequest.setAppliedDate(LocalDate.now());

        // Create test backoffice response
        backofficeResponse = new BackofficeResponse();
        backofficeResponse.setId(1);
        backofficeResponse.setAppliedDate(LocalDate.now());
        backofficeResponse.setLoanDetails(loanResponse);
    }

    @Test
    @DisplayName("Should get all backoffice loans with pagination successfully")
    void testGetAllBackOfficeLoanSuccess() {
        // Arrange
        int page = 0;
        int size = 20;
        Pageable pageable = PageRequest.of(page, size);

        List<BackOfficeEntity> backOfficeList = Arrays.asList(backOfficeEntity);
        Page<BackOfficeEntity> pageResult = new PageImpl<>(backOfficeList, pageable, 1);

        List<BackofficeResponse> responseList = Arrays.asList(backofficeResponse);

        when(backOfficeRepository.findAll(pageable)).thenReturn(pageResult);
        when(backOfficeMapper.toResponseList(backOfficeList)).thenReturn(responseList);

        // Act
        List<BackofficeResponse> responses = backOfficeService.getAllBackOfficeLoans(page, size);

        // Assert
        assertNotNull(responses);
        assertEquals(1, responses.size());
        assertEquals(1, responses.get(0).getId());

        // Verify
        verify(backOfficeRepository, times(1)).findAll(pageable);
        verify(backOfficeMapper, times(1)).toResponseList(backOfficeList);
    }

    @Test
    @DisplayName("Should get all backoffice loans with empty list")
    void testGetAllBackOfficeLoanEmptyList() {
        // Arrange
        int page = 0;
        int size = 20;
        Pageable pageable = PageRequest.of(page, size);

        Page<BackOfficeEntity> emptyPage = new PageImpl<>(Arrays.asList(), pageable, 0);

        when(backOfficeRepository.findAll(pageable)).thenReturn(emptyPage);
        when(backOfficeMapper.toResponseList(Arrays.asList())).thenReturn(Arrays.asList());

        // Act
        List<BackofficeResponse> responses = backOfficeService.getAllBackOfficeLoans(page, size);

        // Assert
        assertNotNull(responses);
        assertTrue(responses.isEmpty());

        // Verify
        verify(backOfficeRepository, times(1)).findAll(pageable);
    }

    @Test
    @DisplayName("Should handle negative pagination values gracefully")
    void testGetAllBackOfficeLoanNegativePagination() {
        // Arrange
        int negativePage = -5;
        int negativeSize = -10;
        Pageable pageable = PageRequest.of(Math.max(0, negativePage), Math.max(1, negativeSize));

        List<BackOfficeEntity> backOfficeList = Arrays.asList(backOfficeEntity);
        Page<BackOfficeEntity> pageResult = new PageImpl<>(backOfficeList, pageable, 1);

        List<BackofficeResponse> responseList = Arrays.asList(backofficeResponse);

        when(backOfficeRepository.findAll(pageable)).thenReturn(pageResult);
        when(backOfficeMapper.toResponseList(any(List.class))).thenReturn(responseList);

        // Act
        List<BackofficeResponse> responses = backOfficeService.getAllBackOfficeLoans(negativePage, negativeSize);

        // Assert
        assertEquals(1, responses.size());

        // Verify
        verify(backOfficeRepository, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @DisplayName("Should create backoffice successfully with join validation")
    void testCreateBackOfficeSuccess() {
        // Arrange
        int rowsInserted = 1;
        LocalDate appliedDate = LocalDate.now();

        when(backOfficeRepository.createBackOfficeWithJoin(1L, 1L, appliedDate))
                .thenReturn(rowsInserted);
        when(backOfficeRepository.findByUserIdAndLoanIdWithLoan(1L, 1L))
                .thenReturn(Optional.of(backOfficeEntity));
        when(backOfficeMapper.toResponse(backOfficeEntity))
                .thenReturn(backofficeResponse);

        // Act
        BackofficeResponse response = backOfficeService.createBackOffice(backofficeRequest);

        // Assert
        assertNotNull(response);
        assertEquals(1, response.getId());
        assertEquals(LocalDate.now(), response.getAppliedDate());
        assertNotNull(response.getLoanDetails());

        // Verify
        verify(backOfficeRepository, times(1)).createBackOfficeWithJoin(1L, 1L, appliedDate);
        verify(backOfficeRepository, times(1)).findByUserIdAndLoanIdWithLoan(1L, 1L);
        verify(backOfficeMapper, times(1)).toResponse(backOfficeEntity);
        verifyNoMoreInteractions(backOfficeRepository, backOfficeMapper);
    }

    @Test
    @DisplayName("Should throw exception when backoffice creation fails - join returned 0")
    void testCreateBackOfficeJoinFailed() {
        // Arrange
        int rowsInserted = 0;
        LocalDate appliedDate = LocalDate.now();

        when(backOfficeRepository.createBackOfficeWithJoin(1L, 1L, appliedDate))
                .thenReturn(rowsInserted);

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                backOfficeService.createBackOffice(backofficeRequest),
                "Should throw exception when join query returns 0 rows");

        assertTrue(exception.getMessage().contains("Failed to create BackOffice record"));

        // Verify
        verify(backOfficeRepository, times(1)).createBackOfficeWithJoin(1L, 1L, appliedDate);
        verify(backOfficeRepository, never()).findByUserIdAndLoanIdWithLoan(any(), any());
    }

    @Test
    @DisplayName("Should throw exception when created backoffice cannot be retrieved")
    void testCreateBackOfficeRetrievalFailed() {
        // Arrange
        int rowsInserted = 1;
        LocalDate appliedDate = LocalDate.now();

        when(backOfficeRepository.createBackOfficeWithJoin(1L, 1L, appliedDate))
                .thenReturn(rowsInserted);
        when(backOfficeRepository.findByUserIdAndLoanIdWithLoan(1L, 1L))
                .thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(RuntimeException.class, () ->
                backOfficeService.createBackOffice(backofficeRequest),
                "Should throw exception when retrieval fails after insert");

        // Verify
        verify(backOfficeRepository, times(1)).createBackOfficeWithJoin(1L, 1L, appliedDate);
        verify(backOfficeRepository, times(1)).findByUserIdAndLoanIdWithLoan(1L, 1L);
    }

    @Test
    @DisplayName("Should get backoffice by user ID successfully")
    void testGetBackOfficeByUserSuccess() {
        // Arrange
        when(backOfficeRepository.findByUserIdWithLoan(1L))
                .thenReturn(Optional.of(backOfficeEntity));
        when(backOfficeMapper.toResponse(backOfficeEntity))
                .thenReturn(backofficeResponse);

        // Act
        BackofficeResponse response = backOfficeService.getBackOfficeByUser(1);

        // Assert
        assertNotNull(response);
        assertEquals(1, response.getId());

        // Verify
        verify(backOfficeRepository, times(1)).findByUserIdWithLoan(1L);
        verify(backOfficeMapper, times(1)).toResponse(backOfficeEntity);
    }

    @Test
    @DisplayName("Should throw exception when backoffice not found for user")
    void testGetBackOfficeByUserNotFound() {
        // Arrange
        when(backOfficeRepository.findByUserIdWithLoan(999L))
                .thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                backOfficeService.getBackOfficeByUser(999),
                "Should throw exception when backoffice not found");

        assertTrue(exception.getMessage().contains("BackOffice record not found"));

        // Verify
        verify(backOfficeRepository, times(1)).findByUserIdWithLoan(999L);
        verify(backOfficeMapper, never()).toResponse(any());
    }

    @Test
    @DisplayName("Should update backoffice successfully")
    void testUpdateBackOfficeSuccess() {
        // Arrange
        BackofficeRequest updateRequest = new BackofficeRequest();
        updateRequest.setUserId(1);
        updateRequest.setLoanId(1);
        updateRequest.setAppliedDate(LocalDate.now().plusDays(1));
        updateRequest.setLoanApproval("APPROVED");

        BackOfficeEntity updatedEntity = new BackOfficeEntity();
        updatedEntity.setId(1L);
        updatedEntity.setUser(userEntity);
        updatedEntity.setLoan(loanEntity);
        updatedEntity.setAppliedDate(LocalDate.now().plusDays(1));

        BackofficeResponse updatedResponse = new BackofficeResponse();
        updatedResponse.setId(1);
        updatedResponse.setAppliedDate(LocalDate.now().plusDays(1));

        when(backOfficeRepository.findById(1L))
                .thenReturn(Optional.of(backOfficeEntity));
        when(userRepository.findById(1L))
                .thenReturn(Optional.of(userEntity));
        when(loanRepository.findById(1L))
                .thenReturn(Optional.of(loanEntity));
        when(backOfficeRepository.save(any(BackOfficeEntity.class)))
                .thenReturn(updatedEntity);
        when(backOfficeMapper.toResponse(updatedEntity))
                .thenReturn(updatedResponse);

        // Act
        BackofficeResponse response = backOfficeService.updateBackOffice(1, updateRequest);

        // Assert
        assertNotNull(response);
        assertEquals(1, response.getId());

        // Verify
        verify(backOfficeRepository, times(1)).findById(1L);
        verify(loanService, times(1)).updateLoanApproval(1L, 1L, "APPROVED");
        verify(userRepository, times(1)).findById(1L);
        verify(loanRepository, times(1)).findById(1L);
        verify(backOfficeRepository, times(1)).save(any(BackOfficeEntity.class));
        verify(backOfficeMapper, times(1)).toResponse(updatedEntity);
    }

    @Test
    @DisplayName("Should throw exception when loan approval is null during update")
    void testUpdateBackOfficeNullApproval() {
        // Arrange
        BackofficeRequest invalidRequest = new BackofficeRequest();
        invalidRequest.setUserId(1);
        invalidRequest.setLoanId(1);
        invalidRequest.setAppliedDate(LocalDate.now());
        invalidRequest.setLoanApproval(null);

        // Act & Assert
        assertThrows(RuntimeException.class, () ->
                backOfficeService.updateBackOffice(1, invalidRequest),
                "Should throw exception when loan approval is null");

        // Verify
        verify(backOfficeRepository, never()).findById(any());
    }

    @Test
    @DisplayName("Should throw exception when backoffice not found during update")
    void testUpdateBackOfficeNotFound() {
        // Arrange
        BackofficeRequest updateRequest = new BackofficeRequest();
        updateRequest.setLoanApproval("APPROVED");

        when(backOfficeRepository.findById(999L))
                .thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(RuntimeException.class, () ->
                backOfficeService.updateBackOffice(999, updateRequest),
                "Should throw exception when backoffice not found");

        // Verify
        verify(backOfficeRepository, times(1)).findById(999L);
        verify(loanService, never()).updateLoanApproval(any(), any(), any());
    }

    @Test
    @DisplayName("Should throw exception when user not found during update")
    void testUpdateBackOfficeUserNotFound() {
        // Arrange
        BackofficeRequest updateRequest = new BackofficeRequest();
        updateRequest.setUserId(999);
        updateRequest.setLoanId(1);
        updateRequest.setAppliedDate(LocalDate.now());
        updateRequest.setLoanApproval("APPROVED");

        when(backOfficeRepository.findById(1L))
                .thenReturn(Optional.of(backOfficeEntity));
        when(userRepository.findById(999L))
                .thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                backOfficeService.updateBackOffice(1, updateRequest),
                "Should throw exception when user not found");

        assertTrue(exception.getMessage().contains("User not found"));

        // Verify
        verify(backOfficeRepository, times(1)).findById(1L);
        verify(loanService, times(1)).updateLoanApproval(1L, 999L, "APPROVED");
        verify(userRepository, times(1)).findById(999L);
    }

    @Test
    @DisplayName("Should throw exception when loan not found during update")
    void testUpdateBackOfficeLoanNotFound() {
        // Arrange
        BackofficeRequest updateRequest = new BackofficeRequest();
        updateRequest.setUserId(1);
        updateRequest.setLoanId(999);
        updateRequest.setAppliedDate(LocalDate.now());
        updateRequest.setLoanApproval("APPROVED");

        when(backOfficeRepository.findById(1L))
                .thenReturn(Optional.of(backOfficeEntity));
        when(userRepository.findById(1L))
                .thenReturn(Optional.of(userEntity));
        when(loanRepository.findById(999L))
                .thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                backOfficeService.updateBackOffice(1, updateRequest),
                "Should throw exception when loan not found");

        assertTrue(exception.getMessage().contains("Loan not found"));

        // Verify
        verify(backOfficeRepository, times(1)).findById(1L);
        verify(loanService, times(1)).updateLoanApproval(999L, 1L, "APPROVED");
        verify(userRepository, times(1)).findById(1L);
        verify(loanRepository, times(1)).findById(999L);
    }

    @Test
    @DisplayName("Should update backoffice with APPROVED status")
    void testUpdateBackOfficeApprovedStatus() {
        // Arrange
        BackofficeRequest approvalRequest = new BackofficeRequest();
        approvalRequest.setUserId(1);
        approvalRequest.setLoanId(1);
        approvalRequest.setAppliedDate(LocalDate.now());
        approvalRequest.setLoanApproval("APPROVED");

        when(backOfficeRepository.findById(1L))
                .thenReturn(Optional.of(backOfficeEntity));
        when(userRepository.findById(1L))
                .thenReturn(Optional.of(userEntity));
        when(loanRepository.findById(1L))
                .thenReturn(Optional.of(loanEntity));
        when(backOfficeRepository.save(any(BackOfficeEntity.class)))
                .thenReturn(backOfficeEntity);
        when(backOfficeMapper.toResponse(any(BackOfficeEntity.class)))
                .thenReturn(backofficeResponse);

        // Act
        BackofficeResponse response = backOfficeService.updateBackOffice(1, approvalRequest);

        // Assert
        assertNotNull(response);

        // Verify
        verify(loanService, times(1)).updateLoanApproval(1L, 1L, "APPROVED");
    }

    @Test
    @DisplayName("Should update backoffice with REJECTED status")
    void testUpdateBackOfficeRejectedStatus() {
        // Arrange
        BackofficeRequest rejectionRequest = new BackofficeRequest();
        rejectionRequest.setUserId(1);
        rejectionRequest.setLoanId(1);
        rejectionRequest.setAppliedDate(LocalDate.now());
        rejectionRequest.setLoanApproval("REJECTED");

        when(backOfficeRepository.findById(1L))
                .thenReturn(Optional.of(backOfficeEntity));
        when(userRepository.findById(1L))
                .thenReturn(Optional.of(userEntity));
        when(loanRepository.findById(1L))
                .thenReturn(Optional.of(loanEntity));
        when(backOfficeRepository.save(any(BackOfficeEntity.class)))
                .thenReturn(backOfficeEntity);
        when(backOfficeMapper.toResponse(any(BackOfficeEntity.class)))
                .thenReturn(backofficeResponse);

        // Act
        BackofficeResponse response = backOfficeService.updateBackOffice(1, rejectionRequest);

        // Assert
        assertNotNull(response);

        // Verify
        verify(loanService, times(1)).updateLoanApproval(1L, 1L, "REJECTED");
    }

    @Test
    @DisplayName("Should get all backoffice loans with multiple records")
    void testGetAllBackOfficeLoanMultipleRecords() {
        // Arrange
        int page = 0;
        int size = 20;
        Pageable pageable = PageRequest.of(page, size);

        BackOfficeEntity backOfficeEntity2 = new BackOfficeEntity();
        backOfficeEntity2.setId(2L);
        backOfficeEntity2.setUser(userEntity);
        backOfficeEntity2.setLoan(loanEntity);
        backOfficeEntity2.setAppliedDate(LocalDate.now().minusDays(1));

        List<BackOfficeEntity> backOfficeList = Arrays.asList(backOfficeEntity, backOfficeEntity2);
        Page<BackOfficeEntity> pageResult = new PageImpl<>(backOfficeList, pageable, 2);

        BackofficeResponse backofficeResponse2 = new BackofficeResponse();
        backofficeResponse2.setId(2);
        backofficeResponse2.setAppliedDate(LocalDate.now().minusDays(1));

        List<BackofficeResponse> responseList = Arrays.asList(backofficeResponse, backofficeResponse2);

        when(backOfficeRepository.findAll(pageable)).thenReturn(pageResult);
        when(backOfficeMapper.toResponseList(backOfficeList)).thenReturn(responseList);

        // Act
        List<BackofficeResponse> responses = backOfficeService.getAllBackOfficeLoans(page, size);

        // Assert
        assertNotNull(responses);
        assertEquals(2, responses.size());
        assertEquals(1, responses.get(0).getId());
        assertEquals(2, responses.get(1).getId());

        // Verify
        verify(backOfficeRepository, times(1)).findAll(pageable);
        verify(backOfficeMapper, times(1)).toResponseList(backOfficeList);
    }

    @Test
    @DisplayName("Should handle repository exception during getAllBackOfficeLoans")
    void testGetAllBackOfficeLoanRepositoryException() {
        // Arrange
        int page = 0;
        int size = 20;
        Pageable pageable = PageRequest.of(page, size);

        when(backOfficeRepository.findAll(pageable))
                .thenThrow(new RuntimeException("Database error"));

        // Act & Assert
        assertThrows(RuntimeException.class, () ->
                backOfficeService.getAllBackOfficeLoans(page, size));

        // Verify
        verify(backOfficeRepository, times(1)).findAll(pageable);
    }

    @Test
    @DisplayName("Should handle mapper exception during creation")
    void testCreateBackOfficeMapperException() {
        // Arrange
        int rowsInserted = 1;
        LocalDate appliedDate = LocalDate.now();

        when(backOfficeRepository.createBackOfficeWithJoin(1L, 1L, appliedDate))
                .thenReturn(rowsInserted);
        when(backOfficeRepository.findByUserIdAndLoanIdWithLoan(1L, 1L))
                .thenReturn(Optional.of(backOfficeEntity));
        when(backOfficeMapper.toResponse(backOfficeEntity))
                .thenThrow(new RuntimeException("Mapping error"));

        // Act & Assert
        assertThrows(RuntimeException.class, () ->
                backOfficeService.createBackOffice(backofficeRequest));

        // Verify
        verify(backOfficeRepository, times(1)).createBackOfficeWithJoin(1L, 1L, appliedDate);
        verify(backOfficeRepository, times(1)).findByUserIdAndLoanIdWithLoan(1L, 1L);
    }

    @Test
    @DisplayName("Should update backoffice with future applied date")
    void testUpdateBackOfficeWithFutureDate() {
        // Arrange
        LocalDate futureDate = LocalDate.now().plusDays(30);
        BackofficeRequest futureRequest = new BackofficeRequest();
        futureRequest.setUserId(1);
        futureRequest.setLoanId(1);
        futureRequest.setAppliedDate(futureDate);
        futureRequest.setLoanApproval("PENDING");

        BackOfficeEntity futureEntity = new BackOfficeEntity();
        futureEntity.setId(1L);
        futureEntity.setUser(userEntity);
        futureEntity.setLoan(loanEntity);
        futureEntity.setAppliedDate(futureDate);

        BackofficeResponse futureResponse = new BackofficeResponse();
        futureResponse.setId(1);
        futureResponse.setAppliedDate(futureDate);

        when(backOfficeRepository.findById(1L))
                .thenReturn(Optional.of(backOfficeEntity));
        when(userRepository.findById(1L))
                .thenReturn(Optional.of(userEntity));
        when(loanRepository.findById(1L))
                .thenReturn(Optional.of(loanEntity));
        when(backOfficeRepository.save(any(BackOfficeEntity.class)))
                .thenReturn(futureEntity);
        when(backOfficeMapper.toResponse(futureEntity))
                .thenReturn(futureResponse);

        // Act
        BackofficeResponse response = backOfficeService.updateBackOffice(1, futureRequest);

        // Assert
        assertNotNull(response);
        assertEquals(futureDate, response.getAppliedDate());

        // Verify
        verify(backOfficeRepository, times(1)).save(any(BackOfficeEntity.class));
    }
}
