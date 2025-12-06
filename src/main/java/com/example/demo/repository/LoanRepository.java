package com.example.demo.repository;

import com.example.demo.entity.LoanEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface LoanRepository extends JpaRepository<LoanEntity, Long> {
    LoanEntity findLoanById(Long id);
    Page<LoanEntity> findAllByUser_Id(Long userId, Pageable pageable);
    
    @Query("SELECT l FROM LoanEntity l WHERE " +
           "(:username IS NULL OR l.user.name LIKE %:username%) AND " +
           "(:loanType IS NULL OR l.loanType LIKE %:loanType%)")
    Page<LoanEntity> searchLoans(@Param("username") String username, @Param("loanType") String loanType, Pageable pageable);
    
    @Modifying
    @Transactional
    @Query("UPDATE LoanEntity l SET l.loanApproval = :loanApproval WHERE l.id = :loanId AND l.user.id = :userId")
    int updateLoanApproval(@Param("loanId") Long loanId, @Param("userId") Long userId, @Param("loanApproval") String loanApproval);
}
