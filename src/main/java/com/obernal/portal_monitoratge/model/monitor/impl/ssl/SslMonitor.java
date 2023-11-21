package com.obernal.portal_monitoratge.model.monitor.impl.ssl;

import com.obernal.portal_monitoratge.app.service.AlertService;
import com.obernal.portal_monitoratge.app.clients.IgnoreCertificateExpirationTrustManager;
import com.obernal.portal_monitoratge.model.monitor.Monitor;

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
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class SslMonitor extends Monitor<SslContext, SslResult> {
    private static final Logger logger = LoggerFactory.getLogger(SslMonitor.class);

    private final Properties properties;

    public SslMonitor(AlertService alertService, SslContext context, Properties properties) {
        super(alertService, context);
        this.properties = properties;
    }

    @Override
    protected SslResult perform() throws Exception {
        try {
            KeyManager[] keyManagers = null;
            if(context.isClientCertificate()) {
                keyManagers = getClientCertificate(properties.getProperty("http.certificateClient.p12"), properties.getProperty("http.certificateClient.password"));
            }
            HttpClient client = context.getClient(keyManagers, getTrustManagers());
            HttpRequest request = context.getRequest();
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

    private KeyManager[] getClientCertificate(String p12Path, String password) {
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
        } catch (NoSuchAlgorithmException | KeyStoreException e) {
            logger.error("Unable to load truststore manager", e);
            return null;
        }
    }

    @Override
    public List<String> getAlerts(SslResult result) {
        List<String> alerts = new ArrayList<>();
        LocalDateTime certificateExpiration = result.getSSLExpirationDate(0);
        LocalDateTime alertDate = LocalDateTime.now().plusDays(context.getDaysInAdvance());
        if(alertDate.isAfter(certificateExpiration)) {
            alerts.add("SSL Certificate expires in les than " + context.getDaysInAdvance() + " days: " + result.getSSLExpirationDate(0));
        }
        return alerts;
    }
}
