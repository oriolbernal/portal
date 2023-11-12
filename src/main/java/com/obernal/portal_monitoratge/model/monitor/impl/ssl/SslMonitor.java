package com.obernal.portal_monitoratge.model.monitor.impl.ssl;

import com.obernal.portal_monitoratge.model.monitor.Monitor;
import com.obernal.portal_monitoratge.model.monitor.impl.clients.IgnoreCertificateExpirationTrustManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.*;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.ConnectException;
import java.net.http.HttpClient;
import java.net.http.HttpConnectTimeoutException;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.security.KeyStore;
import java.time.LocalDateTime;

public class SslMonitor extends Monitor<SslMetadata, SslResult> {
    private static final Logger logger = LoggerFactory.getLogger(SslMonitor.class);

    public SslMonitor(SslMetadata metadata) {
        super(metadata);
    }

    @Override
    protected SslResult perform() throws Exception {
        HttpClient client = metadata.getClient(
                getClientAuth("src/main/resources/CDA-1_SGNM_00.p12", "1234"),
                getTrustManagers()
        );
        HttpRequest request = metadata.getRequest();
        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            return new SslResult(response);
        } catch (HttpConnectTimeoutException e) {
            throw new RuntimeException("Connection Timeout: " + e.getMessage());
        } catch (SSLHandshakeException e) {
            throw new RuntimeException("SSL Connection problem: " + e.getMessage());
        } catch (ConnectException e) {
            throw new RuntimeException("ConnectException (Unresolved host?): " + e.getMessage());
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Nonexistent SSLProtocol: " + e.getMessage());
        } catch (IOException e) {
            throw new RuntimeException("I/O error occurred when sending or receiving: " + e.getMessage());
        }
    }

    private static KeyManager[] getClientAuth(String p12Path, String password) {
        try {
            KeyStore keyStore = KeyStore.getInstance("PKCS12");
            keyStore.load(new FileInputStream(p12Path), password.toCharArray());
            KeyManagerFactory keyMgrFactory = KeyManagerFactory.getInstance("SunX509");
            keyMgrFactory.init(keyStore, password.toCharArray());
            return keyMgrFactory.getKeyManagers();
        } catch (Exception e) {
            logger.warn("Unable to load client certificate, no certificate will be configured", e);
        }
        return null;
    }

    private static TrustManager[] getTrustManagers() {
        try {
            TrustManagerFactory factory = TrustManagerFactory.getInstance("X509");
            factory.init((KeyStore) null);
            TrustManager[] trustManagers = factory.getTrustManagers();
            for (int i = 0; i < trustManagers.length; i++) {
                if (trustManagers[i] instanceof X509TrustManager) {
                    trustManagers[i] = new IgnoreCertificateExpirationTrustManager((X509TrustManager) trustManagers[i]);
                }
            }
            return trustManagers;
        } catch (Exception e) {
            logger.error("Unable to load trustore manager", e);
            return null;
        }
    }

    @Override
    protected boolean isAlert(SslResult result) {
        if (result.getSSLExpirationDate(0).isEmpty()) {
            throw new RuntimeException("SSL certificate not found");
        }
        LocalDateTime certificateExpiration = result.getSSLExpirationDate(0).get();
        LocalDateTime alertDate = LocalDateTime.now().plusDays(metadata.getDaysInAdvance());
        return alertDate.isAfter(certificateExpiration);
    }

}
