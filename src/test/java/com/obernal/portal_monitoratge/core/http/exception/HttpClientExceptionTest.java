package com.obernal.portal_monitoratge.core.http.exception;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class HttpClientExceptionTest {

    @Test
    void testHttpClientException() {
        // Arrange
        Integer statusCode = 404;
        String message = "Not Found";

        // Act
        HttpClientException exception = new HttpClientException(statusCode, message);

        // Assert
        assertEquals(statusCode, exception.getStatusCode());
        assertEquals(message, exception.getMessage());
    }

}