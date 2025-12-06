package com.example.demo.controller;

import com.example.demo.generated.model.ExternalApiResponse;
import com.example.demo.service.ExternalApiService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.OffsetDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ExternalApiController Tests")
class ExternalApiControllerTest {

    @Mock
    private ExternalApiService externalApiService;

    @InjectMocks
    private ExternalApiController externalApiController;

    private ExternalApiResponse externalApiResponse;

    @BeforeEach
    void setUp() {
        // Create test external API response
        externalApiResponse = new ExternalApiResponse();
        externalApiResponse.setStatusCode(200);
        externalApiResponse.setUrl("https://www.google.com");
        externalApiResponse.setResponseBody("<html>Google homepage</html>");
        externalApiResponse.setTimestamp(OffsetDateTime.now());
    }

    @Test
    @DisplayName("Should call external API successfully")
    void testCallExternalApiSuccess() {
        // Arrange
        String url = "https://www.google.com";
        when(externalApiService.callExternalApi(url))
                .thenReturn(externalApiResponse);

        // Act
        ResponseEntity<ExternalApiResponse> response = 
                externalApiController.callExternalApi(url);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(200, response.getBody().getStatusCode());
        assertEquals(url, response.getBody().getUrl());
        assertNotNull(response.getBody().getResponseBody());

        // Verify
        verify(externalApiService, times(1)).callExternalApi(url);
        verifyNoMoreInteractions(externalApiService);
    }

    @Test
    @DisplayName("Should call external API with response body")
    void testCallExternalApiWithResponseBody() {
        // Arrange
        String url = "https://www.example.com";
        ExternalApiResponse response = new ExternalApiResponse();
        response.setStatusCode(200);
        response.setUrl(url);
        response.setResponseBody("<html>Example page</html>");
        response.setTimestamp(OffsetDateTime.now());

        when(externalApiService.callExternalApi(url))
                .thenReturn(response);

        // Act
        ResponseEntity<ExternalApiResponse> result = 
                externalApiController.callExternalApi(url);

        // Assert
        assertNotNull(result.getBody());
        assertEquals("<html>Example page</html>", result.getBody().getResponseBody());
        assertEquals(url, result.getBody().getUrl());

        // Verify
        verify(externalApiService, times(1)).callExternalApi(url);
    }

    @Test
    @DisplayName("Should handle invalid URL with 400 Bad Request")
    void testCallExternalApiInvalidUrl() {
        // Arrange
        String invalidUrl = "invalid-url";
        when(externalApiService.callExternalApi(invalidUrl))
                .thenThrow(new IllegalArgumentException("Invalid URL format"));

        // Act
        ResponseEntity<ExternalApiResponse> response = 
                externalApiController.callExternalApi(invalidUrl);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNull(response.getBody());

        // Verify
        verify(externalApiService, times(1)).callExternalApi(invalidUrl);
        verifyNoMoreInteractions(externalApiService);
    }

    @Test
    @DisplayName("Should handle malformed URL format")
    void testCallExternalApiMalformedUrl() {
        // Arrange
        String malformedUrl = "ht!tp://invalid.com";
        when(externalApiService.callExternalApi(malformedUrl))
                .thenThrow(new IllegalArgumentException("Malformed URL"));

        // Act
        ResponseEntity<ExternalApiResponse> response = 
                externalApiController.callExternalApi(malformedUrl);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());

        // Verify
        verify(externalApiService, times(1)).callExternalApi(malformedUrl);
    }

    @Test
    @DisplayName("Should handle service exception with 500 Internal Server Error")
    void testCallExternalApiServiceException() {
        // Arrange
        String url = "https://www.example.com";
        when(externalApiService.callExternalApi(url))
                .thenThrow(new RuntimeException("Network error"));

        // Act
        ResponseEntity<ExternalApiResponse> response = 
                externalApiController.callExternalApi(url);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNull(response.getBody());

        // Verify
        verify(externalApiService, times(1)).callExternalApi(url);
        verifyNoMoreInteractions(externalApiService);
    }

    @Test
    @DisplayName("Should handle timeout exception")
    void testCallExternalApiTimeout() {
        // Arrange
        String url = "https://www.slowserver.com";
        when(externalApiService.callExternalApi(url))
                .thenThrow(new RuntimeException("Connection timeout"));

        // Act
        ResponseEntity<ExternalApiResponse> response = 
                externalApiController.callExternalApi(url);

        // Assert
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());

        // Verify
        verify(externalApiService, times(1)).callExternalApi(url);
    }

    @Test
    @DisplayName("Should call external API with HTTPS URL")
    void testCallExternalApiWithHttpsUrl() {
        // Arrange
        String httpsUrl = "https://www.secure.com";
        ExternalApiResponse response = new ExternalApiResponse();
        response.setStatusCode(200);
        response.setUrl(httpsUrl);
        response.setResponseBody("<html>Secure site</html>");

        when(externalApiService.callExternalApi(httpsUrl))
                .thenReturn(response);

        // Act
        ResponseEntity<ExternalApiResponse> result = 
                externalApiController.callExternalApi(httpsUrl);

        // Assert
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(httpsUrl, result.getBody().getUrl());

        // Verify
        verify(externalApiService, times(1)).callExternalApi(httpsUrl);
    }

    @Test
    @DisplayName("Should call external API with HTTP URL")
    void testCallExternalApiWithHttpUrl() {
        // Arrange
        String httpUrl = "http://www.example.com";
        ExternalApiResponse response = new ExternalApiResponse();
        response.setStatusCode(200);
        response.setUrl(httpUrl);
        response.setResponseBody("<html>Example</html>");

        when(externalApiService.callExternalApi(httpUrl))
                .thenReturn(response);

        // Act
        ResponseEntity<ExternalApiResponse> result = 
                externalApiController.callExternalApi(httpUrl);

        // Assert
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(httpUrl, result.getBody().getUrl());

        // Verify
        verify(externalApiService, times(1)).callExternalApi(httpUrl);
    }

    @Test
    @DisplayName("Should call external API with complex URL")
    void testCallExternalApiWithComplexUrl() {
        // Arrange
        String complexUrl = "https://api.example.com/v1/users?id=123&name=test";
        ExternalApiResponse response = new ExternalApiResponse();
        response.setStatusCode(200);
        response.setUrl(complexUrl);
        response.setResponseBody("{\"id\":123,\"name\":\"test\"}");

        when(externalApiService.callExternalApi(complexUrl))
                .thenReturn(response);

        // Act
        ResponseEntity<ExternalApiResponse> result = 
                externalApiController.callExternalApi(complexUrl);

        // Assert
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(complexUrl, result.getBody().getUrl());
        assertEquals("{\"id\":123,\"name\":\"test\"}", result.getBody().getResponseBody());

        // Verify
        verify(externalApiService, times(1)).callExternalApi(complexUrl);
    }

    @Test
    @DisplayName("Should include timestamp in response")
    void testCallExternalApiIncludesTimestamp() {
        // Arrange
        String url = "https://www.example.com";
        OffsetDateTime testTime = OffsetDateTime.now();
        ExternalApiResponse response = new ExternalApiResponse();
        response.setStatusCode(200);
        response.setUrl(url);
        response.setResponseBody("content");
        response.setTimestamp(testTime);

        when(externalApiService.callExternalApi(url))
                .thenReturn(response);

        // Act
        ResponseEntity<ExternalApiResponse> result = 
                externalApiController.callExternalApi(url);

        // Assert
        assertNotNull(result.getBody().getTimestamp());
        assertEquals(testTime, result.getBody().getTimestamp());

        // Verify
        verify(externalApiService, times(1)).callExternalApi(url);
    }

    @Test
    @DisplayName("Should handle empty response body")
    void testCallExternalApiEmptyResponseBody() {
        // Arrange
        String url = "https://www.example.com";
        ExternalApiResponse response = new ExternalApiResponse();
        response.setStatusCode(200);
        response.setUrl(url);
        response.setResponseBody("");
        response.setTimestamp(OffsetDateTime.now());

        when(externalApiService.callExternalApi(url))
                .thenReturn(response);

        // Act
        ResponseEntity<ExternalApiResponse> result = 
                externalApiController.callExternalApi(url);

        // Assert
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals("", result.getBody().getResponseBody());

        // Verify
        verify(externalApiService, times(1)).callExternalApi(url);
    }

    @Test
    @DisplayName("Should handle large response body")
    void testCallExternalApiLargeResponseBody() {
        // Arrange
        String url = "https://www.example.com";
        StringBuilder largeBody = new StringBuilder();
        for (int i = 0; i < 1000; i++) {
            largeBody.append("Large response content line ").append(i).append("\n");
        }

        ExternalApiResponse response = new ExternalApiResponse();
        response.setStatusCode(200);
        response.setUrl(url);
        response.setResponseBody(largeBody.toString());
        response.setTimestamp(OffsetDateTime.now());

        when(externalApiService.callExternalApi(url))
                .thenReturn(response);

        // Act
        ResponseEntity<ExternalApiResponse> result = 
                externalApiController.callExternalApi(url);

        // Assert
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertTrue(result.getBody().getResponseBody().length() > 10000);

        // Verify
        verify(externalApiService, times(1)).callExternalApi(url);
    }

    @Test
    @DisplayName("Should handle null pointer exception")
    void testCallExternalApiNullPointerException() {
        // Arrange
        String url = "https://www.example.com";
        when(externalApiService.callExternalApi(url))
                .thenThrow(new NullPointerException("Unexpected null value"));

        // Act
        ResponseEntity<ExternalApiResponse> response = 
                externalApiController.callExternalApi(url);

        // Assert
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());

        // Verify
        verify(externalApiService, times(1)).callExternalApi(url);
    }

    @Test
    @DisplayName("Should handle different exception types")
    void testCallExternalApiVariousExceptions() {
        // Test with different exception types
        String[] urls = {"https://url1.com", "https://url2.com", "https://url3.com"};
        RuntimeException[] exceptions = {
                new RuntimeException("Network error"),
                new IllegalArgumentException("Invalid URL"),
                new RuntimeException("General error")
        };

        for (int i = 0; i < urls.length; i++) {
            // Reset mocks for each iteration
            reset(externalApiService);

            String url = urls[i];
            RuntimeException exception = exceptions[i];

            if (exception instanceof IllegalArgumentException) {
                when(externalApiService.callExternalApi(url))
                        .thenThrow(exception);

                // Act
                ResponseEntity<ExternalApiResponse> response = 
                        externalApiController.callExternalApi(url);

                // Assert
                assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
            } else {
                when(externalApiService.callExternalApi(url))
                        .thenThrow(exception);

                // Act
                ResponseEntity<ExternalApiResponse> response = 
                        externalApiController.callExternalApi(url);

                // Assert
                assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
            }
        }
    }
}
