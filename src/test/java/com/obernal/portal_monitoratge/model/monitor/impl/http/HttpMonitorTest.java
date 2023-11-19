package com.obernal.portal_monitoratge.model.monitor.impl.http;

import com.obernal.portal_monitoratge.model.monitor.MonitorMetadata;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.InputStream;
import java.net.http.HttpClient;
import java.util.HashSet;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.*;

class HttpMonitorTest {

    private Properties testProperties;

    @BeforeEach
    public void setUp() throws Exception {
        testProperties = loadTestProperties();
    }

    @Test
    void connect_with_http() {
        var monitor = createMonitor(
                "http://github.com",
                new String[]{"TLSv1.2", "TLSv1.3"},
                false,
                301
        );
        var execution = monitor.run();
        assertFalse(execution.isError());
        assertFalse(execution.isAlert());
    }

    @Test
    void connect_with_https() {
        var monitor = createMonitor(
                "https://github.com",
                new String[]{"TLSv1.2", "TLSv1.3"},
                false,
                200
        );
        var execution = monitor.run();
        assertFalse(execution.isError());
        assertFalse(execution.isAlert());
    }

    @Test
    void error_if_timeout() {
        var monitor = createMonitor(
                "https://10.255.255.1/", // non-routable ip adress
                new String[]{"TLSv1.2", "TLSv1.3"},
                false,
                200);
        var execution = monitor.run();
        assertEquals("Connection Timeout: HTTP connect timed out", execution.getErrorMessage());
    }

    @Test
    void error_if_tlsProtocol_notConfigured() {
        var monitor = createMonitor(
                "https://github.com/",
                new String[]{},
                false,
                200);
        var execution = monitor.run();
        assertEquals("SSL Connection problem: No appropriate protocol (protocol is disabled or cipher suites are inappropriate)", execution.getErrorMessage());
    }

    @Test
    void error_if_tlsProtocol_nonExistent() {
        var monitor = createMonitor(
                "https://httpbin.org/anything",
                new String[]{"A"},
                false,
                200
        );
        var execution = monitor.run();
        assertEquals("Nonexistent SSLProtocol: Unsupported protocol: A", execution.getErrorMessage());
    }

    @Test
    void error_if_tlsProtocol_unsupported() {
        var monitor = createMonitor(
                "https://tls-v1-2.badssl.com:1012/",
                new String[]{"TLSv1.3"},
                false,
                200
        );
        var execution = monitor.run();
        assertEquals("SSL Connection problem: Received fatal alert: handshake_failure", execution.getErrorMessage());
    }

    @Test
    void error_if_http2() {
        var monitor = createMonitor(
                "https://hestiacertif.aoc.cat",
                new String[]{"TLSv1.2", "TLSv1.3"},
                false,
                200
        );
        var execution = monitor.run();
        assertEquals("I/O error occurred when sending or receiving: Received RST_STREAM: Use HTTP/1.1 for request", execution.getErrorMessage());
    }

    @Test
    void error_if_certificate_handshake() {
        var monitor = createMonitor(
                "https://ssc.catcert.cat:8090/",
                new String[]{"TLSv1.2", "TLSv1.3"},
                false,
                200
        );
        var execution = monitor.run();
        assertEquals("SSL Connection problem: Remote host terminated the handshake", execution.getErrorMessage());
    }

    @Test
    void error_if_host_unresolved() {
        var monitor = createMonitor(
                "https://123456789.aoc.cat",
                new String[]{"TLSv1.2", "TLSv1.3"},
                false,
                200
        );
        var execution = monitor.run();
        assertEquals("ConnectException (Unresolved host?): null", execution.getErrorMessage());
    }

    @Test
    void error_if_certificate_expired() {
        var monitor = createMonitor(
                "https://expired.badssl.com/",
                new String[]{"TLSv1.2", "TLSv1.3"},
                false,
                200
        );
        var execution = monitor.run();
        assertEquals("SSL Connection problem: PKIX path validation failed: java.security.cert.CertPathValidatorException: validity check failed", execution.getErrorMessage());
    }

    @Test
    void error_if_certificate_revoked() {
        var monitor = createMonitor(
                "https://revoked.badssl.com/",
                new String[]{"TLSv1.2", "TLSv1.3"},
                false,
                200
        );
        var execution = monitor.run();
        assertEquals("SSL Connection problem: PKIX path validation failed: java.security.cert.CertPathValidatorException: validity check failed", execution.getErrorMessage());
    }

    @Test
    void error_if_host_doesNotMatch_certificate() {
        var monitor = createMonitor(
                "https://wrong.host.badssl.com/",
                new String[]{"TLSv1.2", "TLSv1.3"},
                false,
                200
        );
        var execution = monitor.run();
        assertEquals("SSL Connection problem: No subject alternative DNS name matching wrong.host.badssl.com found.", execution.getErrorMessage());
    }

    @Test
    void error_if_certificate_untrusted_notInCacerts() {
        var monitor = createMonitor(
                "https://untrusted-root.badssl.com/",
                new String[]{"TLSv1.2", "TLSv1.3"},
                false,
                200
        );
        var execution = monitor.run();
        assertEquals("SSL Connection problem: PKIX path building failed: sun.security.provider.certpath.SunCertPathBuilderException: unable to find valid certification path to requested target", execution.getErrorMessage());
    }

    @Test
    void error_if_clientCertificate_missing() {
        var monitor = createMonitor(
                "https://client.badssl.com/",
                new String[]{"TLSv1.2", "TLSv1.3"},
                false,
                400
        );
        var execution = monitor.run();
        assertFalse(execution.isError());
        assertFalse(execution.isAlert());
    }

    @Test
    void connect_with_clientCertificate() {
        var monitor = createMonitor(
                "https://client.badssl.com/",
                new String[]{"TLSv1.2", "TLSv1.3"},
                true,
                200
        );
        var execution = monitor.run();
        assertFalse(execution.isError());
        assertFalse(execution.isAlert());
    }

    @Test
    void alert_with_statusCode() {
        var monitor = createMonitor(
                "http://github.com",
                new String[]{"TLSv1.2", "TLSv1.3"},
                false,
                404
        );
        var execution = monitor.run();
        assertFalse(execution.isError());
        assertTrue(execution.isAlert());
    }

    @Test
    void alert_with_expectedBody_nonStrictMode() {
        var monitor = createPostMonitor(
                "https://httpbin.org/anything",
                "Hello world",
                "{\"data\": \"Hello world\"}", false
        );
        var execution = monitor.run();
        assertFalse(execution.isError());
        assertFalse(execution.isAlert());
    }

    @Test
    void alert_with_expectedBody_strictMode() {
        var monitor = createPostMonitor(
                "https://httpbin.org/anything",
                "Hello world",
                "{\"data\": \"Hello world\"}",
                true
        );
        var execution = monitor.run();
        assertFalse(execution.isError());
        assertTrue(execution.isAlert());
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

    private HttpMonitor createMonitor(String endpoint, String[] sslProtocols, boolean clientCertificate, int statusCode) {
        return new HttpMonitor(
                new HttpContext(
                        new MonitorMetadata(
                                "name",
                                "desc",
                                "cron",
                                "service",
                                new HashSet<>(),
                                "docs"),
                        endpoint,
                        HttpContext.RequestMethod.GET,
                        null,
                        1,
                        HttpClient.Version.HTTP_2,
                        HttpClient.Redirect.NEVER,
                        sslProtocols,
                        clientCertificate,
                        statusCode,
                        null,
                        false
                ),
                testProperties);
    }

    private HttpMonitor createPostMonitor(String endpoint, String body, String expectedBody, boolean strict) {
        return new HttpMonitor(
                new HttpContext(
                        new MonitorMetadata(
                                "name",
                                "desc",
                                "cron",
                                "service",
                                new HashSet<>(),
                                "docs"),
                        endpoint,
                        HttpContext.RequestMethod.POST,
                        body,
                        1,
                        HttpClient.Version.HTTP_2,
                        HttpClient.Redirect.NEVER,
                        new String[]{"TLSv1.2", "TLSv1.3"},
                        false,
                        200,
                        expectedBody,
                        strict
                ),
                testProperties);
    }

}