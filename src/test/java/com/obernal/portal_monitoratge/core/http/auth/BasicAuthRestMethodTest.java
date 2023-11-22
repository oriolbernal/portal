package com.obernal.portal_monitoratge.core.http.auth;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BasicAuthRestMethodTest {

    @Test
    void getAuthorization() {
        // Arrange
        String username = "testuser";
        String password = "testpassword";
        BasicAuthRestMethod authMethod = new BasicAuthRestMethod(username, password);

        // Act
        String authorization = authMethod.getAuthorization();

        // Assert
        String expectedAuth = "Basic dGVzdHVzZXI6dGVzdHBhc3N3b3Jk"; // Base64 encoded "testuser:testpassword"
        assertEquals(expectedAuth, authorization);
    }

}