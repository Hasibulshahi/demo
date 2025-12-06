package com.example.demo.controller;

import com.example.demo.generated.api.ExternalApi;
import com.example.demo.generated.model.ExternalApiResponse;
import com.example.demo.service.ExternalApiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ExternalApiController implements ExternalApi {

    @Autowired
    private ExternalApiService externalApiService;

    @Override
    public ResponseEntity<ExternalApiResponse> callExternalApi(String url) {
        try {
            ExternalApiResponse response = externalApiService.callExternalApi(url);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
