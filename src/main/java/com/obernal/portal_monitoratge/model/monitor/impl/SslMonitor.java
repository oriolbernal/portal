package com.obernal.portal_monitoratge.model.monitor.impl;

import com.obernal.portal_monitoratge.model.Execution;
import com.obernal.portal_monitoratge.model.Task;
import com.obernal.portal_monitoratge.model.monitor.impl.clients.IgnoreCertificateExpirationTrustManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.*;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.ConnectException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpConnectTimeoutException;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.security.KeyStore;
import java.security.SecureRandom;
import java.time.Duration;
import java.time.LocalDateTime;

public class SslMonitor implements Task<Execution> {

    private static final Logger logger = LoggerFactory.getLogger(DbMonitor.class);

    private final String id;
    private final String endpoint;
    private final RequestMethod method;
    private final HttpRequest.BodyPublisher publisher;
    private final int timeOutInSeconds;
    private final HttpClient.Version version;
    private final HttpClient.Redirect redirect;
    private final SSLParameters sslParameters;
    private final int daysInAdvance;

    public SslMonitor(String id, String endpoint, int timeOutInSeconds, HttpClient.Version version, HttpClient.Redirect redirect, String[] sslProtocols, int daysInAdvance) {
        this.id = id;
        this.endpoint = endpoint;
        this.method = RequestMethod.GET;
        this.publisher = null;
        this.timeOutInSeconds = timeOutInSeconds;
        this.version = version;
        this.redirect = redirect;
        sslParameters = new SSLParameters();
        sslParameters.setProtocols(sslProtocols);
        this.daysInAdvance = daysInAdvance;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public Execution run() {
        logger.info("Executing monitor: {}", id);
        long start = System.currentTimeMillis();
        try {
            SslResult result = compute();
            boolean alert = isAlert(result);
            return new Execution(start, alert);
        } catch (Exception exception) {
            logger.error("Error executing monitor: {} --> {}", id, exception.getMessage(), exception);
            return new Execution(start, exception);
        }
    }

    private SslResult compute() throws InterruptedException {
        HttpClient client = getClient();
        HttpRequest request = getRequest();
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

    private HttpClient getClient() {
        return HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(timeOutInSeconds))
                .version(version)
                .followRedirects(redirect)
                .sslContext(getSSLContext())
                .sslParameters(sslParameters)
                .build();
    }

    private static SSLContext getSSLContext() {
        SSLContext sslContext;
        try {
            sslContext = SSLContext.getInstance("TLS");
            sslContext.init(
                    getClientAuth("src/main/resources/CDA-1_SGNM_00.p12", "1234"),
                    getTrustManagers(),
                    new SecureRandom()
            );
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return sslContext;
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
            e.printStackTrace();
            return null;
        }
    }

    private HttpRequest getRequest() {
        HttpRequest.Builder builder = HttpRequest.newBuilder()
                .uri(URI.create(endpoint));
        return switch (method) {
            case GET -> builder.GET().build();
            case POST -> builder.POST(publisher).build();
            case PUT -> builder.PUT(publisher).build();
            case DELETE -> builder.DELETE().build();
            default -> builder.method(method.toString(), publisher).build();
        };
    }

    private enum RequestMethod {
        GET,  POST, PUT, DELETE, HEAD, PATCH, OPTIONS, TRACE;
    }

    private boolean isAlert(SslResult result) {
        if (result.getSSLExpirationDate(0).isEmpty()) {
            throw new RuntimeException("SSL certificate not found");
        }
        LocalDateTime certificateExpiration = result.getSSLExpirationDate(0).get();
        LocalDateTime alertDate = LocalDateTime.now().plusDays(daysInAdvance);
        return alertDate.isAfter(certificateExpiration);
    }

}
