package com.example.demo.service.impl;

import com.example.demo.generated.model.ExternalApiResponse;
import com.example.demo.service.ExternalApiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.RestClientException;

import java.net.MalformedURLException;
import java.net.URL;
import java.time.OffsetDateTime;

@Service
public class ExternalApiServiceImpl implements ExternalApiService {

    @Autowired
    private RestTemplate restTemplate;

    @Override
    public ExternalApiResponse callExternalApi(String urlString) {
        // Validate URL
        try {
            new URL(urlString);
        } catch (MalformedURLException e) {
            throw new IllegalArgumentException("Invalid URL: " + urlString, e);
        }

        try {
            // Call the external API
            String responseBody = restTemplate.getForObject(urlString, String.class);
            
            ExternalApiResponse response = new ExternalApiResponse();
            response.setStatusCode(200);
            response.setResponseBody(responseBody);
            response.setUrl(urlString);
            response.setTimestamp(OffsetDateTime.now());
            
            return response;
        } catch (RestClientException e) {
            throw new RuntimeException("Error calling external API: " + e.getMessage(), e);
        }
    }
}
