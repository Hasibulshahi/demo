package com.example.demo.repository;

import com.example.demo.entity.BackOfficeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Optional;

public interface BackOfficeRepository extends JpaRepository<BackOfficeEntity, Long> {
    
    @Query("SELECT b FROM BackOfficeEntity b WHERE b.user.id = :userId")
    Optional<BackOfficeEntity> findByUserId(@Param("userId") Long userId);

    @Query("SELECT b FROM BackOfficeEntity b JOIN FETCH b.loan l JOIN FETCH b.user u WHERE u.id = :userId")
    Optional<BackOfficeEntity> findByUserIdWithLoan(@Param("userId") Long userId);

    @Query("SELECT b FROM BackOfficeEntity b JOIN FETCH b.loan l JOIN FETCH b.user u WHERE u.id = :userId AND l.id = :loanId")
    Optional<BackOfficeEntity> findByUserIdAndLoanIdWithLoan(@Param("userId") Long userId, @Param("loanId") Long loanId);
    
    @Modifying
    @Transactional
    @Query(value = "INSERT INTO back_office_entity (user_id, loan_id, applied_date) " +
                   "SELECT u.id, l.id, :appliedDate " +
                   "FROM user_entity u " +
                   "INNER JOIN loan_entity l ON u.id = l.user_id " +
                   "WHERE u.id = :userId AND l.id = :loanId", nativeQuery = true)
    int createBackOfficeWithJoin(@Param("userId") Long userId, @Param("loanId") Long loanId, @Param("appliedDate") LocalDate appliedDate);
}

