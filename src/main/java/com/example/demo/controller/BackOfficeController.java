package com.example.demo.controller;

import com.example.demo.generated.api.BackOfficeOperationApi;
import com.example.demo.generated.model.BackofficeRequest;
import com.example.demo.generated.model.BackofficeResponse;
import com.example.demo.service.BackOfficeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.RequestParam;

@RestController
public class BackOfficeController implements BackOfficeOperationApi {

    @Autowired
    private BackOfficeService backOfficeService;

    @Override
    public ResponseEntity<List<BackofficeResponse>> getAllBackOfficeLoans(
            @Min(0L) @Valid Integer page,
            @Min(1L) @Max(100L) @Valid Integer size) {
        // Use provided page/size parameters or defaults
        int pageNum = page != null ? page : 0;
        int pageSize = size != null ? size : 20;
        return ResponseEntity.ok(backOfficeService.getAllBackOfficeLoans(pageNum, pageSize));
    }

    @Override
    public ResponseEntity<BackofficeResponse> createBackOffice(BackofficeRequest backofficeRequest) {
        return ResponseEntity.status(HttpStatus.CREATED).body(backOfficeService.createBackOffice(backofficeRequest));
    }

    @Override
    public ResponseEntity<BackofficeResponse> getBackOfficeByUser(Integer id) {
        return ResponseEntity.ok(backOfficeService.getBackOfficeByUser(id));
    }

    @Override
    public ResponseEntity<BackofficeResponse> updateBackOffice(Integer id, BackofficeRequest backofficeRequest) {
        return ResponseEntity.ok(backOfficeService.updateBackOffice(id, backofficeRequest));
    }
}
