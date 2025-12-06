package com.example.demo.service.impl;

import com.example.demo.entity.LoanEntity;
import com.example.demo.entity.UserEntity;
import com.example.demo.generated.model.LoanRequest;
import com.example.demo.generated.model.LoanResponse;
import com.example.demo.mapper.LoanMapper;
import com.example.demo.repository.LoanRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.LoanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LoanServiceImpl implements LoanService {

    private final LoanRepository loanRepository;

    private final UserRepository userRepository;

    private final LoanMapper loanMapper;

    public LoanServiceImpl(LoanRepository loanRepository, UserRepository userRepository, LoanMapper loanMapper) {
        this.loanRepository = loanRepository;
        this.userRepository = userRepository;
        this.loanMapper = loanMapper;
    }

    @Override
    public LoanResponse applyLoan(LoanRequest request) {
        UserEntity existingUser = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        LoanEntity loanEntity = loanMapper.toEntity(request);
        loanEntity.setUser(existingUser);  // Set the user entity
        Double payableLoanInterest = (request.getInterestRate()/100)* request.getAmount();
        loanEntity.setPayablelLoanAmount(payableLoanInterest+request.getAmount());
        loanRepository.save(loanEntity);
        return loanMapper.toResponse(loanEntity);
    }

    @Override
    public List<LoanResponse> getAllLoans() {
        return loanMapper.toLoansResponse(loanRepository.findAll());
    }

    @Override
    public LoanResponse getLoanById(Long loanId) {
        return loanMapper.toResponse(loanRepository.findLoanById(loanId));
    }

    @Override
    public List<LoanResponse> getLoansByUserId(Long userId, int page, int size) {
        org.springframework.data.domain.Pageable pageable = org.springframework.data.domain.PageRequest.of(Math.max(0, page), Math.max(1, size));
        org.springframework.data.domain.Page<LoanEntity> pageResult = loanRepository.findAllByUser_Id(userId, pageable);
        return loanMapper.toLoansResponse(pageResult.getContent());
    }

    @Override
    public List<LoanResponse> searchLoans(String username, String loanType, int page, int size) {
        org.springframework.data.domain.Pageable pageable = org.springframework.data.domain.PageRequest.of(Math.max(0, page), Math.max(1, size));
        org.springframework.data.domain.Page<LoanEntity> pageResult = loanRepository.searchLoans(username, loanType, pageable);
        return loanMapper.toLoansResponse(pageResult.getContent());
    }

    @Override
    public int updateLoanApproval(Long loanId, Long userId, String loanApproval) {
        return loanRepository.updateLoanApproval(loanId, userId, loanApproval);
    }
}
