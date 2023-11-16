package com.obernal.portal_monitoratge.model.monitor.impl.http;

import org.junit.jupiter.api.Test;

import java.net.http.HttpClient;
import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.*;

class HttpMonitorTest {

    @Test
    void GET_http() {
        var monitor = new HttpMonitor(
                new HttpMetadata(
                        "name",
                        "desc",
                        "cron",
                        "service",
                        new HashSet<>(),
                        "docs",
                        "http://github.com",
                        HttpMetadata.RequestMethod.GET,
                        null,
                        1,
                        HttpClient.Version.HTTP_2,
                        HttpClient.Redirect.NEVER,
                        new String[]{"TLSv1", "TLSv1.1", "TLSv1.2", "TLSv1.3"},
                        false,
                        301,
                        null
                ));
        var execution = monitor.run();
        assertFalse(execution.isError());
        assertFalse(execution.isAlert());
    }

    @Test
    void GET_https() {
        var monitor = new HttpMonitor(
                new HttpMetadata(
                        "name",
                        "desc",
                        "cron",
                        "service",
                        new HashSet<>(),
                        "docs",
                        "https://github.com",
                        HttpMetadata.RequestMethod.GET,
                        null,
                        1,
                        HttpClient.Version.HTTP_2,
                        HttpClient.Redirect.NEVER,
                        new String[]{"TLSv1", "TLSv1.1", "TLSv1.2", "TLSv1.3"},
                        false,
                        200,
                        null
                ));
        var execution = monitor.run();
        assertFalse(execution.isError());
        assertFalse(execution.isAlert());
    }

    @Test
    void POST() {
        var monitor = new HttpMonitor(
                new HttpMetadata(
                        "name",
                        "desc",
                        "cron",
                        "service",
                        new HashSet<>(),
                        "docs",
                        "https://httpbin.org/anything",
                        HttpMetadata.RequestMethod.POST,
                        "Hello world",
                        1,
                        HttpClient.Version.HTTP_2,
                        HttpClient.Redirect.NEVER,
                        new String[]{"TLSv1", "TLSv1.1", "TLSv1.2", "TLSv1.3"},
                        false,
                        200,
                        "\"data\": \"Hello world\""
                ));
        var execution = monitor.run();
        assertFalse(execution.isError());
        assertFalse(execution.isAlert());
    }

    @Test
    void error_if_tlsProtocol_nonExistent() {
        var monitor = new HttpMonitor(
                new HttpMetadata(
                        "name",
                        "desc",
                        "cron",
                        "service",
                        new HashSet<>(),
                        "docs",
                        "https://httpbin.org/anything",
                        HttpMetadata.RequestMethod.POST,
                        "Hello world",
                        1,
                        HttpClient.Version.HTTP_2,
                        HttpClient.Redirect.NEVER,
                        new String[]{"A"},
                        false,
                        200,
                        "\"data\": \"Hello world\""
                ));
        var execution = monitor.run();
        assertEquals("Nonexistent SSLProtocol: Unsupported protocol: A", execution.getErrorMessage());
    }

    @Test
    void sslCertificate_expired_shouldNot_returnError() {
        var monitor = new HttpMonitor(
                new HttpMetadata(
                        "name",
                        "desc",
                        "cron",
                        "service",
                        new HashSet<>(),
                        "docs",
                        "https://expired.badssl.com/",
                        HttpMetadata.RequestMethod.GET,
                        null,
                        1,
                        HttpClient.Version.HTTP_2,
                        HttpClient.Redirect.NEVER,
                        new String[]{"TLSv1", "TLSv1.1", "TLSv1.2", "TLSv1.3"},
                        false,
                        200,
                        null
                ));
        var execution = monitor.run();
        assertEquals("I/O error occurred when sending or receiving: PKIX path validation failed: java.security.cert.CertPathValidatorException: validity check failed", execution.getErrorMessage());
    }

    @Test
    void error_if_timeout() {
        var monitor = new HttpMonitor(
                new HttpMetadata(
                        "name",
                        "desc",
                        "cron",
                        "service",
                        new HashSet<>(),
                        "docs",
                        "https://10.255.255.1/", // non-routable ip adress
                        HttpMetadata.RequestMethod.GET,
                        null,
                        1,
                        HttpClient.Version.HTTP_2,
                        HttpClient.Redirect.NEVER,
                        new String[]{"TLSv1", "TLSv1.1", "TLSv1.2", "TLSv1.3"},
                        false,
                        200,
                        null
                ));
        var execution = monitor.run();
        assertEquals("Connection Timeout: HTTP connect timed out", execution.getErrorMessage());
    }

    @Test
    void error_if_host_unresolved() {
        var monitor = new HttpMonitor(
                new HttpMetadata(
                        "name",
                        "desc",
                        "cron",
                        "service",
                        new HashSet<>(),
                        "docs",
                        "https://123456789.aoc.cat",
                        HttpMetadata.RequestMethod.GET,
                        null,
                        1,
                        HttpClient.Version.HTTP_2,
                        HttpClient.Redirect.NEVER,
                        new String[]{"TLSv1", "TLSv1.1", "TLSv1.2", "TLSv1.3"},
                        false,
                        200,
                        null
                ));
        var execution = monitor.run();
        assertEquals("ConnectException (Unresolved host?): null", execution.getErrorMessage());
    }

    @Test
    void alert_with_statusCode() {
        var monitor = new HttpMonitor(
                new HttpMetadata(
                        "name",
                        "desc",
                        "cron",
                        "service",
                        new HashSet<>(),
                        "docs",
                        "http://github.com",
                        HttpMetadata.RequestMethod.GET,
                        null,
                        1,
                        HttpClient.Version.HTTP_2,
                        HttpClient.Redirect.NEVER,
                        new String[]{"TLSv1", "TLSv1.1", "TLSv1.2", "TLSv1.3"},
                        false,
                        404,
                        null
                ));
        var execution = monitor.run();
        assertFalse(execution.isError());
        assertTrue(execution.isAlert());
    }

}