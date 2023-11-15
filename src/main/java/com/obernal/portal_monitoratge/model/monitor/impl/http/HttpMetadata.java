package com.obernal.portal_monitoratge.model.monitor.impl.http;

import com.obernal.portal_monitoratge.model.monitor.MonitorMetadata;
import com.obernal.portal_monitoratge.model.monitor.MonitorType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.*;
import java.io.FileInputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Set;

public class HttpMetadata extends MonitorMetadata {
    private static final Logger logger = LoggerFactory.getLogger(HttpMetadata.class);
    private final String endpoint;
    private final RequestMethod method;
    private final HttpRequest.BodyPublisher publisher;
    private final int timeOutInSeconds;
    private final HttpClient.Version version;
    private final HttpClient.Redirect redirect;
    private final SSLParameters sslParameters;
    private final boolean sslAuth;
    private final Integer statusCode;
    private final String expectedBody;

    public HttpMetadata(String name, String description, String cron, String service, Set<String> labels, String documentation, String endpoint, RequestMethod method, String body, int timeOutInSeconds, HttpClient.Version version, HttpClient.Redirect redirect, String[] sslProtocols, boolean sslAuth, Integer statusCode, String expectedBody) {
        super(name, description, cron, service, labels, documentation);
        this.endpoint = endpoint;
        this.method = method;
        this.publisher = body != null ? HttpRequest.BodyPublishers.ofString(body) : null;
        this.timeOutInSeconds = timeOutInSeconds;
        this.version = version;
        this.redirect = redirect;
        sslParameters = new SSLParameters();
        sslParameters.setProtocols(sslProtocols);
        this.sslAuth = sslAuth;
        this.statusCode = statusCode;
        this.expectedBody = expectedBody;
    }

    public HttpMetadata(String id, LocalDateTime created, LocalDateTime updated, String name, String description, String cron, String service, Set<String> labels, String documentation, boolean active, String endpoint, RequestMethod method, String body, int timeOutInSeconds, HttpClient.Version version, HttpClient.Redirect redirect, String[] sslProtocols, boolean sslAuth, Integer statusCode, String expectedBody) {
        super(id, created, updated, name, description, cron, service, labels, documentation, active);
        this.endpoint = endpoint;
        this.method = method;
        this.publisher = body != null ? HttpRequest.BodyPublishers.ofString(body) : null;
        this.timeOutInSeconds = timeOutInSeconds;
        this.version = version;
        this.redirect = redirect;
        sslParameters = new SSLParameters();
        sslParameters.setProtocols(sslProtocols);
        this.sslAuth = sslAuth;
        this.statusCode = statusCode;
        this.expectedBody = expectedBody;
    }

    @Override
    public MonitorType getType() {
        return MonitorType.HTTP;
    }

    public HttpClient getClient(TrustManager[] trustManagers) throws NoSuchAlgorithmException, KeyManagementException {
        KeyManager[] keyManagers = null;
        if(sslAuth) {
            keyManagers = getClientAuth("src/main/resources/CDA-1_SGNM_00.p12", "1234");
        }
        SSLContext sslContext = SSLContext.getInstance("TLS");
        sslContext.init(
                keyManagers,
                trustManagers,
                new SecureRandom()
        );
        return HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(timeOutInSeconds))
                .version(version)
                .followRedirects(redirect)
                .sslContext(sslContext)
                .sslParameters(sslParameters)
                .build();
    }

    public HttpClient getClient() throws NoSuchAlgorithmException, KeyManagementException {
        return getClient(null);
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

    public HttpRequest getRequest() {
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

    public String getEndpoint() {
        return endpoint;
    }

    public RequestMethod getMethod() {
        return method;
    }

    public HttpRequest.BodyPublisher getPublisher() {
        return publisher;
    }

    public int getTimeOutInSeconds() {
        return timeOutInSeconds;
    }

    public HttpClient.Version getVersion() {
        return version;
    }

    public HttpClient.Redirect getRedirect() {
        return redirect;
    }

    public SSLParameters getSslParameters() {
        return sslParameters;
    }

    public Integer getStatusCode() {
        return statusCode;
    }

    public String getExpectedBody() {
        return expectedBody;
    }

    public enum RequestMethod {
        GET, POST, PUT, DELETE, HEAD, PATCH, OPTIONS, TRACE;
    }

}
