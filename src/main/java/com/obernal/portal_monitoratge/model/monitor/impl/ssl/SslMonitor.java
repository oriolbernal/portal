package com.obernal.portal_monitoratge.model.monitor.impl.ssl;

import com.obernal.portal_monitoratge.clients.IgnoreCertificateExpirationTrustManager;
import com.obernal.portal_monitoratge.model.monitor.impl.http.HttpMonitor;
import com.obernal.portal_monitoratge.model.monitor.impl.http.HttpResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.*;
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
import java.util.Properties;

public class SslMonitor extends HttpMonitor {
    private static final Logger logger = LoggerFactory.getLogger(SslMonitor.class);

    public SslMonitor(SslMetadata metadata, Properties properties) {
        super(metadata, properties);
    }

    @Override
    protected SslResult perform() throws Exception {
        try {
            KeyManager[] keyManagers = null;
            if(metadata.isClientCertificate()) {
                keyManagers = getClientCertificate("src/main/resources/CDA-1_SGNM_00.p12", "1234");
            }
            HttpClient client = metadata.getClient(keyManagers, getTrustManagers());
            HttpRequest request = metadata.getRequest();
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
    protected boolean isAlert(HttpResult result) {
        LocalDateTime certificateExpiration = ((SslResult) result).getSSLExpirationDate(0);
        LocalDateTime alertDate = LocalDateTime.now().plusDays(((SslMetadata) metadata).getDaysInAdvance());
        return alertDate.isAfter(certificateExpiration);
    }

}
