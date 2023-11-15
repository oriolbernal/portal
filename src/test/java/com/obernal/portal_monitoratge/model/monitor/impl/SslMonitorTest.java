package com.obernal.portal_monitoratge.model.monitor.impl;

import com.obernal.portal_monitoratge.model.monitor.impl.ssl.SslMetadata;
import com.obernal.portal_monitoratge.model.monitor.impl.ssl.SslMonitor;
import org.junit.jupiter.api.Test;

import java.net.http.HttpClient;
import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.*;

class SslMonitorTest {

    @Test
    void sslCertificate_inLessThan_1ms() {
        SslMonitor monitor = new SslMonitor(
                new SslMetadata(
                        "name",
                        "desc",
                        "cron",
                        "service",
                        new HashSet<>(),
                        "docs",
                        "https://google.es",
                        1,
                        HttpClient.Version.HTTP_2,
                        HttpClient.Redirect.NEVER,
                        new String[]{"TLSv1", "TLSv1.1", "TLSv1.2", "TLSv1.3"},
                        false,
                        0
                ));
        var execution = monitor.run();
        assertFalse(execution.isError());
        assertFalse(execution.isAlert());
    }

    @Test
    void error_if_http() {
        SslMonitor monitor = new SslMonitor(
                new SslMetadata("name",
                        "desc",
                        "cron",
                        "service",
                        new HashSet<>(),
                        "docs",
                        "http://github.com",
                        1,
                        HttpClient.Version.HTTP_2,
                        HttpClient.Redirect.NEVER,
                        new String[]{"TLSv1", "TLSv1.1", "TLSv1.2", "TLSv1.3"},
                        false,
                        0
                ));
        var execution = monitor.run();
        assertEquals("Connection is not ssl!", execution.getErrorMessage());
    }

    @Test
    void error_if_timeout() {
        SslMonitor monitor = new SslMonitor(
                new SslMetadata(
                        "name",
                        "desc",
                        "cron",
                        "service",
                        new HashSet<>(),
                        "docs",
                        "https://10.255.255.1/", // non-routable ip adress
                        1,
                        HttpClient.Version.HTTP_2,
                        HttpClient.Redirect.NEVER,
                        new String[]{"TLSv1", "TLSv1.1", "TLSv1.2", "TLSv1.3"},
                        false,
                        0
                ));
        var execution = monitor.run();
        assertEquals("Connection Timeout: HTTP connect timed out", execution.getErrorMessage());
    }

    @Test
    void error_if_tlsProtocol_notConfigured() {
        SslMonitor monitor = new SslMonitor(
                new SslMetadata(
                        "name",
                        "desc",
                        "cron",
                        "service",
                        new HashSet<>(),
                        "docs",
                        "https://github.com/",
                        1,
                        HttpClient.Version.HTTP_2,
                        HttpClient.Redirect.NEVER,
                        new String[]{},
                        false,
                        0
                ));
        var execution = monitor.run();
        assertEquals("SSL Connection problem: No appropriate protocol (protocol is disabled or cipher suites are inappropriate)", execution.getErrorMessage());
    }

    @Test
    void error_if_tlsProtocol_nonExistent() {
        SslMonitor monitor = new SslMonitor(
                new SslMetadata(
                        "name",
                        "desc",
                        "cron",
                        "service",
                        new HashSet<>(),
                        "docs",
                        "https://github.com/",
                        1,
                        HttpClient.Version.HTTP_2,
                        HttpClient.Redirect.NEVER,
                        new String[]{"A"},
                        false,
                        0
                ));
        var execution = monitor.run();
        assertEquals("Nonexistent SSLProtocol: Unsupported protocol: A", execution.getErrorMessage());
    }

    @Test
    void error_if_certificate_handshake() {
        SslMonitor monitor = new SslMonitor(
                new SslMetadata(
                        "name",
                        "desc",
                        "cron",
                        "service",
                        new HashSet<>(),
                        "docs",
                        "https://ssc.catcert.cat:8090/",
                        1,
                        HttpClient.Version.HTTP_2,
                        HttpClient.Redirect.NEVER,
                        new String[]{"TLSv1", "TLSv1.1", "TLSv1.2", "TLSv1.3"},
                        false,
                        0
                ));
        var execution = monitor.run();
        assertEquals("SSL Connection problem: Remote host terminated the handshake", execution.getErrorMessage());
    }

    @Test
    void error_if_http2() {
        SslMonitor monitor = new SslMonitor(
                new SslMetadata(
                        "name",
                        "desc",
                        "cron",
                        "service",
                        new HashSet<>(),
                        "docs",
                        "https://hestiacertif.aoc.cat",
                        1,
                        HttpClient.Version.HTTP_2,
                        HttpClient.Redirect.NEVER,
                        new String[]{"TLSv1", "TLSv1.1", "TLSv1.2", "TLSv1.3"},
                        false,
                        0
                ));
        var execution = monitor.run();
        assertEquals("I/O error occurred when sending or receiving: Received RST_STREAM: Use HTTP/1.1 for request", execution.getErrorMessage());
    }

    @Test
    void error_if_host_doesNotMatch_certificate() {
        SslMonitor monitor = new SslMonitor(
                new SslMetadata(
                        "name",
                        "desc",
                        "cron",
                        "service",
                        new HashSet<>(),
                        "docs",
                        "https://wrong.host.badssl.com/",
                        1,
                        HttpClient.Version.HTTP_2,
                        HttpClient.Redirect.NEVER,
                        new String[]{"TLSv1", "TLSv1.1", "TLSv1.2", "TLSv1.3"},
                        false,
                        0
                ));
        var execution = monitor.run();
        assertEquals("SSL Connection problem: No subject alternative DNS name matching wrong.host.badssl.com found.", execution.getErrorMessage());
    }

    @Test
    void error_if_host_unresolved() {
        SslMonitor monitor = new SslMonitor(
                new SslMetadata(
                        "name",
                        "desc",
                        "cron",
                        "service",
                        new HashSet<>(),
                        "docs",
                        "https://serveis3-dev.nt.aoc.cat",
                        1,
                        HttpClient.Version.HTTP_2,
                        HttpClient.Redirect.NEVER,
                        new String[]{"TLSv1", "TLSv1.1", "TLSv1.2", "TLSv1.3"},
                        false,
                        0
                ));
        var execution = monitor.run();
        assertEquals("ConnectException (Unresolved host?): null", execution.getErrorMessage());
    }

    @Test
    void error_if_certificate_untrusted_NotInCacerts() {
        SslMonitor monitor = new SslMonitor(
                new SslMetadata(
                        "name",
                        "desc",
                        "cron",
                        "service",
                        new HashSet<>(),
                        "docs",
                        "https://self-signed.badssl.com/",
                        1,
                        HttpClient.Version.HTTP_2,
                        HttpClient.Redirect.NEVER,
                        new String[]{"TLSv1", "TLSv1.1", "TLSv1.2", "TLSv1.3"},
                        false,
                        0
                ));
        var execution = monitor.run();
        assertEquals("SSL Connection problem: PKIX path building failed: sun.security.provider.certpath.SunCertPathBuilderException: unable to find valid certification path to requested target", execution.getErrorMessage());
    }

    @Test
    void connect_with_clientAuth() {
        SslMonitor monitor = new SslMonitor(
                new SslMetadata(
                        "name",
                        "desc",
                        "cron",
                        "service",
                        new HashSet<>(),
                        "docs",
                        "https://etramrouter-pre.aoc.cat/etramrouter/Sincron#SSL",
                        1,
                        HttpClient.Version.HTTP_2,
                        HttpClient.Redirect.NEVER,
                        new String[]{"TLSv1", "TLSv1.1", "TLSv1.2", "TLSv1.3"},
                        false,
                        0
                ));
        assertDoesNotThrow(monitor::run);
    }

    @Test
    void error_if_certificate_revoked() {
        SslMonitor monitor = new SslMonitor(
                new SslMetadata(
                        "name",
                        "desc",
                        "cron",
                        "service",
                        new HashSet<>(),
                        "docs",
                        "https://revoked.badssl.com/",
                        1,
                        HttpClient.Version.HTTP_2,
                        HttpClient.Redirect.NEVER,
                        new String[]{"TLSv1", "TLSv1.1", "TLSv1.2", "TLSv1.3"},
                        false,
                        0
                ));
        var execution = monitor.run();
        assertEquals("SSL Connection problem: PKIX path validation failed: java.security.cert.CertPathValidatorException: validity check failed", execution.getErrorMessage());
    }

    @Test
    void sslCertificate_expired_shouldNot_returnError() {
        SslMonitor monitor = new SslMonitor(
                new SslMetadata(
                        "name",
                        "desc",
                        "cron",
                        "service",
                        new HashSet<>(),
                        "docs",
                        "https://expired.badssl.com/",
                        1,
                        HttpClient.Version.HTTP_2,
                        HttpClient.Redirect.NEVER,
                        new String[]{"TLSv1", "TLSv1.1", "TLSv1.2", "TLSv1.3"},
                        false,
                        0
                ));
        assertDoesNotThrow(monitor::run);
    }


}