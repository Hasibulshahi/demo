package com.example.demo.service;

import com.example.demo.generated.model.ExternalApiResponse;

public interface ExternalApiService {
    /**
     * Call an external API and return the response
     *
     * @param url The URL to call
     * @return ExternalApiResponse with status code and response body
     */
    ExternalApiResponse callExternalApi(String url);
}
