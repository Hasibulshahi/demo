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
import com.example.demo.service.BackOfficeService;
import com.example.demo.service.LoanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BackOfficeServiceImpl implements BackOfficeService {

        private final BackOfficeRepository backOfficeRepository;

        private final UserRepository userRepository;

        private final LoanRepository loanRepository;

        private final BackOfficeMapper backOfficeMapper;

        private final LoanService loanService;

        public BackOfficeServiceImpl(BackOfficeRepository backOfficeRepository,
                                                                 UserRepository userRepository,
                                                                 LoanRepository loanRepository,
                                                                 BackOfficeMapper backOfficeMapper,
                                                                 LoanService loanService) {
                this.backOfficeRepository = backOfficeRepository;
                this.userRepository = userRepository;
                this.loanRepository = loanRepository;
                this.backOfficeMapper = backOfficeMapper;
                this.loanService = loanService;
        }

    @Override
        public List<BackofficeResponse> getAllBackOfficeLoans(int page, int size) {
                org.springframework.data.domain.Pageable pageable = org.springframework.data.domain.PageRequest.of(Math.max(0, page), Math.max(1, size));
                org.springframework.data.domain.Page<BackOfficeEntity> pageResult = backOfficeRepository.findAll(pageable);
                java.util.List<BackOfficeEntity> entities = pageResult.getContent();
                return backOfficeMapper.toResponseList(entities);
        }

    @Override
    public BackofficeResponse createBackOffice(BackofficeRequest request) {
        // Use custom query to insert into backoffice with join
        // This will only insert if user exists AND loan exists AND loan belongs to user
        int rowsInserted = backOfficeRepository.createBackOfficeWithJoin(
                request.getUserId().longValue(),
                request.getLoanId().longValue(),
                request.getAppliedDate()
        );
        
        // If join produced no results, throw exception
        if (rowsInserted <= 0) {
            throw new RuntimeException("Failed to create BackOffice record. " +
                    "User not found, Loan not found, or Loan does not belong to this user.");
        }

        // Fetch and return the created record using join-fetch to include loan details
        BackOfficeEntity entity = backOfficeRepository
                .findByUserIdAndLoanIdWithLoan(request.getUserId().longValue(), request.getLoanId().longValue())
                .orElseThrow(() -> new RuntimeException("Failed to retrieve created BackOffice record"));

        return backOfficeMapper.toResponse(entity);
    }

    @Override
    public BackofficeResponse getBackOfficeByUser(Integer userId) {
        BackOfficeEntity entity = backOfficeRepository.findByUserIdWithLoan(userId.longValue())
                .orElseThrow(() -> new RuntimeException("BackOffice record not found for user ID: " + userId));
        return backOfficeMapper.toResponse(entity);
    }

    @Override
    public BackofficeResponse updateBackOffice(Integer id, BackofficeRequest request) {
        if (request.getLoanApproval() == null){
            throw new RuntimeException("loan Approval can't be null");
        }
        BackOfficeEntity existing = backOfficeRepository.findById(id.longValue())
                .orElseThrow(() -> new RuntimeException("BackOffice record not found for user ID: " + id));

        loanService.updateLoanApproval(request.getLoanId().longValue(), request.getUserId().longValue(), request.getLoanApproval());

        UserEntity user = userRepository.findById(request.getUserId().longValue())
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + request.getUserId()));
        
        LoanEntity loan = loanRepository.findById(request.getLoanId().longValue())
                .orElseThrow(() -> new RuntimeException("Loan not found with ID: " + request.getLoanId()));

        loan.setLoanApproval(request.getLoanApproval());

        existing.setUser(user);
        existing.setLoan(loan);
        existing.setAppliedDate(request.getAppliedDate());
        
        BackOfficeEntity updated = backOfficeRepository.save(existing);
        return backOfficeMapper.toResponse(updated);
    }
}
