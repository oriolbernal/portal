package com.obernal.portal_monitoratge.app.clients;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.net.ssl.X509TrustManager;
import java.security.cert.X509Certificate;
import java.util.Date;

import static org.mockito.Mockito.*;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

public class IgnoreCertificateExpirationTrustManagerTest {

    private IgnoreCertificateExpirationTrustManager trustManager;

    @BeforeEach
    public void setup() {
        X509TrustManager innerTrustManager = mock(X509TrustManager.class);
        trustManager = new IgnoreCertificateExpirationTrustManager(innerTrustManager);
    }

    @Test
    public void checkServerTrusted_WithValidCertificate_ShouldNotThrowException() {
        X509Certificate[] chain = new X509Certificate[1];
        X509Certificate validCertificate = createValidCertificate();
        chain[0] = validCertificate;
        assertDoesNotThrow(() -> trustManager.checkServerTrusted(chain, "RSA"));
    }

    @Test
    public void checkClientTrusted_WithValidCertificate_ShouldNotThrowException() {
        X509Certificate[] chain = new X509Certificate[1];
        X509Certificate validCertificate = createValidCertificate();
        chain[0] = validCertificate;
        assertDoesNotThrow(() -> trustManager.checkClientTrusted(chain, "RSA"));
    }

    @Test
    public void checkServerTrusted_WithExpiredCertificate_ShouldNotThrowException() {
        X509Certificate[] chain = new X509Certificate[1];
        X509Certificate expiredCertificate = createExpiredCertificate();
        chain[0] = expiredCertificate;
        assertDoesNotThrow(() -> trustManager.checkServerTrusted(chain, "RSA"));
    }

    @Test
    public void checkClientTrusted_WithExpiredCertificate_ShouldNotThrowException() {
        X509Certificate[] chain = new X509Certificate[1];
        X509Certificate expiredCertificate = createExpiredCertificate();
        chain[0] = expiredCertificate;
        assertDoesNotThrow(() -> trustManager.checkClientTrusted(chain, "RSA"));
    }

    private X509Certificate createExpiredCertificate() {
        X509Certificate certificate = mock(X509Certificate.class);
        when(certificate.getNotAfter()).thenReturn(new Date(System.currentTimeMillis() - 1000));
        return certificate;
    }

    private X509Certificate createValidCertificate() {
        X509Certificate certificate = mock(X509Certificate.class);
        when(certificate.getNotAfter()).thenReturn(new Date(System.currentTimeMillis() + 1000));
        return certificate;
    }
}
