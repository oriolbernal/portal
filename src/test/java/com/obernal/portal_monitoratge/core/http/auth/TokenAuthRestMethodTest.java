package com.obernal.portal_monitoratge.core.http.auth;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TokenAuthRestMethodTest {

    @Test
    void getAuthorization() {
        // Arrange
        String email = "test@example.com";
        String token = "mytoken";
        TokenAuthRestMethod authMethod = new TokenAuthRestMethod(email, token);

        // Act
        String authorization = authMethod.getAuthorization();

        // Assert
        String expectedAuth = "Basic dGVzdEBleGFtcGxlLmNvbS90b2tlbjpteXRva2Vu"; // Base64 encoded "test@example.com/token:mytoken"
        assertEquals(expectedAuth, authorization);
    }
}