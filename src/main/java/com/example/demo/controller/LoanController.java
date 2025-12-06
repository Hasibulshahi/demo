package com.example.demo.controller;

import com.example.demo.generated.api.LoanApi;
import com.example.demo.generated.model.LoanRequest;
import com.example.demo.generated.model.LoanResponse;
import java.util.List;
import com.example.demo.service.LoanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import java.util.Map;

@RestController
public class LoanController implements LoanApi {

    private final LoanService loanService;

    public LoanController(LoanService loanService) {
        this.loanService = loanService;
    }

    @Override
    public ResponseEntity<LoanResponse> applyLoan(LoanRequest request) {
        return ResponseEntity.ok(loanService.applyLoan(request));
    }

    @Override
    public ResponseEntity<List<LoanResponse>> getLoansByUserId(
            Long userId,
            @jakarta.validation.constraints.Min(0L) @jakarta.validation.Valid Integer page,
            @jakarta.validation.constraints.Min(1L) @jakarta.validation.constraints.Max(100L) @jakarta.validation.Valid Integer size) {
        int pageNum = page != null ? page : 0;
        int pageSize = size != null ? size : 20;
        return ResponseEntity.ok(loanService.getLoansByUserId(userId, pageNum, pageSize));
    }
}

