package com.obernal.portal_monitoratge.model.monitor.impl.ssl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.InputStream;
import java.net.http.HttpClient;
import java.util.HashSet;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.*;

class SslMonitorTest {

    private Properties testProperties;

    @BeforeEach
    public void setUp() throws Exception {
        testProperties = loadTestProperties();
    }

    @Test
    void error_if_http() {
        var monitor = createMonitor(
                "http://github.com/",
                new String[]{"TLSv1", "TLSv1.1", "TLSv1.2", "TLSv1.3"},
                false
        );
        var execution = monitor.run();
        assertEquals("Connection is not ssl!", execution.getErrorMessage());
    }

    @Test
    void sslCertificate_expired_shouldNot_returnError() {
        var monitor = createMonitor(
                "https://expired.badssl.com/",
                new String[]{"TLSv1", "TLSv1.1", "TLSv1.2", "TLSv1.3"},
                false
        );
        assertDoesNotThrow(monitor::run);
    }

    private Properties loadTestProperties() throws Exception {
        Properties properties = new Properties();
        try (InputStream input = getClass().getClassLoader().getResourceAsStream("application.properties")) {
            if (input == null) {
                throw new RuntimeException("Unable to find test-application.properties");
            }
            properties.load(input);
        }
        return properties;
    }

    private SslMonitor createMonitor(String endpoint, String[] sslProtocols, boolean clientCertificate) {
        return new SslMonitor(
                new SslMetadata(
                        "name",
                        "desc",
                        "cron",
                        "service",
                        new HashSet<>(),
                        "docs",
                        endpoint,
                        1,
                        HttpClient.Version.HTTP_2,
                        HttpClient.Redirect.NEVER,
                        sslProtocols,
                        clientCertificate,
                        0
                ),
                testProperties);
    }

}